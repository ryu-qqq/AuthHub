package com.ryuqq.authhub.application.security.blacklist.port.out;

/**
 * 블랙리스트 확인 Port (Port Out).
 *
 * <p>Redis SET에서 JWT 토큰 존재 여부를 확인하는 아웃바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Infrastructure Layer와의 경계 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Redis SET에서 JTI 존재 확인 (SISMEMBER 명령)</li>
 *   <li>O(1) 복잡도로 빠른 조회</li>
 *   <li>블랙리스트 여부 boolean 반환</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Infrastructure Layer 경계</li>
 *   <li>✅ Command/Query 분리 - 조회 전용 (Query)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Transaction 경계 - Redis 연산은 트랜잭션 밖에서 처리</li>
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
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface CheckBlacklistPort {

    /**
     * Redis SET에서 JTI 존재 여부를 확인합니다.
     *
     * <p>Redis SISMEMBER 명령을 사용하여 O(1) 복잡도로 빠르게 확인합니다.</p>
     *
     * @param jti JWT ID (고유 식별자)
     * @return 블랙리스트에 존재하면 true, 아니면 false
     * @throws IllegalArgumentException jti가 null이거나 빈 문자열인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    boolean exists(String jti);
}
