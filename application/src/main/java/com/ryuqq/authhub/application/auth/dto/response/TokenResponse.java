package com.ryuqq.authhub.application.auth.dto.response;

/**
 * Token Response DTO
 *
 * <p>토큰 갱신 성공 시 반환되는 응답 객체입니다.
 *
 * <p>LoginResponse보다 간소화된 형태 (userId 미포함)
 *
 * @author development-team
 * @since 1.0.0
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn,
        String tokenType) {}
