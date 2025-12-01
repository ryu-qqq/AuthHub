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
    INVALID_USER_ID("USER-002", 400, "Invalid user ID"),
    INVALID_USER_TYPE("USER-003", 400, "Invalid user type"),
    INVALID_USER_STATUS("USER-004", 400, "Invalid user status"),
    INVALID_USER_STATE("USER-005", 400, "Invalid user state"),
    DUPLICATE_EMAIL("USER-006", 409, "Email already exists"),
    DUPLICATE_PHONE_NUMBER("USER-007", 409, "Phone number already exists"),
    INVALID_PASSWORD("USER-008", 400, "Invalid password"),
    INVALID_PHONE_NUMBER("USER-009", 400, "Invalid phone number format");

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
