package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantAdminQueryDslRepository;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantDetailResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantSummaryResponse;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantAdminQueryPort;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * TenantAdminQueryAdapter - Admin 테넌트 Query Adapter
 *
 * <p>TenantAdminQueryPort 구현체로서 어드민 친화적 테넌트 조회를 담당합니다. DTO Projection을 통해 연관 데이터를 직접 반환합니다.
 *
 * <p><strong>TenantQueryAdapter와의 차이점:</strong>
 *
 * <ul>
 *   <li>DTO 직접 반환 (Domain 변환 불필요)
 *   <li>연관 데이터 포함 (organizationCount)
 *   <li>확장 필터 지원 (날짜 범위, 정렬)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>{@code @Transactional} 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임만)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see TenantQueryAdapter Domain 조회 Adapter
 */
@Component
public class TenantAdminQueryAdapter implements TenantAdminQueryPort {

    private final TenantAdminQueryDslRepository repository;

    public TenantAdminQueryAdapter(TenantAdminQueryDslRepository repository) {
        this.repository = repository;
    }

    /**
     * Admin 목록 검색 (확장 필터 + 페이징)
     *
     * <p>organizationCount를 포함한 Summary DTO를 직접 반환합니다.
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return 페이징된 테넌트 Summary 목록
     */
    @Override
    public PageResponse<TenantSummaryResponse> searchTenants(SearchTenantsQuery query) {
        List<TenantSummaryResponse> content = repository.searchTenants(query);
        long totalElements = repository.countTenants(query);
        int totalPages = (int) Math.ceil((double) totalElements / query.size());
        boolean isFirst = query.page() == 0;
        boolean isLast = query.page() >= totalPages - 1;

        return PageResponse.of(
                content, query.page(), query.size(), totalElements, totalPages, isFirst, isLast);
    }

    /**
     * Admin 상세 조회 (연관 데이터 포함)
     *
     * <p>organizations, organizationCount를 포함한 Detail DTO를 직접 반환합니다.
     *
     * @param tenantId 테넌트 ID
     * @return 테넌트 상세 정보 (Optional)
     */
    @Override
    public Optional<TenantDetailResponse> findTenantDetail(TenantId tenantId) {
        return repository.findTenantDetail(tenantId.value());
    }
}
