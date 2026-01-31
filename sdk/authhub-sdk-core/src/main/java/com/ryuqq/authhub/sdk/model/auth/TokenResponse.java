package com.ryuqq.authhub.sdk.model.auth;

/**
 * 토큰 갱신 응답 DTO
 *
 * @param accessToken 액세스 토큰
 * @param refreshToken 리프레시 토큰
 * @param accessTokenExpiresIn 액세스 토큰 만료 시간(초)
 * @param refreshTokenExpiresIn 리프레시 토큰 만료 시간(초)
 * @param tokenType 토큰 타입 (Bearer)
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn,
        String tokenType) {}
