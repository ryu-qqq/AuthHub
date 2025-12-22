package com.ryuqq.authhub.application.role.port.out.query;

import com.ryuqq.authhub.application.role.dto.query.SearchRoleUsersQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleUserResponse;
import java.util.List;

/**
 * RoleUserQueryPort - 역할-사용자 관계 조회 Port (Port-Out)
 *
 * <p>역할에 할당된 사용자 목록을 조회하는 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Bc}QueryPort} 네이밍
 *   <li>Application Layer DTO 반환 (DTO Projection)
 *   <li>구현은 Adapter 책임
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RoleUserQueryPort {

    /**
     * 역할에 할당된 사용자 목록 조회
     *
     * @param query 조회 조건 (역할 ID, 페이징 정보)
     * @return 사용자 목록
     */
    List<RoleUserResponse> searchUsersByRoleId(SearchRoleUsersQuery query);

    /**
     * 역할에 할당된 사용자 수 조회
     *
     * @param query 조회 조건
     * @return 사용자 수
     */
    long countUsersByRoleId(SearchRoleUsersQuery query);
}
