package com.ryuqq.authhub.application.tenantservice.service.command;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.tenantservice.dto.command.UpdateTenantServiceStatusCommand;
import com.ryuqq.authhub.application.tenantservice.factory.TenantServiceCommandFactory;
import com.ryuqq.authhub.application.tenantservice.manager.TenantServiceCommandManager;
import com.ryuqq.authhub.application.tenantservice.port.in.command.UpdateTenantServiceStatusUseCase;
import com.ryuqq.authhub.application.tenantservice.validator.TenantServiceValidator;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import org.springframework.stereotype.Service;

/**
 * UpdateTenantServiceStatusService - 테넌트 서비스 구독 상태 변경 Service
 *
 * <p>UpdateTenantServiceStatusUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 -> Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 -> 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>APP-VAL-001: Validator의 findExistingOrThrow 메서드로 Domain 객체를 조회합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdateTenantServiceStatusService implements UpdateTenantServiceStatusUseCase {

    private final TenantServiceValidator validator;
    private final TenantServiceCommandFactory commandFactory;
    private final TenantServiceCommandManager commandManager;

    public UpdateTenantServiceStatusService(
            TenantServiceValidator validator,
            TenantServiceCommandFactory commandFactory,
            TenantServiceCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateTenantServiceStatusCommand command) {
        // 1. Factory: StatusChangeContext 생성 (id, changedAt 번들)
        StatusChangeContext<TenantServiceId> context =
                commandFactory.createStatusChangeContext(command);

        // 2. VO: 대상 상태 변환
        TenantServiceStatus targetStatus =
                TenantServiceStatus.valueOf(command.status().toUpperCase());

        // 3. Validator: 기존 엔티티 조회 (없으면 예외)
        TenantService tenantService = validator.findExistingOrThrow(context.id());

        // 4. Domain: 상태 변경 적용
        tenantService.changeStatus(targetStatus, context.changedAt());

        // 5. Manager: 영속화
        commandManager.persist(tenantService);
    }
}
