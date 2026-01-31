package com.ryuqq.authhub.sdk.api;

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
 * 인증 관련 API 인터페이스.
 *
 * <p>로그인, 로그아웃, 토큰 갱신, 내 정보 조회 기능을 제공합니다.
 */
public interface AuthApi {

    /**
     * 로그인을 수행합니다.
     *
     * <p>이메일/사용자명과 비밀번호로 로그인하여 액세스 토큰과 리프레시 토큰을 발급받습니다.
     *
     * @param request 로그인 요청 (identifier, password)
     * @return 로그인 응답 (userId, accessToken, refreshToken, expiresIn, tokenType)
     */
    ApiResponse<LoginResponse> login(LoginRequest request);

    /**
     * 토큰을 갱신합니다.
     *
     * <p>리프레시 토큰으로 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다.
     *
     * @param request 토큰 갱신 요청 (refreshToken)
     * @return 토큰 응답 (accessToken, refreshToken, expiresIn)
     */
    ApiResponse<TokenResponse> refresh(RefreshTokenRequest request);

    /**
     * 로그아웃을 수행합니다.
     *
     * <p>리프레시 토큰을 무효화하여 로그아웃합니다.
     *
     * @param request 로그아웃 요청 (userId)
     */
    void logout(LogoutRequest request);

    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     *
     * <p>테넌트, 조직, 역할, 권한 정보를 포함한 사용자 컨텍스트를 반환합니다.
     *
     * @return 사용자 컨텍스트 응답
     */
    ApiResponse<MyContextResponse> getMe();

    /**
     * 사용자 정보를 수정합니다.
     *
     * <p>전화번호 등 사용자 정보를 수정합니다.
     *
     * @param userId 사용자 ID
     * @param request 수정 요청 (phoneNumber)
     * @return 수정된 사용자 ID
     */
    ApiResponse<UserIdResponse> updateUser(String userId, UpdateUserRequest request);
}
