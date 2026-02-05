package com.ryuqq.authhub.sdk.autoconfigure;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AuthHub SDK 설정 속성. application.yml/properties에서 authhub.* 접두사로 설정할 수 있습니다.
 *
 * <p>예시:
 *
 * <pre>
 * authhub:
 *   base-url: https://auth.example.com
 *   service-token: ${AUTHHUB_SERVICE_TOKEN}
 *   timeout:
 *     connect: 5s
 *     read: 30s
 * </pre>
 */
@ConfigurationProperties(prefix = "authhub")
public class AuthHubProperties {

    /** AuthHub 서버 기본 URL. 필수 설정입니다. */
    private String baseUrl;

    /** 서비스 토큰 (머신 간 통신용). ThreadLocal 토큰이 없을 경우 fallback으로 사용됩니다. */
    private String serviceToken;

    /** 서비스 코드. Role-Permission 자동 매핑 시 사용됩니다. (optional) */
    private String serviceCode;

    /** 타임아웃 설정. */
    private final Timeout timeout = new Timeout();

    /** 재시도 설정. */
    private final Retry retry = new Retry();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getServiceToken() {
        return serviceToken;
    }

    public void setServiceToken(String serviceToken) {
        this.serviceToken = serviceToken;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Timeout getTimeout() {
        return timeout;
    }

    public Retry getRetry() {
        return retry;
    }

    /** 타임아웃 설정. */
    public static class Timeout {

        /** 연결 타임아웃. 기본값: 5초 */
        private Duration connect = Duration.ofSeconds(5);

        /** 읽기 타임아웃. 기본값: 30초 */
        private Duration read = Duration.ofSeconds(30);

        public Duration getConnect() {
            return connect;
        }

        public void setConnect(Duration connect) {
            this.connect = connect;
        }

        public Duration getRead() {
            return read;
        }

        public void setRead(Duration read) {
            this.read = read;
        }
    }

    /** 재시도 설정. */
    public static class Retry {

        /** 재시도 활성화 여부. 기본값: true */
        private boolean enabled = true;

        /** 최대 재시도 횟수. 기본값: 3 */
        private int maxAttempts = 3;

        /** 재시도 대기 시간. 기본값: 1초 */
        private Duration delay = Duration.ofSeconds(1);

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public Duration getDelay() {
            return delay;
        }

        public void setDelay(Duration delay) {
            this.delay = delay;
        }
    }
}
