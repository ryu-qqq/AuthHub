package com.ryuqq.authhub.adapter.in.rest.role.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/**
 * RolePermissionApiResponse - 역할 권한 API 응답 DTO
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
@Schema(description = "역할 권한 응답")
public record RolePermissionApiResponse(
        @Schema(description = "역할 ID") UUID roleId,
        @Schema(description = "권한 ID") UUID permissionId,
        @Schema(description = "권한 부여 일시") Instant grantedAt) {}
