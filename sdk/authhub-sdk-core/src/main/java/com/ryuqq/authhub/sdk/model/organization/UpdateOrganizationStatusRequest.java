package com.ryuqq.authhub.sdk.model.organization;

import java.util.Objects;

/** 조직 상태 변경 요청. */
public record UpdateOrganizationStatusRequest(String status) {

    public UpdateOrganizationStatusRequest {
        Objects.requireNonNull(status, "status must not be null");
    }
}
