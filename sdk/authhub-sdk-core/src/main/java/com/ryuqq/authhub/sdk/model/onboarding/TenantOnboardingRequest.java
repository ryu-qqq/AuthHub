package com.ryuqq.authhub.sdk.model.onboarding;

import java.util.Objects;

/**
 * 테넌트 온보딩 요청.
 *
 * <p>테넌트와 조직을 일괄 생성합니다.
 *
 * @param tenantName 테넌트 이름 (필수)
 * @param organizationName 조직 이름 (필수)
 */
public record TenantOnboardingRequest(String tenantName, String organizationName) {

    public TenantOnboardingRequest {
        Objects.requireNonNull(tenantName, "tenantName must not be null");
        Objects.requireNonNull(organizationName, "organizationName must not be null");
        if (tenantName.isBlank()) {
            throw new IllegalArgumentException("tenantName must not be blank");
        }
        if (organizationName.isBlank()) {
            throw new IllegalArgumentException("organizationName must not be blank");
        }
    }
}
