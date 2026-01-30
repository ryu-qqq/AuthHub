package com.ryuqq.authhub.adapter.in.rest.token.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 로그아웃 API 요청 DTO
 *
 * <p>사용자 로그아웃 요청 데이터를 전달합니다.
 *
 * @param userId 사용자 ID (UUIDv7 문자열)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "로그아웃 요청")
public record LogoutApiRequest(
        @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "사용자 ID는 필수입니다")
                String userId) {}
