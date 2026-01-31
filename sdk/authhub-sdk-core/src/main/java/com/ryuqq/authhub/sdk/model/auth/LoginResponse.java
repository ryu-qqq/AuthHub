package com.ryuqq.authhub.sdk.model.auth;

/**
 * 로그인 응답 DTO
 *
 * @param userId 사용자 ID (UUIDv7 문자열)
 * @param accessToken 액세스 토큰
 * @param refreshToken 리프레시 토큰
 * @param expiresIn 액세스 토큰 만료 시간(초)
 * @param tokenType 토큰 타입 (Bearer)
 */
public record LoginResponse(
        String userId, String accessToken, String refreshToken, Long expiresIn, String tokenType) {}
