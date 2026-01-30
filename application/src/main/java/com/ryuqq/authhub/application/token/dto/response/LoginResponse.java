package com.ryuqq.authhub.application.token.dto.response;

/**
 * LoginResponse - 로그인 Response DTO
 *
 * <p>로그인 성공 시 반환되는 응답 객체입니다.
 *
 * @param userId 사용자 ID (UUIDv7 String)
 * @param accessToken Access Token 문자열
 * @param refreshToken Refresh Token 문자열
 * @param expiresIn Access Token 만료 시간 (초)
 * @param tokenType 토큰 타입 (Bearer)
 * @author development-team
 * @since 1.0.0
 */
public record LoginResponse(
        String userId, String accessToken, String refreshToken, Long expiresIn, String tokenType) {}
