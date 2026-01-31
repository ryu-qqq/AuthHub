package com.ryuqq.authhub.application.tenant.service.command;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.factory.TenantCommandFactory;
import com.ryuqq.authhub.application.tenant.manager.TenantCommandManager;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantStatusUseCase;
import com.ryuqq.authhub.application.tenant.validator.TenantValidator;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import org.springframework.stereotype.Service;

/**
 * UpdateTenantStatusService - 테넌트 상태 변경 Service
 *
 * <p>UpdateTenantStatusUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>APP-VAL-001: Validator의 findExistingOrThrow 메서드로 Domain 객체를 조회합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdateTenantStatusService implements UpdateTenantStatusUseCase {

    private final TenantValidator validator;
    private final TenantCommandFactory commandFactory;
    private final TenantCommandManager commandManager;

    public UpdateTenantStatusService(
            TenantValidator validator,
            TenantCommandFactory commandFactory,
            TenantCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateTenantStatusCommand command) {
        // 1. Factory: StatusChangeContext 생성 (id, changedAt 번들)
        StatusChangeContext<TenantId> context = commandFactory.createStatusChangeContext(command);

        // 2. VO: 대상 상태 변환
        TenantStatus targetStatus = TenantStatus.valueOf(command.status());

        // 3. Validator: 기존 엔티티 조회 (없으면 예외)
        Tenant tenant = validator.findExistingOrThrow(context.id());

        // 4. Domain: 상태 변경 적용
        tenant.changeStatus(targetStatus, context.changedAt());

        // 5. Manager: 영속화
        commandManager.persist(tenant);
    }
}
