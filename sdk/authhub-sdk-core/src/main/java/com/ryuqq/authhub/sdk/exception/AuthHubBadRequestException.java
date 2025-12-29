package com.ryuqq.authhub.sdk.exception;

/** HTTP 400 Bad Request 예외. 잘못된 요청 파라미터나 요청 본문이 전달된 경우 발생합니다. */
public class AuthHubBadRequestException extends AuthHubException {

    private static final int STATUS_CODE = 400;

    public AuthHubBadRequestException(String errorCode, String errorMessage) {
        super(STATUS_CODE, errorCode, errorMessage);
    }

    public AuthHubBadRequestException(String errorCode, String errorMessage, Throwable cause) {
        super(STATUS_CODE, errorCode, errorMessage, cause);
    }
}
