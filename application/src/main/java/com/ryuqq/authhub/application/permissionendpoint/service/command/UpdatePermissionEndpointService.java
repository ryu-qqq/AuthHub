package com.ryuqq.authhub.application.permissionendpoint.service.command;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.UpdatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.factory.PermissionEndpointCommandFactory;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointCommandManager;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.UpdatePermissionEndpointUseCase;
import com.ryuqq.authhub.application.permissionendpoint.validator.PermissionEndpointValidator;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpointUpdateData;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * UpdatePermissionEndpointService - PermissionEndpoint 수정 Service
 *
 * <p>UpdatePermissionEndpointUseCase를 구현합니다.
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
public class UpdatePermissionEndpointService implements UpdatePermissionEndpointUseCase {

    private final PermissionEndpointValidator validator;
    private final PermissionEndpointCommandFactory commandFactory;
    private final PermissionEndpointCommandManager commandManager;

    public UpdatePermissionEndpointService(
            PermissionEndpointValidator validator,
            PermissionEndpointCommandFactory commandFactory,
            PermissionEndpointCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void update(UpdatePermissionEndpointCommand command) {
        // 1. Factory: Command → UpdateContext (id, updateData, changedAt)
        UpdateContext<PermissionEndpointId, PermissionEndpointUpdateData> context =
                commandFactory.createUpdateContext(command);

        // 2. Validator: PermissionEndpoint 조회 및 존재 여부 검증
        PermissionEndpoint permissionEndpoint = validator.findExistingOrThrow(context.id());

        // 3. Validator: URL 패턴 + HTTP 메서드 중복 검증 (자기 자신 제외)
        validator.validateNoDuplicateExcludeSelf(
                context.id(),
                command.urlPattern(),
                command.httpMethod() != null ? HttpMethod.from(command.httpMethod()) : null);

        // 4. Domain: 수정
        permissionEndpoint.update(context.updateData(), context.changedAt());

        // 5. Manager: 영속화
        commandManager.persist(permissionEndpoint);
    }
}
