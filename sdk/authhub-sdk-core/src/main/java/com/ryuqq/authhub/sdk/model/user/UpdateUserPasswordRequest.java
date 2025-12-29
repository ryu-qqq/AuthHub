package com.ryuqq.authhub.sdk.model.user;

import java.util.Objects;

/** 사용자 비밀번호 변경 요청. */
public record UpdateUserPasswordRequest(String currentPassword, String newPassword) {

    public UpdateUserPasswordRequest {
        Objects.requireNonNull(currentPassword, "currentPassword must not be null");
        Objects.requireNonNull(newPassword, "newPassword must not be null");
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("newPassword must be at least 8 characters");
        }
    }
}
