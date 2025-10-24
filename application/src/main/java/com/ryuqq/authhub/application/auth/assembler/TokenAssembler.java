package com.ryuqq.authhub.application.auth.assembler;

import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import com.ryuqq.authhub.domain.auth.token.Token;
import org.springframework.stereotype.Component;

/**
 * Token Assembler - Token Domain ↔ LoginUseCase.Response 변환.
 *
 * <p>Application Layer의 Assembler 패턴을 따르며, Domain Aggregate(Token)를
 * UseCase Response로 변환하는 책임을 가집니다.</p>
 *
 * <p><strong>책임 범위:</strong></p>
 * <ul>
 *   <li>Domain → Response 변환 (Token → LoginUseCase.Response)</li>
 *   <li>Value Object 추출 및 Primitive 타입 변환</li>
 *   <li>Law of Demeter 준수 - Token의 행위 메서드 활용</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ 비즈니스 로직 금지 - 순수 변환 로직만</li>
 *   <li>✅ Port 호출 금지 - 외부 의존성 없음</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class TokenAssembler {

    /**
     * Token Aggregate를 LoginUseCase.Response로 변환합니다.
     *
     * <p>Access Token과 Refresh Token의 정보를 추출하여
     * LoginUseCase.Response Record로 변환합니다.</p>
     *
     * <p><strong>Law of Demeter 준수:</strong></p>
     * <ul>
     *   <li>❌ {@code accessToken.getJwtToken().getValue()} (Getter chaining)</li>
     *   <li>✅ {@code accessToken.getJwtToken()} → Value Object 활용</li>
     * </ul>
     *
     * @param accessToken Access Token Domain Aggregate (null 불가)
     * @param refreshToken Refresh Token Domain Aggregate (null 불가)
     * @return LoginUseCase.Response (accessToken, refreshToken, tokenType, expiresIn)
     * @throws IllegalArgumentException accessToken 또는 refreshToken이 null인 경우
     * @throws IllegalArgumentException accessToken이 ACCESS 타입이 아니거나 refreshToken이 REFRESH 타입이 아닌 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public LoginUseCase.Response toLoginResponse(final Token accessToken, final Token refreshToken) {
        if (accessToken == null) {
            throw new IllegalArgumentException("accessToken cannot be null");
        }

        if (refreshToken == null) {
            throw new IllegalArgumentException("refreshToken cannot be null");
        }

        // 토큰 타입 검증 (Law of Demeter 준수 - Token의 행위 메서드 활용)
        if (!accessToken.isAccessToken()) {
            throw new IllegalArgumentException("accessToken must be ACCESS type");
        }

        if (!refreshToken.isRefreshToken()) {
            throw new IllegalArgumentException("refreshToken must be REFRESH type");
        }

        // ✅ Value Object에서 Primitive 타입 추출
        // Law of Demeter 준수 - JwtToken은 Value Object이므로 value() 메서드 호출 허용
        final String accessTokenValue = accessToken.getJwtToken().value();
        final String refreshTokenValue = refreshToken.getJwtToken().value();

        // ✅ Token의 행위 메서드 활용 - remainingValidity()
        // Law of Demeter 준수 - Token이 직접 제공하는 메서드 사용
        final int expiresInSeconds = (int) accessToken.remainingValidity().toSeconds();

        return new LoginUseCase.Response(
                accessTokenValue,
                refreshTokenValue,
                "Bearer",  // RFC 6750 OAuth 2.0 Bearer Token 표준
                expiresInSeconds
        );
    }
}
