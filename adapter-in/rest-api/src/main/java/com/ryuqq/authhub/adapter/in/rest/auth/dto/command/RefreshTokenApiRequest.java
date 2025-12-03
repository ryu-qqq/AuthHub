package com.ryuqq.authhub.adapter.in.rest.auth.dto.command;

import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 갱신 API 요청 DTO
 *
 * <p>리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.
 *
 * @param refreshToken 리프레시 토큰
 * @author development-team
 * @since 1.0.0
 */
public record RefreshTokenApiRequest(
        @NotBlank(message = "리프레시 토큰은 필수입니다")
        String refreshToken
) {}
