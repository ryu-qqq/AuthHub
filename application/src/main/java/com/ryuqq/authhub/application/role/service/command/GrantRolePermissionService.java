package com.ryuqq.authhub.application.role.service.command;

import com.ryuqq.authhub.application.role.assembler.RolePermissionAssembler;
import com.ryuqq.authhub.application.role.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.response.RolePermissionResponse;
import com.ryuqq.authhub.application.role.factory.command.RolePermissionCommandFactory;
import com.ryuqq.authhub.application.role.manager.command.RolePermissionTransactionManager;
import com.ryuqq.authhub.application.role.port.in.command.GrantRolePermissionUseCase;
import com.ryuqq.authhub.application.role.validator.RolePermissionValidator;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import org.springframework.stereotype.Service;

/**
 * GrantRolePermissionService - 역할에 권한 부여 Service
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>UseCase 구현
 *   <li>Validator → Factory → Manager → Assembler 흐름
 *   <li>{@code @Transactional} 금지 (Manager에서 처리)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GrantRolePermissionService implements GrantRolePermissionUseCase {

    private final RolePermissionValidator validator;
    private final RolePermissionCommandFactory commandFactory;
    private final RolePermissionTransactionManager transactionManager;
    private final RolePermissionAssembler assembler;

    public GrantRolePermissionService(
            RolePermissionValidator validator,
            RolePermissionCommandFactory commandFactory,
            RolePermissionTransactionManager transactionManager,
            RolePermissionAssembler assembler) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.assembler = assembler;
    }

    @Override
    public RolePermissionResponse execute(GrantRolePermissionCommand command) {
        RoleId roleId = RoleId.of(command.roleId());
        PermissionId permissionId = PermissionId.of(command.permissionId());

        // 1. Validator: 중복 부여 검사
        validator.validateNotAlreadyGranted(roleId, permissionId);

        // 2. Factory: Command → Domain
        RolePermission rolePermission = commandFactory.create(command);

        // 3. Manager: 영속화
        RolePermission saved = transactionManager.persist(rolePermission);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
