package com.ryuqq.authhub.application.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.auth.assembler.LoginResponseAssembler;
import com.ryuqq.authhub.application.auth.component.LoginValidator;
import com.ryuqq.authhub.application.auth.dto.command.LoginCommand;
import com.ryuqq.authhub.application.auth.dto.command.TokenClaimsContext;
import com.ryuqq.authhub.application.auth.dto.response.LoginResponse;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.auth.manager.TokenManager;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.auth.exception.InvalidCredentialsException;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import java.util.Optional;
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
 * LoginService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 단위 테스트")
class LoginServiceTest {

    @Mock private UserQueryPort userQueryPort;

    @Mock private TenantQueryPort tenantQueryPort;

    @Mock private OrganizationQueryPort organizationQueryPort;

    @Mock private LoginValidator loginValidator;

    @Mock private TokenManager tokenManager;

    @Mock private LoginResponseAssembler loginResponseAssembler;

    private LoginService service;

    @BeforeEach
    void setUp() {
        service =
                new LoginService(
                        userQueryPort,
                        tenantQueryPort,
                        organizationQueryPort,
                        loginValidator,
                        tokenManager,
                        loginResponseAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("로그인을 성공적으로 수행한다")
        void shouldLoginSuccessfully() {
            // given
            User user = UserFixture.create();
            Tenant tenant = TenantFixture.create();
            Organization organization = OrganizationFixture.create();

            UUID tenantId = user.tenantIdValue();
            UUID userId = user.userIdValue();
            String identifier = "testuser";
            String password = "password123";

            LoginCommand command = new LoginCommand(tenantId, identifier, password);
            TokenResponse tokenResponse =
                    new TokenResponse("access-token", "refresh-token", 3600L, 86400L, "Bearer");
            LoginResponse expectedResponse =
                    new LoginResponse(userId, "access-token", "refresh-token", 3600L, "Bearer");

            given(userQueryPort.findByTenantIdAndIdentifier(TenantId.of(tenantId), identifier))
                    .willReturn(Optional.of(user));
            given(tenantQueryPort.findById(user.getTenantId())).willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(user.getOrganizationId()))
                    .willReturn(Optional.of(organization));
            given(tokenManager.issueTokens(any(TokenClaimsContext.class)))
                    .willReturn(tokenResponse);
            given(loginResponseAssembler.toLoginResponse(user, tokenResponse))
                    .willReturn(expectedResponse);

            // when
            LoginResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.accessToken()).isEqualTo("access-token");
            verify(loginValidator).validate(eq(password), eq(user), eq(tenantId), eq(identifier));
            verify(tokenManager).issueTokens(any(TokenClaimsContext.class));
        }

        @Test
        @DisplayName("사용자를 찾을 수 없으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            UUID tenantId = UUID.randomUUID();
            String identifier = "nonexistent";
            String password = "password123";
            LoginCommand command = new LoginCommand(tenantId, identifier, password);

            given(userQueryPort.findByTenantIdAndIdentifier(TenantId.of(tenantId), identifier))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidCredentialsException.class);

