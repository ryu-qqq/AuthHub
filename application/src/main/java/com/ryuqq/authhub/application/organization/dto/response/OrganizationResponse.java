package com.ryuqq.authhub.application.organization.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * OrganizationResponse - 조직 응답 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record
 *   <li>Jackson 어노테이션 금지 (REST API 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @param organizationId 조직 ID
 * @param tenantId 테넌트 ID
 * @param name 조직 이름
 * @param status 조직 상태
 * @param createdAt 생성 시간
 * @param updatedAt 수정 시간
 * @author development-team
 * @since 1.0.0
 */
public record OrganizationResponse(
        UUID organizationId,
        UUID tenantId,
        String name,
        String status,
        Instant createdAt,
        Instant updatedAt) {}
