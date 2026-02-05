package com.ryuqq.authhub.domain.service.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * ServiceErrorCode - Service 도메인 에러 코드
 *
 * <p>Service Aggregate 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum ServiceErrorCode implements ErrorCode {
    SERVICE_NOT_FOUND("SERVICE-001", 404, "Service not found"),
    INVALID_SERVICE_STATE("SERVICE-002", 400, "Invalid service state transition"),
    DUPLICATE_SERVICE_CODE("SERVICE-003", 409, "Service code already exists"),
    SERVICE_IN_USE("SERVICE-004", 400, "Service is in use and cannot be deleted");

    private final String code;
    private final int httpStatus;
    private final String message;

    ServiceErrorCode(String code, int httpStatus, String message) {
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
