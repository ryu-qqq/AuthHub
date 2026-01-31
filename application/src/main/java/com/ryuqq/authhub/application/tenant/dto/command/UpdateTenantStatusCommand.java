package com.ryuqq.authhub.application.tenant.dto.command;

/**
 * UpdateTenantStatusCommand - 테넌트 상태 변경 Command DTO
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
 * @param status 새로운 상태 (ACTIVE, INACTIVE)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateTenantStatusCommand(String tenantId, String status) {}
