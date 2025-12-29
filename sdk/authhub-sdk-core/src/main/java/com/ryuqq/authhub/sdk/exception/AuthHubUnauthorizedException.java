package com.ryuqq.authhub.sdk.exception;

/** HTTP 401 Unauthorized 예외. 인증 토큰이 없거나 유효하지 않은 경우 발생합니다. */
public class AuthHubUnauthorizedException extends AuthHubException {

    private static final int STATUS_CODE = 401;

    public AuthHubUnauthorizedException(String errorCode, String errorMessage) {
        super(STATUS_CODE, errorCode, errorMessage);
    }

    public AuthHubUnauthorizedException(String errorCode, String errorMessage, Throwable cause) {
        super(STATUS_CODE, errorCode, errorMessage, cause);
    }
}
