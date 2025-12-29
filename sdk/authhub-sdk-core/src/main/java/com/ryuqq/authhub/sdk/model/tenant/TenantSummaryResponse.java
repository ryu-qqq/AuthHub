package com.ryuqq.authhub.sdk.model.tenant;

import java.time.Instant;

/** 테넌트 목록 조회 응답 (Admin용). */
public record TenantSummaryResponse(
        String tenantId,
        String name,
        String status,
        int organizationCount,
        Instant createdAt,
        Instant updatedAt) {}
