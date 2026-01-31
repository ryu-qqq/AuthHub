package com.ryuqq.authhub.sdk.model.auth;

/**
 * 로그아웃 요청 DTO
 *
 * @param userId 사용자 ID (UUIDv7 문자열)
 */
public record LogoutRequest(String userId) {

    public LogoutRequest {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
    }
}
