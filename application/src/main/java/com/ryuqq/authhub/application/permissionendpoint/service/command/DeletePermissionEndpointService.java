package com.ryuqq.authhub.application.permissionendpoint.service.command;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.DeletePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.factory.PermissionEndpointCommandFactory;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointCommandManager;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.DeletePermissionEndpointUseCase;
import com.ryuqq.authhub.application.permissionendpoint.validator.PermissionEndpointValidator;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import org.springframework.stereotype.Service;

/**
 * DeletePermissionEndpointService - PermissionEndpoint 삭제 Service
 *
 * <p>DeletePermissionEndpointUseCase를 구현합니다.
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
public class DeletePermissionEndpointService implements DeletePermissionEndpointUseCase {

    private final PermissionEndpointValidator validator;
    private final PermissionEndpointCommandFactory commandFactory;
    private final PermissionEndpointCommandManager commandManager;

    public DeletePermissionEndpointService(
            PermissionEndpointValidator validator,
            PermissionEndpointCommandFactory commandFactory,
            PermissionEndpointCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void delete(DeletePermissionEndpointCommand command) {
        // 1. Factory: Command → StatusChangeContext (id, changedAt)
        StatusChangeContext<PermissionEndpointId> context =
                commandFactory.createDeleteContext(command);

        // 2. Validator: PermissionEndpoint 조회 및 존재 여부 검증
        PermissionEndpoint permissionEndpoint = validator.findExistingOrThrow(context.id());

        // 3. Domain: 삭제 (Soft Delete)
        permissionEndpoint.delete(context.changedAt());

        // 4. Manager: 영속화
        commandManager.persist(permissionEndpoint);
    }
}
