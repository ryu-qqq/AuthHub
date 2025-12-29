package com.ryuqq.authhub.sdk.model.organization;

import java.time.Instant;

/** 조직 응답. */
public record OrganizationResponse(
        String organizationId,
        String tenantId,
        String name,
        String status,
        Instant createdAt,
        Instant updatedAt) {}
