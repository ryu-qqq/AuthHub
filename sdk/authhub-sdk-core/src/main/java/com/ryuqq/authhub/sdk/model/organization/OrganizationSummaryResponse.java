package com.ryuqq.authhub.sdk.model.organization;

import java.time.Instant;

/** 조직 목록 조회 응답 (Admin용). */
public record OrganizationSummaryResponse(
        String organizationId,
        String tenantId,
        String tenantName,
        String name,
        String status,
        int userCount,
        Instant createdAt,
        Instant updatedAt) {}
