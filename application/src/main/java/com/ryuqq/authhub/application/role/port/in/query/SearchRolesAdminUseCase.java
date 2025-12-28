package com.ryuqq.authhub.application.role.port.in.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleSummaryResponse;

/**
 * SearchRolesAdminUseCase - 역할 목록 검색 UseCase (Admin용)
 *
 * <p>Admin 화면을 위한 확장된 역할 목록 검색 인터페이스입니다. 페이징 정보와 관련 엔티티 정보를 포함한 응답을 반환합니다.
 *
 * <p><strong>일반 SearchRolesUseCase와의 차이점:</strong>
 *
 * <ul>
 *   <li>PageResponse 반환 (페이징 메타 정보 포함)
 *   <li>RoleSummaryResponse 반환 (tenantName, permissionCount, userCount 포함)
 *   <li>확장 필터 지원 (날짜 범위, 정렬)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>UseCase 인터페이스 (Port-In)
 *   <li>단일 execute() 메서드
 *   <li>Application Layer에서 정의
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see SearchRolesUseCase 기본 역할 검색 UseCase
 * @see RoleSummaryResponse 목록 조회용 응답 DTO
 */
public interface SearchRolesAdminUseCase {

    /**
     * 역할 목록 검색 (Admin용)
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return 페이징된 역할 Summary 목록
     */
    PageResponse<RoleSummaryResponse> execute(SearchRolesQuery query);
}
