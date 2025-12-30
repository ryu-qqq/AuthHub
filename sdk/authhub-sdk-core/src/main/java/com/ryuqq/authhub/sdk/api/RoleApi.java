package com.ryuqq.authhub.sdk.api;

import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.common.PageResponse;
import com.ryuqq.authhub.sdk.model.role.CreateRoleRequest;
import com.ryuqq.authhub.sdk.model.role.CreateRoleResponse;
import com.ryuqq.authhub.sdk.model.role.GrantRolePermissionRequest;
import com.ryuqq.authhub.sdk.model.role.RoleResponse;
import com.ryuqq.authhub.sdk.model.role.RoleSummaryResponse;
import com.ryuqq.authhub.sdk.model.role.UpdateRoleRequest;
import java.util.Map;

/** Role 관련 API 인터페이스. 역할 생성, 조회, 수정, 권한 부여/회수 등의 기능을 제공합니다. */
public interface RoleApi {

    /**
     * 새로운 역할을 생성합니다.
     *
     * @param request 역할 생성 요청
     * @return 생성된 역할 ID를 포함한 응답
     */
    ApiResponse<CreateRoleResponse> create(CreateRoleRequest request);

    /**
     * 역할 ID로 역할을 조회합니다.
     *
     * @param roleId 역할 ID
     * @return 역할 정보
     */
    ApiResponse<RoleResponse> getById(String roleId);

    /**
     * 역할 목록을 검색합니다.
     *
     * @param queryParams 검색 조건 (tenantId, name, page, size, sortKey, sortDirection)
     * @return 역할 목록
     */
    ApiResponse<PageResponse<RoleResponse>> search(Map<String, Object> queryParams);

    /**
     * Admin용 역할 목록을 검색합니다.
     *
     * @param queryParams 검색 조건
     * @return Admin용 역할 목록 (Summary 포함)
     */
    ApiResponse<PageResponse<RoleSummaryResponse>> searchAdmin(Map<String, Object> queryParams);

    /**
     * 역할 정보를 수정합니다.
     *
     * @param roleId 역할 ID
     * @param request 수정 요청
     * @return 수정된 역할 정보
     */
    ApiResponse<RoleResponse> update(String roleId, UpdateRoleRequest request);

    /**
     * 역할에 권한을 부여합니다.
     *
     * @param roleId 역할 ID
     * @param request 권한 부여 요청
     */
    void grantPermissions(String roleId, GrantRolePermissionRequest request);

    /**
     * 역할에서 권한을 회수합니다.
     *
     * @param roleId 역할 ID
     * @param request 권한 회수 요청
     */
    void revokePermissions(String roleId, GrantRolePermissionRequest request);

    /**
     * 역할을 삭제합니다.
     *
     * @param roleId 역할 ID
     */
    void delete(String roleId);
}
