package com.ryuqq.authhub.application.user.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserStatusCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
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
 * UpdateUserStatusService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserStatusService 단위 테스트")
class UpdateUserStatusServiceTest {

    @Mock private UserTransactionManager transactionManager;

    @Mock private UserReadManager readManager;

    @Mock private UserAssembler assembler;

    private Clock clock;
    private UpdateUserStatusService service;

    @BeforeEach
    void setUp() {
        clock = UserFixture.fixedClock();
        service = new UpdateUserStatusService(transactionManager, readManager, assembler, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("사용자 상태를 성공적으로 변경한다")
        void shouldChangeStatusSuccessfully() {
            // given
            User existingUser = UserFixture.create();
            User updatedUser = UserFixture.createWithStatus(UserStatus.INACTIVE);
            UpdateUserStatusCommand command =
                    new UpdateUserStatusCommand(existingUser.userIdValue(), "INACTIVE");
            UserResponse expectedResponse =
                    new UserResponse(
                            updatedUser.userIdValue(),
                            updatedUser.tenantIdValue(),
                            updatedUser.organizationIdValue(),
                            updatedUser.getIdentifier(),
                            updatedUser.getPhoneNumber(),
                            "INACTIVE",
                            updatedUser.getCreatedAt(),
                            updatedUser.getUpdatedAt());

            given(readManager.getById(UserId.of(command.userId()))).willReturn(existingUser);
            given(transactionManager.persist(any(User.class))).willReturn(updatedUser);
            given(assembler.toResponse(updatedUser)).willReturn(expectedResponse);

            // when
            UserResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.status()).isEqualTo("INACTIVE");
            verify(readManager).getById(UserId.of(command.userId()));
            verify(transactionManager).persist(any(User.class));
            verify(assembler).toResponse(updatedUser);
        }
    }
}
