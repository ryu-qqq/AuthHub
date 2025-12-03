package com.ryuqq.authhub.application.user.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.common.component.PasswordHasher;
import com.ryuqq.authhub.application.user.component.PasswordChangeValidator;
import com.ryuqq.authhub.application.user.component.UserUpdater;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.manager.UserManager;
import com.ryuqq.authhub.application.user.port.in.ChangePasswordUseCase;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidPasswordException;
import com.ryuqq.authhub.domain.user.exception.InvalidUserStateException;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.Password;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
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
 * ChangePasswordUseCase 단위 테스트
 *
 * <p>UseCase 테스트 규칙:
 *
 * <ul>
 *   <li>Service → Manager → Port 구조
 *   <li>Validator 컴포넌트로 비밀번호 검증 위임
 *   <li>Updater 컴포넌트로 Domain 업데이트 위임
 *   <li>도메인 예외 사용 (IllegalArgumentException 금지)
 * </ul>
 *
 * <p>비밀번호 변경 시나리오:
 *
 * <ul>
 *   <li>일반 변경 (verified=false): 현재 비밀번호 검증 필요
 *   <li>재설정 (verified=true): 본인 인증 완료, 검증 건너뜀
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChangePasswordUseCase 테스트")
class ChangePasswordUseCaseTest {

    @Mock private UserQueryPort userQueryPort;

    @Mock private UserManager userManager;

    @Mock private UserUpdater userUpdater;

    @Mock private PasswordChangeValidator passwordChangeValidator;

    @Mock private PasswordHasher passwordHasher;

    private ChangePasswordUseCase changePasswordUseCase;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CURRENT_PASSWORD = "currentPassword123!";
    private static final String NEW_PASSWORD = "newPassword456!";
    private static final String HASHED_NEW_PASSWORD = "$2a$10$hashedNewPassword";

    @BeforeEach
    void setUp() {
        changePasswordUseCase =
                new ChangePasswordService(
                        userQueryPort,
                        userManager,
                        userUpdater,
                        passwordChangeValidator,
                        passwordHasher);
    }

    @Nested
    @DisplayName("execute() - 일반 비밀번호 변경 (verified=false)")
    class ExecuteNormalChange {

        @Test
        @DisplayName("유효한 현재 비밀번호로 변경이 성공해야 한다")
        void shouldChangePasswordSuccessfully() {
            // Given
            ChangePasswordCommand command =
                    ChangePasswordCommand.forChange(USER_ID, CURRENT_PASSWORD, NEW_PASSWORD);
            User existingUser = createMockUser();
            User updatedUser = createMockUser();

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            willDoNothing().given(passwordChangeValidator).validate(command, existingUser);
            given(passwordHasher.hash(NEW_PASSWORD)).willReturn(HASHED_NEW_PASSWORD);
            given(userUpdater.changePassword(any(User.class), any(Password.class)))
                    .willReturn(updatedUser);

            // When
            changePasswordUseCase.execute(command);

            // Then
            verify(passwordChangeValidator).validate(command, existingUser);
            verify(passwordHasher).hash(NEW_PASSWORD);
            verify(userUpdater).changePassword(any(User.class), any(Password.class));
            verify(userManager).persist(updatedUser);
        }
    }

    @Nested
    @DisplayName("execute() - 비밀번호 재설정 (verified=true)")
    class ExecutePasswordReset {

        @Test
        @DisplayName("본인 인증 완료 시 현재 비밀번호 없이 변경이 성공해야 한다")
        void shouldResetPasswordWithoutCurrentPassword() {
            // Given
            ChangePasswordCommand command = ChangePasswordCommand.forReset(USER_ID, NEW_PASSWORD);
            User existingUser = createMockUser();
            User updatedUser = createMockUser();

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            willDoNothing().given(passwordChangeValidator).validate(command, existingUser);
            given(passwordHasher.hash(NEW_PASSWORD)).willReturn(HASHED_NEW_PASSWORD);
            given(userUpdater.changePassword(any(User.class), any(Password.class)))
                    .willReturn(updatedUser);

            // When
            changePasswordUseCase.execute(command);

            // Then
            verify(passwordChangeValidator).validate(command, existingUser);
            verify(passwordHasher).hash(NEW_PASSWORD);
            verify(userUpdater).changePassword(any(User.class), any(Password.class));
            verify(userManager).persist(updatedUser);
        }
    }

    @Nested
    @DisplayName("검증 실패 시나리오")
    class ValidationFailure {

        @Test
        @DisplayName("존재하지 않는 사용자 비밀번호 변경 시 UserNotFoundException이 발생해야 한다")
        void shouldThrowWhenUserNotFound() {
            // Given
            ChangePasswordCommand command =
                    ChangePasswordCommand.forChange(USER_ID, CURRENT_PASSWORD, NEW_PASSWORD);
            given(userQueryPort.findById(UserId.of(command.userId()))).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> changePasswordUseCase.execute(command))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userManager, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("현재 비밀번호가 일치하지 않으면 InvalidPasswordException이 발생해야 한다")
        void shouldThrowWhenCurrentPasswordNotMatch() {
            // Given
            ChangePasswordCommand command =
                    ChangePasswordCommand.forChange(USER_ID, "wrongPassword", NEW_PASSWORD);
            User existingUser = createMockUser();

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            willThrow(new InvalidPasswordException(command.userId()))
                    .given(passwordChangeValidator)
                    .validate(command, existingUser);

            // When & Then
            assertThatThrownBy(() -> changePasswordUseCase.execute(command))
                    .isInstanceOf(InvalidPasswordException.class);

            verify(userManager, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("비활성 사용자 비밀번호 변경 시 InvalidUserStateException이 발생해야 한다")
        void shouldThrowWhenUserNotActive() {
            // Given
            ChangePasswordCommand command =
                    ChangePasswordCommand.forChange(USER_ID, CURRENT_PASSWORD, NEW_PASSWORD);
            User existingUser = createMockUser();

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            willDoNothing().given(passwordChangeValidator).validate(command, existingUser);
            given(passwordHasher.hash(NEW_PASSWORD)).willReturn(HASHED_NEW_PASSWORD);
            willThrow(
                            new InvalidUserStateException(
                                    UserStatus.INACTIVE, "비밀번호 변경은 활성 상태의 사용자만 가능합니다"))
                    .given(userUpdater)
                    .changePassword(any(User.class), any(Password.class));

            // When & Then
            assertThatThrownBy(() -> changePasswordUseCase.execute(command))
                    .isInstanceOf(InvalidUserStateException.class);

            verify(userManager, never()).persist(any(User.class));
        }
    }

    @Nested
    @DisplayName("Manager 연동 검증")
    class ManagerIntegration {

        @Test
        @DisplayName("비밀번호 변경된 User가 Manager를 통해 영속화되어야 한다")
        void shouldPersistUserThroughManager() {
            // Given
            ChangePasswordCommand command =
                    ChangePasswordCommand.forChange(USER_ID, CURRENT_PASSWORD, NEW_PASSWORD);
            User existingUser = createMockUser();
            User updatedUser = createMockUser();

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            willDoNothing().given(passwordChangeValidator).validate(command, existingUser);
            given(passwordHasher.hash(NEW_PASSWORD)).willReturn(HASHED_NEW_PASSWORD);
            given(userUpdater.changePassword(any(User.class), any(Password.class)))
                    .willReturn(updatedUser);

            // When
            changePasswordUseCase.execute(command);

            // Then
            verify(userManager).persist(updatedUser);
        }
    }

    // ========== Helper Methods ==========

    private User createMockUser() {
        return mock(User.class);
    }
}
