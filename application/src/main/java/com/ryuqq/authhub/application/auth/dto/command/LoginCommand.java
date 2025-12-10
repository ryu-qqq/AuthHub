package com.ryuqq.authhub.application.auth.dto.command;

import java.util.UUID;

/**
 * Login Command DTO
 *
 * <p>사용자 로그인 요청 데이터를 전달하는 명령 객체입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record LoginCommand(UUID tenantId, String identifier, String password) {}
