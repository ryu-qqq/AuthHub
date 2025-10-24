package com.ryuqq.authhub.application.auth.assembler;

import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import com.ryuqq.authhub.domain.auth.token.JwtToken;
import com.ryuqq.authhub.domain.auth.token.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/**
 * TokenAssembler 단위 테스트.
 *
 * <p>TokenAssembler의 Domain → Response 변환 로직을 검증하는 단위 테스트입니다.
 * 외부 의존성 없이 순수한 변환 로직만 테스트합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>✅ Spring Context 로딩 없음</li>
 *   <li>✅ Mock Token 객체 사용 (시간 의존성 제거)</li>
 *   <li>✅ Law of Demeter 준수 검증</li>
 *   <li>✅ 예외 케이스 검증</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("TokenAssembler 단위 테스트")
class TokenAssemblerTest {

    private TokenAssembler tokenAssembler;

    @BeforeEach
    void setUp() {
        tokenAssembler = new TokenAssembler();
    }

    @Test
    @DisplayName("toLoginResponse 성공 - Access Token과 Refresh Token을 Response로 변환")
    void toLoginResponse_Success() {
        // Given
        // Mock Token 객체 생성 (실제 시간 의존성 제거)
        Token accessToken = org.mockito.Mockito.mock(Token.class);
        Token refreshToken = org.mockito.Mockito.mock(Token.class);

        given(accessToken.getJwtToken()).willReturn(JwtToken.from("test-access-token-value"));
        given(accessToken.isAccessToken()).willReturn(true);
        given(accessToken.isRefreshToken()).willReturn(false);
        given(accessToken.remainingValidity()).willReturn(Duration.ofMinutes(15));

        given(refreshToken.getJwtToken()).willReturn(JwtToken.from("test-refresh-token-value"));
        given(refreshToken.isAccessToken()).willReturn(false);
        given(refreshToken.isRefreshToken()).willReturn(true);

        // When
        LoginUseCase.Response response = tokenAssembler.toLoginResponse(accessToken, refreshToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("test-access-token-value");
        assertThat(response.refreshToken()).isEqualTo("test-refresh-token-value");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900);  // 15분 = 900초
    }

    @Test
    @DisplayName("toLoginResponse 실패 - accessToken이 null")
    void toLoginResponse_Failure_AccessTokenNull() {
        // Given
        Token refreshToken = org.mockito.Mockito.mock(Token.class);
        given(refreshToken.isRefreshToken()).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> tokenAssembler.toLoginResponse(null, refreshToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("accessToken cannot be null");
    }

    @Test
    @DisplayName("toLoginResponse 실패 - refreshToken이 null")
    void toLoginResponse_Failure_RefreshTokenNull() {
        // Given
        Token accessToken = org.mockito.Mockito.mock(Token.class);
        given(accessToken.isAccessToken()).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> tokenAssembler.toLoginResponse(accessToken, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("refreshToken cannot be null");
    }

    @Test
    @DisplayName("toLoginResponse 실패 - accessToken이 ACCESS 타입이 아님")
    void toLoginResponse_Failure_AccessTokenNotAccessType() {
        // Given
        // ❌ REFRESH 타입을 accessToken으로 전달
        Token wrongAccessToken = org.mockito.Mockito.mock(Token.class);
        given(wrongAccessToken.isAccessToken()).willReturn(false);
        given(wrongAccessToken.isRefreshToken()).willReturn(true);

        Token refreshToken = org.mockito.Mockito.mock(Token.class);
        given(refreshToken.isRefreshToken()).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> tokenAssembler.toLoginResponse(wrongAccessToken, refreshToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("accessToken must be ACCESS type");
    }

    @Test
    @DisplayName("toLoginResponse 실패 - refreshToken이 REFRESH 타입이 아님")
    void toLoginResponse_Failure_RefreshTokenNotRefreshType() {
        // Given
        Token accessToken = org.mockito.Mockito.mock(Token.class);
        given(accessToken.isAccessToken()).willReturn(true);

        // ❌ ACCESS 타입을 refreshToken으로 전달
        Token wrongRefreshToken = org.mockito.Mockito.mock(Token.class);
        given(wrongRefreshToken.isAccessToken()).willReturn(true);
        given(wrongRefreshToken.isRefreshToken()).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> tokenAssembler.toLoginResponse(accessToken, wrongRefreshToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("refreshToken must be REFRESH type");
    }

    @Test
    @DisplayName("toLoginResponse - expiresIn이 올바르게 계산됨 (초 단위)")
    void toLoginResponse_ExpiresInCalculatedCorrectly() {
        // Given
        // 30분 유효 기간
        Token accessToken = org.mockito.Mockito.mock(Token.class);
        Token refreshToken = org.mockito.Mockito.mock(Token.class);

        given(accessToken.getJwtToken()).willReturn(JwtToken.from("test-access-token-30min"));
        given(accessToken.isAccessToken()).willReturn(true);
        given(accessToken.isRefreshToken()).willReturn(false);
        given(accessToken.remainingValidity()).willReturn(Duration.ofMinutes(30));

        given(refreshToken.getJwtToken()).willReturn(JwtToken.from("test-refresh-token-30min"));
        given(refreshToken.isAccessToken()).willReturn(false);
        given(refreshToken.isRefreshToken()).willReturn(true);

        // When
        LoginUseCase.Response response = tokenAssembler.toLoginResponse(accessToken, refreshToken);

        // Then
        assertThat(response.expiresIn()).isEqualTo(1800);  // 30분 = 1800초
    }
}
