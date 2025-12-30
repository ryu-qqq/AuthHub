package com.ryuqq.authhub.sdk.model.permission;

import java.time.Instant;

/** 권한 응답. */
public record PermissionResponse(
        String permissionId,
        String resource,
        String action,
        String description,
        boolean isSystem,
        Instant createdAt,
        Instant updatedAt) {}
