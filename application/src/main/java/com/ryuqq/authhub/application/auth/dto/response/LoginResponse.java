package com.ryuqq.authhub.application.auth.dto.response;

import java.util.UUID;

/**
 * Login Response DTO
 *
 * <p>로그인 성공 시 반환되는 응답 객체입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record LoginResponse(
        UUID userId,
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType
) {
}
