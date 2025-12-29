package com.ryuqq.authhub.sdk.model.organization;

import java.util.Objects;

/** 조직 생성 요청. */
public record CreateOrganizationRequest(String tenantId, String name) {

    public CreateOrganizationRequest {
        Objects.requireNonNull(tenantId, "tenantId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
