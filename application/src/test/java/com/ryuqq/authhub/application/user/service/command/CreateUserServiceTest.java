package com.ryuqq.authhub.application.user.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.factory.UserCommandFactory;
import com.ryuqq.authhub.application.user.fixture.UserCommandFixtures;
import com.ryuqq.authhub.application.user.manager.UserCommandManager;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserIdentifierException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateUserService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserService 단위 테스트")
class CreateUserServiceTest {

    @Mock private UserValidator validator;

    @Mock private UserCommandFactory commandFactory;

    @Mock private UserCommandManager commandManager;

    private CreateUserService sut;

    @BeforeEach
    void setUp() {
        sut = new CreateUserService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Validator → Factory → Manager 순서로 호출하고 ID 반환")
        void shouldOrchestrate_ValidatorThenFactoryThenManager_AndReturnId() {
            // given
            CreateUserCommand command = UserCommandFixtures.createCommand();
            User user = UserFixture.createNew();
            String expectedId = UserFixture.defaultIdString();

            given(commandFactory.create(command)).willReturn(user);
            given(commandManager.persist(user)).willReturn(expectedId);

            // when
            String result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator).should().validateIdentifierNotDuplicated(any(), any());
            then(validator).should().validatePhoneNumberNotDuplicated(any(), any());
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(user);
        }

        @Test
        @DisplayName("실패: 식별자 중복 시 DuplicateUserIdentifierException 발생")
        void shouldThrowException_WhenIdentifierDuplicated() {
            // given
            CreateUserCommand command = UserCommandFixtures.createCommand();

            willThrow(new DuplicateUserIdentifierException(UserFixture.defaultIdentifier()))
                    .given(validator)
                    .validateIdentifierNotDuplicated(any(), any());

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(DuplicateUserIdentifierException.class);
            then(commandFactory).should(never()).create(any());
            then(commandManager).should(never()).persist(any());
        }
    }
}
