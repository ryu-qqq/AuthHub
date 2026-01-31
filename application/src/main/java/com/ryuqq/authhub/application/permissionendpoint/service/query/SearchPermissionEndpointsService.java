package com.ryuqq.authhub.application.permissionendpoint.service.query;

import com.ryuqq.authhub.application.permissionendpoint.assembler.PermissionEndpointAssembler;
import com.ryuqq.authhub.application.permissionendpoint.dto.query.PermissionEndpointSearchParams;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointPageResult;
import com.ryuqq.authhub.application.permissionendpoint.factory.PermissionEndpointQueryFactory;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointReadManager;
import com.ryuqq.authhub.application.permissionendpoint.port.in.query.SearchPermissionEndpointsUseCase;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchPermissionEndpointsService - PermissionEndpoint 검색 Service
 *
 * <p>SearchPermissionEndpointsUseCase를 구현합니다.
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
@Service
public class SearchPermissionEndpointsService implements SearchPermissionEndpointsUseCase {

    private final PermissionEndpointQueryFactory queryFactory;
    private final PermissionEndpointReadManager readManager;
    private final PermissionEndpointAssembler assembler;

    public SearchPermissionEndpointsService(
            PermissionEndpointQueryFactory queryFactory,
            PermissionEndpointReadManager readManager,
            PermissionEndpointAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public PermissionEndpointPageResult search(PermissionEndpointSearchParams params) {
        // 1. Factory: SearchParams → Criteria 변환
        PermissionEndpointSearchCriteria criteria = queryFactory.toCriteria(params);

        // 2. Manager: 권한 엔드포인트 목록 조회
        List<PermissionEndpoint> permissionEndpoints =
                readManager.findAllBySearchCriteria(criteria);

        // 3. Manager: 전체 개수 조회
        long totalElements = readManager.countBySearchCriteria(criteria);

        // 4. Assembler: Domain → Result 변환
        return assembler.toPageResult(
                permissionEndpoints, criteria.pageNumber(), criteria.size(), totalElements);
    }
}
