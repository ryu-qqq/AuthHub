package com.ryuqq.authhub.application.organization.port.in.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;

/**
 * SearchOrganizationsUseCase - 조직 목록 조회 UseCase (Port-In)
 *
 * <p>테넌트 범위 내 조직 목록 조회 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code Search{Bc}sUseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Query DTO 파라미터, PageResponse 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchOrganizationsUseCase {

    /**
     * 조직 목록 조회 실행
     *
     * @param query 조직 검색 Query
     * @return 페이징된 조직 Response 목록
     */
    PageResponse<OrganizationResponse> execute(SearchOrganizationsQuery query);
}
