package com.ryuqq.authhub.sdk.model.tenant;

import java.util.Objects;

/** 테넌트 상태 변경 요청. */
public record UpdateTenantStatusRequest(String status) {

    public UpdateTenantStatusRequest {
        Objects.requireNonNull(status, "status must not be null");
    }
}
