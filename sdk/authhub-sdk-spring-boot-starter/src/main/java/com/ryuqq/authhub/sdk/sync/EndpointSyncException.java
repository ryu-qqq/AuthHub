package com.ryuqq.authhub.sdk.sync;

/**
 * EndpointSyncException - 엔드포인트 동기화 실패 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class EndpointSyncException extends RuntimeException {

    public EndpointSyncException(String message) {
        super(message);
    }

    public EndpointSyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
