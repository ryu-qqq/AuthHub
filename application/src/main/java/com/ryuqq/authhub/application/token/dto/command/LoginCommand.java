package com.ryuqq.authhub.application.token.dto.command;

/**
 * LoginCommand - 로그인 Command DTO
 *
 * <p>사용자 로그인 요청 데이터를 전달하는 명령 객체입니다.
 *
 * <p>테넌트는 사용자 조회 후 자동으로 결정됩니다.
 *
 * @param identifier 사용자 식별자 (이메일)
 * @param password 비밀번호
 * @author development-team
 * @since 1.0.0
 */
public record LoginCommand(String identifier, String password) {}
