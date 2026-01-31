package com.ryuqq.authhub.application.permission.service.command;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.application.permission.factory.PermissionCommandFactory;
import com.ryuqq.authhub.application.permission.manager.PermissionCommandManager;
import com.ryuqq.authhub.application.permission.port.in.command.UpdatePermissionUseCase;
import com.ryuqq.authhub.application.permission.validator.PermissionValidator;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.aggregate.PermissionUpdateData;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import org.springframework.stereotype.Service;

/**
 * UpdatePermissionService - 권한 수정 Service
 *
 * <p>UpdatePermissionUseCase를 구현합니다.
 *
 * <p>CUSTOM 권한의 description만 수정할 수 있습니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdatePermissionService implements UpdatePermissionUseCase {

    private final PermissionValidator validator;
    private final PermissionCommandFactory commandFactory;
    private final PermissionCommandManager commandManager;

    public UpdatePermissionService(
            PermissionValidator validator,
            PermissionCommandFactory commandFactory,
            PermissionCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdatePermissionCommand command) {
        // 1. Factory: Command → UpdateContext (id, updateData, changedAt)
        UpdateContext<PermissionId, PermissionUpdateData> context =
                commandFactory.createUpdateContext(command);

        // 2. Validator: Permission 조회 및 존재 여부 검증
        Permission permission = validator.findExistingOrThrow(context.id());

        // 3. Domain: 수정 (SYSTEM 권한이면 예외 발생)
        permission.update(context.updateData(), context.changedAt());

        // 4. Manager: 영속화
        commandManager.persist(permission);
    }
}
