package com.ryuqq.authhub.application.organization.service.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.factory.query.OrganizationQueryFactory;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsUseCase;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchOrganizationsService - 조직 목록 조회 Service
 *
 * <p>SearchOrganizationsUseCase를 구현합니다.
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
public class SearchOrganizationsService implements SearchOrganizationsUseCase {

    private final OrganizationQueryFactory queryFactory;
    private final OrganizationReadManager readManager;
    private final OrganizationAssembler assembler;

    public SearchOrganizationsService(
            OrganizationQueryFactory queryFactory,
            OrganizationReadManager readManager,
            OrganizationAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public PageResponse<OrganizationResponse> execute(SearchOrganizationsQuery query) {
        // 1. Query → Criteria 변환
        OrganizationCriteria criteria = queryFactory.toCriteria(query);

        // 2. ReadManager: 조회
        List<Organization> organizations = readManager.findAllByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        // 3. Assembler: Response 변환
        List<OrganizationResponse> content = assembler.toResponseList(organizations);

        // 4. PageResponse 생성
        int page = criteria.page().page();
        int size = criteria.page().size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = page == 0;
        boolean last = page >= totalPages - 1 || totalPages == 0;

        return PageResponse.of(content, page, size, totalElements, totalPages, first, last);
    }
}
