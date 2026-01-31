package com.ryuqq.authhub.application.userrole.facade;

import com.ryuqq.authhub.application.permission.manager.PermissionReadManager;
import com.ryuqq.authhub.application.role.manager.RoleReadManager;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionReadManager;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.application.userrole.manager.UserRoleReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * UserRoleReadFacade - 사용자 역할/권한 조회 Facade
 *
 * <p>사용자의 역할과 권한 정보를 조회하기 위해 여러 ReadManager를 조율합니다.
 *
 * <p><strong>조회 흐름:</strong>
 *
 * <ol>
 *   <li>UserRoleReadManager: userId → UserRole 목록
 *   <li>RoleReadManager: roleIds → Role 목록 (IN절)
 *   <li>RolePermissionReadManager: roleIds → RolePermission 목록 (IN절)
 *   <li>PermissionReadManager: permissionIds → Permission 목록 (IN절)
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleReadFacade {

    private final UserRoleReadManager userRoleReadManager;
    private final RoleReadManager roleReadManager;
    private final RolePermissionReadManager rolePermissionReadManager;
    private final PermissionReadManager permissionReadManager;

    public UserRoleReadFacade(
            UserRoleReadManager userRoleReadManager,
            RoleReadManager roleReadManager,
            RolePermissionReadManager rolePermissionReadManager,
            PermissionReadManager permissionReadManager) {
        this.userRoleReadManager = userRoleReadManager;
        this.roleReadManager = roleReadManager;
        this.rolePermissionReadManager = rolePermissionReadManager;
        this.permissionReadManager = permissionReadManager;
    }

    /**
     * 사용자 ID로 역할 이름과 권한 키 조회
     *
     * <p>모든 조회를 IN절로 처리하여 N+1 문제를 방지합니다.
     *
     * @param userId 사용자 ID
     * @return 역할 이름과 권한 키를 담은 Composite
     */
    public RolesAndPermissionsComposite findRolesAndPermissionsByUserId(UserId userId) {
        List<UserRole> userRoles = userRoleReadManager.findAllByUserId(userId);

        if (userRoles.isEmpty()) {
            return RolesAndPermissionsComposite.empty();
        }

        List<RoleId> roleIds = userRoles.stream().map(UserRole::getRoleId).toList();

        Set<String> roleNames = findRoleNames(roleIds);
        Set<String> permissionKeys = findPermissionKeys(roleIds);

        return new RolesAndPermissionsComposite(roleNames, permissionKeys);
    }

    private Set<String> findRoleNames(List<RoleId> roleIds) {
        List<Role> roles = roleReadManager.findAllByIds(roleIds);

        return roles.stream()
                .filter(Role::isActive)
                .map(Role::nameValue)
                .collect(Collectors.toSet());
    }

    private Set<String> findPermissionKeys(List<RoleId> roleIds) {
        List<RolePermission> rolePermissions = rolePermissionReadManager.findAllByRoleIds(roleIds);

        if (rolePermissions.isEmpty()) {
            return Set.of();
        }

        List<PermissionId> permissionIds =
                rolePermissions.stream().map(RolePermission::getPermissionId).distinct().toList();

        List<Permission> permissions = permissionReadManager.findAllByIds(permissionIds);

        return permissions.stream()
                .filter(Permission::isActive)
                .map(Permission::permissionKeyValue)
                .collect(Collectors.toSet());
    }
}
