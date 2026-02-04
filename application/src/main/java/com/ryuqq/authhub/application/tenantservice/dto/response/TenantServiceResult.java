package com.ryuqq.authhub.application.tenantservice.dto.response;

import java.time.Instant;

/**
 * TenantServiceResult - 테넌트-서비스 구독 조회 결과 DTO
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param tenantServiceId 테넌트-서비스 ID
 * @param tenantId 테넌트 ID
 * @param serviceId 서비스 ID
 * @param status 구독 상태 (ACTIVE, INACTIVE, SUSPENDED)
 * @param subscribedAt 구독 일시
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author development-team
 * @since 1.0.0
 */
public record TenantServiceResult(
        Long tenantServiceId,
        String tenantId,
        Long serviceId,
        String status,
        Instant subscribedAt,
        Instant createdAt,
        Instant updatedAt) {}
