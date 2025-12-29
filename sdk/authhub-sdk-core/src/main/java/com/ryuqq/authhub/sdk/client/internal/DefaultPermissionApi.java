package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.api.PermissionApi;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.common.PageResponse;
import com.ryuqq.authhub.sdk.model.permission.CreatePermissionRequest;
import com.ryuqq.authhub.sdk.model.permission.CreatePermissionResponse;
import com.ryuqq.authhub.sdk.model.permission.PermissionResponse;
import com.ryuqq.authhub.sdk.model.permission.UpdatePermissionRequest;
import java.util.Map;

/** PermissionApi의 기본 구현체. */
final class DefaultPermissionApi implements PermissionApi {

    private static final String BASE_PATH = "/api/v1/permissions";

    private final HttpClientSupport httpClient;

    DefaultPermissionApi(HttpClientSupport httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ApiResponse<CreatePermissionResponse> create(CreatePermissionRequest request) {
        return httpClient.post(
                BASE_PATH, request, new TypeReference<ApiResponse<CreatePermissionResponse>>() {});
    }

    @Override
    public ApiResponse<PermissionResponse> getById(Long permissionId) {
        return httpClient.get(
                BASE_PATH + "/" + permissionId,
                Map.of(),
                new TypeReference<ApiResponse<PermissionResponse>>() {});
    }

    @Override
    public ApiResponse<PageResponse<PermissionResponse>> search(Map<String, Object> queryParams) {
        return httpClient.get(
                BASE_PATH,
                queryParams,
                new TypeReference<ApiResponse<PageResponse<PermissionResponse>>>() {});
    }

    @Override
    public ApiResponse<PermissionResponse> update(
            Long permissionId, UpdatePermissionRequest request) {
        return httpClient.put(
                BASE_PATH + "/" + permissionId,
                request,
                new TypeReference<ApiResponse<PermissionResponse>>() {});
    }

    @Override
    public void delete(Long permissionId) {
        httpClient.delete(BASE_PATH + "/" + permissionId);
    }
}
