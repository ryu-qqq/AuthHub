package com.ryuqq.authhub.application.tenantservice.service.command;

import com.ryuqq.authhub.application.tenantservice.dto.command.SubscribeTenantServiceCommand;
import com.ryuqq.authhub.application.tenantservice.factory.TenantServiceCommandFactory;
import com.ryuqq.authhub.application.tenantservice.manager.TenantServiceCommandManager;
import com.ryuqq.authhub.application.tenantservice.port.in.command.SubscribeTenantServiceUseCase;
import com.ryuqq.authhub.application.tenantservice.validator.TenantServiceValidator;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;

/**
 * SubscribeTenantServiceService - 테넌트 서비스 구독 Service
 *
 * <p>SubscribeTenantServiceUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 -> Factory 사용.
 *
 * <p>SVC-006: @Transactional 금지 -> Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 -> 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 -> Manager 사용.
 *
 * @author development-team
 * @since 1.0.0
 */
@org.springframework.stereotype.Service
public class SubscribeTenantServiceService implements SubscribeTenantServiceUseCase {

    private final TenantServiceValidator validator;
    private final TenantServiceCommandFactory commandFactory;
    private final TenantServiceCommandManager commandManager;

    public SubscribeTenantServiceService(
            TenantServiceValidator validator,
            TenantServiceCommandFactory commandFactory,
            TenantServiceCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(SubscribeTenantServiceCommand command) {
        // 1. Validator: 중복 구독 검증
        TenantId tenantId = TenantId.of(command.tenantId());
        ServiceId serviceId = ServiceId.of(command.serviceId());
        validator.validateNotDuplicated(tenantId, serviceId);

        // 2. Factory: Command -> Domain
        TenantService tenantService = commandFactory.create(command);

        // 3. Manager: 영속화 -> ID 반환
        return commandManager.persist(tenantService);
    }
}
