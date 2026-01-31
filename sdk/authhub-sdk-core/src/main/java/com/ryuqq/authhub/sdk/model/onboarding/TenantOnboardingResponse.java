package com.ryuqq.authhub.sdk.model.onboarding;

/**
 * 테넌트 온보딩 응답.
 *
 * @param tenantId 생성된 테넌트 ID (UUIDv7)
 * @param organizationId 생성된 조직 ID (UUIDv7)
 */
public record TenantOnboardingResponse(String tenantId, String organizationId) {}
