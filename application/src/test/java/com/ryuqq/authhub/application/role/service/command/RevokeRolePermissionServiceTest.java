package com.ryuqq.authhub.application.role.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.role.dto.command.RevokeRolePermissionCommand;
import com.ryuqq.authhub.application.role.manager.command.RolePermissionTransactionManager;
import com.ryuqq.authhub.application.role.manager.query.RolePermissionReadManager;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.exception.RolePermissionNotFoundException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
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
 * RevokeRolePermissionService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RevokeRolePermissionService 단위 테스트")
class RevokeRolePermissionServiceTest {

    @Mock private RolePermissionTransactionManager transactionManager;

    @Mock private RolePermissionReadManager readManager;

    private RevokeRolePermissionService service;

    @BeforeEach
    void setUp() {
        service = new RevokeRolePermissionService(transactionManager, readManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("역할에서 권한을 성공적으로 해제한다")
        void shouldRevokePermissionSuccessfully() {
            // given
            UUID roleId = RoleFixture.defaultUUID();
            UUID permissionId = UUID.randomUUID();
            RevokeRolePermissionCommand command =
                    new RevokeRolePermissionCommand(roleId, permissionId);

            given(
                            readManager.existsByRoleIdAndPermissionId(
                                    RoleId.of(roleId), PermissionId.of(permissionId)))
                    .willReturn(true);

            // when
            service.execute(command);

            // then
            verify(readManager)
                    .existsByRoleIdAndPermissionId(
                            RoleId.of(roleId), PermissionId.of(permissionId));
            verify(transactionManager).delete(RoleId.of(roleId), PermissionId.of(permissionId));
        }

        @Test
        @DisplayName("존재하지 않는 권한 해제 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenPermissionNotFound() {
            // given
            UUID roleId = RoleFixture.defaultUUID();
            UUID permissionId = UUID.randomUUID();
            RevokeRolePermissionCommand command =
                    new RevokeRolePermissionCommand(roleId, permissionId);

            given(
                            readManager.existsByRoleIdAndPermissionId(
                                    RoleId.of(roleId), PermissionId.of(permissionId)))
                    .willReturn(false);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(RolePermissionNotFoundException.class);

            verify(transactionManager, never())
                    .delete(RoleId.of(roleId), PermissionId.of(permissionId));
        }
    }
}
