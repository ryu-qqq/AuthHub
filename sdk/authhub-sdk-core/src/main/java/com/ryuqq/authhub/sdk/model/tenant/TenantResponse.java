package com.ryuqq.authhub.sdk.model.tenant;

import java.time.Instant;

/** 테넌트 응답. */
public record TenantResponse(
        String tenantId, String name, String status, Instant createdAt, Instant updatedAt) {}
