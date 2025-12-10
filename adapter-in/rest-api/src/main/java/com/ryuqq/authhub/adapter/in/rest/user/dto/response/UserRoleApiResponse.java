package com.ryuqq.authhub.adapter.in.rest.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/**
 * UserRoleApiResponse - 사용자 역할 API 응답 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>불변 객체
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "사용자 역할 응답")
public record UserRoleApiResponse(
        @Schema(description = "사용자 ID") UUID userId,
        @Schema(description = "역할 ID") UUID roleId,
        @Schema(description = "역할 할당 일시") Instant assignedAt) {}
