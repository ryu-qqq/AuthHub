package com.ryuqq.authhub.application.rolepermission.internal;

import com.ryuqq.authhub.application.permission.validator.PermissionValidator;
import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.application.rolepermission.factory.RolePermissionCommandFactory;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionReadManager;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * GrantRolePermissionCoordinator - 역할 권한 부여 Coordinator
 *
 * <p>크로스 도메인 검증 및 RolePermission 생성을 조율합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>Role 존재 검증 (RoleValidator)
 *   <li>Permission 일괄 존재 검증 (IN절 조회)
 *   <li>이미 부여된 RolePermission 필터링 (IN절 최적화)
 *   <li>Factory를 통한 RolePermission 목록 생성
 * </ul>
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>Role 존재 여부 검증
 *   <li>Permission IN절 일괄 조회 및 검증
 *   <li>요청된 permissionIds 중 이미 부여된 것만 조회 (IN절 최적화)
 *   <li>이미 부여된 권한 필터링
 *   <li>Factory.createAll()로 RolePermission 목록 생성
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class GrantRolePermissionCoordinator {

    private final RoleValidator roleValidator;
    private final PermissionValidator permissionValidator;
    private final RolePermissionReadManager rolePermissionReadManager;
    private final RolePermissionCommandFactory commandFactory;

    public GrantRolePermissionCoordinator(
            RoleValidator roleValidator,
            PermissionValidator permissionValidator,
            RolePermissionReadManager rolePermissionReadManager,
            RolePermissionCommandFactory commandFactory) {
        this.roleValidator = roleValidator;
        this.permissionValidator = permissionValidator;
        this.rolePermissionReadManager = rolePermissionReadManager;
        this.commandFactory = commandFactory;
    }

    /**
     * 역할 권한 부여 조율
     *
     * <p>검증 → 필터링 → RolePermission 생성을 한 번에 처리합니다.
     *
     * @param roleId 역할 ID (Long)
     * @param permissionIds 권한 ID 목록 (Long 리스트)
     * @return 생성된 RolePermission 목록 (이미 부여된 권한은 제외됨)
     */
    public List<RolePermission> coordinate(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return List.of();
        }

        RoleId roleIdVo = RoleId.of(roleId);
        List<PermissionId> permissionIdVos = permissionIds.stream().map(PermissionId::of).toList();

        // 1. Role 존재 검증
        roleValidator.findExistingOrThrow(roleIdVo);

        // 2. Permission IN절 일괄 조회 및 검증
        permissionValidator.validateAllExist(permissionIdVos);

        // 3. 이미 부여된 권한 필터링 (IN절 최적화)
        List<PermissionId> newPermissionIds = filterNotGranted(roleIdVo, permissionIdVos);

        if (newPermissionIds.isEmpty()) {
            return List.of();
        }

        // 4. Factory로 RolePermission 목록 생성
        return commandFactory.createAll(roleIdVo, newPermissionIds);
    }

    /**
     * 이미 부여된 권한 필터링 (IN절 최적화)
     *
     * <p>요청된 permissionIds 중 이미 부여된 것만 조회하여 필터링합니다. 해당 Role의 모든 권한을 조회하지 않고, 요청된 것만 확인하여 성능을
     * 최적화합니다.
     *
     * <p><strong>쿼리 최적화:</strong>
     *
     * <pre>
     * Before: SELECT * FROM role_permissions WHERE role_id = ?
     *         → 해당 Role의 모든 권한 조회 (수백 개 가능)
     *
     * After:  SELECT permission_id FROM role_permissions
     *         WHERE role_id = ? AND permission_id IN (?,?,?)
     *         → 요청된 것 중 이미 존재하는 것만 조회
     * </pre>
     *
     * @param roleId 역할 ID
     * @param permissionIds 요청된 권한 ID 목록
     * @return 아직 부여되지 않은 권한 ID 목록
     */
    private List<PermissionId> filterNotGranted(RoleId roleId, List<PermissionId> permissionIds) {
        // 요청된 permissionIds 중 이미 부여된 것만 조회 (IN절 최적화)
        List<PermissionId> grantedPermissionIds =
                rolePermissionReadManager.findGrantedPermissionIds(roleId, permissionIds);

        // 이미 부여된 permissionId Set
        Set<Long> grantedIds =
                grantedPermissionIds.stream().map(PermissionId::value).collect(Collectors.toSet());

        // 아직 부여되지 않은 permissionId만 필터링
        return permissionIds.stream().filter(pid -> !grantedIds.contains(pid.value())).toList();
    }
}
