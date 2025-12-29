package com.ryuqq.authhub.sdk.exception;

/** HTTP 404 Not Found 예외. 요청한 리소스를 찾을 수 없는 경우 발생합니다. */
public class AuthHubNotFoundException extends AuthHubException {

    private static final int STATUS_CODE = 404;

    public AuthHubNotFoundException(String errorCode, String errorMessage) {
        super(STATUS_CODE, errorCode, errorMessage);
    }

    public AuthHubNotFoundException(String errorCode, String errorMessage, Throwable cause) {
        super(STATUS_CODE, errorCode, errorMessage, cause);
    }
}
