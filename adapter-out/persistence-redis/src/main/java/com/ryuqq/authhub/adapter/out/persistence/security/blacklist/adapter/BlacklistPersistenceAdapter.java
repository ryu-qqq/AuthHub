package com.ryuqq.authhub.adapter.out.persistence.security.blacklist.adapter;

import com.ryuqq.authhub.adapter.out.persistence.security.blacklist.entity.BlacklistedTokenRedisEntity;
import com.ryuqq.authhub.adapter.out.persistence.security.blacklist.mapper.BlacklistEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.security.blacklist.repository.BlacklistRedisRepository;
import com.ryuqq.authhub.application.security.blacklist.port.out.AddToBlacklistPort;
import com.ryuqq.authhub.application.security.blacklist.port.out.CheckBlacklistPort;
import com.ryuqq.authhub.application.security.blacklist.port.out.RemoveFromBlacklistPort;
import com.ryuqq.authhub.domain.security.blacklist.BlacklistedToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Blacklist Persistence Adapter.
 *
 * <p>Redis를 사용하여 블랙리스트 토큰을 관리하는 Adapter입니다.
 * {@link AddToBlacklistPort}, {@link CheckBlacklistPort}, {@link RemoveFromBlacklistPort}를 구현하여
 * Application Layer의 Port를 Persistence Layer의 Redis와 연결합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>블랙리스트 토큰을 Redis에 저장 (Hash + SET + ZSET)</li>
 *   <li>JTI 존재 여부를 빠르게 확인 (SET O(1))</li>
 *   <li>만료된 토큰을 배치로 제거 (ZSET ZRANGEBYSCORE)</li>
 * </ul>
 *
 * <p><strong>Redis 구조:</strong></p>
 * <ul>
 *   <li><strong>Hash</strong>: {@code blacklist_token:{jti}} - 상세 정보 저장</li>
 *   <li><strong>SET</strong>: {@code blacklist:tokens} - JTI 목록 (빠른 조회용)</li>
 *   <li><strong>ZSET</strong>: {@code blacklist:expiry} - 만료 시간 기준 정렬 (배치 삭제용)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Law of Demeter 준수 - RedisTemplate 직접 사용, 체이닝 최소화</li>
 *   <li>✅ Transaction 경계 - Redis는 내부 시스템, @Transactional 내부 호출 가능</li>
 *   <li>✅ Null 안전성 - null 체크 철저히 수행</li>
 *   <li>✅ Batch 처리 - 대량 삭제 시 성능 최적화</li>
 * </ul>
 *
 * <p><strong>성능 고려사항:</strong></p>
 * <ul>
 *   <li>exists() - O(1) 복잡도 (SET SISMEMBER)</li>
 *   <li>add() - O(1) 복잡도 (Hash 저장 + SET SADD + ZSET ZADD)</li>
 *   <li>findExpiredJtis() - O(log(N) + M) 복잡도 (ZSET ZRANGEBYSCORE)</li>
 *   <li>removeAll() - O(N) 복잡도 (Hash DEL + SET SREM + ZSET ZREM)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class BlacklistPersistenceAdapter implements
        AddToBlacklistPort,
        CheckBlacklistPort,
        RemoveFromBlacklistPort {

    private static final String SET_KEY = "blacklist:tokens";
    private static final String ZSET_KEY = "blacklist:expiry";
    private static final String HASH_KEY_PREFIX = "blacklist_token:";

    private final BlacklistRedisRepository repository;
    private final BlacklistEntityMapper mapper;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * BlacklistPersistenceAdapter 생성자.
     * Spring의 의존성 주입을 통해 필요한 컴포넌트들을 주입받습니다.
     *
     * @param repository BlacklistRedisRepository (null 불가)
     * @param mapper BlacklistEntityMapper (null 불가)
     * @param redisTemplate RedisTemplate&lt;String, String&gt; (null 불가)
     * @throws NullPointerException 인자가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public BlacklistPersistenceAdapter(
            final BlacklistRedisRepository repository,
            final BlacklistEntityMapper mapper,
            final RedisTemplate<String, String> redisTemplate
    ) {
        this.repository = Objects.requireNonNull(repository, "BlacklistRedisRepository cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "BlacklistEntityMapper cannot be null");
        this.redisTemplate = Objects.requireNonNull(redisTemplate, "RedisTemplate cannot be null");
    }

    /**
     * 블랙리스트 토큰을 Redis에 추가합니다.
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>Domain → Entity 변환</li>
     *   <li>Redis Hash에 저장 (Repository)</li>
     *   <li>Redis SET에 JTI 추가 (SADD blacklist:tokens {jti})</li>
     *   <li>Redis ZSET에 만료 시간 추가 (ZADD blacklist:expiry {expiresAt} {jti})</li>
     * </ol>
     *
     * <p><strong>멱등성:</strong></p>
     * <ul>
     *   <li>이미 존재하는 JTI를 다시 추가해도 문제 없음</li>
     *   <li>SADD는 중복을 자동으로 처리</li>
     *   <li>ZADD는 스코어를 업데이트</li>
     * </ul>
     *
     * @param token 블랙리스트 토큰 Aggregate (null 불가)
     * @throws IllegalArgumentException token이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public void add(final BlacklistedToken token) {
        Objects.requireNonNull(token, "BlacklistedToken cannot be null");

        // 1. Domain → Entity 변환
        final BlacklistedTokenRedisEntity entity = this.mapper.toEntity(token);
        final String jti = entity.getJti();
        final double expiresAtScore = convertToScore(entity.getExpiresAt());

        try {
            // 2. Redis Hash에 저장 (TTL 자동 설정)
            this.repository.save(entity);

            // 3. Redis SET + ZSET을 MULTI/EXEC 트랜잭션으로 원자적 실행
            this.redisTemplate.execute(new org.springframework.data.redis.core.SessionCallback<java.util.List<Object>>() {
                @Override
                public java.util.List<Object> execute(org.springframework.data.redis.core.RedisOperations operations)
                        throws org.springframework.dao.DataAccessException {
                    operations.multi();
                    // Redis SET에 JTI 추가 (빠른 조회용)
                    operations.opsForSet().add(SET_KEY, jti);
                    // Redis ZSET에 만료 시간 추가 (배치 삭제용)
                    operations.opsForZSet().add(ZSET_KEY, jti, expiresAtScore);
                    return operations.exec();
                }
            });
        } catch (Exception e) {
            // 4. 예외 발생 시 Hash 롤백 시도 (Best Effort)
            try {
                this.repository.deleteById(jti);
            } catch (Exception rollbackException) {
                // 롤백 실패는 무시 (TTL로 자동 정리됨)
            }
            throw new IllegalStateException("Failed to add token to blacklist: " + jti, e);
        }
    }

    /**
     * JTI가 블랙리스트에 존재하는지 확인합니다.
     *
     * <p>Redis SET의 SISMEMBER 명령을 사용하여 O(1) 복잡도로 빠르게 확인합니다.</p>
     *
     * @param jti JWT ID (null 불가, 빈 문자열 불가)
     * @return 블랙리스트에 존재하면 true, 아니면 false
     * @throws IllegalArgumentException jti가 null이거나 빈 문자열인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean exists(final String jti) {
        validateJti(jti);

        final Boolean exists = this.redisTemplate.opsForSet().isMember(SET_KEY, jti);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * Redis ZSET에서 만료된 JTI 목록을 조회합니다.
     *
     * <p>Redis ZRANGEBYSCORE 명령을 사용하여 현재 시간보다 작은 스코어를 가진 JTI를 조회합니다.</p>
     *
     * <p><strong>성능 최적화:</strong></p>
     * <ul>
     *   <li>limit 파라미터로 배치 크기 제한</li>
     *   <li>O(log(N) + M) 복잡도 (N: 전체 크기, M: 결과 개수)</li>
     *   <li>대량 삭제 시 여러 번 호출하여 처리</li>
     * </ul>
     *
     * @param maxEpochSeconds 최대 Epoch 초 (현재 시간)
     * @param limit 조회할 최대 개수 (배치 크기)
     * @return 만료된 JTI 목록 (Set)
     * @throws IllegalArgumentException maxEpochSeconds가 음수이거나 limit이 0 이하인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public Set<String> findExpiredJtis(final long maxEpochSeconds, final int limit) {
        if (maxEpochSeconds < 0) {
            throw new IllegalArgumentException("maxEpochSeconds cannot be negative: " + maxEpochSeconds);
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive: " + limit);
        }

        final double maxScore = convertEpochSecondsToScore(maxEpochSeconds);

        // ZRANGEBYSCORE blacklist:expiry -inf {maxScore} LIMIT 0 {limit}
        final Set<String> expiredJtis = this.redisTemplate.opsForZSet()
                .rangeByScore(ZSET_KEY, Double.NEGATIVE_INFINITY, maxScore, 0, limit);

        return expiredJtis != null ? expiredJtis : Set.of();
    }

    /**
     * Redis Hash, SET, ZSET에서 JTI 목록을 제거합니다.
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>각 JTI에 대해 Redis Hash 삭제 (DEL blacklist_token:{jti})</li>
     *   <li>Redis SET에서 JTI 제거 (SREM blacklist:tokens {jti1} {jti2} ...)</li>
     *   <li>Redis ZSET에서 만료 정보 제거 (ZREM blacklist:expiry {jti1} {jti2} ...)</li>
     * </ol>
     *
     * <p><strong>성능 고려사항:</strong></p>
     * <ul>
     *   <li>배치 크기: 기본 1000개 (조정 가능)</li>
     *   <li>SET/ZSET 연산은 가변 인자로 한 번에 처리 (네트워크 왕복 최소화)</li>
     * </ul>
     *
     * @param jtis 제거할 JTI 목록
     * @return 실제로 제거된 개수
     * @throws IllegalArgumentException jtis가 null이거나 비어있는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int removeAll(final Set<String> jtis) {
        if (jtis == null || jtis.isEmpty()) {
            throw new IllegalArgumentException("JTI set cannot be null or empty");
        }

        // 1. Redis Hash 키 목록 생성 (blacklist_token:{jti} 형식)
        final java.util.Set<String> hashKeys = jtis.stream()
                .map(jti -> HASH_KEY_PREFIX + jti)
                .collect(java.util.stream.Collectors.toSet());

        final String[] jtiArray = jtis.toArray(new String[0]);

        try {
            // 2. Redis Hash 삭제 (단일 배치 명령 - 진짜 배치!)
            this.redisTemplate.delete(hashKeys);

            // 3. Redis SET + ZSET을 MULTI/EXEC 트랜잭션으로 원자적 실행
            final java.util.List<Object> results = this.redisTemplate.execute(
                    new org.springframework.data.redis.core.SessionCallback<java.util.List<Object>>() {
                        @Override
                        public java.util.List<Object> execute(org.springframework.data.redis.core.RedisOperations operations)
                                throws org.springframework.dao.DataAccessException {
                            operations.multi();
                            // Redis SET에서 JTI 제거 (배치) - 실제 제거된 개수 반환
                            operations.opsForSet().remove(SET_KEY, (Object[]) jtiArray);
                            // Redis ZSET에서 만료 정보 제거 (배치)
                            operations.opsForZSet().remove(ZSET_KEY, (Object[]) jtiArray);
                            return operations.exec();
                        }
                    });

            // 4. 실제 제거된 JTI 개수 반환 (SET에서 제거된 개수)
            if (results != null && !results.isEmpty() && results.get(0) instanceof Long) {
                return ((Long) results.get(0)).intValue();
            }
            return 0;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to remove tokens from blacklist", e);
        }
    }

    /**
     * JTI 유효성을 검증합니다.
     *
     * @param jti JWT ID
     * @throws IllegalArgumentException jti가 null이거나 빈 문자열인 경우
     */
    private void validateJti(final String jti) {
        if (jti == null || jti.trim().isEmpty()) {
            throw new IllegalArgumentException("jti cannot be null or empty");
        }
    }

    /**
     * Unix timestamp (milliseconds)를 ZSET 스코어로 변환합니다.
     *
     * <p>ZSET 스코어는 double 타입이며, milliseconds를 seconds로 변환합니다.</p>
     *
     * @param epochMillis Unix timestamp (milliseconds)
     * @return ZSET 스코어 (seconds as double)
     */
    private double convertToScore(final Long epochMillis) {
        if (epochMillis == null) {
            throw new IllegalArgumentException("epochMillis cannot be null");
        }
        return epochMillis / 1000.0;
    }

    /**
     * Epoch seconds를 ZSET 스코어로 변환합니다.
     *
     * @param epochSeconds Epoch seconds
     * @return ZSET 스코어 (seconds as double)
     */
    private double convertEpochSecondsToScore(final long epochSeconds) {
        return (double) epochSeconds;
    }
}
