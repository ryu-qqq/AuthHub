package com.ryuqq.authhub.sdk.model.tenant;

import java.time.Instant;

/**
 * 테넌트 상세 조회 시 포함되는 조직 요약 정보.
 *
 * @param organizationId 조직 ID
 * @param name 조직 이름
 * @param status 조직 상태 (ACTIVE, INACTIVE, SUSPENDED)
 * @param createdAt 생성 일시
 */
public record TenantOrganizationSummaryResponse(
        String organizationId, String name, String status, Instant createdAt) {}
