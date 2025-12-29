package com.ryuqq.authhub.application.role.service.command;

import com.ryuqq.authhub.application.role.dto.command.RevokeRolePermissionCommand;
import com.ryuqq.authhub.application.role.manager.command.RolePermissionTransactionManager;
import com.ryuqq.authhub.application.role.port.in.command.RevokeRolePermissionUseCase;
import com.ryuqq.authhub.application.role.validator.RolePermissionValidator;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import org.springframework.stereotype.Service;

/**
 * RevokeRolePermissionService - 역할에서 권한 해제 Service
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>UseCase 구현
 *   <li>Validator → Manager 흐름
 *   <li>{@code @Transactional} 금지 (Manager에서 처리)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class RevokeRolePermissionService implements RevokeRolePermissionUseCase {

    private final RolePermissionValidator validator;
    private final RolePermissionTransactionManager transactionManager;

    public RevokeRolePermissionService(
            RolePermissionValidator validator,
            RolePermissionTransactionManager transactionManager) {
        this.validator = validator;
        this.transactionManager = transactionManager;
    }

    @Override
    public void execute(RevokeRolePermissionCommand command) {
        RoleId roleId = RoleId.of(command.roleId());
        PermissionId permissionId = PermissionId.of(command.permissionId());

        // 1. Validator: 존재 여부 검사
        validator.validateExists(roleId, permissionId);

        // 2. Manager: 삭제
        transactionManager.delete(roleId, permissionId);
    }
}