            verify(loginValidator, never()).validate(any(), any(), any(), any());
            verify(tokenManager, never()).issueTokens(any());
        }

        @Test
        @DisplayName("테넌트를 찾을 수 없으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantNotFound() {
            // given
            User user = UserFixture.create();
            UUID tenantId = user.tenantIdValue();
            String identifier = "testuser";
            String password = "password123";
            LoginCommand command = new LoginCommand(tenantId, identifier, password);

            given(userQueryPort.findByTenantIdAndIdentifier(TenantId.of(tenantId), identifier))
                    .willReturn(Optional.of(user));
            given(tenantQueryPort.findById(user.getTenantId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidCredentialsException.class);

            verify(tokenManager, never()).issueTokens(any());
        }

        @Test
        @DisplayName("조직을 찾을 수 없으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenOrganizationNotFound() {
            // given
            User user = UserFixture.create();
            Tenant tenant = TenantFixture.create();
            UUID tenantId = user.tenantIdValue();
            String identifier = "testuser";
            String password = "password123";
            LoginCommand command = new LoginCommand(tenantId, identifier, password);

            given(userQueryPort.findByTenantIdAndIdentifier(TenantId.of(tenantId), identifier))
                    .willReturn(Optional.of(user));
            given(tenantQueryPort.findById(user.getTenantId())).willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(user.getOrganizationId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidCredentialsException.class);

            verify(tokenManager, never()).issueTokens(any());
        }
    }

    @Nested
    @DisplayName("TokenClaimsContext 생성 검증")
    class TokenClaimsContextCreationTest {

        @Test
        @DisplayName("TokenClaimsContext에 tenant_name이 포함된다")
        void shouldIncludeTenantNameInContext() {
            // given
            User user = UserFixture.create();
            Tenant tenant = TenantFixture.create();
            Organization organization = OrganizationFixture.create();

            UUID tenantId = user.tenantIdValue();
            String identifier = "testuser";
            String password = "password123";

            LoginCommand command = new LoginCommand(tenantId, identifier, password);
            TokenResponse tokenResponse =
                    new TokenResponse("access-token", "refresh-token", 3600L, 86400L, "Bearer");
            LoginResponse expectedResponse =
                    new LoginResponse(
                            user.userIdValue(), "access-token", "refresh-token", 3600L, "Bearer");

            given(userQueryPort.findByTenantIdAndIdentifier(TenantId.of(tenantId), identifier))
                    .willReturn(Optional.of(user));
            given(tenantQueryPort.findById(user.getTenantId())).willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(user.getOrganizationId()))
                    .willReturn(Optional.of(organization));
            given(tokenManager.issueTokens(any(TokenClaimsContext.class)))
                    .willReturn(tokenResponse);
            given(loginResponseAssembler.toLoginResponse(user, tokenResponse))
                    .willReturn(expectedResponse);

            // when
            service.execute(command);

            // then
            ArgumentCaptor<TokenClaimsContext> contextCaptor =
                    ArgumentCaptor.forClass(TokenClaimsContext.class);
            verify(tokenManager).issueTokens(contextCaptor.capture());

            TokenClaimsContext capturedContext = contextCaptor.getValue();
            assertThat(capturedContext.tenantName()).isEqualTo(tenant.nameValue());
        }

        @Test
        @DisplayName("TokenClaimsContext에 organization_name이 포함된다")
        void shouldIncludeOrganizationNameInContext() {
            // given
            User user = UserFixture.create();
            Tenant tenant = TenantFixture.create();
            Organization organization = OrganizationFixture.create();

            UUID tenantId = user.tenantIdValue();
            String identifier = "testuser";
            String password = "password123";

            LoginCommand command = new LoginCommand(tenantId, identifier, password);
            TokenResponse tokenResponse =
                    new TokenResponse("access-token", "refresh-token", 3600L, 86400L, "Bearer");
            LoginResponse expectedResponse =
                    new LoginResponse(
                            user.userIdValue(), "access-token", "refresh-token", 3600L, "Bearer");

            given(userQueryPort.findByTenantIdAndIdentifier(TenantId.of(tenantId), identifier))
                    .willReturn(Optional.of(user));
            given(tenantQueryPort.findById(user.getTenantId())).willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(user.getOrganizationId()))
                    .willReturn(Optional.of(organization));
            given(tokenManager.issueTokens(any(TokenClaimsContext.class)))
                    .willReturn(tokenResponse);
            given(loginResponseAssembler.toLoginResponse(user, tokenResponse))
                    .willReturn(expectedResponse);

            // when
            service.execute(command);

            // then
            ArgumentCaptor<TokenClaimsContext> contextCaptor =
                    ArgumentCaptor.forClass(TokenClaimsContext.class);
            verify(tokenManager).issueTokens(contextCaptor.capture());

            TokenClaimsContext capturedContext = contextCaptor.getValue();
            assertThat(capturedContext.organizationName()).isEqualTo(organization.nameValue());
        }

        @Test
        @DisplayName("TokenClaimsContext에 email이 포함된다")
        void shouldIncludeEmailInContext() {
            // given
            User user = UserFixture.create();
            Tenant tenant = TenantFixture.create();
            Organization organization = OrganizationFixture.create();

            UUID tenantId = user.tenantIdValue();
            String identifier = "testuser";
            String password = "password123";

            LoginCommand command = new LoginCommand(tenantId, identifier, password);
            TokenResponse tokenResponse =
                    new TokenResponse("access-token", "refresh-token", 3600L, 86400L, "Bearer");
            LoginResponse expectedResponse =
                    new LoginResponse(
                            user.userIdValue(), "access-token", "refresh-token", 3600L, "Bearer");

            given(userQueryPort.findByTenantIdAndIdentifier(TenantId.of(tenantId), identifier))
                    .willReturn(Optional.of(user));
            given(tenantQueryPort.findById(user.getTenantId())).willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(user.getOrganizationId()))
                    .willReturn(Optional.of(organization));
            given(tokenManager.issueTokens(any(TokenClaimsContext.class)))
                    .willReturn(tokenResponse);
            given(loginResponseAssembler.toLoginResponse(user, tokenResponse))
                    .willReturn(expectedResponse);

            // when
            service.execute(command);

            // then
            ArgumentCaptor<TokenClaimsContext> contextCaptor =
                    ArgumentCaptor.forClass(TokenClaimsContext.class);
            verify(tokenManager).issueTokens(contextCaptor.capture());

            TokenClaimsContext capturedContext = contextCaptor.getValue();
            assertThat(capturedContext.email()).isEqualTo(user.getIdentifier());
        }

        @Test
        @DisplayName("TokenClaimsContext에 모든 Hybrid JWT 정보가 포함된다")
        void shouldIncludeAllHybridJwtInfoInContext() {
            // given
            User user = UserFixture.create();
            Tenant tenant = TenantFixture.create();
            Organization organization = OrganizationFixture.create();

            UUID tenantId = user.tenantIdValue();
            String identifier = "testuser";
            String password = "password123";

            LoginCommand command = new LoginCommand(tenantId, identifier, password);
            TokenResponse tokenResponse =
                    new TokenResponse("access-token", "refresh-token", 3600L, 86400L, "Bearer");
            LoginResponse expectedResponse =
                    new LoginResponse(
                            user.userIdValue(), "access-token", "refresh-token", 3600L, "Bearer");

            given(userQueryPort.findByTenantIdAndIdentifier(TenantId.of(tenantId), identifier))
                    .willReturn(Optional.of(user));
            given(tenantQueryPort.findById(user.getTenantId())).willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(user.getOrganizationId()))
                    .willReturn(Optional.of(organization));
            given(tokenManager.issueTokens(any(TokenClaimsContext.class)))
                    .willReturn(tokenResponse);
            given(loginResponseAssembler.toLoginResponse(user, tokenResponse))
                    .willReturn(expectedResponse);

            // when
            service.execute(command);

            // then
            ArgumentCaptor<TokenClaimsContext> contextCaptor =
                    ArgumentCaptor.forClass(TokenClaimsContext.class);
            verify(tokenManager).issueTokens(contextCaptor.capture());

            TokenClaimsContext capturedContext = contextCaptor.getValue();
            assertThat(capturedContext.userId()).isEqualTo(user.getUserId());
            assertThat(capturedContext.tenantId()).isEqualTo(tenant.tenantIdValue());
            assertThat(capturedContext.tenantName()).isEqualTo(tenant.nameValue());
            assertThat(capturedContext.organizationId())
                    .isEqualTo(organization.organizationIdValue());
            assertThat(capturedContext.organizationName()).isEqualTo(organization.nameValue());
            assertThat(capturedContext.email()).isEqualTo(user.getIdentifier());
        }
    }
}
