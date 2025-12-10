package com.ryuqq.authhub.application.role.service.command;

import com.ryuqq.authhub.application.role.assembler.RolePermissionAssembler;
import com.ryuqq.authhub.application.role.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.response.RolePermissionResponse;
import com.ryuqq.authhub.application.role.factory.command.RolePermissionCommandFactory;
import com.ryuqq.authhub.application.role.manager.command.RolePermissionTransactionManager;
import com.ryuqq.authhub.application.role.manager.query.RolePermissionReadManager;
import com.ryuqq.authhub.application.role.port.in.command.GrantRolePermissionUseCase;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.exception.DuplicateRolePermissionException;
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
 *   <li>Factory → Manager → Assembler 흐름
 *   <li>{@code @Transactional} 금지 (Manager에서 처리)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GrantRolePermissionService implements GrantRolePermissionUseCase {

    private final RolePermissionCommandFactory commandFactory;
    private final RolePermissionTransactionManager transactionManager;
    private final RolePermissionReadManager readManager;
    private final RolePermissionAssembler assembler;

    public GrantRolePermissionService(
            RolePermissionCommandFactory commandFactory,
            RolePermissionTransactionManager transactionManager,
            RolePermissionReadManager readManager,
            RolePermissionAssembler assembler) {
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public RolePermissionResponse execute(GrantRolePermissionCommand command) {
        RoleId roleId = RoleId.of(command.roleId());
        PermissionId permissionId = PermissionId.of(command.permissionId());

        // 1. 중복 부여 검사
        if (readManager.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            throw new DuplicateRolePermissionException(command.roleId(), command.permissionId());
        }

        // 2. Factory: Command → Domain
        RolePermission rolePermission = commandFactory.create(command);

        // 3. Manager: 영속화
        RolePermission saved = transactionManager.persist(rolePermission);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
