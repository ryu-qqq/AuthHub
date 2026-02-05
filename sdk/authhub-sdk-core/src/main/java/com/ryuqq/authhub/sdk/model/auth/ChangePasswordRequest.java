package com.ryuqq.authhub.sdk.model.auth;

/**
 * 비밀번호 변경 요청 DTO
 *
 * @param currentPassword 현재 비밀번호
 * @param newPassword 새 비밀번호
 */
public record ChangePasswordRequest(String currentPassword, String newPassword) {

    public ChangePasswordRequest {
        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("currentPassword must not be blank");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("newPassword must not be blank");
        }
    }
}
