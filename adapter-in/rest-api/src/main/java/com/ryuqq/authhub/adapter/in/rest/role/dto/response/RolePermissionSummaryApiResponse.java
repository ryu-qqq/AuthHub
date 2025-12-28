package com.ryuqq.authhub.adapter.in.rest.role.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * RolePermissionSummaryApiResponse - 역할 상세 조회 시 포함되는 권한 요약 정보 (Admin용)
 *
 * <p>역할에 할당된 권한의 핵심 정보만 포함합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiResponse 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see RoleDetailApiResponse 상세 조회용 응답 DTO
 */
@Schema(description = "역할 권한 요약 정보")
public record RolePermissionSummaryApiResponse(
        @Schema(description = "권한 ID") String permissionId,
        @Schema(description = "권한 키") String permissionKey,
        @Schema(description = "권한 설명") String description,
        @Schema(description = "리소스") String resource,
        @Schema(description = "액션") String action) {}
