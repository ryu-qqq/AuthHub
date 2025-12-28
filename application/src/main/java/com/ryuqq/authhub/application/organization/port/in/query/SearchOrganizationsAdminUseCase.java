package com.ryuqq.authhub.application.organization.port.in.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationSummaryResponse;

/**
 * SearchOrganizationsAdminUseCase - Admin 조직 목록 검색 UseCase (Query)
 *
 * <p>어드민 친화적 조직 목록 검색을 위한 Port-In 인터페이스입니다. tenantName, userCount를 포함한 Summary 정보를 반환합니다.
 *
 * <p><strong>SearchOrganizationsUseCase와의 차이점:</strong>
 *
 * <ul>
 *   <li>OrganizationSummaryResponse 반환 (연관 데이터 포함)
 *   <li>확장 필터 지원 (날짜 범위, 정렬)
 *   <li>전체 테넌트 조회 가능
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>단일 execute 메서드만 제공
 *   <li>Query DTO 입력, Response DTO 출력
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see SearchOrganizationsUseCase 기본 검색 UseCase
 */
public interface SearchOrganizationsAdminUseCase {

    /**
     * Admin 조직 목록 검색 실행
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return 페이징된 조직 Summary 목록
     */
    PageResponse<OrganizationSummaryResponse> execute(SearchOrganizationsQuery query);
}
