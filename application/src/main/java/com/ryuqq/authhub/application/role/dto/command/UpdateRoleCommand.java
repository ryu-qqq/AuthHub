package com.ryuqq.authhub.application.role.dto.command;

import java.util.UUID;

/**
 * UpdateRoleCommand - 역할 수정 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param roleId 역할 ID
 * @param name 새로운 역할 이름 (null이면 변경 안함)
 * @param description 새로운 역할 설명 (null이면 변경 안함)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateRoleCommand(UUID roleId, String name, String description) {}
