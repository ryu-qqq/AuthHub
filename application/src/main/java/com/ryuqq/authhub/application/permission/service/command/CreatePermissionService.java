package com.ryuqq.authhub.application.permission.service.command;

import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.factory.PermissionCommandFactory;
import com.ryuqq.authhub.application.permission.manager.PermissionCommandManager;
import com.ryuqq.authhub.application.permission.port.in.command.CreatePermissionUseCase;
import com.ryuqq.authhub.application.permission.validator.PermissionValidator;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import org.springframework.stereotype.Service;

/**
 * CreatePermissionService - 권한 생성 Service
 *
 * <p>CreatePermissionUseCase를 구현합니다.
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
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class CreatePermissionService implements CreatePermissionUseCase {

    private final PermissionValidator validator;
    private final PermissionCommandFactory commandFactory;
    private final PermissionCommandManager commandManager;

    public CreatePermissionService(
            PermissionValidator validator,
            PermissionCommandFactory commandFactory,
            PermissionCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(CreatePermissionCommand command) {
        // 1. Factory: Command → Domain (Permission이 내부에서 type 판단)
        Permission permission = commandFactory.create(command);

        // 2. Validator: 서비스 내 권한 키 중복 검증
        validator.validateKeyNotDuplicated(
                permission.getServiceId(), permission.permissionKeyValue());

        // 3. Manager: 영속화 → ID 반환
        return commandManager.persist(permission);
    }
}
