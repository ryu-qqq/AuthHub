package com.ryuqq.authhub.sdk.model.role;

import java.time.Instant;

/** 역할 목록 조회 응답 (Admin용). */
public record RoleSummaryResponse(
        Long roleId,
        String tenantId,
        String tenantName,
        String name,
        String description,
        int permissionCount,
        int userCount,
        Instant createdAt,
        Instant updatedAt) {}
