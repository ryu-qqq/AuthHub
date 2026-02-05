package com.ryuqq.authhub.application.tenantservice.factory;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.tenantservice.dto.command.SubscribeTenantServiceCommand;
import com.ryuqq.authhub.application.tenantservice.dto.command.UpdateTenantServiceStatusCommand;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * TenantServiceCommandFactory - TenantService Command -> Domain 변환 Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>C-006: 시간 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 -> Factory에 위임.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantServiceCommandFactory {

    private final TimeProvider timeProvider;

    public TenantServiceCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * SubscribeTenantServiceCommand로부터 TenantService 도메인 객체 생성
     *
     * @param command 구독 Command
     * @return TenantService 도메인 객체
     */
    public TenantService create(SubscribeTenantServiceCommand command) {
        Instant now = timeProvider.now();
        TenantId tenantId = TenantId.of(command.tenantId());
        ServiceId serviceId = ServiceId.of(command.serviceId());
        return TenantService.create(tenantId, serviceId, now);
    }

    /**
     * UpdateTenantServiceStatusCommand로부터 StatusChangeContext 생성
     *
     * <p>C-006: 시간 생성은 Factory에서만 허용됩니다.
     *
     * @param command 상태 변경 Command
     * @return StatusChangeContext (id + changedAt 번들)
     */
    public StatusChangeContext<TenantServiceId> createStatusChangeContext(
            UpdateTenantServiceStatusCommand command) {
        TenantServiceId id = TenantServiceId.of(command.tenantServiceId());
        return new StatusChangeContext<>(id, timeProvider.now());
    }
}
