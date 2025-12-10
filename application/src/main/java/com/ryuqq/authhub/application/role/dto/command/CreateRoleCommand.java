package com.ryuqq.authhub.application.role.dto.command;

import java.util.UUID;

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
 * @param tenantId 테넌트 ID (GLOBAL 범위 역할인 경우 null)
 * @param name 역할 이름
 * @param description 역할 설명 (선택)
 * @param scope 역할 범위 (GLOBAL, TENANT, ORGANIZATION)
 * @param isSystem 시스템 역할 여부
 * @author development-team
 * @since 1.0.0
 */
public record CreateRoleCommand(
        UUID tenantId, String name, String description, String scope, boolean isSystem) {}
