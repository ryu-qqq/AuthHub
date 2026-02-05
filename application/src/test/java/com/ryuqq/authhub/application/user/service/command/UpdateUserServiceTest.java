package com.ryuqq.authhub.application.user.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.factory.UserCommandFactory;
import com.ryuqq.authhub.application.user.fixture.UserCommandFixtures;
import com.ryuqq.authhub.application.user.manager.UserCommandManager;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.aggregate.UserUpdateData;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UpdateUserService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserService 단위 테스트")
class UpdateUserServiceTest {

    @Mock private UserValidator validator;
    @Mock private UserCommandFactory commandFactory;
    @Mock private UserCommandManager commandManager;

    private UpdateUserService sut;

    @BeforeEach
    void setUp() {
        sut = new UpdateUserService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Validator → Manager 순서로 호출")
        void shouldOrchestrate_FactoryThenValidatorThenManager() {
            // given
            UpdateUserCommand command = UserCommandFixtures.updateUserCommand();
            User user = UserFixture.create();
            UpdateContext<UserId, UserUpdateData> context =
                    new UpdateContext<>(
                            UserFixture.defaultId(),
                            UserUpdateData.of(PhoneNumber.of("010-9999-8888")),
                            java.time.Instant.now());

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(user);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(context.id());
            then(commandManager).should().persist(user);
        }

        @Test
        @DisplayName("실패: 사용자 미존재 시 UserNotFoundException 발생")
        void shouldThrowException_WhenUserNotFound() {
            // given
            UpdateUserCommand command = UserCommandFixtures.updateUserCommand();
            UpdateContext<UserId, UserUpdateData> context =
                    new UpdateContext<>(
                            UserFixture.defaultId(),
                            UserUpdateData.of(PhoneNumber.of("010-9999-8888")),
                            java.time.Instant.now());

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id()))
                    .willThrow(new UserNotFoundException(UserFixture.defaultId()));

            // when & then
            org.assertj.core.api.Assertions.assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(UserNotFoundException.class);
            then(commandManager).should(never()).persist(any());
        }
    }
}
