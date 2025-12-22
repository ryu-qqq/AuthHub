package com.ryuqq.authhub.application.organization.port.in.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationUsersQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationUserResponse;

/**
 * SearchOrganizationUsersUseCase - 조직별 사용자 조회 UseCase (Port-In)
 *
 * <p>특정 조직에 소속된 사용자 목록 조회 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code Search{Entity}sUseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Query DTO 파라미터, PageResponse 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchOrganizationUsersUseCase {

    /**
     * 조직별 사용자 목록 조회 실행
     *
     * @param query 조직 사용자 검색 Query
     * @return 페이징된 사용자 Response 목록
     */
    PageResponse<OrganizationUserResponse> execute(SearchOrganizationUsersQuery query);
}
