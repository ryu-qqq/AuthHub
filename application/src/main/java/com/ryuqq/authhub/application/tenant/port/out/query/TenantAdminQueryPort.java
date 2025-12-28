package com.ryuqq.authhub.application.tenant.port.out.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantDetailResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantSummaryResponse;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.Optional;

/**
 * TenantAdminQueryPort - Admin 테넌트 조회 포트 (DTO 반환)
 *
 * <p>어드민 친화적 조회를 위한 Query 포트입니다. Domain 대신 DTO를 직접 반환하여 N+1 문제 없이 연관 데이터(organizationCount)를 제공합니다.
 *
 * <p><strong>TenantQueryPort와의 차이점:</strong>
 *
 * <ul>
 *   <li>Domain 대신 DTO 직접 반환
 *   <li>연관 데이터 포함 (organizationCount, organizations)
 *   <li>확장 필터 지원 (날짜 범위, 정렬)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>DTO 반환 (Domain 반환 금지)
 *   <li>TenantId Value Object 파라미터
 *   <li>저장/수정/삭제 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see TenantQueryPort Domain 조회 포트
 */
public interface TenantAdminQueryPort {

    /**
     * Admin 목록 검색 (확장 필터 + 페이징)
     *
     * <p>organizationCount를 포함한 Summary DTO를 직접 반환합니다.
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return 페이징된 테넌트 Summary 목록
     */
    PageResponse<TenantSummaryResponse> searchTenants(SearchTenantsQuery query);

    /**
     * Admin 상세 조회 (연관 데이터 포함)
     *
     * <p>organizations, organizationCount를 포함한 Detail DTO를 직접 반환합니다.
     *
     * @param tenantId 테넌트 ID
     * @return 테넌트 상세 정보 (Optional)
     */
    Optional<TenantDetailResponse> findTenantDetail(TenantId tenantId);
}
