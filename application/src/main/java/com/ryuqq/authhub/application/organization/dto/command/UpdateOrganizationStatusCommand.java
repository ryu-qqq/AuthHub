package com.ryuqq.authhub.application.organization.dto.command;

import java.util.UUID;

/**
 * UpdateOrganizationStatusCommand - 조직 상태 변경 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param organizationId 조직 ID (필수)
 * @param status 새로운 상태 (ACTIVE, INACTIVE)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateOrganizationStatusCommand(UUID organizationId, String status) {}
