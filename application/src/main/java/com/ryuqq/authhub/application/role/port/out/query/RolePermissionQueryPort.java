package com.ryuqq.authhub.application.role.port.out.query;

import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import java.util.List;
import java.util.Optional;

/**
 * RolePermissionQueryPort - 역할 권한 조회 Port (Port-Out)
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Domain 객체 반환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RolePermissionQueryPort {

    /**
     * 역할 ID와 권한 ID로 역할 권한을 조회합니다.
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 역할 권한 (없으면 Optional.empty())
     */
    Optional<RolePermission> findByRoleIdAndPermissionId(RoleId roleId, PermissionId permissionId);

    /**
     * 역할 ID로 모든 권한을 조회합니다.
     *
     * @param roleId 역할 ID
     * @return 역할 권한 목록
     */
    List<RolePermission> findAllByRoleId(RoleId roleId);

    /**
     * 역할에 특정 권한이 부여되어 있는지 확인합니다.
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 부여 여부
     */
    boolean existsByRoleIdAndPermissionId(RoleId roleId, PermissionId permissionId);

    /**
     * 여러 역할 ID로 모든 권한을 조회합니다.
     *
     * @param roleIds 역할 ID Set
     * @return 역할 권한 목록
     */
    List<RolePermission> findAllByRoleIds(java.util.Set<RoleId> roleIds);
}
