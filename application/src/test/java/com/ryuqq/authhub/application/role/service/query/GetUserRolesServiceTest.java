package com.ryuqq.authhub.application.role.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.role.manager.query.RolePermissionReadManager;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.application.user.manager.query.UserRoleReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.time.Instant;
import java.util.List;
import java.util.Set;
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
 * GetUserRolesService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserRolesService 단위 테스트")
class GetUserRolesServiceTest {

    @Mock private UserRoleReadManager userRoleReadManager;

    @Mock private RoleReadManager roleReadManager;

    @Mock private RolePermissionReadManager rolePermissionReadManager;

    @Mock private PermissionReadManager permissionReadManager;

    private GetUserRolesService service;

    @BeforeEach
    void setUp() {
        service =
                new GetUserRolesService(
                        userRoleReadManager,
                        roleReadManager,
                        rolePermissionReadManager,
                        permissionReadManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("사용자 역할과 권한을 성공적으로 조회한다")
        void shouldRetrieveUserRolesAndPermissionsSuccessfully() {
            // given
            UUID userId = UserFixture.defaultUUID();
            UserId userIdVo = UserId.of(userId);

            RoleId roleId = RoleFixture.defaultId();
            Role role = RoleFixture.create();

            PermissionId permissionId = PermissionFixture.createPermissionId();
            Permission permission = PermissionFixture.create();

            UserRole userRole =
                    UserRole.of(userIdVo, roleId, Instant.parse("2025-01-01T00:00:00Z"));
            RolePermission rolePermission =
                    RolePermission.of(roleId, permissionId, Instant.parse("2025-01-01T00:00:00Z"));

            given(userRoleReadManager.findAllByUserId(userIdVo)).willReturn(List.of(userRole));
            given(roleReadManager.findAllByIds(Set.of(roleId))).willReturn(List.of(role));
            given(rolePermissionReadManager.findAllByRoleIds(Set.of(roleId)))
                    .willReturn(List.of(rolePermission));
            given(permissionReadManager.findAllByIds(Set.of(permissionId)))
                    .willReturn(List.of(permission));

            // when
            UserRolesResponse response = service.execute(userId);

            // then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.roles()).contains(role.nameValue());
            assertThat(response.permissions()).contains(permission.keyValue());
            verify(userRoleReadManager).findAllByUserId(userIdVo);
            verify(roleReadManager).findAllByIds(Set.of(roleId));
        }

        @Test
        @DisplayName("역할이 없으면 빈 응답을 반환한다")
        void shouldReturnEmptyResponseWhenNoRoles() {
            // given
            UUID userId = UserFixture.defaultUUID();
            UserId userIdVo = UserId.of(userId);

            given(userRoleReadManager.findAllByUserId(userIdVo)).willReturn(List.of());

            // when
            UserRolesResponse response = service.execute(userId);

            // then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.roles()).isEmpty();
            assertThat(response.permissions()).isEmpty();
            verifyNoInteractions(roleReadManager);
            verifyNoInteractions(rolePermissionReadManager);
            verifyNoInteractions(permissionReadManager);
        }

        @Test
        @DisplayName("역할에 권한이 없으면 권한 빈 응답을 반환한다")
        void shouldReturnEmptyPermissionsWhenNoPermissions() {
            // given
            UUID userId = UserFixture.defaultUUID();
            UserId userIdVo = UserId.of(userId);

            RoleId roleId = RoleFixture.defaultId();
            Role role = RoleFixture.create();

            UserRole userRole =
                    UserRole.of(userIdVo, roleId, Instant.parse("2025-01-01T00:00:00Z"));

            given(userRoleReadManager.findAllByUserId(userIdVo)).willReturn(List.of(userRole));
            given(roleReadManager.findAllByIds(Set.of(roleId))).willReturn(List.of(role));
            given(rolePermissionReadManager.findAllByRoleIds(Set.of(roleId))).willReturn(List.of());

            // when
            UserRolesResponse response = service.execute(userId);

            // then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.roles()).contains(role.nameValue());
            assertThat(response.permissions()).isEmpty();
            verifyNoInteractions(permissionReadManager);
        }
    }
}
