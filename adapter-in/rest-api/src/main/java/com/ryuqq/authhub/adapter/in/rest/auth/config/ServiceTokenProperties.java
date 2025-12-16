package com.ryuqq.authhub.adapter.in.rest.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 서비스 토큰 설정 프로퍼티
 *
 * <p>서비스 간 통신(Internal API)을 위한 X-Service-Token 인증 설정입니다.
 *
 * <p>설정 예시:
 *
 * <pre>{@code
 * security:
 *   service-token:
 *     enabled: true
 *     secret: your-service-token-secret
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "security.service-token")
public class ServiceTokenProperties {

    /** 서비스 토큰 인증 활성화 여부 (기본: false) */
    private boolean enabled = false;

    /** 서비스 토큰 시크릿 (Gateway와 공유) */
    private String secret;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
