package com.ryuqq.authhub.application.organization.service.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationSummaryResponse;
import com.ryuqq.authhub.application.organization.factory.query.OrganizationQueryFactory;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsAdminUseCase;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationAdminQueryPort;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationCriteria;
import org.springframework.stereotype.Service;

/**
 * SearchOrganizationsAdminService - Admin 조직 목록 검색 서비스
 *
 * <p>어드민 친화적 조직 목록 검색을 처리합니다. AdminQueryPort를 통해 DTO Projection을 직접 조회합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>QueryFactory: Query → Criteria 변환
 *   <li>AdminQueryPort: Criteria 기반 DTO Projection 조회
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션 필수
 *   <li>Port 의존 (구현체 직접 의존 금지)
 *   <li>{@code @Transactional} 금지 (Manager/Facade 책임)
 *   <li>QueryFactory → Port 흐름
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class SearchOrganizationsAdminService implements SearchOrganizationsAdminUseCase {

    private final OrganizationQueryFactory queryFactory;
    private final OrganizationAdminQueryPort adminQueryPort;

    /**
     * 생성자 주입
     *
     * @param queryFactory Query → Criteria 변환 Factory
     * @param adminQueryPort Admin 조회 포트
     */
    public SearchOrganizationsAdminService(
            OrganizationQueryFactory queryFactory, OrganizationAdminQueryPort adminQueryPort) {
        this.queryFactory = queryFactory;
        this.adminQueryPort = adminQueryPort;
    }

    /**
     * Admin 조직 목록 검색 실행
     *
     * <p>QueryFactory를 통해 Query → Criteria 변환 후, AdminQueryPort를 통해 tenantName, userCount를 포함한
     * Summary DTO를 직접 조회합니다.
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return 페이징된 조직 Summary 목록
     */
    @Override
    public PageResponse<OrganizationSummaryResponse> execute(SearchOrganizationsQuery query) {
        // 1. Query → Criteria 변환
        OrganizationCriteria criteria = queryFactory.toCriteria(query);

        // 2. AdminQueryPort: Criteria 기반 DTO Projection 조회
        return adminQueryPort.searchOrganizations(criteria);
    }
}
