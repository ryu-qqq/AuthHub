package com.ryuqq.authhub.application.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.assembler.UserCommandAssembler;
import com.ryuqq.authhub.application.user.component.OrganizationValidator;
import com.ryuqq.authhub.application.user.component.TenantValidator;
import com.ryuqq.authhub.application.user.component.UserValidator;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;
import com.ryuqq.authhub.application.user.manager.UserManager;
import com.ryuqq.authhub.application.user.port.in.CreateUserUseCase;
import com.ryuqq.authhub.domain.organization.exception.InvalidOrganizationStateException;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.PhoneNumberAlreadyExistsException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateUserUseCase 단위 테스트
 *
 * <p>UseCase 테스트 규칙:
 *
 * <ul>
 *   <li>Service → Manager → Port 구조
 *   <li>Validator 컴포넌트로 검증 분리
 *   <li>Assembler로 Command → Domain 변환
 *   <li>도메인 예외 사용 (IllegalArgumentException 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase 테스트")
class CreateUserUseCaseTest {

    @Mock private UserManager userManager;

    @Mock private TenantValidator tenantValidator;

    @Mock private OrganizationValidator organizationValidator;

    @Mock private UserValidator userValidator;

    @Mock private UserCommandAssembler userCommandAssembler;

    private CreateUserUseCase createUserUseCase;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-15T10:00:00Z");
    private static final UUID CREATED_USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createUserUseCase =
                new CreateUserService(
                        userManager,
                        tenantValidator,
                        organizationValidator,
                        userValidator,
                        userCommandAssembler);
    }

    @Nested
    @DisplayName("execute() - 사용자 생성")
    class Execute {

        @Test
        @DisplayName("유효한 Command로 사용자를 성공적으로 생성해야 한다")
        void shouldCreateUserSuccessfully() {
            // Given
            CreateUserCommand command = createValidCommand();
            User mockUser = createMockUser();

            given(userCommandAssembler.toUser(command)).willReturn(mockUser);
            given(userManager.persist(mockUser)).willReturn(UserId.of(CREATED_USER_ID));

            // When
            CreateUserResponse response = createUserUseCase.execute(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(CREATED_USER_ID);

            // Verify interactions
            verify(tenantValidator).validate(TenantId.of(command.tenantId()));
            verify(organizationValidator).validate(OrganizationId.of(command.organizationId()));
            verify(userCommandAssembler).toUser(command);
            verify(userManager).persist(mockUser);
        }

        @Test
        @DisplayName("User 생성 시 Assembler가 호출되어야 한다")
        void shouldCallAssemblerWhenCreatingUser() {
            // Given
            CreateUserCommand command = createValidCommand();
            User mockUser = createMockUser();

            given(userCommandAssembler.toUser(command)).willReturn(mockUser);
            given(userManager.persist(mockUser)).willReturn(UserId.of(CREATED_USER_ID));

            // When
            createUserUseCase.execute(command);

            // Then
            ArgumentCaptor<CreateUserCommand> commandCaptor =
                    ArgumentCaptor.forClass(CreateUserCommand.class);
            verify(userCommandAssembler).toUser(commandCaptor.capture());

            assertThat(commandCaptor.getValue()).isEqualTo(command);
        }
    }

    @Nested
    @DisplayName("검증 실패 시나리오")
    class ValidationFailure {

        @Test
        @DisplayName("존재하지 않는 Tenant로 생성 시 TenantNotFoundException이 발생해야 한다")
        void shouldThrowWhenTenantNotFound() {
            // Given
            CreateUserCommand command = createValidCommand();
            willThrow(new TenantNotFoundException(command.tenantId()))
                    .given(tenantValidator)
                    .validate(any(TenantId.class));

            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(command))
                    .isInstanceOf(TenantNotFoundException.class);

            verify(userManager, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("비활성 Tenant로 생성 시 InvalidTenantStateException이 발생해야 한다")
        void shouldThrowWhenTenantInactive() {
            // Given
            CreateUserCommand command = createValidCommand();
            willThrow(new InvalidTenantStateException(command.tenantId(), "Tenant가 비활성 상태입니다"))
                    .given(tenantValidator)
                    .validate(any(TenantId.class));

            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(command))
                    .isInstanceOf(InvalidTenantStateException.class);

            verify(userManager, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("존재하지 않는 Organization으로 생성 시 OrganizationNotFoundException이 발생해야 한다")
        void shouldThrowWhenOrganizationNotFound() {
            // Given
            CreateUserCommand command = createValidCommand();
            willThrow(new OrganizationNotFoundException(command.organizationId()))
                    .given(organizationValidator)
                    .validate(any(OrganizationId.class));

            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(command))
                    .isInstanceOf(OrganizationNotFoundException.class);

            verify(userManager, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("비활성 Organization으로 생성 시 InvalidOrganizationStateException이 발생해야 한다")
        void shouldThrowWhenOrganizationInactive() {
            // Given
            CreateUserCommand command = createValidCommand();
            willThrow(
                            new InvalidOrganizationStateException(
                                    command.organizationId(), "Organization이 비활성 상태입니다"))
                    .given(organizationValidator)
                    .validate(any(OrganizationId.class));

            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(command))
                    .isInstanceOf(InvalidOrganizationStateException.class);

            verify(userManager, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("중복된 전화번호로 생성 시 PhoneNumberAlreadyExistsException이 발생해야 한다")
        void shouldThrowWhenPhoneNumberDuplicate() {
            // Given
            CreateUserCommand command = createValidCommand();
            willThrow(
                            new PhoneNumberAlreadyExistsException(
                                    command.tenantId(), command.phoneNumber()))
                    .given(userValidator)
                    .validatePhoneNumberForCreate(any(TenantId.class), anyString());

            // When & Then
            assertThatThrownBy(() -> createUserUseCase.execute(command))
                    .isInstanceOf(PhoneNumberAlreadyExistsException.class);

            verify(userManager, never()).persist(any(User.class));
        }
    }

    @Nested
    @DisplayName("Manager 연동 검증")
    class ManagerIntegration {

        @Test
        @DisplayName("생성된 User가 Manager를 통해 영속화되어야 한다")
        void shouldPersistUserThroughManager() {
            // Given
            CreateUserCommand command = createValidCommand();
            User mockUser = createMockUser();

            given(userCommandAssembler.toUser(command)).willReturn(mockUser);
            given(userManager.persist(mockUser)).willReturn(UserId.of(CREATED_USER_ID));

            // When
            createUserUseCase.execute(command);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userManager).persist(userCaptor.capture());

            assertThat(userCaptor.getValue()).isEqualTo(mockUser);
        }
    }

    // ========== Helper Methods ==========

    private CreateUserCommand createValidCommand() {
        return new CreateUserCommand(
                1L, // tenantId
                1L, // organizationId
                "test@example.com", // identifier
                "password123", // rawPassword
                "PUBLIC", // userType
                "홍길동", // name
                "+821012345678" // phoneNumber
                );
    }

    private User createMockUser() {
        User mockUser = mock(User.class);
        given(mockUser.createdAt()).willReturn(FIXED_TIME);
        return mockUser;
    }
}
