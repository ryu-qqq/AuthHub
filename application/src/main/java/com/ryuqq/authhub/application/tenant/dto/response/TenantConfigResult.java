package com.ryuqq.authhub.application.tenant.dto.response;

/**
 * TenantConfigResult - Gateway용 테넌트 설정 DTO
 *
 * <p>Gateway가 테넌트 유효성 검증을 위해 필요한 설정 정보를 제공합니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param tenantId 테넌트 ID
 * @param name 테넌트 이름
 * @param status 테넌트 상태 (ACTIVE, INACTIVE)
 * @param active 활성 여부 (Gateway에서 빠른 검증용)
 * @author development-team
 * @since 1.0.0
 */
public record TenantConfigResult(String tenantId, String name, String status, boolean active) {}
