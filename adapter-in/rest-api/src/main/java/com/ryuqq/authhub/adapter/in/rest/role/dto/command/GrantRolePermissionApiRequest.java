package com.ryuqq.authhub.adapter.in.rest.role.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * GrantRolePermissionApiRequest - 역할에 권한 부여 API 요청 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>{@code @Valid} 검증 어노테이션 필수
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "역할 권한 부여 요청")
public record GrantRolePermissionApiRequest(
        @Schema(description = "부여할 권한 ID", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "권한 ID는 필수입니다")
                UUID permissionId) {}
