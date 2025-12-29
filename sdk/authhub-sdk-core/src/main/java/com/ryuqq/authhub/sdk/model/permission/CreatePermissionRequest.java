package com.ryuqq.authhub.sdk.model.permission;

import java.util.Objects;

/** 권한 생성 요청. */
public record CreatePermissionRequest(
        String resource, String action, String description, Boolean isSystem) {

    public CreatePermissionRequest {
        Objects.requireNonNull(resource, "resource must not be null");
        Objects.requireNonNull(action, "action must not be null");
        if (resource.isBlank()) {
            throw new IllegalArgumentException("resource must not be blank");
        }
        if (action.isBlank()) {
            throw new IllegalArgumentException("action must not be blank");
        }
    }
}
