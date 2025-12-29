package com.ryuqq.authhub.sdk.model.onboarding;

/** 테넌트 온보딩 응답. */
public record TenantOnboardingResponse(
        String tenantId, String organizationId, String userId, String temporaryPassword) {}
