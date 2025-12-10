package com.ryuqq.authhub.adapter.in.rest.user.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * AssignUserRoleApiRequest - 사용자 역할 할당 API 요청 DTO
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
@Schema(description = "사용자 역할 할당 요청")
public record AssignUserRoleApiRequest(
        @Schema(description = "할당할 역할 ID", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "역할 ID는 필수입니다")
                UUID roleId) {}
