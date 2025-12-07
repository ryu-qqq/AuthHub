package com.ryuqq.authhub.application.tenant.service.query;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.query.GetTenantQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantUseCase;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Service;

/**
 * GetTenantService - 테넌트 단건 조회 Service
 *
 * <p>GetTenantUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 금지 (읽기 전용, 불필요)
 *   <li>ReadManager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetTenantService implements GetTenantUseCase {

    private final TenantReadManager readManager;
    private final TenantAssembler assembler;

    public GetTenantService(TenantReadManager readManager, TenantAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public TenantResponse execute(GetTenantQuery query) {
        // 1. ReadManager: 조회
        TenantId tenantId = TenantId.of(query.tenantId());
        Tenant tenant = readManager.findById(tenantId);

        // 2. Assembler: Response 변환
        return assembler.toResponse(tenant);
    }
}
