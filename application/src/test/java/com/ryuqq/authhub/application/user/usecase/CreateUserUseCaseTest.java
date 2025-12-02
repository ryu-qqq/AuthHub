package com.ryuqq.authhub.application.user.usecase;

import com.ryuqq.authhub.application.common.port.out.security.PasswordHasherPort;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;
import com.ryuqq.authhub.application.user.port.out.command.UserPersistencePort;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * CreateUserUseCase 단위 테스트
 *
 * <p>Kent Beck TDD - Red Phase: 실패하는 테스트 먼저 작성
 *
 * <p>UseCase 테스트 규칙:
 * <ul>
 *   <li>Port 모킹으로 외부 의존성 격리</li>
 *   <li>성공 시나리오와 실패 시나리오 모두 테스트</li>
 *   <li>Domain 로직이 아닌 UseCase 흐름만 테스트</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase 테스트")
class CreateUserUseCaseTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private TenantQueryPort tenantQueryPort;

    @Mock
    private OrganizationQueryPort organizationQueryPort;

    @Mock
    private PasswordHasherPort passwordHasherPort;

    @Mock
    private Clock clock;

    private CreateUserUseCase createUserUseCase;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-15T10:00:00Z");
    private static final String HASHED_PASSWORD = "$2a$10$hashedPasswordValue";
    private static final UUID CREATED_USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createUserUseCase = new CreateUserUseCaseImpl(
                userPersistencePort,
                tenantQueryPort,
                organizationQueryPort,
                passwordHasherPort,
                clock
        );
    }

    @Nested
    @DisplayName("execute() - 사용자 생성")
    class Execute {

        @Test
        @DisplayName("유효한 Command로 사용자를 성공적으로 생성해야 한다")
        void shouldCreateUserSuccessfully() {
            // Given
            CreateUserCommand command = createValidCommand();
            Tenant tenant = TenantFixture.aTenant();
            Organization organization = OrganizationFixture.anOrganization();

            given(tenantQueryPort.findById(any(TenantId.class)))
                    .willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(any(OrganizationId.class)))
                    .willReturn(Optional.of(organization));
            given(passwordHasherPort.hash(command.rawPassword()))
                    .willReturn(HASHED_PASSWORD);
            given(clock.now()).willReturn(FIXED_TIME);
            given(userPersistencePort.persist(any(User.class)))
                    .willReturn(UserId.of(CREATED_USER_ID));

            // When
            CreateUserResponse response = createUserUseCase.execute(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(CREATED_USER_ID);
            assertThat(response.createdAt()).isEqualTo(FIXED_TIME);

            // Verify interactions
            verify(tenantQueryPort).findById(any(TenantId.class));
            verify(organizationQueryPort).findById(any(OrganizationId.class));
            verify(passwordHasherPort).hash(command.rawPassword());
            verify(userPersistencePort).persist(any(User.class));
        }

        @Test
        @DisplayName("Organization 없이 사용자를 생성할 수 있어야 한다")
        void shouldCreateUserWithoutOrganization() {
            // Given
            CreateUserCommand command = createCommandWithoutOrganization();
            Tenant tenant = TenantFixture.aTenant();

            given(tenantQueryPort.findById(any(TenantId.class)))
                    .willReturn(Optional.of(tenant));
            given(passwordHasherPort.hash(command.rawPassword()))
                    .willReturn(HASHED_PASSWORD);
            given(clock.now()).willReturn(FIXED_TIME);
            given(userPersistencePort.persist(any(User.class)))
                    .willReturn(UserId.of(CREATED_USER_ID));

            // When
            CreateUserResponse response = createUserUseCase.execute(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(CREATED_USER_ID);

            // Organization 조회는 호출되지 않아야 함
            verify(organizationQueryPort, never()).findById(any(OrganizationId.class));
        }

        @Test
        @DisplayName("User 생성 시 비밀번호가 해싱되어야 한다")
        void shouldHashPasswordWhenCreatingUser() {
            // Given
            CreateUserCommand command = createValidCommand();
            Tenant tenant = TenantFixture.aTenant();
            Organization organization = OrganizationFixture.anOrganization();

            given(tenantQueryPort.findById(any(TenantId.class)))
                    .willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(any(OrganizationId.class)))
                    .willReturn(Optional.of(organization));
            given(passwordHasherPort.hash(command.rawPassword()))
                    .willReturn(HASHED_PASSWORD);
            given(clock.now()).willReturn(FIXED_TIME);
            given(userPersistencePort.persist(any(User.class)))
                    .willReturn(UserId.of(CREATED_USER_ID));

            // When
            createUserUseCase.execute(command);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userPersistencePort).persist(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getCredential().password().hashedValue())
                    .isEqualTo(HASHED_PASSWORD);
        }
    }

    @Nested
    @DisplayName("검증 실패 시나리오")
    class ValidationFailure {

        @Test
        @DisplayName("존재하지 않는 Tenant로 생성 시 예외가 발생해야 한다")
        void shouldThrowWhenTenantNotFound() {
            // Given
            CreateUserCommand command = createValidCommand();
            given(tenantQueryPort.findById(any(TenantId.class)))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tenant");

            verify(userPersistencePort, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("비활성 Tenant로 생성 시 예외가 발생해야 한다")
        void shouldThrowWhenTenantInactive() {
            // Given
            CreateUserCommand command = createValidCommand();
            Tenant inactiveTenant = TenantFixture.anInactiveTenant();

            given(tenantQueryPort.findById(any(TenantId.class)))
                    .willReturn(Optional.of(inactiveTenant));

            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Tenant");

            verify(userPersistencePort, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("존재하지 않는 Organization으로 생성 시 예외가 발생해야 한다")
        void shouldThrowWhenOrganizationNotFound() {
            // Given
            CreateUserCommand command = createValidCommand();
            Tenant tenant = TenantFixture.aTenant();

            given(tenantQueryPort.findById(any(TenantId.class)))
                    .willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(any(OrganizationId.class)))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Organization");

            verify(userPersistencePort, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("비활성 Organization으로 생성 시 예외가 발생해야 한다")
        void shouldThrowWhenOrganizationInactive() {
            // Given
            CreateUserCommand command = createValidCommand();
            Tenant tenant = TenantFixture.aTenant();
            Organization inactiveOrganization = OrganizationFixture.anInactiveOrganization();

            given(tenantQueryPort.findById(any(TenantId.class)))
                    .willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(any(OrganizationId.class)))
                    .willReturn(Optional.of(inactiveOrganization));

            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Organization");

            verify(userPersistencePort, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("null Command로 생성 시 예외가 발생해야 한다")
        void shouldThrowWhenCommandIsNull() {
            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(null))
                    .isInstanceOf(NullPointerException.class);

            verify(userPersistencePort, never()).persist(any(User.class));
        }
    }

    @Nested
    @DisplayName("Domain 객체 생성 검증")
    class DomainObjectCreation {

        @Test
        @DisplayName("Command 필드가 User Domain에 올바르게 매핑되어야 한다")
        void shouldMapCommandFieldsToUserDomain() {
            // Given
            CreateUserCommand command = createValidCommand();
            Tenant tenant = TenantFixture.aTenant();
            Organization organization = OrganizationFixture.anOrganization();

            given(tenantQueryPort.findById(any(TenantId.class)))
                    .willReturn(Optional.of(tenant));
            given(organizationQueryPort.findById(any(OrganizationId.class)))
                    .willReturn(Optional.of(organization));
            given(passwordHasherPort.hash(command.rawPassword()))
                    .willReturn(HASHED_PASSWORD);
            given(clock.now()).willReturn(FIXED_TIME);
            given(userPersistencePort.persist(any(User.class)))
                    .willReturn(UserId.of(CREATED_USER_ID));

            // When
            createUserUseCase.execute(command);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userPersistencePort).persist(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.isNew()).isTrue();
            assertThat(capturedUser.tenantIdValue()).isEqualTo(command.tenantId());
            assertThat(capturedUser.organizationIdValue()).isEqualTo(command.organizationId());
            assertThat(capturedUser.userTypeValue()).isEqualTo(command.userType());
            assertThat(capturedUser.isActive()).isTrue();
            assertThat(capturedUser.getCredential().identifier()).isEqualTo(command.identifier());
            assertThat(capturedUser.getProfile().name()).isEqualTo(command.name());
            assertThat(capturedUser.getProfile().nickname()).isEqualTo(command.nickname());
        }

        @Test
        @DisplayName("UserType이 null이면 기본값 PUBLIC으로 설정되어야 한다")
        void shouldUseDefaultUserTypeWhenNull() {
            // Given
            CreateUserCommand command = createCommandWithNullUserType();
            Tenant tenant = TenantFixture.aTenant();

            given(tenantQueryPort.findById(any(TenantId.class)))
                    .willReturn(Optional.of(tenant));
            given(passwordHasherPort.hash(command.rawPassword()))
                    .willReturn(HASHED_PASSWORD);
            given(clock.now()).willReturn(FIXED_TIME);
            given(userPersistencePort.persist(any(User.class)))
                    .willReturn(UserId.of(CREATED_USER_ID));

            // When
            createUserUseCase.execute(command);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userPersistencePort).persist(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.userTypeValue()).isEqualTo("PUBLIC");
        }
    }

    // ========== Helper Methods ==========

    private CreateUserCommand createValidCommand() {
        return new CreateUserCommand(
                1L,                           // tenantId
                1L,                           // organizationId
                "EMAIL",                      // credentialType
                "test@example.com",           // identifier
                "password123",                // rawPassword
                "PUBLIC",                     // userType
                "홍길동",                       // name
                "길동이",                       // nickname
                "https://example.com/img.jpg" // profileImageUrl
        );
    }

    private CreateUserCommand createCommandWithoutOrganization() {
        return new CreateUserCommand(
                1L,                           // tenantId
                null,                         // organizationId (없음)
                "EMAIL",                      // credentialType
                "test@example.com",           // identifier
                "password123",                // rawPassword
                "PUBLIC",                     // userType
                "홍길동",                       // name
                "길동이",                       // nickname
                null                          // profileImageUrl
        );
    }

    private CreateUserCommand createCommandWithNullUserType() {
        return new CreateUserCommand(
                1L,                           // tenantId
                null,                         // organizationId
                "EMAIL",                      // credentialType
                "test@example.com",           // identifier
                "password123",                // rawPassword
                null,                         // userType (null → PUBLIC 기본값)
                "홍길동",                       // name
                null,                         // nickname
                null                          // profileImageUrl
        );
    }
}
