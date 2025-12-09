package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * UserErrorCode - User 도메인 에러 코드
 *
 * <p>User Aggregate 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND("USER-001", 404, "User not found"),
    INVALID_USER_STATE("USER-002", 400, "Invalid user state"),
    DUPLICATE_USER_IDENTIFIER("USER-003", 409, "User identifier already exists");

    private final String code;
    private final int httpStatus;
    private final String message;

    UserErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
