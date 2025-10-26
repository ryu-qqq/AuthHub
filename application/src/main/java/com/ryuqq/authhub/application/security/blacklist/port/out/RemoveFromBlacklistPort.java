package com.ryuqq.authhub.application.security.blacklist.port.out;

import java.util.Set;

/**
 * 블랙리스트 제거 Port (Port Out).
 *
 * <p>Redis SET과 ZSET에서 만료된 JWT 토큰을 제거하는 아웃바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Infrastructure Layer와의 경계 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Redis ZSET에서 만료된 JTI 조회 (ZRANGEBYSCORE 명령)</li>
 *   <li>Redis SET에서 JTI 제거 (SREM 명령)</li>
 *   <li>Redis ZSET에서 만료 정보 제거 (ZREM 명령)</li>
 *   <li>배치 삭제 지원 (1000개씩)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Infrastructure Layer 경계</li>
 *   <li>✅ Command/Query 분리 - 삭제 전용 (Command)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Transaction 경계 - Redis 연산은 트랜잭션 밖에서 처리</li>
 *   <li>✅ Batch 처리 - 대량 삭제 시 성능 최적화</li>
 * </ul>
 *
 * <p><strong>구현 계층 (Adapter-Out-Persistence):</strong></p>
 * <ul>
 *   <li>BlacklistPersistenceAdapter (Port 구현체)</li>
 *   <li>BlacklistRedisRepository (Spring Data Redis)</li>
 *   <li>RedisTemplate 사용</li>
 * </ul>
 *
 * <p><strong>Redis 구조:</strong></p>
 * <pre>
 * blacklist:tokens = SET of JTIs
 * blacklist:expiry = ZSET {jti: expiration_timestamp}
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface RemoveFromBlacklistPort {

    /**
     * Redis ZSET에서 만료된 JTI 목록을 조회합니다.
     *
     * <p>Redis ZRANGEBYSCORE 명령을 사용하여 현재 시간보다 작은 스코어를 가진 JTI를 조회합니다.</p>
     *
     * @param maxEpochSeconds 최대 Epoch 초 (현재 시간)
     * @param limit 조회할 최대 개수 (배치 크기)
     * @return 만료된 JTI 목록 (Set)
     * @throws IllegalArgumentException maxEpochSeconds가 음수이거나 limit이 0 이하인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Set<String> findExpiredJtis(long maxEpochSeconds, int limit);

    /**
     * Redis SET과 ZSET에서 JTI 목록을 제거합니다.
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>Redis SET에서 JTI 제거 (SREM blacklist:tokens {jti1} {jti2} ...)</li>
     *   <li>Redis ZSET에서 만료 정보 제거 (ZREM blacklist:expiry {jti1} {jti2} ...)</li>
     * </ol>
     *
     * <p><strong>성능 고려사항:</strong></p>
     * <ul>
     *   <li>Redis Pipeline 사용 권장 (네트워크 왕복 최소화)</li>
     *   <li>배치 크기: 1000개 (조정 가능)</li>
     * </ul>
     *
     * @param jtis 제거할 JTI 목록
     * @return 실제로 제거된 개수
     * @throws IllegalArgumentException jtis가 null이거나 비어있는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    int removeAll(Set<String> jtis);
}
