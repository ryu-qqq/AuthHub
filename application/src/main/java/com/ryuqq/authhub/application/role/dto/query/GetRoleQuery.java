package com.ryuqq.authhub.application.role.dto.query;

import java.util.UUID;

/**
 * GetRoleQuery - 역할 단건 조회 Query DTO
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
 * @author development-team
 * @since 1.0.0
 */
public record GetRoleQuery(UUID roleId) {}
