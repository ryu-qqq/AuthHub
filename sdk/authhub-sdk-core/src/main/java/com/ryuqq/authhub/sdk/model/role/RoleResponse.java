package com.ryuqq.authhub.sdk.model.role;

import java.time.Instant;

/** 역할 응답. */
public record RoleResponse(
        String roleId,
        String tenantId,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt) {}
