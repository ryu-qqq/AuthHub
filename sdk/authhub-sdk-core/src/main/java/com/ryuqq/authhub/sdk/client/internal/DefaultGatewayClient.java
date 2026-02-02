package com.ryuqq.authhub.sdk.client.internal;

import com.ryuqq.authhub.sdk.api.InternalApi;
import com.ryuqq.authhub.sdk.client.GatewayClient;
import com.ryuqq.authhub.sdk.config.GatewayClientConfig;

/**
 * GatewayClient의 기본 구현체.
 *
 * <p>서비스 토큰 인증을 사용하여 Internal API에 접근합니다.
 */
public final class DefaultGatewayClient implements GatewayClient {

    private final InternalApi internalApi;

    public DefaultGatewayClient(GatewayClientConfig config) {
        ServiceTokenHttpClientSupport httpClient = new ServiceTokenHttpClientSupport(config);
        this.internalApi = new DefaultInternalApi(httpClient);
    }

    @Override
    public InternalApi internal() {
        return internalApi;
    }
}
