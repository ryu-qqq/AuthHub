package com.ryuqq.authhub.application.tenantservice.dto.command;

/**
 * SubscribeTenantServiceCommand - 테넌트 서비스 구독 Command DTO
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
 * @param serviceId 서비스 ID (필수)
 * @author development-team
 * @since 1.0.0
 */
public record SubscribeTenantServiceCommand(String tenantId, Long serviceId) {}
