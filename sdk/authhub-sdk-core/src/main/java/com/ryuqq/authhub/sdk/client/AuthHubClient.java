package com.ryuqq.authhub.sdk.client;

import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.api.OrganizationApi;
import com.ryuqq.authhub.sdk.api.PermissionApi;
import com.ryuqq.authhub.sdk.api.RoleApi;
import com.ryuqq.authhub.sdk.api.TenantApi;
import com.ryuqq.authhub.sdk.api.UserApi;

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
 * // Tenant API 호출
 * TenantResponse tenant = client.tenants().create(createRequest);
 *
 * // User API 호출
 * UserResponse user = client.users().getById(userId);
 * }</pre>
 */
public interface AuthHubClient {

    /**
     * Tenant 관련 API를 반환합니다.
     *
     * @return TenantApi
     */
    TenantApi tenants();

    /**
     * Organization 관련 API를 반환합니다.
     *
     * @return OrganizationApi
     */
    OrganizationApi organizations();

    /**
     * Role 관련 API를 반환합니다.
     *
     * @return RoleApi
     */
    RoleApi roles();

    /**
     * User 관련 API를 반환합니다.
     *
     * @return UserApi
     */
    UserApi users();

    /**
     * Permission 관련 API를 반환합니다.
     *
     * @return PermissionApi
     */
    PermissionApi permissions();

    /**
     * Onboarding 관련 API를 반환합니다.
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
