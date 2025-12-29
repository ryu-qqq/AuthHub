package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingRequest;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingResponse;

/** OnboardingApi의 기본 구현체. */
final class DefaultOnboardingApi implements OnboardingApi {

    private static final String BASE_PATH = "/api/v1/system/onboarding";

    private final HttpClientSupport httpClient;

    DefaultOnboardingApi(HttpClientSupport httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ApiResponse<TenantOnboardingResponse> onboard(TenantOnboardingRequest request) {
        return httpClient.post(
                BASE_PATH, request, new TypeReference<ApiResponse<TenantOnboardingResponse>>() {});
    }
}
