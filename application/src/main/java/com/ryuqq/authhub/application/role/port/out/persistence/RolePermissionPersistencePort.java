package com.ryuqq.authhub.application.role.port.out.persistence;

import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;

/**
 * RolePermissionPersistencePort - 역할 권한 영속화 Port (Port-Out)
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Domain 객체 파라미터/반환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RolePermissionPersistencePort {

    /**
     * 역할 권한을 저장합니다.
     *
     * @param rolePermission 역할 권한 도메인 객체
     * @return 저장된 역할 권한
     */
    RolePermission save(RolePermission rolePermission);

    /**
     * 역할 권한을 삭제합니다.
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     */
    void delete(RoleId roleId, PermissionId permissionId);
}
