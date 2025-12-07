package com.ryuqq.authhub.application.tenant.dto.query;

import java.util.UUID;

/**
 * GetTenantQuery - 테넌트 단건 조회 Query DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record
 *   <li>기본값 처리 금지 (REST API 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @param tenantId 테넌트 ID (UUID)
 * @author development-team
 * @since 1.0.0
 */
public record GetTenantQuery(UUID tenantId) {

    public static GetTenantQuery of(UUID value) {
        return new GetTenantQuery(value);
    }
}
