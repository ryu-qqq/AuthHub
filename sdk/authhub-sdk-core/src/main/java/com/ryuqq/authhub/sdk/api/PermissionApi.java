package com.ryuqq.authhub.sdk.api;

import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.common.PageResponse;
import com.ryuqq.authhub.sdk.model.permission.CreatePermissionRequest;
import com.ryuqq.authhub.sdk.model.permission.CreatePermissionResponse;
import com.ryuqq.authhub.sdk.model.permission.PermissionResponse;
import com.ryuqq.authhub.sdk.model.permission.UpdatePermissionRequest;
import java.util.Map;

/** Permission 관련 API 인터페이스. 권한 생성, 조회, 수정, 삭제 등의 기능을 제공합니다. */
public interface PermissionApi {

    /**
     * 새로운 권한을 생성합니다.
     *
     * @param request 권한 생성 요청
     * @return 생성된 권한 ID를 포함한 응답
     */
    ApiResponse<CreatePermissionResponse> create(CreatePermissionRequest request);

    /**
     * 권한 ID로 권한을 조회합니다.
     *
     * @param permissionId 권한 ID
     * @return 권한 정보
     */
    ApiResponse<PermissionResponse> getById(String permissionId);

    /**
     * 권한 목록을 검색합니다.
     *
     * @param queryParams 검색 조건 (tenantId, name, code, page, size)
     * @return 권한 목록
     */
    ApiResponse<PageResponse<PermissionResponse>> search(Map<String, Object> queryParams);

    /**
     * 권한 정보를 수정합니다.
     *
     * @param permissionId 권한 ID
     * @param request 수정 요청
     * @return 수정된 권한 정보
     */
    ApiResponse<PermissionResponse> update(String permissionId, UpdatePermissionRequest request);

    /**
     * 권한을 삭제합니다.
     *
     * @param permissionId 권한 ID
     */
    void delete(String permissionId);
}
