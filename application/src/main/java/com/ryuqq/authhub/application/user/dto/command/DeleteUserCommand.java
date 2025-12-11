package com.ryuqq.authhub.application.user.dto.command;

import java.util.UUID;

/**
 * DeleteUserCommand - 사용자 삭제 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param userId 삭제할 사용자 ID
 * @author development-team
 * @since 1.0.0
 */
public record DeleteUserCommand(UUID userId) {}
