package com.ryuqq.authhub.sdk.model.tenant;

import java.util.Objects;

/** 테넌트 생성 요청. */
public record CreateTenantRequest(String name) {

    public CreateTenantRequest {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
