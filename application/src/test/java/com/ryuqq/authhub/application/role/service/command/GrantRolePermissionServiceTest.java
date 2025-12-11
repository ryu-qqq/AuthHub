package com.ryuqq.authhub.application.role.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.role.assembler.RolePermissionAssembler;
import com.ryuqq.authhub.application.role.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.response.RolePermissionResponse;
import com.ryuqq.authhub.application.role.factory.command.RolePermissionCommandFactory;
import com.ryuqq.authhub.application.role.manager.command.RolePermissionTransactionManager;
import com.ryuqq.authhub.application.role.manager.query.RolePermissionReadManager;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.exception.DuplicateRolePermissionException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
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
 * GrantRolePermissionService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GrantRolePermissionService 단위 테스트")
class GrantRolePermissionServiceTest {

    @Mock private RolePermissionCommandFactory commandFactory;

    @Mock private RolePermissionTransactionManager transactionManager;

    @Mock private RolePermissionReadManager readManager;

    @Mock private RolePermissionAssembler assembler;

    private GrantRolePermissionService service;

    @BeforeEach
    void setUp() {
        service =
                new GrantRolePermissionService(
                        commandFactory, transactionManager, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("역할에 권한을 성공적으로 부여한다")
        void shouldGrantPermissionSuccessfully() {
            // given
            UUID roleId = RoleFixture.defaultUUID();
            UUID permissionId = UUID.randomUUID();
            Instant grantedAt = Instant.parse("2025-01-01T00:00:00Z");

            GrantRolePermissionCommand command =
                    new GrantRolePermissionCommand(roleId, permissionId);
            RolePermission rolePermission =
                    RolePermission.of(RoleId.of(roleId), PermissionId.of(permissionId), grantedAt);
            RolePermissionResponse expectedResponse =
                    new RolePermissionResponse(roleId, permissionId, grantedAt);

            given(
                            readManager.existsByRoleIdAndPermissionId(
                                    RoleId.of(roleId), PermissionId.of(permissionId)))
                    .willReturn(false);
            given(commandFactory.create(command)).willReturn(rolePermission);
            given(transactionManager.persist(rolePermission)).willReturn(rolePermission);
            given(assembler.toResponse(rolePermission)).willReturn(expectedResponse);

            // when
            RolePermissionResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.roleId()).isEqualTo(roleId);
            assertThat(response.permissionId()).isEqualTo(permissionId);
            verify(commandFactory).create(command);
            verify(transactionManager).persist(rolePermission);
        }

        @Test
        @DisplayName("중복 권한 부여 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicatePermission() {
            // given
            UUID roleId = RoleFixture.defaultUUID();
            UUID permissionId = UUID.randomUUID();
            GrantRolePermissionCommand command =
                    new GrantRolePermissionCommand(roleId, permissionId);

            given(
                            readManager.existsByRoleIdAndPermissionId(
                                    RoleId.of(roleId), PermissionId.of(permissionId)))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateRolePermissionException.class);

            verify(commandFactory, never()).create(command);
            verify(transactionManager, never()).persist(RolePermission.class.cast(null));
        }
    }
}
