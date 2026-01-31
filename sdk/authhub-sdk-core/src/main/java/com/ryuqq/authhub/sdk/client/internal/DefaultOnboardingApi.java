package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingRequest;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingResponse;
import java.util.Map;

/**
 * OnboardingApi의 기본 구현체.
 *
 * <p>멱등키 필수: X-Idempotency-Key 헤더로 멱등키 전송
 */
final class DefaultOnboardingApi implements OnboardingApi {

    private static final String BASE_PATH = "/api/v1/internal/onboarding";
    private static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";

    private final HttpClientSupport httpClient;

    DefaultOnboardingApi(HttpClientSupport httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ApiResponse<TenantOnboardingResponse> onboard(
            TenantOnboardingRequest request, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey must not be null or blank");
        }
        Map<String, String> headers = Map.of(IDEMPOTENCY_KEY_HEADER, idempotencyKey);
        return httpClient.post(
                BASE_PATH,
                request,
                new TypeReference<ApiResponse<TenantOnboardingResponse>>() {},
                headers);
    }
}
