package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.api.RoleApi;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.common.PageResponse;
import com.ryuqq.authhub.sdk.model.role.CreateRoleRequest;
import com.ryuqq.authhub.sdk.model.role.CreateRoleResponse;
import com.ryuqq.authhub.sdk.model.role.GrantRolePermissionRequest;
import com.ryuqq.authhub.sdk.model.role.RoleResponse;
import com.ryuqq.authhub.sdk.model.role.RoleSummaryResponse;
import com.ryuqq.authhub.sdk.model.role.UpdateRoleRequest;
import java.util.Map;

/** RoleApi의 기본 구현체. */
final class DefaultRoleApi implements RoleApi {

    private static final String BASE_PATH = "/api/v1/roles";

    private final HttpClientSupport httpClient;

    DefaultRoleApi(HttpClientSupport httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ApiResponse<CreateRoleResponse> create(CreateRoleRequest request) {
        return httpClient.post(
                BASE_PATH, request, new TypeReference<ApiResponse<CreateRoleResponse>>() {});
    }

    @Override
    public ApiResponse<RoleResponse> getById(Long roleId) {
        return httpClient.get(
                BASE_PATH + "/" + roleId,
                Map.of(),
                new TypeReference<ApiResponse<RoleResponse>>() {});
    }

    @Override
    public ApiResponse<PageResponse<RoleResponse>> search(Map<String, Object> queryParams) {
        return httpClient.get(
                BASE_PATH,
                queryParams,
                new TypeReference<ApiResponse<PageResponse<RoleResponse>>>() {});
    }

    @Override
    public ApiResponse<PageResponse<RoleSummaryResponse>> searchAdmin(
            Map<String, Object> queryParams) {
        return httpClient.get(
                BASE_PATH + "/admin/search",
                queryParams,
                new TypeReference<ApiResponse<PageResponse<RoleSummaryResponse>>>() {});
    }

    @Override
    public ApiResponse<RoleResponse> update(Long roleId, UpdateRoleRequest request) {
        return httpClient.put(
                BASE_PATH + "/" + roleId,
                request,
                new TypeReference<ApiResponse<RoleResponse>>() {});
    }

    @Override
    public void grantPermissions(Long roleId, GrantRolePermissionRequest request) {
        httpClient.post(
                BASE_PATH + "/" + roleId + "/permissions",
                request,
                new TypeReference<ApiResponse<Void>>() {});
    }

    @Override
    public void revokePermissions(Long roleId, GrantRolePermissionRequest request) {
        httpClient.delete(BASE_PATH + "/" + roleId + "/permissions");
    }

    @Override
    public void delete(Long roleId) {
        httpClient.delete(BASE_PATH + "/" + roleId);
    }
}
