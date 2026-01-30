package com.ryuqq.authhub.application.organization.service.query;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.query.OrganizationSearchParams;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationPageResult;
import com.ryuqq.authhub.application.organization.factory.OrganizationQueryFactory;
import com.ryuqq.authhub.application.organization.manager.OrganizationReadManager;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsByOffsetUseCase;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchOrganizationsByOffsetService - 조직 목록 조회 Service (Offset 기반)
 *
 * <p>SearchOrganizationsByOffsetUseCase를 구현합니다.
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
public class SearchOrganizationsByOffsetService implements SearchOrganizationsByOffsetUseCase {

    private final OrganizationQueryFactory queryFactory;
    private final OrganizationReadManager readManager;
    private final OrganizationAssembler assembler;

    public SearchOrganizationsByOffsetService(
            OrganizationQueryFactory queryFactory,
            OrganizationReadManager readManager,
            OrganizationAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public OrganizationPageResult execute(OrganizationSearchParams params) {
        // 1. SearchParams → Criteria 변환
        OrganizationSearchCriteria criteria = queryFactory.toCriteria(params);

        // 2. ReadManager: 조회
        List<Organization> organizations = readManager.findAllBySearchCriteria(criteria);
        long totalElements = readManager.countBySearchCriteria(criteria);

        // 3. Assembler: PageResult 생성 및 반환
        return assembler.toPageResult(
                organizations, criteria.pageNumber(), criteria.size(), totalElements);
    }
}
