package com.ryuqq.authhub.application.tenant.service.command;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.manager.command.TenantTransactionManager;
import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantStatusUseCase;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * UpdateTenantStatusService - 테넌트 상태 변경 Service
 *
 * <p>UpdateTenantStatusUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>ReadManager → Domain → TransactionManager 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdateTenantStatusService implements UpdateTenantStatusUseCase {

    private final TenantReadManager readManager;
    private final TenantTransactionManager transactionManager;
    private final TenantAssembler assembler;
    private final Clock clock;

    public UpdateTenantStatusService(
            TenantReadManager readManager,
            TenantTransactionManager transactionManager,
            TenantAssembler assembler,
            Clock clock) {
        this.readManager = readManager;
        this.transactionManager = transactionManager;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    public TenantResponse execute(UpdateTenantStatusCommand command) {
        // 1. 기존 테넌트 조회
        TenantId tenantId = TenantId.of(command.tenantId());
        Tenant tenant = readManager.findById(tenantId);

        // 2. 상태 변경 (도메인 로직 실행)
        TenantStatus targetStatus = TenantStatus.valueOf(command.status());
        Tenant updatedTenant = applyStatusTransition(tenant, targetStatus);

        // 3. 영속화
        Tenant savedTenant = transactionManager.persist(updatedTenant);

        // 4. 응답 변환
        return assembler.toResponse(savedTenant);
    }

    private Tenant applyStatusTransition(Tenant tenant, TenantStatus targetStatus) {
        return switch (targetStatus) {
            case ACTIVE -> tenant.activate(clock);
            case INACTIVE -> tenant.deactivate(clock);
            case DELETED ->
                    throw new IllegalArgumentException(
                            "DELETED 상태로의 변경은 DeleteTenantUseCase를 사용하세요");
        };
    }
}
