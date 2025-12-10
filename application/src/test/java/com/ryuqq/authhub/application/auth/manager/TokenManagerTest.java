package com.ryuqq.authhub.application.auth.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.auth.port.out.client.TokenProviderPort;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.role.port.in.GetUserRolesUseCase;
import com.ryuqq.authhub.domain.auth.exception.InvalidRefreshTokenException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TokenManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TokenManager 단위 테스트")
class TokenManagerTest {

    @Mock private TokenProviderPort tokenProviderPort;
    @Mock private RefreshTokenPersistenceManager refreshTokenPersistenceManager;
    @Mock private RefreshTokenCacheManager refreshTokenCacheManager;
    @Mock private RefreshTokenReader refreshTokenReader;
    @Mock private GetUserRolesUseCase getUserRolesUseCase;

    private TokenManager tokenManager;

    @BeforeEach
    void setUp() {
        tokenManager =
                new TokenManager(
                        tokenProviderPort,
                        refreshTokenPersistenceManager,
                        refreshTokenCacheManager,
                        refreshTokenReader,
                        getUserRolesUseCase);
    }

    @Nested
    @DisplayName("issueTokens 메서드")
    class IssueTokensTest {

        @Test
        @DisplayName("토큰을 성공적으로 발급하고 저장한다")
        void shouldIssueTokensAndSaveSuccessfully() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            Set<String> roles = Set.of("ROLE_USER");
            Set<String> permissions = Set.of("READ_USER");
            UserRolesResponse userRolesResponse =
                    new UserRolesResponse(userId.value(), roles, permissions);
            TokenResponse expectedToken =
                    new TokenResponse("access-token", "refresh-token", 3600L, 604800L, "Bearer");

            given(getUserRolesUseCase.execute(userId.value())).willReturn(userRolesResponse);
            given(tokenProviderPort.generateTokenPair(eq(userId), anySet(), anySet()))
                    .willReturn(expectedToken);

            // when
            TokenResponse result = tokenManager.issueTokens(userId);

            // then
            assertThat(result).isEqualTo(expectedToken);
            verify(refreshTokenPersistenceManager).persist(userId, "refresh-token");
            verify(refreshTokenCacheManager).save(userId, "refresh-token", 604800L);
        }
    }

    @Nested
    @DisplayName("revokeTokensByUserId 메서드")
    class RevokeTokensByUserIdTest {

        @Test
        @DisplayName("사용자 ID로 모든 토큰을 무효화한다")
        void shouldRevokeAllTokensByUserId() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());

            // when
            tokenManager.revokeTokensByUserId(userId);

            // then
            verify(refreshTokenCacheManager).deleteByUserId(userId);
            verify(refreshTokenPersistenceManager).deleteByUserId(userId);
        }
    }

    @Nested
    @DisplayName("revokeToken 메서드")
    class RevokeTokenTest {

        @Test
        @DisplayName("특정 토큰을 무효화한다")
        void shouldRevokeSpecificToken() {
            // given
            String refreshToken = "refresh-token-to-revoke";

            // when
            tokenManager.revokeToken(refreshToken);

            // then
            verify(refreshTokenCacheManager).deleteByToken(refreshToken);
        }
    }

    @Nested
    @DisplayName("refreshTokens 메서드")
    class RefreshTokensTest {

        @Test
        @DisplayName("유효한 Refresh Token으로 새 토큰 쌍을 발급한다")
        void shouldRefreshTokensWithValidToken() {
            // given
            String oldRefreshToken = "old-refresh-token";
            UserId userId = UserId.of(UUID.randomUUID());
            Set<String> roles = Set.of("ROLE_USER");
            Set<String> permissions = Set.of("READ_USER");
            UserRolesResponse userRolesResponse =
                    new UserRolesResponse(userId.value(), roles, permissions);
            TokenResponse newTokenResponse =
                    new TokenResponse(
                            "new-access-token", "new-refresh-token", 3600L, 604800L, "Bearer");

            given(refreshTokenReader.findUserIdByToken(oldRefreshToken))
                    .willReturn(Optional.of(userId));
            given(getUserRolesUseCase.execute(userId.value())).willReturn(userRolesResponse);
            given(tokenProviderPort.generateTokenPair(eq(userId), anySet(), anySet()))
                    .willReturn(newTokenResponse);

            // when
            TokenResponse result = tokenManager.refreshTokens(oldRefreshToken);

            // then
            assertThat(result).isEqualTo(newTokenResponse);
            verify(refreshTokenCacheManager).deleteByToken(oldRefreshToken);
        }

        @Test
        @DisplayName("유효하지 않은 Refresh Token으로 예외를 발생시킨다")
        void shouldThrowExceptionForInvalidRefreshToken() {
            // given
            String invalidRefreshToken = "invalid-refresh-token";
            given(refreshTokenReader.findUserIdByToken(invalidRefreshToken))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tokenManager.refreshTokens(invalidRefreshToken))
                    .isInstanceOf(InvalidRefreshTokenException.class);
        }
    }
}
