package com.ryuqq.authhub.application.user.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.component.UserUpdater;
import com.ryuqq.authhub.application.user.dto.command.ChangeUserStatusCommand;
import com.ryuqq.authhub.application.user.manager.UserManager;
import com.ryuqq.authhub.application.user.port.in.ChangeUserStatusUseCase;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidUserStateException;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
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
 * ChangeUserStatusUseCase 단위 테스트
 *
 * <p>UseCase 테스트 규칙:
 *
 * <ul>
 *   <li>Service → Manager → Port 구조
 *   <li>Updater 컴포넌트로 Domain 업데이트 위임
 *   <li>상태 전환 검증은 Domain에서 수행
 *   <li>도메인 예외 사용 (IllegalArgumentException 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChangeUserStatusUseCase 테스트")
class ChangeUserStatusUseCaseTest {

    @Mock private UserQueryPort userQueryPort;

    @Mock private UserManager userManager;

    @Mock private UserUpdater userUpdater;

    private ChangeUserStatusUseCase changeUserStatusUseCase;

    private static final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        changeUserStatusUseCase =
                new ChangeUserStatusService(userQueryPort, userManager, userUpdater);
    }

    @Nested
    @DisplayName("execute() - 사용자 상태 변경")
    class Execute {

        @Test
        @DisplayName("ACTIVE → INACTIVE 상태 변경이 성공해야 한다")
        void shouldChangeStatusToInactive() {
            // Given
            ChangeUserStatusCommand command =
                    new ChangeUserStatusCommand(USER_ID, "INACTIVE", "휴면 처리");
            User existingUser = createMockUser();
            User updatedUser = createMockUser();

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            given(userUpdater.changeStatus(existingUser, UserStatus.INACTIVE))
                    .willReturn(updatedUser);

            // When
            changeUserStatusUseCase.execute(command);

            // Then
            verify(userUpdater).changeStatus(existingUser, UserStatus.INACTIVE);
            verify(userManager).persist(updatedUser);
        }

        @Test
        @DisplayName("INACTIVE → ACTIVE 상태 변경이 성공해야 한다")
        void shouldChangeStatusToActive() {
            // Given
            ChangeUserStatusCommand command =
                    new ChangeUserStatusCommand(USER_ID, "ACTIVE", "계정 활성화");
            User existingUser = createMockUser();
            User updatedUser = createMockUser();

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            given(userUpdater.changeStatus(existingUser, UserStatus.ACTIVE))
                    .willReturn(updatedUser);

            // When
            changeUserStatusUseCase.execute(command);

            // Then
            verify(userUpdater).changeStatus(existingUser, UserStatus.ACTIVE);
            verify(userManager).persist(updatedUser);
        }

        @Test
        @DisplayName("SUSPENDED 상태로 변경이 성공해야 한다")
        void shouldChangeStatusToSuspended() {
            // Given
            ChangeUserStatusCommand command =
                    new ChangeUserStatusCommand(USER_ID, "SUSPENDED", "정책 위반");
            User existingUser = createMockUser();
            User updatedUser = createMockUser();

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            given(userUpdater.changeStatus(existingUser, UserStatus.SUSPENDED))
                    .willReturn(updatedUser);

            // When
            changeUserStatusUseCase.execute(command);

            // Then
            verify(userUpdater).changeStatus(existingUser, UserStatus.SUSPENDED);
            verify(userManager).persist(updatedUser);
        }

        @Test
        @DisplayName("DELETED 상태로 변경이 성공해야 한다")
        void shouldChangeStatusToDeleted() {
            // Given
            ChangeUserStatusCommand command =
                    new ChangeUserStatusCommand(USER_ID, "DELETED", "회원 탈퇴");
            User existingUser = createMockUser();
            User updatedUser = createMockUser();

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            given(userUpdater.changeStatus(existingUser, UserStatus.DELETED))
                    .willReturn(updatedUser);

            // When
            changeUserStatusUseCase.execute(command);

            // Then
            verify(userUpdater).changeStatus(existingUser, UserStatus.DELETED);
            verify(userManager).persist(updatedUser);
        }
    }

    @Nested
    @DisplayName("검증 실패 시나리오")
    class ValidationFailure {

        @Test
        @DisplayName("존재하지 않는 사용자 상태 변경 시 UserNotFoundException이 발생해야 한다")
        void shouldThrowWhenUserNotFound() {
            // Given
            ChangeUserStatusCommand command =
                    new ChangeUserStatusCommand(USER_ID, "INACTIVE", "휴면 처리");
            given(userQueryPort.findById(UserId.of(command.userId()))).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> changeUserStatusUseCase.execute(command))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userManager, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("유효하지 않은 상태 전환 시 InvalidUserStateException이 발생해야 한다")
        void shouldThrowWhenInvalidStateTransition() {
            // Given
            ChangeUserStatusCommand command =
                    new ChangeUserStatusCommand(USER_ID, "ACTIVE", "계정 활성화");
            User existingUser = createMockUser();

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            willThrow(new InvalidUserStateException(UserStatus.DELETED, UserStatus.ACTIVE))
                    .given(userUpdater)
                    .changeStatus(existingUser, UserStatus.ACTIVE);

            // When & Then
            assertThatThrownBy(() -> changeUserStatusUseCase.execute(command))
                    .isInstanceOf(InvalidUserStateException.class);

            verify(userManager, never()).persist(any(User.class));
        }
    }

    @Nested
    @DisplayName("Manager 연동 검증")
    class ManagerIntegration {

        @Test
        @DisplayName("상태 변경된 User가 Manager를 통해 영속화되어야 한다")
        void shouldPersistUserThroughManager() {
            // Given
            ChangeUserStatusCommand command =
                    new ChangeUserStatusCommand(USER_ID, "INACTIVE", "휴면 처리");
            User existingUser = createMockUser();
            User updatedUser = createMockUser();

            given(userQueryPort.findById(UserId.of(command.userId())))
                    .willReturn(Optional.of(existingUser));
            given(userUpdater.changeStatus(existingUser, UserStatus.INACTIVE))
                    .willReturn(updatedUser);

            // When
            changeUserStatusUseCase.execute(command);

            // Then
            verify(userManager).persist(updatedUser);
        }
    }

    // ========== Helper Methods ==========

    private User createMockUser() {
        return mock(User.class);
    }
}
