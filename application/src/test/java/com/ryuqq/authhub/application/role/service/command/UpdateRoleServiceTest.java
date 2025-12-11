package com.ryuqq.authhub.application.role.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.manager.command.RoleTransactionManager;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotModifiableException;
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
 * UpdateRoleService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateRoleService 단위 테스트")
class UpdateRoleServiceTest {

    @Mock private RoleTransactionManager transactionManager;

    @Mock private RoleReadManager readManager;

    @Mock private RoleAssembler assembler;

    private Clock clock;
    private UpdateRoleService service;

    @BeforeEach
    void setUp() {
        clock = RoleFixture.fixedClock();
        service = new UpdateRoleService(transactionManager, readManager, assembler, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("역할 이름을 성공적으로 변경한다")
        void shouldUpdateNameSuccessfully() {
            // given
            Role existingRole = RoleFixture.create();
            Role updatedRole = RoleFixture.createWithName("UPDATED_ROLE");
            UpdateRoleCommand command =
                    new UpdateRoleCommand(existingRole.roleIdValue(), "UPDATED_ROLE", null);
            RoleResponse expectedResponse =
                    new RoleResponse(
                            updatedRole.roleIdValue(),
                            updatedRole.tenantIdValue(),
                            "UPDATED_ROLE",
                            updatedRole.descriptionValue(),
                            updatedRole.getScope().name(),
                            updatedRole.getType().name(),
                            updatedRole.createdAt(),
                            updatedRole.updatedAt());

            given(readManager.getById(RoleId.of(command.roleId()))).willReturn(existingRole);
            given(transactionManager.persist(any(Role.class))).willReturn(updatedRole);
            given(assembler.toResponse(updatedRole)).willReturn(expectedResponse);

            // when
            RoleResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.name()).isEqualTo("UPDATED_ROLE");
            verify(readManager).getById(RoleId.of(command.roleId()));
            verify(transactionManager).persist(any(Role.class));
        }

        @Test
        @DisplayName("역할 설명을 성공적으로 변경한다")
        void shouldUpdateDescriptionSuccessfully() {
            // given
            Role existingRole = RoleFixture.create();
            UpdateRoleCommand command =
                    new UpdateRoleCommand(existingRole.roleIdValue(), null, "Updated description");
            RoleResponse expectedResponse =
                    new RoleResponse(
                            existingRole.roleIdValue(),
                            existingRole.tenantIdValue(),
                            existingRole.nameValue(),
                            "Updated description",
                            existingRole.getScope().name(),
                            existingRole.getType().name(),
                            existingRole.createdAt(),
                            existingRole.updatedAt());

            given(readManager.getById(RoleId.of(command.roleId()))).willReturn(existingRole);
            given(transactionManager.persist(any(Role.class))).willReturn(existingRole);
            given(assembler.toResponse(any(Role.class))).willReturn(expectedResponse);

            // when
            RoleResponse response = service.execute(command);

            // then
            assertThat(response.description()).isEqualTo("Updated description");
            verify(transactionManager).persist(any(Role.class));
        }

        @Test
        @DisplayName("SYSTEM 역할 수정 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenModifyingSystemRole() {
            // given
            Role systemRole = RoleFixture.createSystemGlobal();
            UpdateRoleCommand command =
                    new UpdateRoleCommand(systemRole.roleIdValue(), "NEW_NAME", null);

            given(readManager.getById(RoleId.of(command.roleId()))).willReturn(systemRole);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(SystemRoleNotModifiableException.class);

            verify(transactionManager, never()).persist(any());
        }
    }
}
