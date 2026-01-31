package com.ryuqq.authhub.sdk.model.auth;

/**
 * 토큰 갱신 요청 DTO
 *
 * @param refreshToken 리프레시 토큰
 */
public record RefreshTokenRequest(String refreshToken) {

    public RefreshTokenRequest {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("refreshToken must not be blank");
        }
    }
}
