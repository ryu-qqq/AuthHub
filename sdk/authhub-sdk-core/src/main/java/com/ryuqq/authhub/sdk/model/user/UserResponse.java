package com.ryuqq.authhub.sdk.model.user;

import java.time.Instant;

/** 사용자 응답. */
public record UserResponse(
        String userId,
        String tenantId,
        String organizationId,
        String identifier,
        String phoneNumber,
        String status,
        Instant createdAt,
        Instant updatedAt) {}
