package com.ryuqq.authhub.sdk.model.role;

import java.util.Objects;

/** 역할 수정 요청. */
public record UpdateRoleRequest(String name, String description) {

    public UpdateRoleRequest {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
