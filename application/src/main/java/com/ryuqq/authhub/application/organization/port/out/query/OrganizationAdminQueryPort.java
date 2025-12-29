package com.ryuqq.authhub.application.organization.port.out.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationDetailResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationSummaryResponse;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationCriteria;
import java.util.Optional;

/**
 * OrganizationAdminQueryPort - Organization Admin 조회 포트 (Query)
 *
 * <p>어드민 친화적 조직 조회를 위한 읽기 전용 Port입니다. Domain Aggregate 대신 DTO Projection을 직접 반환합니다.
 *
 * <p><strong>QueryPort와의 차이점:</strong>
 *
 * <ul>
 *   <li>Domain 대신 DTO 반환 (직접 Projection)
 *   <li>연관 데이터 포함 (tenantName, userCount)
 *   <li>확장 필터 지원 (날짜 범위, 정렬)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 메서드만 제공
 *   <li>DTO 반환 (Domain/Entity 반환 금지)
 *   <li>Optional 반환 (단건 조회 시 null 방지)
 *   <li>Criteria 기반 조회 (Query DTO 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see OrganizationQueryPort Domain 조회 포트
 */
public interface OrganizationAdminQueryPort {

    /**
     * Admin 목록 검색 (Criteria 기반 + 페이징)
     *
     * <p>tenantName, userCount를 포함한 Summary DTO를 직접 반환합니다.
     *
     * @param criteria 검색 조건 (OrganizationCriteria)
     * @return 페이징된 조직 Summary 목록
     */
    PageResponse<OrganizationSummaryResponse> searchOrganizations(OrganizationCriteria criteria);

    /**
     * Admin 상세 조회 (연관 데이터 포함)
     *
     * <p>tenantName, users, userCount를 포함한 Detail DTO를 직접 반환합니다.
     *
     * @param organizationId 조직 ID
     * @return 조직 상세 정보 (Optional)
     */
    Optional<OrganizationDetailResponse> findOrganizationDetail(OrganizationId organizationId);
}
