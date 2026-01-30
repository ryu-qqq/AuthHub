package com.ryuqq.authhub.application.rolepermission.service.command;

import com.ryuqq.authhub.application.rolepermission.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.rolepermission.internal.GrantRolePermissionCoordinator;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionCommandManager;
import com.ryuqq.authhub.application.rolepermission.port.in.command.GrantRolePermissionUseCase;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GrantRolePermissionService - 역할에 권한 부여 Service
 *
 * <p>역할에 하나 이상의 권한을 부여하는 비즈니스 로직을 수행합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>Coordinator를 통한 검증 및 RolePermission 생성
 *   <li>Manager를 통한 저장
 * </ol>
 *
 * <p><strong>Coordinator 책임:</strong>
 *
 * <ul>
 *   <li>Role 존재 검증
 *   <li>Permission IN절 일괄 조회 및 검증
 *   <li>이미 부여된 권한 필터링
 *   <li>Factory를 통한 RolePermission 목록 생성
 * </ul>
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GrantRolePermissionService implements GrantRolePermissionUseCase {

    private final GrantRolePermissionCoordinator coordinator;
    private final RolePermissionCommandManager commandManager;

    public GrantRolePermissionService(
            GrantRolePermissionCoordinator coordinator,
            RolePermissionCommandManager commandManager) {
        this.coordinator = coordinator;
        this.commandManager = commandManager;
    }

    /**
     * 역할에 권한 부여
     *
     * @param command 권한 부여 Command (roleId, permissionIds)
     */
    @Override
    public void grant(GrantRolePermissionCommand command) {
        // 1. Coordinator: 검증 + 필터링 + RolePermission 생성
        List<RolePermission> rolePermissions =
                coordinator.coordinate(command.roleId(), command.permissionIds());

        if (rolePermissions.isEmpty()) {
            return; // 새로 부여할 권한이 없으면 종료
        }

        // 2. 저장
        commandManager.persistAll(rolePermissions);
    }
}
