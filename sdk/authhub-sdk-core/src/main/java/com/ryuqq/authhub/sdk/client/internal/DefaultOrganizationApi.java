package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.api.OrganizationApi;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.common.PageResponse;
import com.ryuqq.authhub.sdk.model.organization.CreateOrganizationRequest;
import com.ryuqq.authhub.sdk.model.organization.CreateOrganizationResponse;
import com.ryuqq.authhub.sdk.model.organization.OrganizationResponse;
import com.ryuqq.authhub.sdk.model.organization.OrganizationSummaryResponse;
import com.ryuqq.authhub.sdk.model.organization.UpdateOrganizationRequest;
import com.ryuqq.authhub.sdk.model.organization.UpdateOrganizationStatusRequest;
import java.util.Map;

/** OrganizationApi의 기본 구현체. */
final class DefaultOrganizationApi implements OrganizationApi {

    private static final String BASE_PATH = "/api/v1/organizations";

    private final HttpClientSupport httpClient;

    DefaultOrganizationApi(HttpClientSupport httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ApiResponse<CreateOrganizationResponse> create(CreateOrganizationRequest request) {
        return httpClient.post(
                BASE_PATH,
                request,
                new TypeReference<ApiResponse<CreateOrganizationResponse>>() {});
    }

    @Override
    public ApiResponse<OrganizationResponse> getById(Long organizationId) {
        return httpClient.get(
                BASE_PATH + "/" + organizationId,
                Map.of(),
                new TypeReference<ApiResponse<OrganizationResponse>>() {});
    }

    @Override
    public ApiResponse<PageResponse<OrganizationResponse>> search(Map<String, Object> queryParams) {
        return httpClient.get(
                BASE_PATH,
                queryParams,
                new TypeReference<ApiResponse<PageResponse<OrganizationResponse>>>() {});
    }

    @Override
    public ApiResponse<PageResponse<OrganizationSummaryResponse>> searchAdmin(
            Map<String, Object> queryParams) {
        return httpClient.get(
                BASE_PATH + "/admin/search",
                queryParams,
                new TypeReference<ApiResponse<PageResponse<OrganizationSummaryResponse>>>() {});
    }

    @Override
    public ApiResponse<OrganizationResponse> update(
            Long organizationId, UpdateOrganizationRequest request) {
        return httpClient.put(
                BASE_PATH + "/" + organizationId,
                request,
                new TypeReference<ApiResponse<OrganizationResponse>>() {});
    }

    @Override
    public ApiResponse<OrganizationResponse> updateStatus(
            Long organizationId, UpdateOrganizationStatusRequest request) {
        return httpClient.patch(
                BASE_PATH + "/" + organizationId + "/status",
                request,
                new TypeReference<ApiResponse<OrganizationResponse>>() {});
    }

    @Override
    public void delete(Long organizationId) {
        httpClient.delete(BASE_PATH + "/" + organizationId + "/delete");
    }
}
