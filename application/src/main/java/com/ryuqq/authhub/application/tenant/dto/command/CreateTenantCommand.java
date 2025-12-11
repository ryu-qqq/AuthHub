package com.ryuqq.authhub.application.tenant.dto.command;

/**
 * CreateTenantCommand - 테넌트 생성 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param name 테넌트 이름 (필수)
 * @author development-team
 * @since 1.0.0
 */
public record CreateTenantCommand(String name) {}
