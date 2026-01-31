package com.ryuqq.authhub.domain.token.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * TokenErrorCode - Token 도메인 에러 코드
 *
 * <p>인증/토큰 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum TokenErrorCode implements ErrorCode {
    INVALID_CREDENTIALS("AUTH-001", 401, "Invalid credentials"),
    INVALID_REFRESH_TOKEN("AUTH-002", 401, "Invalid refresh token"),
    EXPIRED_REFRESH_TOKEN("AUTH-003", 401, "Refresh token has expired"),
    INVALID_ACCESS_TOKEN("AUTH-004", 401, "Invalid access token"),
    EXPIRED_ACCESS_TOKEN("AUTH-005", 401, "Access token has expired"),
    UNAUTHORIZED("AUTH-006", 401, "Unauthorized access"),
    FORBIDDEN("AUTH-007", 403, "Access forbidden");

    private final String code;
    private final int httpStatus;
    private final String message;

    TokenErrorCode(String code, int httpStatus, String message) {
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
