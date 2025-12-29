package com.ryuqq.authhub.sdk.client.internal;

import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.api.OrganizationApi;
import com.ryuqq.authhub.sdk.api.PermissionApi;
import com.ryuqq.authhub.sdk.api.RoleApi;
import com.ryuqq.authhub.sdk.api.TenantApi;
import com.ryuqq.authhub.sdk.api.UserApi;
import com.ryuqq.authhub.sdk.auth.TokenResolver;
import com.ryuqq.authhub.sdk.client.AuthHubClient;
import com.ryuqq.authhub.sdk.config.AuthHubConfig;

/** AuthHubClient의 기본 구현체. HTTP 클라이언트를 사용하여 AuthHub REST API를 호출합니다. */
public final class DefaultAuthHubClient implements AuthHubClient {

    private final HttpClientSupport httpClient;
    private final TenantApi tenantApi;
    private final OrganizationApi organizationApi;
    private final RoleApi roleApi;
    private final UserApi userApi;
    private final PermissionApi permissionApi;
    private final OnboardingApi onboardingApi;

    public DefaultAuthHubClient(AuthHubConfig config, TokenResolver tokenResolver) {
        this.httpClient = new HttpClientSupport(config, tokenResolver);
        this.tenantApi = new DefaultTenantApi(httpClient);
        this.organizationApi = new DefaultOrganizationApi(httpClient);
        this.roleApi = new DefaultRoleApi(httpClient);
        this.userApi = new DefaultUserApi(httpClient);
        this.permissionApi = new DefaultPermissionApi(httpClient);
        this.onboardingApi = new DefaultOnboardingApi(httpClient);
    }

    @Override
    public TenantApi tenants() {
        return tenantApi;
    }

    @Override
    public OrganizationApi organizations() {
        return organizationApi;
    }

    @Override
    public RoleApi roles() {
        return roleApi;
    }

    @Override
    public UserApi users() {
        return userApi;
    }

    @Override
    public PermissionApi permissions() {
        return permissionApi;
    }

    @Override
    public OnboardingApi onboarding() {
        return onboardingApi;
    }
}
