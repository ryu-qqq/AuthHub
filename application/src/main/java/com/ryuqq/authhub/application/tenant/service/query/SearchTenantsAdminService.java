package com.ryuqq.authhub.application.tenant.service.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantSummaryResponse;
import com.ryuqq.authhub.application.tenant.factory.query.TenantQueryFactory;
import com.ryuqq.authhub.application.tenant.port.in.query.SearchTenantsAdminUseCase;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantAdminQueryPort;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantCriteria;
import org.springframework.stereotype.Service;

/**
 * SearchTenantsAdminService - Admin 테넌트 목록 검색 Service
 *
 * <p>SearchTenantsAdminUseCase 구현체입니다. TenantAdminQueryPort를 통해 확장 필터가 적용된 Summary DTO를 직접 조회합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션 필수
 *   <li>{@code @Transactional} 금지 (Manager/Facade에서 관리)
 *   <li>QueryFactory → Port 흐름
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see SearchTenantsService 기본 검색 Service
 */
@Service
public class SearchTenantsAdminService implements SearchTenantsAdminUseCase {

    private final TenantQueryFactory queryFactory;
    private final TenantAdminQueryPort adminQueryPort;

    public SearchTenantsAdminService(
            TenantQueryFactory queryFactory, TenantAdminQueryPort adminQueryPort) {
        this.queryFactory = queryFactory;
        this.adminQueryPort = adminQueryPort;
    }

    /**
     * Admin 테넌트 목록 검색 실행
     *
     * <p>TenantAdminQueryPort를 통해 organizationCount가 포함된 Summary DTO를 조회합니다.
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return 페이징된 테넌트 Summary 목록
     */
    @Override
    public PageResponse<TenantSummaryResponse> execute(SearchTenantsQuery query) {
        TenantCriteria criteria = queryFactory.toCriteria(query);
        return adminQueryPort.searchTenants(criteria);
    }
}
