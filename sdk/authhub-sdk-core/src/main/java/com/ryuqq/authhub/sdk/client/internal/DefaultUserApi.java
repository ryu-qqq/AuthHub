package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.api.UserApi;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.user.CreateUserWithRolesRequest;
import com.ryuqq.authhub.sdk.model.user.CreateUserWithRolesResponse;

/**
 * UserApi의 기본 구현체.
 *
 * <p>Internal User API를 호출합니다.
 */
final class DefaultUserApi implements UserApi {

    private static final String BASE_PATH = "/api/v1/internal/users";

    private final HttpClientSupport httpClient;

    DefaultUserApi(HttpClientSupport httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ApiResponse<CreateUserWithRolesResponse> createUserWithRoles(
            CreateUserWithRolesRequest request) {
        return httpClient.post(
                BASE_PATH + "/register",
                request,
                new TypeReference<ApiResponse<CreateUserWithRolesResponse>>() {});
    }
}
