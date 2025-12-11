package com.ryuqq.authhub.application.user.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserIdentifierException;
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
 * UpdateUserService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserService 단위 테스트")
class UpdateUserServiceTest {

    @Mock private UserTransactionManager transactionManager;

    @Mock private UserReadManager readManager;

    @Mock private UserAssembler assembler;

    private Clock clock;
    private UpdateUserService service;

    @BeforeEach
    void setUp() {
        clock = UserFixture.fixedClock();
        service = new UpdateUserService(transactionManager, readManager, assembler, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("사용자 식별자를 성공적으로 변경한다")
        void shouldUpdateIdentifierSuccessfully() {
            // given
            User existingUser = UserFixture.create();
            String newIdentifier = "newuser@example.com";
            UpdateUserCommand command =
                    new UpdateUserCommand(existingUser.userIdValue(), newIdentifier);
            User updatedUser = UserFixture.createWithIdentifier(newIdentifier);
            UserResponse expectedResponse =
                    new UserResponse(
                            updatedUser.userIdValue(),
                            updatedUser.tenantIdValue(),
                            updatedUser.organizationIdValue(),
                            newIdentifier,
                            updatedUser.getUserStatus().name(),
                            updatedUser.getCreatedAt(),
                            updatedUser.getUpdatedAt());

            given(readManager.getById(UserId.of(command.userId()))).willReturn(existingUser);
            given(
                            readManager.existsByTenantIdAndOrganizationIdAndIdentifier(
                                    any(TenantId.class),
                                    any(OrganizationId.class),
                                    eq(newIdentifier)))
                    .willReturn(false);
            given(transactionManager.persist(any(User.class))).willReturn(updatedUser);
            given(assembler.toResponse(updatedUser)).willReturn(expectedResponse);

            // when
            UserResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.identifier()).isEqualTo(newIdentifier);
            verify(readManager).getById(UserId.of(command.userId()));
            verify(transactionManager).persist(any(User.class));
        }

        @Test
        @DisplayName("중복 식별자 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateIdentifier() {
            // given
            User existingUser = UserFixture.create();
            String duplicateIdentifier = "existing@example.com";
            UpdateUserCommand command =
                    new UpdateUserCommand(existingUser.userIdValue(), duplicateIdentifier);

            given(readManager.getById(UserId.of(command.userId()))).willReturn(existingUser);
            given(
                            readManager.existsByTenantIdAndOrganizationIdAndIdentifier(
                                    any(TenantId.class),
                                    any(OrganizationId.class),
                                    eq(duplicateIdentifier)))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateUserIdentifierException.class);

            verify(transactionManager, never()).persist(any());
            verify(assembler, never()).toResponse(any());
        }

        @Test
        @DisplayName("식별자가 null이면 변경하지 않고 저장한다")
        void shouldNotChangeIdentifierWhenNull() {
            // given
            User existingUser = UserFixture.create();
            UpdateUserCommand command = new UpdateUserCommand(existingUser.userIdValue(), null);
            UserResponse expectedResponse =
                    new UserResponse(
                            existingUser.userIdValue(),
                            existingUser.tenantIdValue(),
                            existingUser.organizationIdValue(),
                            existingUser.getIdentifier(),
                            existingUser.getUserStatus().name(),
                            existingUser.getCreatedAt(),
                            existingUser.getUpdatedAt());

            given(readManager.getById(UserId.of(command.userId()))).willReturn(existingUser);
            given(transactionManager.persist(existingUser)).willReturn(existingUser);
            given(assembler.toResponse(existingUser)).willReturn(expectedResponse);

            // when
            UserResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(readManager, never())
                    .existsByTenantIdAndOrganizationIdAndIdentifier(any(), any(), any());
        }
    }
}
