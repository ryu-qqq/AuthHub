package com.ryuqq.authhub.application.auth.port.out;

import com.ryuqq.authhub.domain.auth.token.Token;

/**
 * SaveRefreshToken Port Interface.
 *
 * <p>Refresh Token을 Redis에 저장하는 Out Port입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴을 따르며, Application Layer가 Persistence Layer(Redis)에 의존하지 않도록 합니다.</p>
 *
 * <p><strong>구현 위치:</strong></p>
 * <ul>
 *   <li>Interface: {@code application/auth/port/out/} (Application Layer)</li>
 *   <li>Adapter: {@code adapter-out-persistence/auth/adapter/} (Persistence Layer - Redis)</li>
 * </ul>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>로그인 성공 시 Refresh Token을 Redis에 저장 (Token Family 관리)</li>
 *   <li>Refresh Token의 유효성 검증 및 재사용 방지</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Redis는 내부 시스템 (DB와 동일한 내부 I/O)</li>
 *   <li>✅ @Transactional 내부에서 호출 가능 (외부 API 아님)</li>
 *   <li>✅ S3, SQS, HTTP Client 같은 외부 API와 구분</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface SaveRefreshTokenPort {

    /**
     * Refresh Token을 Redis에 저장합니다.
     *
     * <p>Refresh Token의 TokenId를 Key로, Token 정보를 Value로 저장합니다.
     * TTL(Time To Live)은 Token의 만료 시각(ExpiresAt)을 기준으로 자동 설정됩니다.</p>
     *
     * <p><strong>트랜잭션 경계:</strong></p>
     * <ul>
     *   <li>이 메서드는 @Transactional 내부에서 호출 가능합니다.</li>
     *   <li>Redis는 내부 시스템으로, MySQL과 동일하게 취급합니다.</li>
     *   <li>외부 API (S3, HTTP, SQS)와 구분됩니다.</li>
     * </ul>
     *
     * @param token 저장할 Refresh Token (TokenType이 REFRESH여야 함) (null 불가)
     * @throws IllegalArgumentException token이 null이거나 TokenType이 REFRESH가 아닌 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    void save(Token token);
}
