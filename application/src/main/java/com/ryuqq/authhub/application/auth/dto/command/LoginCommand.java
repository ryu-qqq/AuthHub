package com.ryuqq.authhub.application.auth.dto.command;

/**
 * Login Command DTO
 *
 * <p>사용자 로그인 요청 데이터를 전달하는 명령 객체입니다.
 *
 * <p>테넌트는 사용자 조회 후 자동으로 결정됩니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record LoginCommand(String identifier, String password) {}
