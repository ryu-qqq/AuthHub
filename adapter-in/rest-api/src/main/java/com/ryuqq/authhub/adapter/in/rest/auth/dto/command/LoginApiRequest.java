package com.ryuqq.authhub.adapter.in.rest.auth.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 로그인 API 요청 DTO
 *
 * <p>사용자 로그인 요청 데이터를 전달합니다.
 *
 * @param tenantId 테넌트 ID
 * @param identifier 사용자 식별자 (이메일 또는 사용자명)
 * @param password 비밀번호
 * @author development-team
 * @since 1.0.0
 */
public record LoginApiRequest(
        @NotNull(message = "테넌트 ID는 필수입니다")
        Long tenantId,

        @NotBlank(message = "식별자는 필수입니다")
        String identifier,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password
) {}
