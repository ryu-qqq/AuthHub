package com.ryuqq.authhub.application.tenant.service.query;

import com.ryuqq.authhub.application.tenant.dto.query.GetTenantQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantDetailResponse;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantDetailUseCase;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantAdminQueryPort;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Service;

/**
 * GetTenantDetailService - Admin 테넌트 상세 조회 Service
 *
 * <p>GetTenantDetailUseCase 구현체입니다. TenantAdminQueryPort를 통해 연관 데이터(organizations,
 * organizationCount)가 포함된 Detail DTO를 직접 조회합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션 필수
 *   <li>{@code @Transactional} 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임만)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see GetTenantService 기본 조회 Service
 */
@Service
public class GetTenantDetailService implements GetTenantDetailUseCase {

    private final TenantAdminQueryPort adminQueryPort;

    public GetTenantDetailService(TenantAdminQueryPort adminQueryPort) {
        this.adminQueryPort = adminQueryPort;
    }

    /**
     * Admin 테넌트 상세 조회 실행
     *
     * <p>TenantAdminQueryPort를 통해 organizations, organizationCount가 포함된 Detail DTO를 조회합니다.
     *
     * @param query 조회 조건
     * @return 테넌트 상세 정보 (연관 데이터 포함)
     * @throws TenantNotFoundException 테넌트가 존재하지 않는 경우
     */
    @Override
    public TenantDetailResponse execute(GetTenantQuery query) {
        TenantId tenantId = TenantId.of(query.tenantId());
        return adminQueryPort
                .findTenantDetail(tenantId)
                .orElseThrow(() -> new TenantNotFoundException(tenantId));
    }
}
