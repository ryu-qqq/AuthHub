package com.ryuqq.authhub.sdk.exception;

/** AuthHub SDK의 기본 예외 클래스. 모든 AuthHub SDK 예외는 이 클래스를 상속합니다. */
public class AuthHubException extends RuntimeException {

    private final int statusCode;
    private final String errorCode;
    private final String errorMessage;

    public AuthHubException(int statusCode, String errorCode, String errorMessage) {
        super(formatMessage(statusCode, errorCode, errorMessage));
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public AuthHubException(
            int statusCode, String errorCode, String errorMessage, Throwable cause) {
        super(formatMessage(statusCode, errorCode, errorMessage), cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    private static String formatMessage(int statusCode, String errorCode, String errorMessage) {
        return String.format("[%d] %s: %s", statusCode, errorCode, errorMessage);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
