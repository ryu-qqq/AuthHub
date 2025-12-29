package com.ryuqq.authhub.sdk.config;

import java.time.Duration;
import java.util.Objects;

/**
 * AuthHub SDK 설정 정보.
 *
 * @param baseUrl AuthHub API 서버 기본 URL
 * @param connectTimeout 연결 타임아웃
 * @param readTimeout 읽기 타임아웃
 */
public record AuthHubConfig(String baseUrl, Duration connectTimeout, Duration readTimeout) {

    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);
    public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);

    public AuthHubConfig {
        Objects.requireNonNull(baseUrl, "baseUrl must not be null");
        if (baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl must not be blank");
        }
        if (connectTimeout == null) {
            connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        }
        if (readTimeout == null) {
            readTimeout = DEFAULT_READ_TIMEOUT;
        }
    }

    /**
     * 기본 타임아웃 설정으로 AuthHubConfig를 생성합니다.
     *
     * @param baseUrl AuthHub API 서버 기본 URL
     * @return AuthHubConfig
     */
    public static AuthHubConfig of(String baseUrl) {
        return new AuthHubConfig(baseUrl, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }
}
