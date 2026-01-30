package com.ryuqq.authhub.adapter.in.rest.tenant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TenantApiResponse - Tenant 조회 API Response
 *
 * <p>Tenant 조회 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param tenantId Tenant ID
 * @param name 테넌트 이름
 * @param status 테넌트 상태
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Tenant 조회 응답")
public record TenantApiResponse(
        @Schema(description = "Tenant ID", example = "550e8400-e29b-41d4-a716-446655440000")
                String tenantId,
        @Schema(description = "테넌트 이름", example = "테넌트A") String name,
        @Schema(description = "테넌트 상태", example = "ACTIVE") String status,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String updatedAt) {}
