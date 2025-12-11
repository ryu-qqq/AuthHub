package com.ryuqq.authhub.application.user.dto.command;

import java.util.UUID;

/**
 * UpdateUserCommand - 사용자 수정 Command DTO
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
 * @param identifier 새 식별자 (선택, null이면 변경 안함)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateUserCommand(UUID userId, String identifier) {}
