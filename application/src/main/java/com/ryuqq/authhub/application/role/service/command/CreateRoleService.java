package com.ryuqq.authhub.application.role.service.command;

import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.factory.RoleCommandFactory;
import com.ryuqq.authhub.application.role.manager.RoleCommandManager;
import com.ryuqq.authhub.application.role.port.in.command.CreateRoleUseCase;
import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import org.springframework.stereotype.Service;

/**
 * CreateRoleService - 역할 생성 Service
 *
 * <p>CreateRoleUseCase를 구현합니다.
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
public class CreateRoleService implements CreateRoleUseCase {

    private final RoleValidator validator;
    private final RoleCommandFactory commandFactory;
    private final RoleCommandManager commandManager;

    public CreateRoleService(
            RoleValidator validator,
            RoleCommandFactory commandFactory,
            RoleCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(CreateRoleCommand command) {
        // 1. Validator: 역할 이름 중복 검증 (테넌트 + 서비스 범위 내)
        TenantId tenantId = TenantId.fromNullable(command.tenantId());
        ServiceId serviceId = ServiceId.fromNullable(command.serviceId());
        RoleName roleName = RoleName.of(command.name());
        validator.validateNameNotDuplicated(tenantId, serviceId, roleName);

        // 2. Factory: Command → Domain 생성
        Role role = commandFactory.create(command);

        // 3. Manager: 영속화 및 ID 반환
        return commandManager.persist(role);
    }
}
