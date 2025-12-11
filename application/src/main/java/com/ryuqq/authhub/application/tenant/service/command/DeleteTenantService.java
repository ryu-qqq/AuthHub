package com.ryuqq.authhub.application.tenant.service.command;

import com.ryuqq.authhub.application.tenant.dto.command.DeleteTenantCommand;
import com.ryuqq.authhub.application.tenant.manager.command.TenantTransactionManager;
import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.application.tenant.port.in.command.DeleteTenantUseCase;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * DeleteTenantService - 테넌트 삭제 Service
 *
 * <p>DeleteTenantUseCase를 구현합니다 (Soft Delete).
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
public class DeleteTenantService implements DeleteTenantUseCase {

    private final TenantReadManager readManager;
    private final TenantTransactionManager transactionManager;
    private final Clock clock;

    public DeleteTenantService(
            TenantReadManager readManager,
            TenantTransactionManager transactionManager,
            Clock clock) {
        this.readManager = readManager;
        this.transactionManager = transactionManager;
        this.clock = clock;
    }

    @Override
    public void execute(DeleteTenantCommand command) {
        // 1. 기존 테넌트 조회
        TenantId tenantId = TenantId.of(command.tenantId());
        Tenant tenant = readManager.findById(tenantId);

        // 2. 도메인 로직 실행 (Soft Delete)
        Tenant deletedTenant = tenant.delete(clock);

        // 3. 영속화
        transactionManager.persist(deletedTenant);
    }
}
