package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationAdminQueryDslRepository;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationDetailResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationSummaryResponse;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationAdminQueryPort;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * OrganizationAdminQueryAdapter - Admin 조직 Query Adapter
 *
 * <p>OrganizationAdminQueryPort 구현체로서 어드민 친화적 조직 조회를 담당합니다. DTO Projection을 통해 연관 데이터를 직접 반환합니다.
 *
 * <p><strong>OrganizationQueryAdapter와의 차이점:</strong>
 *
 * <ul>
 *   <li>DTO 직접 반환 (Domain 변환 불필요)
 *   <li>연관 데이터 포함 (tenantName, userCount)
 *   <li>확장 필터 지원 (날짜 범위, 정렬)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>{@code @Transactional} 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임만)
 *   <li>Criteria 기반 조회 (Query DTO 금지)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see OrganizationQueryAdapter Domain 조회 Adapter
 */
@Component
public class OrganizationAdminQueryAdapter implements OrganizationAdminQueryPort {

    private final OrganizationAdminQueryDslRepository repository;

    public OrganizationAdminQueryAdapter(OrganizationAdminQueryDslRepository repository) {
        this.repository = repository;
    }

    /**
     * Admin 목록 검색 (Criteria 기반 + 페이징)
     *
     * <p>tenantName, userCount를 포함한 Summary DTO를 직접 반환합니다.
     *
     * @param criteria 검색 조건 (OrganizationCriteria)
     * @return 페이징된 조직 Summary 목록
     */
    @Override
    public PageResponse<OrganizationSummaryResponse> searchOrganizations(
            OrganizationCriteria criteria) {
        List<OrganizationSummaryResponse> content = repository.searchOrganizations(criteria);
        long totalElements = repository.countOrganizations(criteria);

        int page = criteria.page().page();
        int size = criteria.page().size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean isFirst = page == 0;
        boolean isLast = page >= totalPages - 1 || totalPages == 0;

        return PageResponse.of(content, page, size, totalElements, totalPages, isFirst, isLast);
    }

    /**
     * Admin 상세 조회 (연관 데이터 포함)
     *
     * <p>tenantName, users, userCount를 포함한 Detail DTO를 직접 반환합니다.
     *
     * @param organizationId 조직 ID
     * @return 조직 상세 정보 (Optional)
     */
    @Override
    public Optional<OrganizationDetailResponse> findOrganizationDetail(
            OrganizationId organizationId) {
        return repository.findOrganizationDetail(organizationId.value());
    }
}
