package com.ryuqq.authhub.application.organization.dto.command;

/**
 * UpdateOrganizationNameCommand - 조직 이름 변경 Command DTO
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
 * @param name 새로운 조직 이름 (필수)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateOrganizationNameCommand(String organizationId, String name) {}
