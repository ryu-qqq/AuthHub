package com.ryuqq.authhub.sdk.client.internal;

import com.ryuqq.authhub.sdk.api.AuthApi;
import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.api.UserApi;
import com.ryuqq.authhub.sdk.auth.TokenResolver;
import com.ryuqq.authhub.sdk.client.AuthHubClient;
import com.ryuqq.authhub.sdk.config.AuthHubConfig;

/** AuthHubClient의 기본 구현체. HTTP 클라이언트를 사용하여 AuthHub REST API를 호출합니다. */
public final class DefaultAuthHubClient implements AuthHubClient {

    private final AuthApi authApi;
    private final OnboardingApi onboardingApi;
    private final UserApi userApi;

    public DefaultAuthHubClient(AuthHubConfig config, TokenResolver tokenResolver) {
        HttpClientSupport httpClient = new HttpClientSupport(config, tokenResolver);
        this.authApi = new DefaultAuthApi(httpClient);
        this.onboardingApi = new DefaultOnboardingApi(httpClient);
        this.userApi = new DefaultUserApi(httpClient);
    }

    @Override
    public AuthApi auth() {
        return authApi;
    }

    @Override
    public OnboardingApi onboarding() {
        return onboardingApi;
    }

    @Override
    public UserApi user() {
        return userApi;
    }
}
