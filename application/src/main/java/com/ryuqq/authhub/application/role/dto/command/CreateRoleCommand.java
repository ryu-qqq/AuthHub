package com.ryuqq.authhub.application.role.dto.command;

/**
 * CreateRoleCommand - 역할 생성 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param tenantId 테넌트 ID (null이면 Global 역할)
 * @param name 역할 이름 (필수, 예: USER_MANAGER)
 * @param displayName 표시 이름 (선택, 예: "사용자 관리자")
 * @param description 역할 설명 (선택)
 * @param isSystem 시스템 역할 여부 (true면 SYSTEM, false면 CUSTOM)
 * @author development-team
 * @since 1.0.0
 */
public record CreateRoleCommand(
        String tenantId,
        Long serviceId,
        String name,
        String displayName,
        String description,
        boolean isSystem) {}
