package com.ryuqq.authhub.application.tenant.service.query;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.query.TenantSearchParams;
import com.ryuqq.authhub.application.tenant.dto.response.TenantPageResult;
import com.ryuqq.authhub.application.tenant.factory.TenantQueryFactory;
import com.ryuqq.authhub.application.tenant.manager.TenantReadManager;
import com.ryuqq.authhub.application.tenant.port.in.query.SearchTenantsByOffsetUseCase;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchTenantsByOffsetService - 테넌트 목록 조회 Service (Offset 기반)
 *
 * <p>SearchTenantsByOffsetUseCase를 구현합니다.
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
public class SearchTenantsByOffsetService implements SearchTenantsByOffsetUseCase {

    private final TenantQueryFactory queryFactory;
    private final TenantReadManager readManager;
    private final TenantAssembler assembler;

    public SearchTenantsByOffsetService(
            TenantQueryFactory queryFactory,
            TenantReadManager readManager,
            TenantAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public TenantPageResult execute(TenantSearchParams params) {
        // 1. SearchParams → Criteria 변환
        TenantSearchCriteria criteria = queryFactory.toCriteria(params);

        // 2. ReadManager: 조회
        List<Tenant> tenants = readManager.findAllByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        // 3. Assembler: PageResult 생성 및 반환
        return assembler.toPageResult(
                tenants, criteria.pageNumber(), criteria.size(), totalElements);
    }
}
