package com.ryuqq.authhub.application.token.dto.response;

/**
 * TokenResponse - 토큰 갱신 Response DTO
 *
 * <p>토큰 갱신 성공 시 반환되는 응답 객체입니다.
 *
 * <p>LoginResponse보다 간소화된 형태 (userId 미포함)
 *
 * @param accessToken Access Token 문자열
 * @param refreshToken Refresh Token 문자열
 * @param accessTokenExpiresIn Access Token 만료 시간 (초)
 * @param refreshTokenExpiresIn Refresh Token 만료 시간 (초)
 * @param tokenType 토큰 타입 (Bearer)
 * @author development-team
 * @since 1.0.0
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn,
        String tokenType) {}
