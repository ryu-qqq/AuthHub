package com.ryuqq.authhub.application.user.dto.command;

import java.util.UUID;

/**
 * UpdateUserStatusCommand - 사용자 상태 변경 Command DTO
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
 * @param status 새 상태 (ACTIVE, INACTIVE, SUSPENDED)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateUserStatusCommand(UUID userId, String status) {}
