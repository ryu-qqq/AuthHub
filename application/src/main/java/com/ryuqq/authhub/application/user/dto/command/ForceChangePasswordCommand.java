package com.ryuqq.authhub.application.user.dto.command;

/**
 * ForceChangePasswordCommand - 강제 비밀번호 변경 Command
 *
 * <p>현재 비밀번호 검증 없이 비밀번호를 변경합니다. Internal API (M2M) 전용입니다.
 *
 * @param userId 대상 사용자 ID
 * @param newPassword 새 비밀번호 (평문)
 * @author development-team
 * @since 1.0.0
 */
public record ForceChangePasswordCommand(String userId, String newPassword) {}
