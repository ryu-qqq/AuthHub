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

import com.ryuqq.authhub.application.user.assembler.UserQueryAssembler;
import com.ryuqq.authhub.application.user.component.UserUpdater;
import com.ryuqq.authhub.application.user.component.UserValidator;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.manager.UserManager;
import com.ryuqq.authhub.application.user.port.in.UpdateUserUseCase;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidUserStateException;
import com.ryuqq.authhub.domain.user.exception.PhoneNumberAlreadyExistsException;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UpdateUserUseCase 단위 테스트
 *
 * <p>UseCase 테스트 규칙:
 *
 * <ul>
 *   <li>Service → Manager → Port 구조
 *   <li>Validator 컴포넌트로 검증 분리
 *   <li>Updater 컴포넌트로 Domain 업데이트 위임
 *   <li>QueryAssembler로 Response 변환
 *   <li>도메인 예외 사용 (IllegalArgumentException 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserUseCase 테스트")
class UpdateUserUseCaseTest {

    @Mock private UserQueryPort userQueryPort;

    @Mock private UserManager userManager;

    @Mock private UserValidator userValidator;

    @Mock private UserUpdater userUpdater;

    @Mock private UserQueryAssembler userQueryAssembler;

    private UpdateUserUseCase updateUserUseCase;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final Long TENANT_ID = 1L;
    private static final Instant FIXED_TIME = Instant.parse("2025-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        updateUserUseCase =
                new UpdateUserService(
                        userQueryPort, userManager, userValidator, userUpdater, userQueryAssembler);
    }

    @Nested
    @DisplayName("execute() - 사용자 정보 수정")
    class Execute {

        @Test
        @DisplayName("유효한 Command로 사용자 정보를 성공적으로 수정해야 한다")
        void shouldUpdateUserSuccessfully() {
            // Given
            UpdateUserCommand command = createValidCommand();
            User existingUser = createMockUser();
            User updatedUser = createMockUser();
            UserResponse expectedResponse = createMockResponse();

            // existingUser stubbing
            given(existingUser.getUserId()).willReturn(UserId.of(USER_ID));
            given(existingUser.getTenantId()).willReturn(TenantId.of(TENANT_ID));

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            given(userUpdater.updateProfile(any(User.class), any(UserProfile.class)))
                    .willReturn(updatedUser);
            given(userQueryAssembler.toResponse(updatedUser)).willReturn(expectedResponse);

            // When
            UserResponse response = updateUserUseCase.execute(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response).isEqualTo(expectedResponse);

            // Verify interactions
            verify(userValidator)
                    .validatePhoneNumberForUpdate(
                            TenantId.of(TENANT_ID), command.phoneNumber(), UserId.of(USER_ID));
            verify(userUpdater).updateProfile(any(User.class), any(UserProfile.class));
            verify(userManager).persist(updatedUser);
            verify(userQueryAssembler).toResponse(updatedUser);
        }

        @Test
        @DisplayName("phoneNumber가 null이어도 정상적으로 수정되어야 한다")
        void shouldUpdateUserWhenPhoneNumberIsNull() {
            // Given
            UpdateUserCommand command = new UpdateUserCommand(USER_ID, "새이름", null);
            User existingUser = createMockUser();
            User updatedUser = createMockUser();
            UserResponse expectedResponse = createMockResponse();

            // existingUser stubbing
            given(existingUser.getUserId()).willReturn(UserId.of(USER_ID));
            given(existingUser.getTenantId()).willReturn(TenantId.of(TENANT_ID));

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            given(userUpdater.updateProfile(any(User.class), any(UserProfile.class)))
                    .willReturn(updatedUser);
            given(userQueryAssembler.toResponse(updatedUser)).willReturn(expectedResponse);

            // When
            UserResponse response = updateUserUseCase.execute(command);

            // Then
            assertThat(response).isNotNull();

            // phoneNumber가 null이어도 validator는 호출됨 (내부에서 null 체크)
            verify(userValidator)
                    .validatePhoneNumberForUpdate(TenantId.of(TENANT_ID), null, UserId.of(USER_ID));
        }
    }

    @Nested
    @DisplayName("검증 실패 시나리오")
    class ValidationFailure {

        @Test
        @DisplayName("존재하지 않는 사용자 수정 시 UserNotFoundException이 발생해야 한다")
        void shouldThrowWhenUserNotFound() {
            // Given
            UpdateUserCommand command = createValidCommand();
            given(userQueryPort.findById(UserId.of(command.userId()))).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> updateUserUseCase.execute(command))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userManager, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("중복된 전화번호로 수정 시 PhoneNumberAlreadyExistsException이 발생해야 한다")
        void shouldThrowWhenPhoneNumberDuplicate() {
            // Given
            UpdateUserCommand command = createValidCommand();
            User existingUser = createMockUser();

            // existingUser stubbing (validator 호출에 필요)
            given(existingUser.getUserId()).willReturn(UserId.of(USER_ID));
            given(existingUser.getTenantId()).willReturn(TenantId.of(TENANT_ID));

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            willThrow(new PhoneNumberAlreadyExistsException(TENANT_ID, command.phoneNumber()))
                    .given(userValidator)
                    .validatePhoneNumberForUpdate(
                            any(TenantId.class), anyString(), any(UserId.class));

            // When & Then
            assertThatThrownBy(() -> updateUserUseCase.execute(command))
                    .isInstanceOf(PhoneNumberAlreadyExistsException.class);

            verify(userManager, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("비활성 사용자 프로필 수정 시 InvalidUserStateException이 발생해야 한다")
        void shouldThrowWhenUserNotActive() {
            // Given
            UpdateUserCommand command = createValidCommand();
            User existingUser = createMockUser();

            // existingUser stubbing
            given(existingUser.getUserId()).willReturn(UserId.of(USER_ID));
            given(existingUser.getTenantId()).willReturn(TenantId.of(TENANT_ID));

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            willThrow(
                            new InvalidUserStateException(
                                    UserStatus.INACTIVE, "프로필 변경은 활성 상태의 사용자만 가능합니다"))
                    .given(userUpdater)
                    .updateProfile(any(User.class), any(UserProfile.class));

            // When & Then
            assertThatThrownBy(() -> updateUserUseCase.execute(command))
                    .isInstanceOf(InvalidUserStateException.class);

            verify(userManager, never()).persist(any(User.class));
        }
    }

    @Nested
    @DisplayName("Manager 연동 검증")
    class ManagerIntegration {

        @Test
        @DisplayName("수정된 User가 Manager를 통해 영속화되어야 한다")
        void shouldPersistUserThroughManager() {
            // Given
            UpdateUserCommand command = createValidCommand();
            User existingUser = createMockUser();
            User updatedUser = createMockUser();
            UserResponse expectedResponse = createMockResponse();

            // existingUser stubbing
            given(existingUser.getUserId()).willReturn(UserId.of(USER_ID));
            given(existingUser.getTenantId()).willReturn(TenantId.of(TENANT_ID));

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            given(userUpdater.updateProfile(any(User.class), any(UserProfile.class)))
                    .willReturn(updatedUser);
            given(userQueryAssembler.toResponse(updatedUser)).willReturn(expectedResponse);

            // When
            updateUserUseCase.execute(command);

            // Then
            verify(userManager).persist(updatedUser);
        }
    }

    // ========== Helper Methods ==========

    private UpdateUserCommand createValidCommand() {
        return new UpdateUserCommand(USER_ID, "홍길동", "+821012345678");
    }

    /** 기본 mock User 생성 (stubbing 없음) 각 테스트에서 필요한 stubbing을 직접 설정 */
    private User createMockUser() {
        return mock(User.class);
    }

    private UserResponse createMockResponse() {
        return new UserResponse(
                USER_ID,
                TENANT_ID,
                1L,
                "PUBLIC",
                "ACTIVE",
                "홍길동",
                "+821012345678",
                FIXED_TIME,
                FIXED_TIME);
    }
}
