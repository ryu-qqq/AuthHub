package com.ryuqq.authhub.sdk.model.onboarding;

import java.util.Objects;

/**
 * 테넌트 온보딩 요청.
 *
 * <p>입점 승인 시 Tenant, Organization, User를 일괄 생성합니다.
 */
public record TenantOnboardingRequest(
        String tenantName, String organizationName, String masterEmail, String masterPhoneNumber) {

    public TenantOnboardingRequest {
        Objects.requireNonNull(tenantName, "tenantName must not be null");
        Objects.requireNonNull(organizationName, "organizationName must not be null");
        Objects.requireNonNull(masterEmail, "masterEmail must not be null");
        Objects.requireNonNull(masterPhoneNumber, "masterPhoneNumber must not be null");
        if (tenantName.isBlank()) {
            throw new IllegalArgumentException("tenantName must not be blank");
        }
        if (organizationName.isBlank()) {
            throw new IllegalArgumentException("organizationName must not be blank");
        }
        if (masterEmail.isBlank()) {
            throw new IllegalArgumentException("masterEmail must not be blank");
        }
    }
}
