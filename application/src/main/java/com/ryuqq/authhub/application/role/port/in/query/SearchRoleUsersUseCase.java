package com.ryuqq.authhub.application.role.port.in.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.dto.query.SearchRoleUsersQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleUserResponse;

/**
 * SearchRoleUsersUseCase - 역할별 사용자 조회 UseCase (Port-In)
 *
 * <p>특정 역할이 할당된 사용자 목록을 조회하는 기능을 정의합니다.
 *
 * <p><strong>권한:</strong>
 *
 * <ul>
 *   <li>SUPER_ADMIN: 모든 역할의 사용자 조회 가능
 *   <li>TENANT_ADMIN: 본인 테넌트 내 역할의 사용자만 조회 가능
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code Search{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Query DTO 파라미터, PageResponse 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchRoleUsersUseCase {

    /**
     * 역할별 사용자 조회 실행
     *
     * @param query 역할 사용자 검색 Query
     * @return 페이징된 사용자 목록 Response
     */
    PageResponse<RoleUserResponse> execute(SearchRoleUsersQuery query);
}
