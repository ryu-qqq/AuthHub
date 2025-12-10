package com.ryuqq.authhub.application.user.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.dto.command.DeleteUserCommand;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.domain.user.aggregate.User;
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
 * DeleteUserService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteUserService 단위 테스트")
class DeleteUserServiceTest {

    @Mock private UserTransactionManager transactionManager;

    @Mock private UserReadManager readManager;

    private Clock clock;
    private DeleteUserService service;

    @BeforeEach
    void setUp() {
        clock = UserFixture.fixedClock();
        service = new DeleteUserService(transactionManager, readManager, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("사용자를 성공적으로 삭제한다")
        void shouldDeleteUserSuccessfully() {
            // given
            User existingUser = UserFixture.create();
            DeleteUserCommand command = new DeleteUserCommand(existingUser.userIdValue());
            User deletedUser =
                    UserFixture.createWithStatus(
                            com.ryuqq.authhub.domain.user.vo.UserStatus.DELETED);

            given(readManager.getById(UserId.of(command.userId()))).willReturn(existingUser);
            given(transactionManager.persist(any(User.class))).willReturn(deletedUser);

            // when
            service.execute(command);

            // then
            verify(readManager).getById(UserId.of(command.userId()));
            verify(transactionManager).persist(any(User.class));
        }
    }
}
