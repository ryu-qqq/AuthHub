package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.api.AuthApi;
import com.ryuqq.authhub.sdk.model.auth.ChangePasswordRequest;
import com.ryuqq.authhub.sdk.model.auth.LoginRequest;
import com.ryuqq.authhub.sdk.model.auth.LoginResponse;
import com.ryuqq.authhub.sdk.model.auth.LogoutRequest;
import com.ryuqq.authhub.sdk.model.auth.MyContextResponse;
import com.ryuqq.authhub.sdk.model.auth.RefreshTokenRequest;
import com.ryuqq.authhub.sdk.model.auth.TokenResponse;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.user.UpdateUserRequest;
import com.ryuqq.authhub.sdk.model.user.UserIdResponse;

/**
 * AuthApi의 기본 구현체.
 *
 * <p>로그인, 로그아웃, 토큰 갱신, 내 정보 조회 기능을 HTTP 클라이언트를 통해 구현합니다.
 */
final class DefaultAuthApi implements AuthApi {

    private static final String BASE_PATH = "/api/v1/auth";
    private static final String LOGIN_PATH = BASE_PATH + "/login";
    private static final String REFRESH_PATH = BASE_PATH + "/refresh";
    private static final String LOGOUT_PATH = BASE_PATH + "/logout";
    private static final String ME_PATH = BASE_PATH + "/me";
    private static final String USERS_PATH = BASE_PATH + "/users";

    private final HttpClientSupport httpClient;

    DefaultAuthApi(HttpClientSupport httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        return httpClient.postPublic(
                LOGIN_PATH, request, new TypeReference<ApiResponse<LoginResponse>>() {});
    }

    @Override
    public ApiResponse<TokenResponse> refresh(RefreshTokenRequest request) {
        return httpClient.postPublic(
                REFRESH_PATH, request, new TypeReference<ApiResponse<TokenResponse>>() {});
    }

    @Override
    public void logout(LogoutRequest request) {
        httpClient.post(LOGOUT_PATH, request, new TypeReference<ApiResponse<Void>>() {});
    }

    @Override
    public ApiResponse<MyContextResponse> getMe() {
        return httpClient.get(
                ME_PATH,
                java.util.Map.of(),
                new TypeReference<ApiResponse<MyContextResponse>>() {});
    }

    @Override
    public ApiResponse<UserIdResponse> updateUser(String userId, UpdateUserRequest request) {
        String path = USERS_PATH + "/" + userId;
        return httpClient.put(path, request, new TypeReference<ApiResponse<UserIdResponse>>() {});
    }

    @Override
    public void changePassword(String userId, ChangePasswordRequest request) {
        String path = USERS_PATH + "/" + userId + "/password";
        httpClient.put(path, request, new TypeReference<ApiResponse<Void>>() {});
    }
}
