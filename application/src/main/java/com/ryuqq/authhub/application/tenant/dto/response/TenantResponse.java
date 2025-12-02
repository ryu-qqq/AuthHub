package com.ryuqq.authhub.application.tenant.dto.response;

import java.time.Instant;

/**
 * Tenant Response DTO
 *
 * <p>테넌트 정보 조회 시 반환되는 응답 객체입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record TenantResponse(
        Long tenantId,
        String name,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
