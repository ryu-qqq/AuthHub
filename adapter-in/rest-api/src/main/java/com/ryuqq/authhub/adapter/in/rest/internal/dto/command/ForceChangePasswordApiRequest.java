package com.ryuqq.authhub.adapter.in.rest.internal.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ForceChangePasswordApiRequest - 강제 비밀번호 변경 요청 DTO
 *
 * <p>Internal API (M2M)용 비밀번호 변경 요청입니다. 현재 비밀번호 없이 새 비밀번호만 받습니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "강제 비밀번호 변경 요청 (Internal)")
public record ForceChangePasswordApiRequest(
        @Schema(
                        description = "새 비밀번호",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 8,
                        maxLength = 100)
                @NotBlank(message = "새 비밀번호는 필수입니다")
                @Size(min = 8, max = 100, message = "새 비밀번호는 8자 이상 100자 이하여야 합니다")
                String newPassword) {}
