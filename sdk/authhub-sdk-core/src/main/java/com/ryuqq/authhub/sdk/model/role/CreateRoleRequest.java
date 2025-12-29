package com.ryuqq.authhub.sdk.model.role;

import java.util.Objects;

/** 역할 생성 요청. */
public record CreateRoleRequest(String tenantId, String name, String description) {

    public CreateRoleRequest {
        Objects.requireNonNull(tenantId, "tenantId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
