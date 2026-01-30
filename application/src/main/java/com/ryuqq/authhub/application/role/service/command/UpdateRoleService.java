package com.ryuqq.authhub.application.role.service.command;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.application.role.factory.RoleCommandFactory;
import com.ryuqq.authhub.application.role.manager.RoleCommandManager;
import com.ryuqq.authhub.application.role.port.in.command.UpdateRoleUseCase;
import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.aggregate.RoleUpdateData;
import com.ryuqq.authhub.domain.role.id.RoleId;
import org.springframework.stereotype.Service;

/**
 * UpdateRoleService - 역할 수정 Service
 *
 * <p>UpdateRoleUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdateRoleService implements UpdateRoleUseCase {

    private final RoleValidator validator;
    private final RoleCommandFactory commandFactory;
    private final RoleCommandManager commandManager;

    public UpdateRoleService(
            RoleValidator validator,
            RoleCommandFactory commandFactory,
            RoleCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateRoleCommand command) {
        // 1. Factory: Command → UpdateContext (id, updateData, changedAt)
        UpdateContext<RoleId, RoleUpdateData> context = commandFactory.createUpdateContext(command);

        // 2. Validator: Role 조회 및 존재 여부 검증
        Role role = validator.findExistingOrThrow(context.id());

        // 3. Domain: 수정 (SYSTEM 역할이면 예외 발생)
        role.update(context.updateData(), context.changedAt());

        // 4. Manager: 영속화
        commandManager.persist(role);
    }
}
