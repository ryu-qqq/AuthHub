package com.ryuqq.authhub.application.tenant.service.query;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.response.TenantConfigResult;
import com.ryuqq.authhub.application.tenant.manager.TenantReadManager;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantConfigUseCase;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import org.springframework.stereotype.Service;

/**
 * GetTenantConfigService - Gateway용 테넌트 설정 조회 서비스
 *
 * <p>Gateway가 테넌트 유효성 검증을 위해 설정 정보를 조회합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 금지 (Manager에서 처리)
 *   <li>ReadManager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetTenantConfigService implements GetTenantConfigUseCase {

    private final TenantReadManager readManager;
    private final TenantAssembler assembler;

    public GetTenantConfigService(TenantReadManager readManager, TenantAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public TenantConfigResult getByTenantId(String tenantId) {
        TenantId id = TenantId.of(tenantId);
        Tenant tenant = readManager.findById(id);
        return assembler.toConfigResult(tenant);
    }
}
