package com.ryuqq.authhub.application.user.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.factory.UserCommandFactory;
import com.ryuqq.authhub.application.user.fixture.UserCommandFixtures;
import com.ryuqq.authhub.application.user.manager.UserCommandManager;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidPasswordException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.HashedPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ChangePasswordService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ChangePasswordService 단위 테스트")
class ChangePasswordServiceTest {

    @Mock private UserValidator validator;
    @Mock private UserCommandFactory commandFactory;
    @Mock private UserCommandManager commandManager;

    private ChangePasswordService sut;

    @BeforeEach
    void setUp() {
        sut = new ChangePasswordService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Validator → Factory → Domain 변경 → Manager 순서로 호출")
        void shouldOrchestrate_ValidatorThenFactoryThenManager() {
            // given
            ChangePasswordCommand command = UserCommandFixtures.changePasswordCommand();
            User user = UserFixture.create();
            UpdateContext<UserId, HashedPassword> context =
                    new UpdateContext<>(
                            UserFixture.defaultId(),
                            HashedPassword.of("newHashed"),
                            java.time.Instant.now());

            given(commandFactory.createPasswordChangeContext(command)).willReturn(context);
            given(validator.validatePasswordAndFindUser(context.id(), command.currentPassword()))
                    .willReturn(user);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createPasswordChangeContext(command);
            then(validator)
                    .should()
                    .validatePasswordAndFindUser(context.id(), command.currentPassword());
            then(commandManager).should().persist(user);
        }

        @Test
        @DisplayName("실패: 현재 비밀번호 불일치 시 InvalidPasswordException 발생")
        void shouldThrowException_WhenCurrentPasswordInvalid() {
            // given
            ChangePasswordCommand command = UserCommandFixtures.changePasswordCommand();
            UpdateContext<UserId, HashedPassword> context =
                    new UpdateContext<>(
                            UserFixture.defaultId(),
                            HashedPassword.of("newHashed"),
                            java.time.Instant.now());

            given(commandFactory.createPasswordChangeContext(command)).willReturn(context);
            given(validator.validatePasswordAndFindUser(context.id(), command.currentPassword()))
                    .willThrow(new InvalidPasswordException(UserFixture.defaultIdentifierString()));

            // when & then
            org.assertj.core.api.Assertions.assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(InvalidPasswordException.class);
            then(commandManager).should(never()).persist(any());
        }
    }
}
