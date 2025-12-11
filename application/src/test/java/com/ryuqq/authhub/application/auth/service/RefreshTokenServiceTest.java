package com.ryuqq.authhub.application.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.auth.dto.command.RefreshTokenCommand;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.auth.manager.TokenManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RefreshTokenService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService 단위 테스트")
class RefreshTokenServiceTest {

    @Mock private TokenManager tokenManager;

    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenService(tokenManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("토큰을 성공적으로 갱신한다")
        void shouldRefreshTokenSuccessfully() {
            // given
            String refreshToken = "valid-refresh-token";
            RefreshTokenCommand command = new RefreshTokenCommand(refreshToken);
            TokenResponse expectedResponse =
                    new TokenResponse(
                            "new-access-token", "new-refresh-token", 3600L, 86400L, "Bearer");

            given(tokenManager.refreshTokens(refreshToken)).willReturn(expectedResponse);

            // when
            TokenResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.accessToken()).isEqualTo("new-access-token");
            assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
            verify(tokenManager).refreshTokens(refreshToken);
        }

        @Test
        @DisplayName("다른 리프레시 토큰으로도 갱신한다")
        void shouldRefreshWithDifferentToken() {
            // given
            String refreshToken = "another-refresh-token";
            RefreshTokenCommand command = new RefreshTokenCommand(refreshToken);
            TokenResponse expectedResponse =
                    new TokenResponse("access-123", "refresh-456", 7200L, 172800L, "Bearer");

            given(tokenManager.refreshTokens(refreshToken)).willReturn(expectedResponse);

            // when
            TokenResponse response = service.execute(command);

            // then
            assertThat(response.accessTokenExpiresIn()).isEqualTo(7200L);
            assertThat(response.refreshTokenExpiresIn()).isEqualTo(172800L);
            verify(tokenManager).refreshTokens(refreshToken);
        }
    }
}
