package com.ryuqq.authhub.application.permission.manager;

import com.ryuqq.authhub.application.permission.port.out.command.PermissionCommandPort;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * PermissionCommandManager - Permission Command 관리자
 *
 * <p>CommandPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionCommandManager {

    private final PermissionCommandPort persistencePort;

    public PermissionCommandManager(PermissionCommandPort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * Permission 영속화
     *
     * @param permission 영속화할 Permission
     * @return 영속화된 Permission ID (Long)
     */
    @Transactional
    public Long persist(Permission permission) {
        return persistencePort.persist(permission);
    }

    /**
     * Permission 다건 영속화 (벌크) 및 permissionKey → ID 매핑 반환
     *
     * <p>벌크 동기화 시 새로운 Permission 목록을 저장하고, permissionKey → ID 매핑을 반환합니다.
     *
     * @param permissions 영속화할 Permission 목록
     * @return permissionKey → permissionId 매핑
     */
    @Transactional
    public Map<String, Long> persistAllAndReturnKeyToIdMap(List<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return Map.of();
        }
        Map<String, Long> result = new HashMap<>();
        for (Permission permission : permissions) {
            Long id = persistencePort.persist(permission);
            result.put(permission.permissionKeyValue(), id);
        }
        return result;
    }
}
