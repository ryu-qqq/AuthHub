package com.ryuqq.authhub.sdk.client;

import com.ryuqq.authhub.sdk.api.InternalApi;

/**
 * Gateway 전용 AuthHub 클라이언트.
 *
 * <p>서비스 토큰 인증을 사용하여 Internal API에 접근합니다.
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * GatewayClient client = GatewayClient.builder()
 *     .baseUrl("https://authhub.example.com")
 *     .serviceName("gateway")
 *     .serviceToken("your-service-token")
 *     .build();
 *
 * // 권한 스펙 조회
 * ApiResponse<EndpointPermissionSpecList> spec = client.internal().getPermissionSpec();
 * }</pre>
 */
public interface GatewayClient {

    /**
     * Internal API를 반환합니다.
     *
     * <p>엔드포인트-권한 스펙 조회 등 내부 서비스용 API를 제공합니다.
     *
     * @return InternalApi
     */
    InternalApi internal();

    /**
     * GatewayClient Builder를 반환합니다.
     *
     * @return GatewayClientBuilder
     */
    static GatewayClientBuilder builder() {
        return new GatewayClientBuilder();
    }
}
