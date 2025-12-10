package com.ryuqq.authhub.application.role.manager.query;

import com.ryuqq.authhub.application.role.port.out.query.RolePermissionQueryPort;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RolePermissionReadManager - 역할 권한 읽기 매니저
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Transactional(readOnly = true)} 적용
 *   <li>Port-Out 의존
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@Transactional(readOnly = true)
public class RolePermissionReadManager {

    private final RolePermissionQueryPort queryPort;

    public RolePermissionReadManager(RolePermissionQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 역할 ID와 권한 ID로 역할 권한을 조회합니다.
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 역할 권한 (없으면 Optional.empty())
     */
    public Optional<RolePermission> findByRoleIdAndPermissionId(
            RoleId roleId, PermissionId permissionId) {
        return queryPort.findByRoleIdAndPermissionId(roleId, permissionId);
    }

    /**
     * 역할 ID로 모든 권한을 조회합니다.
     *
     * @param roleId 역할 ID
     * @return 역할 권한 목록
     */
    public List<RolePermission> findAllByRoleId(RoleId roleId) {
        return queryPort.findAllByRoleId(roleId);
    }

    /**
     * 역할에 특정 권한이 부여되어 있는지 확인합니다.
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 부여 여부
     */
    public boolean existsByRoleIdAndPermissionId(RoleId roleId, PermissionId permissionId) {
        return queryPort.existsByRoleIdAndPermissionId(roleId, permissionId);
    }

    /**
     * 여러 역할 ID로 모든 권한을 조회합니다.
     *
     * @param roleIds 역할 ID Set
     * @return 역할 권한 목록
     */
    public List<RolePermission> findAllByRoleIds(Set<RoleId> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return queryPort.findAllByRoleIds(roleIds);
    }
}
