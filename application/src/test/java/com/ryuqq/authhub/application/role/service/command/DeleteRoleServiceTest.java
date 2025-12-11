package com.ryuqq.authhub.application.role.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.manager.command.RoleTransactionManager;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotDeletableException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
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
 * DeleteRoleService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteRoleService 단위 테스트")
class DeleteRoleServiceTest {

    @Mock private RoleTransactionManager transactionManager;

    @Mock private RoleReadManager readManager;

    private Clock clock;
    private DeleteRoleService service;

    @BeforeEach
    void setUp() {
        clock = RoleFixture.fixedClock();
        service = new DeleteRoleService(transactionManager, readManager, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("역할을 성공적으로 삭제한다")
        void shouldDeleteRoleSuccessfully() {
            // given
            Role existingRole = RoleFixture.create();
            Role deletedRole = RoleFixture.createDeleted();
            DeleteRoleCommand command = new DeleteRoleCommand(existingRole.roleIdValue());

            given(readManager.getById(RoleId.of(command.roleId()))).willReturn(existingRole);
            given(transactionManager.persist(any(Role.class))).willReturn(deletedRole);

            // when
            service.execute(command);

            // then
            verify(readManager).getById(RoleId.of(command.roleId()));
            verify(transactionManager).persist(any(Role.class));
        }

        @Test
        @DisplayName("SYSTEM 역할 삭제 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDeletingSystemRole() {
            // given
            Role systemRole = RoleFixture.createSystemGlobal();
            DeleteRoleCommand command = new DeleteRoleCommand(systemRole.roleIdValue());

            given(readManager.getById(RoleId.of(command.roleId()))).willReturn(systemRole);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(SystemRoleNotDeletableException.class);

            verify(transactionManager, never()).persist(any());
        }
    }
}
