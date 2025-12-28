package com.ryuqq.authhub.adapter.in.rest.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * UserRoleSummaryApiResponse - 사용자 상세 조회 시 포함되는 역할 요약 정보 (Admin용)
 *
 * <p>사용자에게 할당된 역할의 핵심 정보만 포함합니다.
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
 * @see UserDetailApiResponse 상세 조회용 응답 DTO
 */
@Schema(description = "사용자 역할 요약 정보")
public record UserRoleSummaryApiResponse(
        @Schema(description = "역할 ID") String roleId,
        @Schema(description = "역할 이름") String name,
        @Schema(description = "역할 설명") String description,
        @Schema(description = "역할 범위 (GLOBAL, TENANT, ORGANIZATION)") String scope,
        @Schema(description = "역할 유형 (SYSTEM, CUSTOM)") String type) {}
