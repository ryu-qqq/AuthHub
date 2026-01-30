package com.ryuqq.authhub.application.rolepermission.manager;

import com.ryuqq.authhub.application.rolepermission.port.out.command.RolePermissionCommandPort;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RolePermissionCommandManager - 역할-권한 관계 Command Manager
 *
 * <p>역할-권한 관계 Command 작업을 관리합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>Command 트랜잭션 관리 (@Transactional)
 *   <li>PersistencePort 위임
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionCommandManager {

    private final RolePermissionCommandPort persistencePort;

    public RolePermissionCommandManager(RolePermissionCommandPort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * 역할-권한 관계 저장
     *
     * @param rolePermission 저장할 역할-권한 관계
     * @return 저장된 역할-권한 관계
     */
    @Transactional
    public RolePermission persist(RolePermission rolePermission) {
        return persistencePort.persist(rolePermission);
    }

    /**
     * 역할-권한 관계 다건 저장
     *
     * @param rolePermissions 저장할 역할-권한 관계 목록
     * @return 저장된 역할-권한 관계 목록
     */
    @Transactional
    public List<RolePermission> persistAll(List<RolePermission> rolePermissions) {
        return persistencePort.persistAll(rolePermissions);
    }

    /**
     * 역할-권한 관계 삭제
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     */
    @Transactional
    public void delete(RoleId roleId, PermissionId permissionId) {
        persistencePort.delete(roleId, permissionId);
    }

    /**
     * 역할의 모든 권한 관계 삭제 (Role 삭제 시 Cascade)
     *
     * @param roleId 역할 ID
     */
    @Transactional
    public void deleteAllByRoleId(RoleId roleId) {
        persistencePort.deleteAllByRoleId(roleId);
    }

    /**
     * 역할-권한 관계 다건 삭제
     *
     * @param roleId 역할 ID
     * @param permissionIds 삭제할 권한 ID 목록
     */
    @Transactional
    public void deleteAll(RoleId roleId, List<PermissionId> permissionIds) {
        persistencePort.deleteAll(roleId, permissionIds);
    }
}
