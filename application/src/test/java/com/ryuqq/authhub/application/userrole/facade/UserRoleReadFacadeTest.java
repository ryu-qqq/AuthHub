package com.ryuqq.authhub.application.userrole.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permission.manager.PermissionReadManager;
import com.ryuqq.authhub.application.role.manager.RoleReadManager;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionReadManager;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.application.userrole.manager.UserRoleReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.fixture.UserRoleFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRoleReadFacade 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleReadFacade 단위 테스트")
class UserRoleReadFacadeTest {

    @Mock private UserRoleReadManager userRoleReadManager;
    @Mock private RoleReadManager roleReadManager;
    @Mock private RolePermissionReadManager rolePermissionReadManager;
    @Mock private PermissionReadManager permissionReadManager;

    private UserRoleReadFacade sut;

    @BeforeEach
    void setUp() {
        sut =
                new UserRoleReadFacade(
                        userRoleReadManager,
                        roleReadManager,
                        rolePermissionReadManager,
                        permissionReadManager);
    }

    @Nested
    @DisplayName("findRolesAndPermissionsByUserId 메서드")
    class FindRolesAndPermissionsByUserId {

        @Test
        @DisplayName("성공: UserRole 있으면 roleNames와 permissionKeys 조립하여 Composite 반환")
        void shouldReturnComposite_WhenUserHasRoles() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            List<UserRole> userRoles = List.of(UserRoleFixture.create());
            List<RoleId> roleIds = List.of(UserRoleFixture.defaultRoleId());
            List<Role> roles = List.of(RoleFixture.create());
            List<RolePermission> rolePermissions = List.of(RolePermissionFixture.create());
            List<Permission> permissions = List.of(PermissionFixture.create());

            given(userRoleReadManager.findAllByUserId(userId)).willReturn(userRoles);
            given(roleReadManager.findAllByIds(roleIds)).willReturn(roles);
            given(rolePermissionReadManager.findAllByRoleIds(roleIds)).willReturn(rolePermissions);
            given(permissionReadManager.findAllByIds(List.of(PermissionFixture.defaultId())))
                    .willReturn(permissions);

            // when
            RolesAndPermissionsComposite result = sut.findRolesAndPermissionsByUserId(userId);

            // then
            assertThat(result.roleNames()).isNotEmpty();
            assertThat(result.permissionKeys()).isNotEmpty();

            then(userRoleReadManager).should().findAllByUserId(userId);
            then(roleReadManager).should().findAllByIds(roleIds);
            then(rolePermissionReadManager).should().findAllByRoleIds(roleIds);
            then(permissionReadManager).should().findAllByIds(anyList());
        }

        @Test
        @DisplayName("UserRole 빈 목록이면 empty Composite 반환")
        void shouldReturnEmptyComposite_WhenUserHasNoRoles() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();

            given(userRoleReadManager.findAllByUserId(userId)).willReturn(List.of());

            // when
            RolesAndPermissionsComposite result = sut.findRolesAndPermissionsByUserId(userId);

            // then
            assertThat(result.roleNames()).isEmpty();
            assertThat(result.permissionKeys()).isEmpty();

            then(userRoleReadManager).should().findAllByUserId(userId);
            then(roleReadManager).shouldHaveNoInteractions();
            then(rolePermissionReadManager).shouldHaveNoInteractions();
            then(permissionReadManager).shouldHaveNoInteractions();
        }
    }
}
