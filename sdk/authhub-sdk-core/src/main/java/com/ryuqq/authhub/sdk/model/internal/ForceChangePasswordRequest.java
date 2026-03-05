package com.ryuqq.authhub.sdk.model.internal;

/**
 * 강제 비밀번호 변경 요청 DTO (Internal API / M2M 전용)
 *
 * <p>현재 비밀번호 없이 새 비밀번호만 전송합니다.
 *
 * @param newPassword 새 비밀번호
 */
public record ForceChangePasswordRequest(String newPassword) {

    public ForceChangePasswordRequest {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("newPassword must not be blank");
        }
    }
}
