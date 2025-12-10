package com.ryuqq.authhub.adapter.in.rest.role.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * RoleApiResponse - 역할 응답 DTO
 *
 * <p>역할 조회 API의 응답 본문을 표현합니다.
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
 */
@Schema(description = "역할 응답")
public record RoleApiResponse(
        @Schema(description = "역할 ID") String roleId,
        @Schema(description = "테넌트 ID") String tenantId,
        @Schema(description = "역할 이름") String name,
        @Schema(description = "역할 설명") String description,
        @Schema(description = "역할 범위") String scope,
        @Schema(description = "역할 유형") String type,
        @Schema(description = "생성 일시") Instant createdAt,
        @Schema(description = "수정 일시") Instant updatedAt) {}
