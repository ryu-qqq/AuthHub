package com.ryuqq.authhub.sdk.model.auth;

/**
 * 로그인 요청 DTO
 *
 * @param identifier 사용자 식별자 (이메일 또는 사용자명)
 * @param password 비밀번호
 */
public record LoginRequest(String identifier, String password) {

    public LoginRequest {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("identifier must not be blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }
    }
}
