package com.ryuqq.authhub.adapter.in.rest.role.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * RoleApiResponse - Role 조회 API Response
 *
 * <p>Role 조회 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param roleId 역할 ID
 * @param tenantId 테넌트 ID (null이면 Global)
 * @param serviceId 서비스 ID (null이면 서비스 무관)
 * @param name 역할 이름
 * @param displayName 표시 이름
 * @param description 역할 설명
 * @param type 역할 유형 (SYSTEM, CUSTOM)
 * @param scope 역할 범위 (GLOBAL, SERVICE, TENANT, TENANT_SERVICE)
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Role 조회 응답")
public record RoleApiResponse(
        @Schema(description = "Role ID", example = "1") Long roleId,
        @Schema(description = "테넌트 ID", example = "550e8400-e29b-41d4-a716-446655440000")
                String tenantId,
        @Schema(description = "서비스 ID", example = "1") Long serviceId,
        @Schema(description = "역할 이름", example = "USER_MANAGER") String name,
        @Schema(description = "표시 이름", example = "사용자 관리자") String displayName,
        @Schema(description = "역할 설명", example = "사용자 관리 권한을 가진 역할") String description,
        @Schema(description = "역할 유형", example = "CUSTOM") String type,
        @Schema(description = "역할 범위", example = "TENANT") String scope,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String updatedAt) {}
