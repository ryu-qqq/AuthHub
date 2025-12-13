package com.ryuqq.authhub.adapter.in.rest.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 검증 설정
 *
 * <p>REST API 레이어에서 JWT 직접 검증에 필요한 설정입니다.
 *
 * <p>rest-api.yml의 security.jwt.* 프로퍼티를 바인딩합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtValidationProperties {

    private String secret;
    private String issuer = "authhub";
    private RsaProperties rsa = new RsaProperties();

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public RsaProperties getRsa() {
        return rsa;
    }

    public void setRsa(RsaProperties rsa) {
        this.rsa = rsa;
    }

    public static class RsaProperties {
        private boolean enabled;
        private String publicKeyPath;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getPublicKeyPath() {
            return publicKeyPath;
        }

        public void setPublicKeyPath(String publicKeyPath) {
            this.publicKeyPath = publicKeyPath;
        }
    }
}
