package com.ryuqq.authhub.application.auth.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.auth.dto.command.TokenClaimsContext;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.auth.port.out.client.TokenProviderPort;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.role.port.in.GetUserRolesUseCase;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.auth.exception.InvalidRefreshTokenException;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
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
import org.mockito.ArgumentCaptor;
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
    @Mock private UserQueryPort userQueryPort;
    @Mock private TenantQueryPort tenantQueryPort;
    @Mock private OrganizationQueryPort organizationQueryPort;

    private TokenManager tokenManager;

    @BeforeEach
    void setUp() {
        tokenManager =
                new TokenManager(
                        tokenProviderPort,
                        refreshTokenPersistenceManager,
                        refreshTokenCacheManager,
                        refreshTokenReader,
                        getUserRolesUseCase,
                        userQueryPort,
                        tenantQueryPort,
                        organizationQueryPort);
    }

    @Nested
    @DisplayName("issueTokens 메서드")
    class IssueTokensTest {

        @Test
        @DisplayName("토큰을 성공적으로 발급하고 저장한다")
        void shouldIssueTokensAndSaveSuccessfully() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            UUID tenantId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            Set<String> roles = Set.of("ROLE_USER");
            Set<String> permissions = Set.of("READ_USER");

            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(userId)
                            .tenantId(tenantId)
                            .tenantName("테스트 테넌트")
                            .organizationId(organizationId)
                            .organizationName("테스트 조직")
                            .email("test@example.com")
                            .build();

            UserRolesResponse userRolesResponse =
                    new UserRolesResponse(userId.value(), roles, permissions);
            TokenResponse expectedToken =
                    new TokenResponse("access-token", "refresh-token", 3600L, 604800L, "Bearer");

            given(getUserRolesUseCase.execute(userId.value())).willReturn(userRolesResponse);
            given(tokenProviderPort.generateTokenPair(any(TokenClaimsContext.class)))
                    .willReturn(expectedToken);

            // when
            TokenResponse result = tokenManager.issueTokens(context);

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
            User user = UserFixture.create();
            UserId userId = user.getUserId();
            Tenant tenant = TenantFixture.create();
            Organization organization = OrganizationFixture.create();

            Set<String> roles = Set.of("ROLE_USER");
            Set<String> permissions = Set.of("READ_USER");
            UserRolesResponse userRolesResponse =
                    new UserRolesResponse(userId.value(), roles, permissions);
            TokenResponse newTokenResponse =
                    new TokenResponse(
                            "new-access-token", "new-refresh-token", 3600L, 604800L, "Bearer");

            given(refreshTokenReader.findUserIdByToken(oldRefreshToken))
                    .willReturn(Optional.of(userId));
            given(userQueryPort.findById(userId)).willReturn(Optional.of(user));
            given(tenantQueryPort.findById(user.getTenantId())).willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(user.getOrganizationId()))
                    .willReturn(Optional.of(organization));
            given(getUserRolesUseCase.execute(userId.value())).willReturn(userRolesResponse);
            given(tokenProviderPort.generateTokenPair(any(TokenClaimsContext.class)))
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

        @Test
        @DisplayName("사용자 정보를 찾을 수 없으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            String refreshToken = "valid-refresh-token";
            UserId userId = UserId.of(UUID.randomUUID());
            given(refreshTokenReader.findUserIdByToken(refreshToken))
                    .willReturn(Optional.of(userId));
            given(userQueryPort.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tokenManager.refreshTokens(refreshToken))
                    .isInstanceOf(InvalidRefreshTokenException.class);
        }

        @Test
        @DisplayName("테넌트 정보를 찾을 수 없으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantNotFound() {
            // given
            String refreshToken = "valid-refresh-token";
            User user = UserFixture.create();
            UserId userId = user.getUserId();

            given(refreshTokenReader.findUserIdByToken(refreshToken))
                    .willReturn(Optional.of(userId));
            given(userQueryPort.findById(userId)).willReturn(Optional.of(user));
            given(tenantQueryPort.findById(user.getTenantId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tokenManager.refreshTokens(refreshToken))
                    .isInstanceOf(InvalidRefreshTokenException.class);
        }

        @Test
        @DisplayName("조직 정보를 찾을 수 없으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenOrganizationNotFound() {
            // given
            String refreshToken = "valid-refresh-token";
            User user = UserFixture.create();
            UserId userId = user.getUserId();
            Tenant tenant = TenantFixture.create();

            given(refreshTokenReader.findUserIdByToken(refreshToken))
                    .willReturn(Optional.of(userId));
            given(userQueryPort.findById(userId)).willReturn(Optional.of(user));
            given(tenantQueryPort.findById(user.getTenantId())).willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(user.getOrganizationId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tokenManager.refreshTokens(refreshToken))
                    .isInstanceOf(InvalidRefreshTokenException.class);
        }
    }

    @Nested
    @DisplayName("TokenClaimsContext 생성 검증")
    class TokenClaimsContextCreationTest {

        @Test
        @DisplayName("issueTokens에서 roles와 permissions이 Context에 추가된다")
        void shouldEnrichContextWithRolesAndPermissions() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            Set<String> roles = Set.of("ROLE_ADMIN", "ROLE_USER");
            Set<String> permissions = Set.of("READ_USER", "WRITE_USER");

            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(userId)
                            .tenantId(UUID.randomUUID())
                            .tenantName("테스트 테넌트")
                            .organizationId(UUID.randomUUID())
                            .organizationName("테스트 조직")
                            .email("test@example.com")
                            .build();

            UserRolesResponse userRolesResponse =
                    new UserRolesResponse(userId.value(), roles, permissions);
            TokenResponse expectedToken =
                    new TokenResponse("access-token", "refresh-token", 3600L, 604800L, "Bearer");

            given(getUserRolesUseCase.execute(userId.value())).willReturn(userRolesResponse);
            given(tokenProviderPort.generateTokenPair(any(TokenClaimsContext.class)))
                    .willReturn(expectedToken);

            // when
            tokenManager.issueTokens(context);

            // then
            ArgumentCaptor<TokenClaimsContext> contextCaptor =
                    ArgumentCaptor.forClass(TokenClaimsContext.class);
            verify(tokenProviderPort).generateTokenPair(contextCaptor.capture());

            TokenClaimsContext enrichedContext = contextCaptor.getValue();
            assertThat(enrichedContext.roles()).isEqualTo(roles);
            assertThat(enrichedContext.permissions()).isEqualTo(permissions);
        }

        @Test
        @DisplayName("refreshTokens에서 TokenClaimsContext가 올바르게 생성된다")
        void shouldBuildCorrectContextOnRefresh() {
            // given
            String oldRefreshToken = "old-refresh-token";
            User user = UserFixture.create();
            UserId userId = user.getUserId();
            Tenant tenant = TenantFixture.create();
            Organization organization = OrganizationFixture.create();

            Set<String> roles = Set.of("ROLE_USER");
            Set<String> permissions = Set.of("READ_USER");
            UserRolesResponse userRolesResponse =
                    new UserRolesResponse(userId.value(), roles, permissions);
            TokenResponse newTokenResponse =
                    new TokenResponse(
                            "new-access-token", "new-refresh-token", 3600L, 604800L, "Bearer");

            given(refreshTokenReader.findUserIdByToken(oldRefreshToken))
                    .willReturn(Optional.of(userId));
            given(userQueryPort.findById(userId)).willReturn(Optional.of(user));
            given(tenantQueryPort.findById(user.getTenantId())).willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(user.getOrganizationId()))
                    .willReturn(Optional.of(organization));
            given(getUserRolesUseCase.execute(userId.value())).willReturn(userRolesResponse);
            given(tokenProviderPort.generateTokenPair(any(TokenClaimsContext.class)))
                    .willReturn(newTokenResponse);

            // when
            tokenManager.refreshTokens(oldRefreshToken);

            // then
            ArgumentCaptor<TokenClaimsContext> contextCaptor =
                    ArgumentCaptor.forClass(TokenClaimsContext.class);
            verify(tokenProviderPort).generateTokenPair(contextCaptor.capture());

            TokenClaimsContext capturedContext = contextCaptor.getValue();
            assertThat(capturedContext.userId()).isEqualTo(userId);
            assertThat(capturedContext.tenantId()).isEqualTo(tenant.tenantIdValue());
            assertThat(capturedContext.tenantName()).isEqualTo(tenant.nameValue());
            assertThat(capturedContext.organizationId())
                    .isEqualTo(organization.organizationIdValue());
            assertThat(capturedContext.organizationName()).isEqualTo(organization.nameValue());
            assertThat(capturedContext.email()).isEqualTo(user.getIdentifier());
        }
    }
}
