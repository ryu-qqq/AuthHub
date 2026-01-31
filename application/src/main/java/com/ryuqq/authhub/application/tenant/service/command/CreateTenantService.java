package com.ryuqq.authhub.application.tenant.service.command;

import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.factory.TenantCommandFactory;
import com.ryuqq.authhub.application.tenant.manager.TenantCommandManager;
import com.ryuqq.authhub.application.tenant.port.in.command.CreateTenantUseCase;
import com.ryuqq.authhub.application.tenant.validator.TenantValidator;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.springframework.stereotype.Service;

/**
 * CreateTenantService - 테넌트 생성 Service
 *
 * <p>CreateTenantUseCase를 구현합니다.
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
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class CreateTenantService implements CreateTenantUseCase {

    private final TenantValidator validator;
    private final TenantCommandFactory commandFactory;
    private final TenantCommandManager commandManager;

    public CreateTenantService(
            TenantValidator validator,
            TenantCommandFactory commandFactory,
            TenantCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public String execute(CreateTenantCommand command) {
        // 1. Validator: 중복 이름 검증
        validator.validateNameNotDuplicated(TenantName.of(command.name()));

        // 2. Factory: Command → Domain
        Tenant tenant = commandFactory.create(command);

        // 3. Manager: 영속화 → ID 반환
        return commandManager.persist(tenant);
    }
}
