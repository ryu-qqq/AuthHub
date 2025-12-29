package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.api.TenantApi;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.common.PageResponse;
import com.ryuqq.authhub.sdk.model.tenant.CreateTenantRequest;
import com.ryuqq.authhub.sdk.model.tenant.CreateTenantResponse;
import com.ryuqq.authhub.sdk.model.tenant.TenantResponse;
import com.ryuqq.authhub.sdk.model.tenant.TenantSummaryResponse;
import com.ryuqq.authhub.sdk.model.tenant.UpdateTenantNameRequest;
import com.ryuqq.authhub.sdk.model.tenant.UpdateTenantStatusRequest;
import java.util.Map;

/** TenantApi의 기본 구현체. */
final class DefaultTenantApi implements TenantApi {

    private static final String BASE_PATH = "/api/v1/tenants";

    private final HttpClientSupport httpClient;

    DefaultTenantApi(HttpClientSupport httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ApiResponse<CreateTenantResponse> create(CreateTenantRequest request) {
        return httpClient.post(
                BASE_PATH, request, new TypeReference<ApiResponse<CreateTenantResponse>>() {});
    }

    @Override
    public ApiResponse<TenantResponse> getById(String tenantId) {
        return httpClient.get(
                BASE_PATH + "/" + tenantId,
                Map.of(),
                new TypeReference<ApiResponse<TenantResponse>>() {});
    }

    @Override
    public ApiResponse<PageResponse<TenantResponse>> search(Map<String, Object> queryParams) {
        return httpClient.get(
                BASE_PATH,
                queryParams,
                new TypeReference<ApiResponse<PageResponse<TenantResponse>>>() {});
    }

    @Override
    public ApiResponse<PageResponse<TenantSummaryResponse>> searchAdmin(
            Map<String, Object> queryParams) {
        return httpClient.get(
                BASE_PATH + "/admin/search",
                queryParams,
                new TypeReference<ApiResponse<PageResponse<TenantSummaryResponse>>>() {});
    }

    @Override
    public ApiResponse<TenantResponse> updateName(
            String tenantId, UpdateTenantNameRequest request) {
        return httpClient.put(
                BASE_PATH + "/" + tenantId + "/name",
                request,
                new TypeReference<ApiResponse<TenantResponse>>() {});
    }

    @Override
    public ApiResponse<TenantResponse> updateStatus(
            String tenantId, UpdateTenantStatusRequest request) {
        return httpClient.patch(
                BASE_PATH + "/" + tenantId + "/status",
                request,
                new TypeReference<ApiResponse<TenantResponse>>() {});
    }

    @Override
    public void delete(String tenantId) {
        httpClient.delete(BASE_PATH + "/" + tenantId + "/delete");
    }
}
