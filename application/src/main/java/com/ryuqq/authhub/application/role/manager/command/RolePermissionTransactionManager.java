package com.ryuqq.authhub.application.role.manager.command;

import com.ryuqq.authhub.application.role.port.out.persistence.RolePermissionPersistencePort;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RolePermissionTransactionManager - 역할 권한 트랜잭션 매니저
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Transactional} 어노테이션은 Manager에서만 사용
 *   <li>Port-Out 의존
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionTransactionManager {

    private final RolePermissionPersistencePort persistencePort;

    public RolePermissionTransactionManager(RolePermissionPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * 역할 권한을 저장합니다.
     *
     * @param rolePermission 역할 권한 도메인 객체
     * @return 저장된 역할 권한
     */
    @Transactional
    public RolePermission persist(RolePermission rolePermission) {
        return persistencePort.save(rolePermission);
    }

    /**
     * 역할 권한을 삭제합니다.
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     */
    @Transactional
    public void delete(RoleId roleId, PermissionId permissionId) {
        persistencePort.delete(roleId, permissionId);
    }
}
