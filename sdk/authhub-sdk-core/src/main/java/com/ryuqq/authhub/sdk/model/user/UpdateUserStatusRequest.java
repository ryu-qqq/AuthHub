package com.ryuqq.authhub.sdk.model.user;

import java.util.Objects;

/** 사용자 상태 변경 요청. */
public record UpdateUserStatusRequest(String status) {

    public UpdateUserStatusRequest {
        Objects.requireNonNull(status, "status must not be null");
        if (status.isBlank()) {
            throw new IllegalArgumentException("status must not be blank");
        }
    }
}
