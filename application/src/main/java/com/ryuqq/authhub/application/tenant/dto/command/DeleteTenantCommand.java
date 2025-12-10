package com.ryuqq.authhub.application.tenant.dto.command;

import java.util.UUID;

/**
 * DeleteTenantCommand - 테넌트 삭제 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param tenantId 테넌트 ID (필수)
 * @author development-team
 * @since 1.0.0
 */
public record DeleteTenantCommand(UUID tenantId) {}
