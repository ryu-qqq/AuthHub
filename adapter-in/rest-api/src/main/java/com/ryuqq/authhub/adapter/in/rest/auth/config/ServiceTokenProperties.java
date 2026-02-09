package com.ryuqq.authhub.adapter.in.rest.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 서비스 토큰 인증 Properties
 *
 * <p>rest-api.yml의 security.service-token 설정을 매핑합니다.
 *
 * <p>사용 예시:
 *
 * <pre>
 * security:
 *   service-token:
 *     enabled: true
 *     secret: ${SECURITY_SERVICE_TOKEN_SECRET}
 * </pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "security.service-token")
public class ServiceTokenProperties {

    private boolean enabled;
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
