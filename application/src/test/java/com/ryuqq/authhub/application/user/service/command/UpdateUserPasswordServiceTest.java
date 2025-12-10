package com.ryuqq.authhub.application.user.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.dto.command.UpdateUserPasswordCommand;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderPort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidPasswordException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UpdateUserPasswordService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserPasswordService 단위 테스트")
class UpdateUserPasswordServiceTest {

    @Mock private UserTransactionManager transactionManager;

    @Mock private UserReadManager readManager;

    @Mock private PasswordEncoderPort passwordEncoderPort;

    private Clock clock;
    private UpdateUserPasswordService service;

    @BeforeEach
    void setUp() {
        clock = UserFixture.fixedClock();
        service =
                new UpdateUserPasswordService(
                        transactionManager, readManager, passwordEncoderPort, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("비밀번호를 성공적으로 변경한다")
        void shouldChangePasswordSuccessfully() {
            // given
            User existingUser = UserFixture.create();
            UpdateUserPasswordCommand command =
                    new UpdateUserPasswordCommand(
                            existingUser.userIdValue(), "currentPassword", "newPassword");
            String newHashedPassword = "new_hashed_password";

            given(readManager.getById(UserId.of(command.userId()))).willReturn(existingUser);
            given(
                            passwordEncoderPort.matches(
                                    command.currentPassword(), existingUser.getHashedPassword()))
                    .willReturn(true);
            given(passwordEncoderPort.hash(command.newPassword())).willReturn(newHashedPassword);
            given(transactionManager.persist(any(User.class))).willReturn(existingUser);

            // when
            service.execute(command);

            // then
            verify(readManager).getById(UserId.of(command.userId()));
            verify(passwordEncoderPort)
                    .matches(command.currentPassword(), existingUser.getHashedPassword());
            verify(passwordEncoderPort).hash(command.newPassword());
            verify(transactionManager).persist(any(User.class));
        }

        @Test
        @DisplayName("현재 비밀번호가 일치하지 않으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenCurrentPasswordNotMatch() {
            // given
            User existingUser = UserFixture.create();
            UpdateUserPasswordCommand command =
                    new UpdateUserPasswordCommand(
                            existingUser.userIdValue(), "wrongPassword", "newPassword");

            given(readManager.getById(UserId.of(command.userId()))).willReturn(existingUser);
            given(
                            passwordEncoderPort.matches(
                                    command.currentPassword(), existingUser.getHashedPassword()))
                    .willReturn(false);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidPasswordException.class);

            verify(passwordEncoderPort, never()).hash(any());
            verify(transactionManager, never()).persist(any());
        }
    }
}
