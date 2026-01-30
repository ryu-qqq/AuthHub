package com.ryuqq.authhub.application.permissionendpoint.service.command;

import com.ryuqq.authhub.application.permissionendpoint.dto.command.CreatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.factory.PermissionEndpointCommandFactory;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointCommandManager;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.CreatePermissionEndpointUseCase;
import com.ryuqq.authhub.application.permissionendpoint.validator.PermissionEndpointValidator;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * CreatePermissionEndpointService - PermissionEndpoint 생성 Service
 *
 * <p>CreatePermissionEndpointUseCase를 구현합니다.
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
public class CreatePermissionEndpointService implements CreatePermissionEndpointUseCase {

    private final PermissionEndpointValidator validator;
    private final PermissionEndpointCommandFactory commandFactory;
    private final PermissionEndpointCommandManager commandManager;

    public CreatePermissionEndpointService(
            PermissionEndpointValidator validator,
            PermissionEndpointCommandFactory commandFactory,
            PermissionEndpointCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long create(CreatePermissionEndpointCommand command) {
        // 1. Validator: Permission 존재 여부 검증
        validator.validatePermissionExists(PermissionId.of(command.permissionId()));

        // 2. Validator: URL 패턴 + HTTP 메서드 중복 검증
        validator.validateNoDuplicate(command.urlPattern(), HttpMethod.from(command.httpMethod()));

        // 3. Factory: Command → Domain 객체 생성
        PermissionEndpoint permissionEndpoint = commandFactory.create(command);

        // 4. Manager: 영속화 → ID 반환
        return commandManager.persist(permissionEndpoint);
    }
}
