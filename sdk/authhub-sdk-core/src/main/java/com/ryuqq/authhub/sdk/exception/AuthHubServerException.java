package com.ryuqq.authhub.sdk.exception;

/** HTTP 5xx Server Error 예외. AuthHub 서버에서 내부 오류가 발생한 경우 발생합니다. */
public class AuthHubServerException extends AuthHubException {

    public AuthHubServerException(int statusCode, String errorCode, String errorMessage) {
        super(statusCode, errorCode, errorMessage);
    }

    public AuthHubServerException(
            int statusCode, String errorCode, String errorMessage, Throwable cause) {
        super(statusCode, errorCode, errorMessage, cause);
    }
}
