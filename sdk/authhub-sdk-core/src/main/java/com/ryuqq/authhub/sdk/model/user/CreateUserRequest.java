package com.ryuqq.authhub.sdk.model.user;

import java.util.Objects;

/** 사용자 생성 요청. */
public record CreateUserRequest(
        String tenantId,
        String organizationId,
        String identifier,
        String phoneNumber,
        String password) {

    public CreateUserRequest {
        Objects.requireNonNull(tenantId, "tenantId must not be null");
        Objects.requireNonNull(organizationId, "organizationId must not be null");
        Objects.requireNonNull(identifier, "identifier must not be null");
        Objects.requireNonNull(phoneNumber, "phoneNumber must not be null");
        Objects.requireNonNull(password, "password must not be null");
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("identifier must not be blank");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("password must be at least 8 characters");
        }
    }
}
