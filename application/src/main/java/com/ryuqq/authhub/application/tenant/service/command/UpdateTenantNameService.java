package com.ryuqq.authhub.application.tenant.service.command;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.manager.command.TenantTransactionManager;
import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantNameUseCase;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * UpdateTenantNameService - 테넌트 이름 변경 Service
 *
 * <p>UpdateTenantNameUseCase를 구현합니다.
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
public class UpdateTenantNameService implements UpdateTenantNameUseCase {

    private final TenantReadManager readManager;
    private final TenantTransactionManager transactionManager;
    private final TenantAssembler assembler;
    private final Clock clock;

    public UpdateTenantNameService(
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
    public TenantResponse execute(UpdateTenantNameCommand command) {
        // 1. 기존 테넌트 조회
        TenantId tenantId = TenantId.of(command.tenantId());
        Tenant tenant = readManager.findById(tenantId);

        // 2. 중복 이름 검사 (자기 자신 제외)
        TenantName newName = TenantName.of(command.name());
        if (!tenant.getName().equals(newName) && readManager.existsByName(newName)) {
            throw new DuplicateTenantNameException(newName);
        }

        // 3. 도메인 로직 실행 (이름 변경)
        Tenant updatedTenant = tenant.changeName(newName, clock);

        // 4. 영속화
        Tenant savedTenant = transactionManager.persist(updatedTenant);

        // 5. 응답 변환
        return assembler.toResponse(savedTenant);
    }
}
