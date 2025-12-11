package com.ryuqq.authhub.application.user.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.assembler.UserRoleAssembler;
import com.ryuqq.authhub.application.user.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.user.dto.response.UserRoleResponse;
import com.ryuqq.authhub.application.user.factory.command.UserRoleCommandFactory;
import com.ryuqq.authhub.application.user.manager.command.UserRoleTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserRoleReadManager;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserRoleException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * AssignUserRoleService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AssignUserRoleService 단위 테스트")
class AssignUserRoleServiceTest {

    @Mock private UserRoleCommandFactory commandFactory;

    @Mock private UserRoleTransactionManager transactionManager;

    @Mock private UserRoleReadManager readManager;

    @Mock private UserRoleAssembler assembler;

    private AssignUserRoleService service;

    @BeforeEach
    void setUp() {
        service =
                new AssignUserRoleService(
                        commandFactory, transactionManager, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("역할을 성공적으로 할당한다")
        void shouldAssignRoleSuccessfully() {
            // given
            UUID userId = UserFixture.defaultUUID();
            UUID roleId = UUID.randomUUID();
            Instant assignedAt = Instant.parse("2025-01-01T00:00:00Z");

            AssignUserRoleCommand command = new AssignUserRoleCommand(userId, roleId);
            UserRole newUserRole = UserRole.of(UserId.of(userId), RoleId.of(roleId), assignedAt);
            UserRoleResponse expectedResponse = new UserRoleResponse(userId, roleId, assignedAt);

            given(readManager.existsByUserIdAndRoleId(UserId.of(userId), RoleId.of(roleId)))
                    .willReturn(false);
            given(commandFactory.create(command)).willReturn(newUserRole);
            given(transactionManager.persist(newUserRole)).willReturn(newUserRole);
            given(assembler.toResponse(newUserRole)).willReturn(expectedResponse);

            // when
            UserRoleResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(readManager).existsByUserIdAndRoleId(UserId.of(userId), RoleId.of(roleId));
            verify(commandFactory).create(command);
            verify(transactionManager).persist(newUserRole);
            verify(assembler).toResponse(newUserRole);
        }

        @Test
        @DisplayName("중복 역할 할당 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateRole() {
            // given
            UUID userId = UserFixture.defaultUUID();
            UUID roleId = UUID.randomUUID();
            AssignUserRoleCommand command = new AssignUserRoleCommand(userId, roleId);

            given(readManager.existsByUserIdAndRoleId(UserId.of(userId), RoleId.of(roleId)))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateUserRoleException.class);

            verify(commandFactory, never()).create(command);
            verify(transactionManager, never()).persist(UserRole.class.cast(null));
            verify(assembler, never()).toResponse(UserRole.class.cast(null));
        }
    }
}
