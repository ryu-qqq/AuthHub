package com.ryuqq.authhub.application.user.dto.command;

/**
 * ChangePasswordCommand - 비밀번호 변경 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param userId 대상 사용자 ID (필수)
 * @param currentPassword 현재 비밀번호 (필수)
 * @param newPassword 새 비밀번호 (필수)
 * @author development-team
 * @since 1.0.0
 */
public record ChangePasswordCommand(String userId, String currentPassword, String newPassword) {}
