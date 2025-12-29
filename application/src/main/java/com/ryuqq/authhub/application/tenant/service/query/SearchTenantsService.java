package com.ryuqq.authhub.application.tenant.service.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.factory.query.TenantQueryFactory;
import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.application.tenant.port.in.query.SearchTenantsUseCase;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchTenantsService - 테넌트 목록 조회 Service
 *
 * <p>SearchTenantsUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 금지 (읽기 전용, 불필요)
 *   <li>QueryFactory → ReadManager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class SearchTenantsService implements SearchTenantsUseCase {

    private final TenantQueryFactory queryFactory;
    private final TenantReadManager readManager;
    private final TenantAssembler assembler;

    public SearchTenantsService(
            TenantQueryFactory queryFactory,
            TenantReadManager readManager,
            TenantAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public PageResponse<TenantResponse> execute(SearchTenantsQuery query) {
        // 1. Query → Criteria 변환
        TenantCriteria criteria = queryFactory.toCriteria(query);

        // 2. ReadManager: 조회
        List<Tenant> tenants = readManager.findAllByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        // 3. Assembler: Response 변환
        List<TenantResponse> content = assembler.toResponseList(tenants);

        // 4. PageResponse 생성
        int page = criteria.page().page();
        int size = criteria.page().size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = page == 0;
        boolean last = page >= totalPages - 1 || totalPages == 0;

        return PageResponse.of(content, page, size, totalElements, totalPages, first, last);
    }
}
