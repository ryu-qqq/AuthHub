package com.ryuqq.authhub.application.role.dto.command;

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
 * @param roleId 역할 ID (필수)
 * @param displayName 표시 이름 (null이면 변경하지 않음)
 * @param description 역할 설명 (null이면 변경하지 않음)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateRoleCommand(Long roleId, String displayName, String description) {}
