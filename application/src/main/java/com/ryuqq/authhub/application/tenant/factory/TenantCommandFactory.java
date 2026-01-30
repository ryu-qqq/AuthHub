package com.ryuqq.authhub.application.tenant.factory;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * TenantCommandFactory - Tenant Command → Domain 변환 Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>C-006: 시간/ID 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 → Factory에 위임.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantCommandFactory {

    private final TimeProvider timeProvider;
    private final IdGeneratorPort idGeneratorPort;

    public TenantCommandFactory(TimeProvider timeProvider, IdGeneratorPort idGeneratorPort) {
        this.timeProvider = timeProvider;
        this.idGeneratorPort = idGeneratorPort;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreateTenantCommand로부터 Tenant 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return Tenant 도메인 객체
     */
    public Tenant create(CreateTenantCommand command) {
        Instant now = timeProvider.now();
        TenantId tenantId = TenantId.forNew(idGeneratorPort.generate());
        return Tenant.create(tenantId, TenantName.of(command.name()), now);
    }

    // ==================== Update Context 생성 ====================

    /**
     * UpdateTenantNameCommand로부터 UpdateContext 생성
     *
     * <p>업데이트에 필요한 ID, TenantName, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<TenantId, TenantName> createNameUpdateContext(
            UpdateTenantNameCommand command) {
        TenantId id = TenantId.of(command.tenantId());
        TenantName newName = TenantName.of(command.name());
        return new UpdateContext<>(id, newName, timeProvider.now());
    }

    // ==================== Status Change Context 생성 ====================

    /**
     * UpdateTenantStatusCommand로부터 StatusChangeContext 생성
     *
     * <p>상태 변경에 필요한 ID와 변경 시간을 한 번에 생성합니다.
     *
     * @param command 상태 변경 Command
     * @return StatusChangeContext (id, changedAt)
     */
    public StatusChangeContext<TenantId> createStatusChangeContext(
            UpdateTenantStatusCommand command) {
        TenantId id = TenantId.of(command.tenantId());
        return new StatusChangeContext<>(id, timeProvider.now());
    }
}
