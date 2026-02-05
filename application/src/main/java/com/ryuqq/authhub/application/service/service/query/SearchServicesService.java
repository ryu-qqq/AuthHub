package com.ryuqq.authhub.application.service.service.query;

import com.ryuqq.authhub.application.service.assembler.ServiceAssembler;
import com.ryuqq.authhub.application.service.dto.query.ServiceSearchParams;
import com.ryuqq.authhub.application.service.dto.response.ServicePageResult;
import com.ryuqq.authhub.application.service.factory.ServiceQueryFactory;
import com.ryuqq.authhub.application.service.manager.ServiceReadManager;
import com.ryuqq.authhub.application.service.port.in.query.SearchServicesUseCase;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import java.util.List;

/**
 * SearchServicesService - 서비스 목록 검색 Service
 *
 * <p>SearchServicesUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * @author development-team
 * @since 1.0.0
 */
@org.springframework.stereotype.Service
public class SearchServicesService implements SearchServicesUseCase {

    private final ServiceQueryFactory queryFactory;
    private final ServiceReadManager readManager;
    private final ServiceAssembler assembler;

    public SearchServicesService(
            ServiceQueryFactory queryFactory,
            ServiceReadManager readManager,
            ServiceAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public ServicePageResult execute(ServiceSearchParams params) {
        // 1. Factory: SearchParams → SearchCriteria
        ServiceSearchCriteria criteria = queryFactory.toCriteria(params);

        // 2. Manager: 조회
        List<Service> services = readManager.findAllByCriteria(criteria);
        long totalCount = readManager.countByCriteria(criteria);

        // 3. Assembler: Domain → PageResult
        return assembler.toPageResult(services, criteria.pageNumber(), criteria.size(), totalCount);
    }
}
