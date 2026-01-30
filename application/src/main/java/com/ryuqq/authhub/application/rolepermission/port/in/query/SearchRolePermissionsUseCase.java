package com.ryuqq.authhub.application.rolepermission.port.in.query;

import com.ryuqq.authhub.application.rolepermission.dto.query.RolePermissionSearchParams;
import com.ryuqq.authhub.application.rolepermission.dto.response.RolePermissionPageResult;

/**
 * SearchRolePermissionsUseCase - 역할-권한 관계 검색 Use Case
 *
 * <p>역할-권한 관계를 검색하는 비즈니스 유스케이스입니다.
 *
 * <p><strong>검색 조건:</strong>
 *
 * <ul>
 *   <li>roleId - 특정 역할의 권한 목록 조회
 *   <li>permissionId - 특정 권한이 부여된 역할 목록 조회
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchRolePermissionsUseCase {

    /**
     * 역할-권한 관계 검색
     *
     * @param params 검색 파라미터
     * @return 역할-권한 관계 페이지 결과
     */
    RolePermissionPageResult search(RolePermissionSearchParams params);
}
