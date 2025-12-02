package com.ryuqq.authhub.application.organization.dto.response;

import java.time.Instant;

/**
 * Organization Response DTO
 *
 * <p>조직 정보 조회 시 반환되는 응답 객체입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record OrganizationResponse(
        Long organizationId,
        Long tenantId,
        String name,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
