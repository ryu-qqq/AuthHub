package com.ryuqq.authhub.adapter.in.rest.permission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * PermissionApiResponse - Permission 조회 API Response (Global Only)
 *
 * <p>Permission 조회 REST API 응답 DTO입니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 필드가 제거되었습니다
 * </ul>
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param permissionId 권한 ID
 * @param permissionKey 권한 키 (예: "user:read")
 * @param resource 리소스명
 * @param action 행위명
 * @param description 권한 설명
 * @param type 권한 유형 (SYSTEM, CUSTOM)
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Permission 조회 응답")
public record PermissionApiResponse(
        @Schema(description = "Permission ID", example = "1") Long permissionId,
        @Schema(description = "권한 키", example = "user:read") String permissionKey,
        @Schema(description = "리소스명", example = "user") String resource,
        @Schema(description = "행위명", example = "read") String action,
        @Schema(description = "권한 설명", example = "사용자 조회 권한") String description,
        @Schema(description = "권한 유형", example = "CUSTOM") String type,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String updatedAt) {}
