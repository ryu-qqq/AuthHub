package com.ryuqq.authhub.sdk.model.tenant;

import java.util.Objects;

/** 테넌트 이름 수정 요청. */
public record UpdateTenantNameRequest(String name) {

    public UpdateTenantNameRequest {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
