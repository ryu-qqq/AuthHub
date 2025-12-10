package com.ryuqq.authhub.application.role.service.query;

import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.role.manager.query.RolePermissionReadManager;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.application.role.port.in.GetUserRolesUseCase;
import com.ryuqq.authhub.application.user.manager.query.UserRoleReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * GetUserRolesService - 사용자 역할/권한 조회 Service
 *
 * <p>GetUserRolesUseCase를 구현합니다.
 *
 * <p>Gateway에서 사용자의 권한을 조회할 때 사용됩니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>Manager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetUserRolesService implements GetUserRolesUseCase {

    private final UserRoleReadManager userRoleReadManager;
    private final RoleReadManager roleReadManager;
    private final RolePermissionReadManager rolePermissionReadManager;
    private final PermissionReadManager permissionReadManager;

    public GetUserRolesService(
            UserRoleReadManager userRoleReadManager,
            RoleReadManager roleReadManager,
            RolePermissionReadManager rolePermissionReadManager,
            PermissionReadManager permissionReadManager) {
        this.userRoleReadManager = userRoleReadManager;
        this.roleReadManager = roleReadManager;
        this.rolePermissionReadManager = rolePermissionReadManager;
        this.permissionReadManager = permissionReadManager;
    }

    @Override
    public UserRolesResponse execute(UUID userId) {
        UserId userIdVo = UserId.of(userId);

        // 1. 사용자의 역할 목록 조회
        List<UserRole> userRoles = userRoleReadManager.findAllByUserId(userIdVo);
        if (userRoles.isEmpty()) {
            return UserRolesResponse.empty(userId);
        }

        // 2. 역할 ID Set 추출
        Set<RoleId> roleIds =
                userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());

        // 3. 역할 조회 및 역할 이름 추출
        List<Role> roles = roleReadManager.findAllByIds(roleIds);
        Set<String> roleNames = roles.stream().map(Role::nameValue).collect(Collectors.toSet());

        // 4. 역할별 권한 조회
        List<RolePermission> rolePermissions = rolePermissionReadManager.findAllByRoleIds(roleIds);
        if (rolePermissions.isEmpty()) {
            return UserRolesResponse.of(userId, roleNames, Set.of());
        }

        // 5. 권한 ID Set 추출
        Set<PermissionId> permissionIds =
                rolePermissions.stream()
                        .map(RolePermission::getPermissionId)
                        .collect(Collectors.toSet());

        // 6. 권한 조회 및 권한 키(resource:action) 추출
        List<Permission> permissions = permissionReadManager.findAllByIds(permissionIds);
        Set<String> permissionKeys =
                permissions.stream().map(Permission::keyValue).collect(Collectors.toSet());

        return UserRolesResponse.of(userId, roleNames, permissionKeys);
    }
}
