package com.ryuqq.authhub.application.rolepermission.service.command;

import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.application.rolepermission.dto.command.RevokeRolePermissionCommand;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionCommandManager;
import com.ryuqq.authhub.application.rolepermission.port.in.command.RevokeRolePermissionUseCase;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * RevokeRolePermissionService - 역할에서 권한 제거 Service
 *
 * <p>역할에서 하나 이상의 권한을 제거하는 비즈니스 로직을 수행합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>역할 존재 여부 검증 (RoleValidator)
 *   <li>역할-권한 관계 삭제 (존재하지 않는 관계는 무시)
 * </ol>
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class RevokeRolePermissionService implements RevokeRolePermissionUseCase {

    private final RoleValidator roleValidator;
    private final RolePermissionCommandManager commandManager;

    public RevokeRolePermissionService(
            RoleValidator roleValidator, RolePermissionCommandManager commandManager) {
        this.roleValidator = roleValidator;
        this.commandManager = commandManager;
    }

    /**
     * 역할에서 권한 제거
     *
     * @param command 권한 제거 Command (roleId, permissionIds)
     */
    @Override
    public void revoke(RevokeRolePermissionCommand command) {
        // 1. 역할 존재 여부 검증
        RoleId roleId = RoleId.of(command.roleId());
        roleValidator.findExistingOrThrow(roleId);

        // 2. 역할-권한 관계 삭제
        List<PermissionId> permissionIds =
                command.permissionIds().stream().map(PermissionId::of).toList();
        commandManager.deleteAll(roleId, permissionIds);
    }
}
