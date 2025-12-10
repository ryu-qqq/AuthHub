package com.ryuqq.authhub.adapter.in.rest.auth.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 로그인 API 요청 DTO
 *
 * <p>사용자 로그인 요청 데이터를 전달합니다.
 *
 * @param tenantId 테넌트 ID (UUID)
 * @param identifier 사용자 식별자 (이메일 또는 사용자명)
 * @param password 비밀번호
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "로그인 요청")
public record LoginApiRequest(
        @Schema(description = "테넌트 ID", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "테넌트 ID는 필수입니다")
                UUID tenantId,
        @Schema(description = "사용자 식별자 (이메일 또는 사용자명)", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "식별자는 필수입니다")
                String identifier,
        @Schema(description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "비밀번호는 필수입니다")
                String password) {}
