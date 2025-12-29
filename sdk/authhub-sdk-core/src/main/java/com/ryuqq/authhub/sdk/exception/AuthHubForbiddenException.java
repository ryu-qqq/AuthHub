package com.ryuqq.authhub.sdk.exception;

/** HTTP 403 Forbidden 예외. 권한이 부족하여 요청이 거부된 경우 발생합니다. */
public class AuthHubForbiddenException extends AuthHubException {

    private static final int STATUS_CODE = 403;

    public AuthHubForbiddenException(String errorCode, String errorMessage) {
        super(STATUS_CODE, errorCode, errorMessage);
    }

    public AuthHubForbiddenException(String errorCode, String errorMessage, Throwable cause) {
        super(STATUS_CODE, errorCode, errorMessage, cause);
    }
}
