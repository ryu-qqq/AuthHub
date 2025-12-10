package com.ryuqq.authhub.adapter.in.rest.permission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * PermissionApiResponse - 권한 응답 DTO
 *
 * <p>권한 조회 API의 응답 본문을 표현합니다.
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
@Schema(description = "권한 응답")
public record PermissionApiResponse(
        @Schema(description = "권한 ID") String permissionId,
        @Schema(description = "권한 키") String key,
        @Schema(description = "리소스명") String resource,
        @Schema(description = "액션명") String action,
        @Schema(description = "권한 설명") String description,
        @Schema(description = "권한 유형") String type,
        @Schema(description = "생성 일시") Instant createdAt,
        @Schema(description = "수정 일시") Instant updatedAt) {}
