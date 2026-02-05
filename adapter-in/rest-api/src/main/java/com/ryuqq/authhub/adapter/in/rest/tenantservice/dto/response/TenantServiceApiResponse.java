package com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TenantServiceApiResponse - TenantService 조회 API Response
 *
 * <p>테넌트-서비스 구독 조회 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param tenantServiceId 테넌트-서비스 ID
 * @param tenantId 테넌트 ID
 * @param serviceId 서비스 ID
 * @param status 구독 상태 (ACTIVE, INACTIVE, SUSPENDED)
 * @param subscribedAt 구독 일시 (ISO 8601 포맷 문자열)
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "TenantService 조회 응답")
public record TenantServiceApiResponse(
        @Schema(description = "TenantService ID", example = "1") Long tenantServiceId,
        @Schema(description = "테넌트 ID", example = "550e8400-e29b-41d4-a716-446655440000")
                String tenantId,
        @Schema(description = "서비스 ID", example = "1") Long serviceId,
        @Schema(description = "구독 상태", example = "ACTIVE") String status,
        @Schema(description = "구독 일시 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String subscribedAt,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String updatedAt) {}
