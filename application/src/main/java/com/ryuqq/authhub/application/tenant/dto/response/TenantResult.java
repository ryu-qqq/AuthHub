package com.ryuqq.authhub.application.tenant.dto.response;

import java.time.Instant;

/**
 * TenantResult - 테넌트 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 Tenant 응답 DTO입니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param tenantId 테넌트 ID
 * @param name 테넌트 이름
 * @param status 테넌트 상태
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author development-team
 * @since 1.0.0
 */
public record TenantResult(
        String tenantId, String name, String status, Instant createdAt, Instant updatedAt) {}
