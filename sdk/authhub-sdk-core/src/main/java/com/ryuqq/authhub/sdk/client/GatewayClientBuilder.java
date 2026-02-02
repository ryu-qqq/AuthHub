package com.ryuqq.authhub.sdk.client;

import com.ryuqq.authhub.sdk.client.internal.DefaultGatewayClient;
import com.ryuqq.authhub.sdk.config.GatewayClientConfig;
import java.time.Duration;
import java.util.Objects;

/**
 * GatewayClient 빌더.
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * GatewayClient client = GatewayClient.builder()
 *     .baseUrl("https://authhub.example.com")
 *     .serviceName("gateway")
 *     .serviceToken("your-service-token")
 *     .build();
 * }</pre>
 */
public final class GatewayClientBuilder {

    private String baseUrl;
    private String serviceName;
    private String serviceToken;
    private Duration connectTimeout = GatewayClientConfig.DEFAULT_CONNECT_TIMEOUT;
    private Duration readTimeout = GatewayClientConfig.DEFAULT_READ_TIMEOUT;

    /** GatewayClientBuilder 생성자. GatewayClient.builder()를 통해 생성합니다. */
    public GatewayClientBuilder() {}

    /**
     * AuthHub 서버 기본 URL을 설정합니다.
     *
     * @param baseUrl 기본 URL (예: "https://authhub.example.com")
     * @return this
     */
    public GatewayClientBuilder baseUrl(String baseUrl) {
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl must not be null");
        return this;
    }

    /**
     * 서비스 이름을 설정합니다.
     *
     * @param serviceName 서비스 이름 (예: "gateway")
     * @return this
     */
    public GatewayClientBuilder serviceName(String serviceName) {
        this.serviceName = Objects.requireNonNull(serviceName, "serviceName must not be null");
        return this;
    }

    /**
     * 서비스 토큰을 설정합니다.
     *
     * @param serviceToken 서비스 토큰
     * @return this
     */
    public GatewayClientBuilder serviceToken(String serviceToken) {
        this.serviceToken = Objects.requireNonNull(serviceToken, "serviceToken must not be null");
        return this;
    }

    /**
     * 연결 타임아웃을 설정합니다. 기본값: 5초
     *
     * @param connectTimeout 연결 타임아웃
     * @return this
     */
    public GatewayClientBuilder connectTimeout(Duration connectTimeout) {
        this.connectTimeout =
                Objects.requireNonNull(connectTimeout, "connectTimeout must not be null");
        return this;
    }

    /**
     * 읽기 타임아웃을 설정합니다. 기본값: 30초
     *
     * @param readTimeout 읽기 타임아웃
     * @return this
     */
    public GatewayClientBuilder readTimeout(Duration readTimeout) {
        this.readTimeout = Objects.requireNonNull(readTimeout, "readTimeout must not be null");
        return this;
    }

    /**
     * GatewayClient를 빌드합니다.
     *
     * @return GatewayClient 인스턴스
     * @throws IllegalStateException 필수 파라미터가 설정되지 않은 경우
     */
    public GatewayClient build() {
        validate();
        GatewayClientConfig config =
                new GatewayClientConfig(
                        baseUrl, serviceName, serviceToken, connectTimeout, readTimeout);
        return new DefaultGatewayClient(config);
    }

    private void validate() {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("baseUrl must be set");
        }
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalStateException("serviceName must be set");
        }
        if (serviceToken == null || serviceToken.isBlank()) {
            throw new IllegalStateException("serviceToken must be set");
        }
    }
}
