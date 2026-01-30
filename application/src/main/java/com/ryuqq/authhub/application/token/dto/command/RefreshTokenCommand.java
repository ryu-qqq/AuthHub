package com.ryuqq.authhub.application.token.dto.command;

/**
 * RefreshTokenCommand - 토큰 갱신 Command DTO
 *
 * <p>토큰 갱신 요청 데이터를 전달하는 명령 객체입니다.
 *
 * <p><strong>최소 필드 원칙:</strong> refreshToken만 포함
 *
 * @param refreshToken Refresh Token 문자열
 * @author development-team
 * @since 1.0.0
 */
public record RefreshTokenCommand(String refreshToken) {}
