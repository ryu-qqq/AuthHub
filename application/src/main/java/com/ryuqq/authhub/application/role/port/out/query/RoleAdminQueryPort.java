package com.ryuqq.authhub.application.role.port.out.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleDetailResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleSummaryResponse;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import java.util.Optional;

/**
 * RoleAdminQueryPort - 역할 Admin 조회 Port-Out
 *
 * <p>어드민 화면을 위한 확장된 역할 조회 인터페이스입니다. 일반 {@link RoleQueryPort}와 달리 DTO Projection을 직접 반환합니다.
 *
 * <p><strong>일반 QueryPort와의 차이점:</strong>
 *
 * <ul>
 *   <li>Domain 대신 DTO Projection 반환
 *   <li>관련 엔티티 정보 포함 (tenantName 등)
 *   <li>집계 정보 포함 (permissionCount, userCount 등)
 *   <li>Admin 화면 최적화된 응답
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>*AdminQueryPort 네이밍 규칙
 *   <li>Application Layer에서 정의
 *   <li>AdminQueryAdapter에서 구현
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see RoleQueryPort 기본 역할 조회 Port
 * @see RoleSummaryResponse 목록 조회용 응답 DTO
 * @see RoleDetailResponse 상세 조회용 응답 DTO
 */
public interface RoleAdminQueryPort {

    /**
     * 조건에 맞는 역할 목록 검색 (Admin용)
     *
     * <p>확장된 필터와 관련 엔티티 정보를 포함한 Summary 응답을 반환합니다.
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return 페이징된 역할 Summary 목록
     */
    PageResponse<RoleSummaryResponse> searchRoles(SearchRolesQuery query);

    /**
     * 역할 상세 조회 (Admin용)
     *
     * <p>역할에 할당된 권한 목록을 포함한 상세 정보를 반환합니다.
     *
     * @param roleId 역할 ID
     * @return 역할 상세 정보 Optional
     */
    Optional<RoleDetailResponse> findRoleDetail(RoleId roleId);

    /**
     * 조건에 맞는 역할 수 조회
     *
     * @param query 검색 조건
     * @return 역할 수
     */
    long countRoles(SearchRolesQuery query);
}
