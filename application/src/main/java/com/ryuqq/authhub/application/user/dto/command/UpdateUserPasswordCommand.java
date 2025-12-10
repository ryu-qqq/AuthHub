package com.ryuqq.authhub.application.user.dto.command;

import java.util.UUID;

/**
 * UpdateUserPasswordCommand - 비밀번호 변경 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param userId 사용자 ID
 * @param currentPassword 현재 비밀번호
 * @param newPassword 새 비밀번호
 * @author development-team
 * @since 1.0.0
 */
public record UpdateUserPasswordCommand(UUID userId, String currentPassword, String newPassword) {}
