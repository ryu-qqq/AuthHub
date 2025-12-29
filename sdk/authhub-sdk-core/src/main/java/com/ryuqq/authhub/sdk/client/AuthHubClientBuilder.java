package com.ryuqq.authhub.sdk.client;

import com.ryuqq.authhub.sdk.auth.StaticTokenResolver;
import com.ryuqq.authhub.sdk.auth.TokenResolver;
import com.ryuqq.authhub.sdk.client.internal.DefaultAuthHubClient;
import com.ryuqq.authhub.sdk.config.AuthHubConfig;
import java.time.Duration;
import java.util.Objects;

/**
 * AuthHubClient 빌더. Fluent API를 통해 AuthHubClient 인스턴스를 생성합니다.
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * // 서비스 토큰만 사용
 * AuthHubClient client = AuthHubClient.builder()
 *     .baseUrl("https://authhub.example.com")
 *     .serviceToken("your-service-token")
 *     .build();
 *
 * // ThreadLocal 우선 + 서비스 토큰 폴백
 * AuthHubClient client = AuthHubClient.builder()
 *     .baseUrl("https://authhub.example.com")
 *     .serviceToken("your-service-token")
 *     .tokenResolver(ChainTokenResolver.withFallback("your-service-token"))
 *     .build();
 *
 * // 커스텀 타임아웃 설정
 * AuthHubClient client = AuthHubClient.builder()
 *     .baseUrl("https://authhub.example.com")
 *     .serviceToken("your-service-token")
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .readTimeout(Duration.ofSeconds(60))
 *     .build();
 * }</pre>
 */
public final class AuthHubClientBuilder {

    private String baseUrl;
    private String serviceToken;
    private TokenResolver tokenResolver;
    private Duration connectTimeout = AuthHubConfig.DEFAULT_CONNECT_TIMEOUT;
    private Duration readTimeout = AuthHubConfig.DEFAULT_READ_TIMEOUT;

    AuthHubClientBuilder() {}

    /**
     * AuthHub API 서버의 기본 URL을 설정합니다.
     *
     * @param baseUrl 기본 URL (예: "https://authhub.example.com")
     * @return this builder
     */
    public AuthHubClientBuilder baseUrl(String baseUrl) {
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl must not be null");
        return this;
    }

    /**
     * 서비스 토큰을 설정합니다. tokenResolver가 설정되지 않은 경우, 이 토큰을 사용하는 StaticTokenResolver가 사용됩니다.
     *
     * @param serviceToken 서비스 토큰
     * @return this builder
     */
    public AuthHubClientBuilder serviceToken(String serviceToken) {
        this.serviceToken = Objects.requireNonNull(serviceToken, "serviceToken must not be null");
        return this;
    }

    /**
     * 커스텀 TokenResolver를 설정합니다. 설정된 경우 serviceToken보다 우선합니다.
     *
     * @param tokenResolver 토큰 리졸버
     * @return this builder
     */
    public AuthHubClientBuilder tokenResolver(TokenResolver tokenResolver) {
        this.tokenResolver =
                Objects.requireNonNull(tokenResolver, "tokenResolver must not be null");
        return this;
    }

    /**
     * 연결 타임아웃을 설정합니다.
     *
     * @param connectTimeout 연결 타임아웃 (기본값: 5초)
     * @return this builder
     */
    public AuthHubClientBuilder connectTimeout(Duration connectTimeout) {
        this.connectTimeout =
                Objects.requireNonNull(connectTimeout, "connectTimeout must not be null");
        return this;
    }

    /**
     * 읽기 타임아웃을 설정합니다.
     *
     * @param readTimeout 읽기 타임아웃 (기본값: 30초)
     * @return this builder
     */
    public AuthHubClientBuilder readTimeout(Duration readTimeout) {
        this.readTimeout = Objects.requireNonNull(readTimeout, "readTimeout must not be null");
        return this;
    }

    /**
     * 동기(Sync) AuthHubClient를 빌드합니다. Spring RestClient를 사용합니다.
     *
     * @return AuthHubClient
     * @throws IllegalStateException 필수 설정이 누락된 경우
     */
    public AuthHubClient build() {
        validate();
        AuthHubConfig config = new AuthHubConfig(baseUrl, connectTimeout, readTimeout);
        TokenResolver resolver = resolveTokenResolver();
        return new DefaultAuthHubClient(config, resolver);
    }

    private void validate() {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("baseUrl must be set");
        }
        if (serviceToken == null && tokenResolver == null) {
            throw new IllegalStateException("Either serviceToken or tokenResolver must be set");
        }
    }

    private TokenResolver resolveTokenResolver() {
        if (tokenResolver != null) {
            return tokenResolver;
        }
        return new StaticTokenResolver(serviceToken);
    }
}
