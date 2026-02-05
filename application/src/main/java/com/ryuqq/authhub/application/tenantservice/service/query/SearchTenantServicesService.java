package com.ryuqq.authhub.application.tenantservice.service.query;

import com.ryuqq.authhub.application.tenantservice.assembler.TenantServiceAssembler;
import com.ryuqq.authhub.application.tenantservice.dto.query.TenantServiceSearchParams;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServicePageResult;
import com.ryuqq.authhub.application.tenantservice.factory.TenantServiceQueryFactory;
import com.ryuqq.authhub.application.tenantservice.manager.TenantServiceReadManager;
import com.ryuqq.authhub.application.tenantservice.port.in.query.SearchTenantServicesUseCase;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import java.util.List;

/**
 * SearchTenantServicesService - 테넌트-서비스 구독 목록 검색 Service
 *
 * <p>SearchTenantServicesUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 -> Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 -> 오케스트레이션만.
 *
 * @author development-team
 * @since 1.0.0
 */
@org.springframework.stereotype.Service
public class SearchTenantServicesService implements SearchTenantServicesUseCase {

    private final TenantServiceQueryFactory queryFactory;
    private final TenantServiceReadManager readManager;
    private final TenantServiceAssembler assembler;

    public SearchTenantServicesService(
            TenantServiceQueryFactory queryFactory,
            TenantServiceReadManager readManager,
            TenantServiceAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public TenantServicePageResult execute(TenantServiceSearchParams params) {
        // 1. Factory: SearchParams -> SearchCriteria
        TenantServiceSearchCriteria criteria = queryFactory.toCriteria(params);

        // 2. Manager: 조회
        List<TenantService> tenantServices = readManager.findAllByCriteria(criteria);
        long totalCount = readManager.countByCriteria(criteria);

        // 3. Assembler: Domain -> PageResult
        return assembler.toPageResult(
                tenantServices, criteria.pageNumber(), criteria.size(), totalCount);
    }
}
