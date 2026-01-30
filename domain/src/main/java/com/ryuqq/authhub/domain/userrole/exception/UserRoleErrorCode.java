package com.ryuqq.authhub.domain.userrole.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * UserRoleErrorCode - 사용자-역할 관계 도메인 에러 코드
 *
 * <p>UserRole Aggregate 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum UserRoleErrorCode implements ErrorCode {
    USER_ROLE_NOT_FOUND("USER_ROLE-001", 404, "User-Role relation not found"),
    DUPLICATE_USER_ROLE("USER_ROLE-002", 409, "User-Role relation already exists"),
    ROLE_IN_USE("USER_ROLE-003", 409, "Role is currently assigned to users and cannot be deleted");

    private final String code;
    private final int httpStatus;
    private final String message;

    UserRoleErrorCode(String code, int httpStatus, String message) {
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
