package com.ryuqq.authhub.application.token.dto.command;

/**
 * LogoutCommand - 로그아웃 Command DTO
 *
 * <p>사용자 로그아웃 요청 데이터를 전달하는 명령 객체입니다.
 *
 * @param userId 사용자 ID (UUIDv7 String)
 * @author development-team
 * @since 1.0.0
 */
public record LogoutCommand(String userId) {}
