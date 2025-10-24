package com.ryuqq.authhub.application.auth.port.out;

import com.ryuqq.authhub.domain.auth.token.Token;
import com.ryuqq.authhub.domain.auth.token.TokenId;

import java.util.Optional;

/**
 * LoadRefreshToken Port Interface.
 *
 * <p>Redis에 저장된 Refresh Token을 조회하는 Out Port입니다.
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
 *   <li>Refresh Token을 사용한 Access Token 재발급 시 Token 존재 여부 확인</li>
 *   <li>Refresh Token Rotation 전략 구현 (기존 토큰 검증 후 새 토큰 발급)</li>
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
public interface LoadRefreshTokenPort {

    /**
     * TokenId를 사용하여 Redis에서 Refresh Token을 조회합니다.
     *
     * <p>Refresh Token이 Redis에 존재하면 Token Domain Aggregate를 반환하고,
     * 존재하지 않으면 {@code Optional.empty()}를 반환합니다.</p>
     *
     * <p><strong>트랜잭션 경계:</strong></p>
     * <ul>
     *   <li>이 메서드는 @Transactional 내부에서 호출 가능합니다.</li>
     *   <li>Redis는 내부 시스템으로, MySQL과 동일하게 취급합니다.</li>
     *   <li>외부 API (S3, HTTP, SQS)와 구분됩니다.</li>
     * </ul>
     *
     * <p><strong>반환값:</strong></p>
     * <ul>
     *   <li>Token이 존재하면: {@code Optional<Token>} (TokenType이 REFRESH)</li>
     *   <li>Token이 존재하지 않으면: {@code Optional.empty()}</li>
     * </ul>
     *
     * @param tokenId Refresh Token의 고유 식별자 (null 불가)
     * @return Refresh Token이 존재하면 {@code Optional<Token>}, 존재하지 않으면 {@code Optional.empty()}
     * @throws IllegalArgumentException tokenId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Optional<Token> load(TokenId tokenId);
}
