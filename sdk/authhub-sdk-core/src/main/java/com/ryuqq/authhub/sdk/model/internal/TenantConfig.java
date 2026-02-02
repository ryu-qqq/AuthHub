package com.ryuqq.authhub.sdk.model.internal;

/**
 * 테넌트 설정 모델.
 *
 * <p>Gateway가 테넌트 유효성 검증을 위해 사용합니다.
 *
 * @param tenantId 테넌트 ID
 * @param name 테넌트 이름
 * @param status 테넌트 상태 (ACTIVE, INACTIVE)
 * @param active 활성 여부 (Gateway에서 빠른 검증용)
 */
public record TenantConfig(String tenantId, String name, String status, boolean active) {}
