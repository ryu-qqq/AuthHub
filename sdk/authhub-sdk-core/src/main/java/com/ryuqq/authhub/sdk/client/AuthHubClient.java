package com.ryuqq.authhub.sdk.client;

import com.ryuqq.authhub.sdk.api.AuthApi;
import com.ryuqq.authhub.sdk.api.OnboardingApi;

/**
 * AuthHub REST API 클라이언트. 모든 AuthHub API에 대한 단일 진입점을 제공합니다.
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * AuthHubClient client = AuthHubClient.builder()
 *     .baseUrl("https://authhub.example.com")
 *     .serviceToken("your-service-token")
 *     .build();
 *
 * // 로그인
 * ApiResponse<LoginResponse> loginResponse = client.auth().login(
 *     new LoginRequest("user@example.com", "password"));
 *
 * // 토큰 갱신
 * ApiResponse<TokenResponse> tokenResponse = client.auth().refresh(
 *     new RefreshTokenRequest(refreshToken));
 *
 * // 내 정보 조회
 * ApiResponse<MyContextResponse> me = client.auth().getMe();
 *
 * // 온보딩 (테넌트 + 조직 일괄 생성)
 * ApiResponse<TenantOnboardingResponse> result = client.onboarding().onboard(request);
 * }</pre>
 */
public interface AuthHubClient {

    /**
     * 인증 관련 API를 반환합니다.
     *
     * <p>로그인, 로그아웃, 토큰 갱신, 내 정보 조회 기능을 제공합니다.
     *
     * @return AuthApi
     */
    AuthApi auth();

    /**
     * Onboarding 관련 API를 반환합니다.
     *
     * <p>테넌트와 조직을 일괄 생성합니다.
     *
     * @return OnboardingApi
     */
    OnboardingApi onboarding();

    /**
     * AuthHubClient Builder를 반환합니다.
     *
     * @return AuthHubClientBuilder
     */
    static AuthHubClientBuilder builder() {
        return new AuthHubClientBuilder();
    }
}
