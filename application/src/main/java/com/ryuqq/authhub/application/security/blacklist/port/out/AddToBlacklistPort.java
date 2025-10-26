package com.ryuqq.authhub.application.security.blacklist.port.out;

import com.ryuqq.authhub.domain.security.blacklist.BlacklistedToken;

/**
 * 블랙리스트 추가 Port (Port Out).
 *
 * <p>Redis SET에 JWT 토큰을 추가하는 아웃바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Infrastructure Layer와의 경계 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Redis SET에 JTI 추가 (SADD 명령)</li>
 *   <li>Redis ZSET에 만료 시간 추가 (ZADD 명령)</li>
 *   <li>TTL 자동 설정 (토큰 만료 시간까지)</li>
 *   <li>멱등성 보장 (중복 등록 허용)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Infrastructure Layer 경계</li>
 *   <li>✅ Command/Query 분리 - 등록 전용 (Command)</li>
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
 * blacklist:expiry = ZSET {jti: expiration_timestamp}
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface AddToBlacklistPort {

    /**
     * Redis SET에 블랙리스트 토큰을 추가합니다.
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>Redis SET에 JTI 추가 (SADD blacklist:tokens {jti})</li>
     *   <li>Redis ZSET에 만료 시간 추가 (ZADD blacklist:expiry {expiresAt} {jti})</li>
     *   <li>TTL 설정 (토큰 만료 시간까지)</li>
     * </ol>
     *
     * <p><strong>멱등성:</strong></p>
     * <ul>
     *   <li>이미 존재하는 JTI를 다시 추가해도 문제 없음</li>
     *   <li>SADD는 중복을 자동으로 처리</li>
     * </ul>
     *
     * @param token 블랙리스트 토큰 Aggregate
     * @throws IllegalArgumentException token이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    void add(BlacklistedToken token);
}
