package com.ryuqq.authhub.domain.common.exception;

import java.util.Map;

/**
 * DomainException - 도메인 계층 기본 예외
 *
 * <p>모든 도메인 예외의 기본 클래스입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class DomainException extends RuntimeException {

    private final String code;
    private final int httpStatus;
    private final Map<String, Object> args;

    public DomainException(String code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = 400;
        this.args = Map.of();
    }

    public DomainException(String code, String message, Map<String, Object> args) {
        super(message);
        this.code = code;
        this.httpStatus = 400;
        this.args = args == null ? Map.of() : Map.copyOf(args);
    }

    public DomainException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
        this.args = Map.of();
    }

    public DomainException(ErrorCode errorCode, Map<String, Object> args) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
        this.args = args == null ? Map.of() : Map.copyOf(args);
    }

    public String code() {
        return code;
    }

    public int httpStatus() {
        return httpStatus;
    }

    public Map<String, Object> args() {
        return args;
    }
}
