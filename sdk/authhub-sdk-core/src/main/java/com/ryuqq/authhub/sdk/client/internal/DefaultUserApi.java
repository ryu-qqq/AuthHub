package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.api.UserApi;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.common.PageResponse;
import com.ryuqq.authhub.sdk.model.user.AssignUserRoleRequest;
import com.ryuqq.authhub.sdk.model.user.CreateUserRequest;
import com.ryuqq.authhub.sdk.model.user.CreateUserResponse;
import com.ryuqq.authhub.sdk.model.user.UpdateUserPasswordRequest;
import com.ryuqq.authhub.sdk.model.user.UpdateUserRequest;
import com.ryuqq.authhub.sdk.model.user.UpdateUserStatusRequest;
import com.ryuqq.authhub.sdk.model.user.UserResponse;
import com.ryuqq.authhub.sdk.model.user.UserSummaryResponse;
import java.util.Map;

/** UserApi의 기본 구현체. */
final class DefaultUserApi implements UserApi {

    private static final String BASE_PATH = "/api/v1/users";

    private final HttpClientSupport httpClient;

    DefaultUserApi(HttpClientSupport httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ApiResponse<CreateUserResponse> create(CreateUserRequest request) {
        return httpClient.post(
                BASE_PATH, request, new TypeReference<ApiResponse<CreateUserResponse>>() {});
    }

    @Override
    public ApiResponse<UserResponse> getById(String userId) {
        return httpClient.get(
                BASE_PATH + "/" + userId,
                Map.of(),
                new TypeReference<ApiResponse<UserResponse>>() {});
    }

    @Override
    public ApiResponse<PageResponse<UserResponse>> search(Map<String, Object> queryParams) {
        return httpClient.get(
                BASE_PATH,
                queryParams,
                new TypeReference<ApiResponse<PageResponse<UserResponse>>>() {});
    }

    @Override
    public ApiResponse<PageResponse<UserSummaryResponse>> searchAdmin(
            Map<String, Object> queryParams) {
        return httpClient.get(
                BASE_PATH + "/admin/search",
                queryParams,
                new TypeReference<ApiResponse<PageResponse<UserSummaryResponse>>>() {});
    }

    @Override
    public ApiResponse<UserResponse> update(String userId, UpdateUserRequest request) {
        return httpClient.put(
                BASE_PATH + "/" + userId,
                request,
                new TypeReference<ApiResponse<UserResponse>>() {});
    }

    @Override
    public ApiResponse<UserResponse> updateStatus(String userId, UpdateUserStatusRequest request) {
        return httpClient.patch(
                BASE_PATH + "/" + userId + "/status",
                request,
                new TypeReference<ApiResponse<UserResponse>>() {});
    }

    @Override
    public void updatePassword(String userId, UpdateUserPasswordRequest request) {
        httpClient.patch(
                BASE_PATH + "/" + userId + "/password",
                request,
                new TypeReference<ApiResponse<Void>>() {});
    }

    @Override
    public void assignRole(String userId, AssignUserRoleRequest request) {
        httpClient.post(
                BASE_PATH + "/" + userId + "/roles",
                request,
                new TypeReference<ApiResponse<Void>>() {});
    }

    @Override
    public void unassignRole(String userId, String roleId) {
        httpClient.delete(BASE_PATH + "/" + userId + "/roles/" + roleId);
    }

    @Override
    public void delete(String userId) {
        httpClient.delete(BASE_PATH + "/" + userId + "/delete");
    }
}
