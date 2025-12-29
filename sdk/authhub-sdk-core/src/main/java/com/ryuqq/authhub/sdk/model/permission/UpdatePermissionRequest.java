package com.ryuqq.authhub.sdk.model.permission;

import java.util.Objects;

/** 권한 수정 요청. */
public record UpdatePermissionRequest(String description) {

    public UpdatePermissionRequest {
        Objects.requireNonNull(description, "description must not be null");
        if (description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
    }
}
