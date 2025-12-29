package com.ryuqq.authhub.sdk.model.organization;

import java.util.Objects;

/** 조직 수정 요청. */
public record UpdateOrganizationRequest(String name) {

    public UpdateOrganizationRequest {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
