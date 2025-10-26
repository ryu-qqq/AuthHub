package com.ryuqq.authhub.adapter.out.persistence.security.ratelimit.adapter;

import com.ryuqq.authhub.application.security.ratelimit.port.out.IncrementRateLimitPort;
import com.ryuqq.authhub.application.security.ratelimit.port.out.LoadRateLimitPort;
import com.ryuqq.authhub.application.security.ratelimit.port.out.ResetRateLimitPort;
import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Rate Limit Persistence Adapter.
 *
 * <p>Redis를 사용하여 Rate Limit 카운트를 관리하는 Adapter입니다.
 * {@link LoadRateLimitPort}, {@link IncrementRateLimitPort}, {@link ResetRateLimitPort}를 구현하여
 * Application Layer의 Port를 Persistence Layer의 Redis와 연결합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Rate Limit 카운트를 Redis에서 조회</li>
 *   <li>Rate Limit 카운트를 원자적으로 증가 (INCR)</li>
 *   <li>Rate Limit 카운트를 삭제(리셋)</li>
 *   <li>Redis Key 생성 (rate_limit:{type}:{identifier}:{endpoint})</li>
 * </ul>
 *
 * <p><strong>Redis Key 패턴:</strong></p>
 * <ul>
 *   <li>IP 기반: {@code rate_limit:ip:192.168.0.1:/api/auth/login}</li>
 *   <li>User 기반: {@code rate_limit:user:uuid-1234:/api/users}</li>
 *   <li>Endpoint 기반: {@code rate_limit:endpoint:global:/api/public}</li>
 * </ul>
 *
 * <p><strong>원자성 보장:</strong></p>
 * <ul>
 *   <li>Redis INCR 명령어 사용 - 단일 스레드 모델로 원자성 보장</li>
 *   <li>동시성 문제 없음 - 여러 스레드에서 동시 증가 가능</li>
 *   <li>첫 증가 시 TTL 자동 설정</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Law of Demeter 준수 - RedisTemplate 직접 사용, 체이닝 최소화</li>
 *   <li>✅ Transaction 경계 - Redis는 내부 시스템, @Transactional 내부 호출 가능</li>
 *   <li>✅ Null 안전성 - null 체크 철저히 수행</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class RateLimitPersistenceAdapter implements
        LoadRateLimitPort,
        IncrementRateLimitPort,
        ResetRateLimitPort {

    private static final String KEY_PREFIX = "rate_limit";
    private static final String KEY_SEPARATOR = ":";

    private final RedisTemplate<String, Integer> redisTemplate;

    /**
     * RateLimitPersistenceAdapter 생성자.
     * Spring의 의존성 주입을 통해 RedisTemplate을 주입받습니다.
     *
     * @param redisTemplate RedisTemplate<String, Integer> (null 불가)
     * @throws NullPointerException redisTemplate이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public RateLimitPersistenceAdapter(
            final RedisTemplate<String, Integer> redisTemplate
    ) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate, "RedisTemplate cannot be null");
    }

    /**
     * Redis에서 현재 Rate Limit 카운트를 조회합니다.
     *
     * <p>Redis GET 명령을 사용하여 카운트를 조회하며,
     * 키가 존재하지 않으면 0을 반환합니다.</p>
     *
     * @param identifier 제한 대상 식별자 (IP 주소, 사용자 ID 등)
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @return 현재 카운트 (키가 없으면 0)
     * @throws IllegalArgumentException identifier, endpoint가 null이거나 빈 문자열이거나, type이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int loadCurrentCount(
            final String identifier,
            final String endpoint,
            final RateLimitType type
    ) {
        validateParameters(identifier, endpoint, type);

        final String key = buildKey(type, identifier, endpoint);
        final Integer count = this.redisTemplate.opsForValue().get(key);

        return count != null ? count : 0;
    }

    /**
     * Redis에서 현재 카운트와 TTL을 함께 조회합니다.
     *
     * <p>성능 최적화를 위해 카운트와 TTL을 한 번에 조회합니다.</p>
     *
     * @param identifier 제한 대상 식별자
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @return CountWithTtl (카운트와 TTL)
     * @throws IllegalArgumentException identifier, endpoint가 null이거나 빈 문자열이거나, type이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public CountWithTtl loadCountWithTtl(
            final String identifier,
            final String endpoint,
            final RateLimitType type
    ) {
        validateParameters(identifier, endpoint, type);

        final String key = buildKey(type, identifier, endpoint);
        final Integer count = this.redisTemplate.opsForValue().get(key);
        final Long ttl = this.redisTemplate.getExpire(key, TimeUnit.SECONDS);

        return new CountWithTtl(
                count != null ? count : 0,
                ttl != null ? ttl : -1L
        );
    }

    /**
     * Redis 카운트를 원자적으로 증가시키고, 증가 후 값을 반환합니다.
     *
     * <p>Redis INCR 명령을 사용하여 카운트를 1 증가시키며,
     * 키가 처음 생성되는 경우 TTL을 자동으로 설정합니다.</p>
     *
     * <p><strong>동작 방식:</strong></p>
     * <ol>
     *   <li>Redis INCR 명령 실행 (원자적)</li>
     *   <li>키가 없었다면 → 0에서 1로 증가</li>
     *   <li>키가 있었다면 → 기존 값에서 1 증가</li>
     *   <li>첫 증가 시 TTL 설정 (EXPIRE 명령)</li>
     *   <li>증가 후 현재 값 반환</li>
     * </ol>
     *
     * @param identifier 제한 대상 식별자 (IP 주소, 사용자 ID 등)
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @param ttlSeconds TTL (Time To Live, 초 단위)
     * @return 증가 후 현재 카운트
     * @throws IllegalArgumentException identifier, endpoint가 null이거나 빈 문자열이거나,
     *                                  type이 null이거나, ttlSeconds가 0 이하인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public long incrementAndGet(
            final String identifier,
            final String endpoint,
            final RateLimitType type,
            final long ttlSeconds
    ) {
        validateParameters(identifier, endpoint, type);
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("ttlSeconds must be positive: " + ttlSeconds);
        }

        final String key = buildKey(type, identifier, endpoint);
        final Long newCount = this.redisTemplate.opsForValue().increment(key);

        if (newCount == null) {
            throw new IllegalStateException("Failed to increment count for key: " + key);
        }

        // 첫 증가 시 (count == 1) TTL 설정
        if (newCount == 1) {
            this.redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }

        return newCount;
    }

    /**
     * Redis 카운트를 지정된 값만큼 증가시킵니다 (배치 증가).
     *
     * <p>여러 요청을 한 번에 처리할 때 사용합니다.</p>
     *
     * @param identifier 제한 대상 식별자
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @param incrementBy 증가할 값
     * @param ttlSeconds TTL (초 단위)
     * @return 증가 후 현재 카운트
     * @throws IllegalArgumentException incrementBy가 0 이하인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public long incrementByAndGet(
            final String identifier,
            final String endpoint,
            final RateLimitType type,
            final long incrementBy,
            final long ttlSeconds
    ) {
        validateParameters(identifier, endpoint, type);
        if (incrementBy <= 0) {
            throw new IllegalArgumentException("incrementBy must be positive: " + incrementBy);
        }
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("ttlSeconds must be positive: " + ttlSeconds);
        }

        final String key = buildKey(type, identifier, endpoint);
        final Long newCount = this.redisTemplate.opsForValue().increment(key, incrementBy);

        if (newCount == null) {
            throw new IllegalStateException("Failed to increment count for key: " + key);
        }

        // 첫 증가 시 TTL 설정
        if (newCount.equals(incrementBy)) {
            this.redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }

        return newCount;
    }

    /**
     * Redis에서 Rate Limit 카운트를 삭제(리셋)합니다.
     *
     * <p>Redis DEL 명령을 사용하여 카운트 키를 완전히 삭제합니다.</p>
     *
     * @param identifier 제한 대상 식별자 (IP 주소, 사용자 ID 등)
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @return 삭제 성공 여부 (true: 삭제됨, false: 키가 없었음)
     * @throws IllegalArgumentException identifier, endpoint가 null이거나 빈 문자열이거나, type이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean reset(
            final String identifier,
            final String endpoint,
            final RateLimitType type
    ) {
        validateParameters(identifier, endpoint, type);

        final String key = buildKey(type, identifier, endpoint);
        final Boolean deleted = this.redisTemplate.delete(key);

        return Boolean.TRUE.equals(deleted);
    }

    /**
     * 특정 식별자의 모든 엔드포인트 Rate Limit을 리셋합니다.
     *
     * <p>특정 IP 또는 사용자의 모든 Rate Limit을 일괄 삭제할 때 사용합니다.</p>
     *
     * @param identifier 제한 대상 식별자
     * @param type Rate Limit 타입
     * @return 삭제된 키의 개수
     * @throws IllegalArgumentException identifier가 null이거나 빈 문자열이거나, type이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int resetAll(final String identifier, final RateLimitType type) {
        validateIdentifier(identifier);
        validateType(type);

        final String pattern = buildKeyPattern(type, identifier, "*");
        final Set<String> keys = this.redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            return 0;
        }

        final Long deletedCount = this.redisTemplate.delete(keys);
        return deletedCount != null ? deletedCount.intValue() : 0;
    }

    /**
     * 특정 엔드포인트의 모든 Rate Limit을 리셋합니다.
     *
     * <p>특정 API 엔드포인트의 모든 IP/사용자 제한을 일괄 삭제할 때 사용합니다.</p>
     *
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @return 삭제된 키의 개수
     * @throws IllegalArgumentException endpoint가 null이거나 빈 문자열이거나, type이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int resetByEndpoint(final String endpoint, final RateLimitType type) {
        validateEndpoint(endpoint);
        validateType(type);

        final String pattern = buildKeyPattern(type, "*", endpoint);
        final Set<String> keys = this.redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            return 0;
        }

        final Long deletedCount = this.redisTemplate.delete(keys);
        return deletedCount != null ? deletedCount.intValue() : 0;
    }

    /**
     * Redis Key를 생성합니다.
     *
     * <p>Key 패턴: {@code rate_limit:{type}:{identifier}:{endpoint}}</p>
     *
     * @param type Rate Limit 타입
     * @param identifier 제한 대상 식별자
     * @param endpoint API 엔드포인트
     * @return Redis Key
     */
    private String buildKey(
            final RateLimitType type,
            final String identifier,
            final String endpoint
    ) {
        final String typeKey = getTypeKey(type);
        return KEY_PREFIX + KEY_SEPARATOR + typeKey + KEY_SEPARATOR + identifier + KEY_SEPARATOR + endpoint;
    }

    /**
     * Redis Key 패턴을 생성합니다 (와일드카드 지원).
     *
     * <p>Key 패턴: {@code rate_limit:{type}:{identifier}:{endpoint}}</p>
     *
     * @param type Rate Limit 타입
     * @param identifier 제한 대상 식별자 (와일드카드 "*" 가능)
     * @param endpoint API 엔드포인트 (와일드카드 "*" 가능)
     * @return Redis Key 패턴
     */
    private String buildKeyPattern(
            final RateLimitType type,
            final String identifier,
            final String endpoint
    ) {
        final String typeKey = getTypeKey(type);
        return KEY_PREFIX + KEY_SEPARATOR + typeKey + KEY_SEPARATOR + identifier + KEY_SEPARATOR + endpoint;
    }

    /**
     * RateLimitType을 Redis Key 부분 문자열로 변환합니다.
     *
     * @param type Rate Limit 타입
     * @return Key 부분 문자열 ("ip", "user", "endpoint")
     */
    private String getTypeKey(final RateLimitType type) {
        if (type.isIpBased()) {
            return "ip";
        } else if (type.isUserBased()) {
            return "user";
        } else if (type.isEndpointBased()) {
            return "endpoint";
        } else {
            throw new IllegalArgumentException("Unknown RateLimitType: " + type);
        }
    }

    /**
     * 모든 필수 파라미터를 검증합니다.
     *
     * @param identifier 제한 대상 식별자
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @throws IllegalArgumentException 파라미터가 유효하지 않은 경우
     */
    private void validateParameters(
            final String identifier,
            final String endpoint,
            final RateLimitType type
    ) {
        validateIdentifier(identifier);
        validateEndpoint(endpoint);
        validateType(type);
    }

    /**
     * identifier를 검증합니다.
     *
     * @param identifier 제한 대상 식별자
     * @throws IllegalArgumentException identifier가 null이거나 빈 문자열인 경우
     */
    private void validateIdentifier(final String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("identifier cannot be null or empty");
        }
    }

    /**
     * endpoint를 검증합니다.
     *
     * @param endpoint API 엔드포인트
     * @throws IllegalArgumentException endpoint가 null이거나 빈 문자열인 경우
     */
    private void validateEndpoint(final String endpoint) {
        if (endpoint == null || endpoint.trim().isEmpty()) {
            throw new IllegalArgumentException("endpoint cannot be null or empty");
        }
    }

    /**
     * type을 검증합니다.
     *
     * @param type Rate Limit 타입
     * @throws IllegalArgumentException type이 null인 경우
     */
    private void validateType(final RateLimitType type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
    }
}
