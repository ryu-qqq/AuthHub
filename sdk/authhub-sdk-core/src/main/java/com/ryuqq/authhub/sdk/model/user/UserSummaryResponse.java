package com.ryuqq.authhub.sdk.model.user;

import java.time.Instant;

/** 사용자 목록 조회 응답 (Admin용). */
public record UserSummaryResponse(
        String userId,
        String tenantId,
        String tenantName,
        String organizationId,
        String organizationName,
        String identifier,
        String phoneNumber,
        String status,
        int roleCount,
        Instant createdAt,
        Instant updatedAt) {}
