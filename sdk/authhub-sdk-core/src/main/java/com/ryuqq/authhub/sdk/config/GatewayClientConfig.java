package com.ryuqq.authhub.sdk.config;

import java.time.Duration;
import java.util.Objects;

/**
 * Gateway 클라이언트 설정.
 *
 * <p>서비스 토큰 인증을 사용하는 Gateway 전용 클라이언트 설정입니다.
 *
 * @param baseUrl AuthHub 서버 기본 URL
 * @param serviceName 서비스 이름 (예: "gateway")
 * @param serviceToken 서비스 토큰
 * @param connectTimeout 연결 타임아웃
 * @param readTimeout 읽기 타임아웃
 */
public record GatewayClientConfig(
        String baseUrl,
        String serviceName,
        String serviceToken,
        Duration connectTimeout,
        Duration readTimeout) {

    /** 기본 연결 타임아웃 (5초). */
    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);

    /** 기본 읽기 타임아웃 (30초). */
    public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);

    /**
     * Compact constructor with validation.
     *
     * <p>필수 필드 검증 및 기본값 적용.
     */
    public GatewayClientConfig {
        Objects.requireNonNull(baseUrl, "baseUrl must not be null");
        Objects.requireNonNull(serviceName, "serviceName must not be null");
        Objects.requireNonNull(serviceToken, "serviceToken must not be null");

        if (baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl must not be blank");
        }
        if (serviceName.isBlank()) {
            throw new IllegalArgumentException("serviceName must not be blank");
        }
        if (serviceToken.isBlank()) {
            throw new IllegalArgumentException("serviceToken must not be blank");
        }

        connectTimeout = connectTimeout != null ? connectTimeout : DEFAULT_CONNECT_TIMEOUT;
        readTimeout = readTimeout != null ? readTimeout : DEFAULT_READ_TIMEOUT;
    }
}
