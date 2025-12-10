package com.ryuqq.authhub.adapter.out.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT Configuration Properties
 *
 * <p>rest-api.yml에서 security.jwt.* 프로퍼티를 바인딩합니다.
 *
 * <pre>
 * security:
 *   jwt:
 *     secret: your-256-bit-secret-key-here
 *     access-token-expiration: 3600  # 1시간 (초)
 *     refresh-token-expiration: 604800  # 7일 (초)
 * </pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenExpiration = 3600L; // 1시간 (초)
    private long refreshTokenExpiration = 604800L; // 7일 (초)
    private String issuer = "authhub";
    private RsaKeyProperties rsa = new RsaKeyProperties();

    public JwtProperties() {}

    public JwtProperties(
            String secret,
            long accessTokenExpiration,
            long refreshTokenExpiration,
            String issuer,
            RsaKeyProperties rsa) {
        this.secret = secret;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.issuer = issuer;
        this.rsa = rsa;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /** Access Token 만료 시간 (밀리초) */
    public long getAccessTokenExpirationMs() {
        return accessTokenExpiration * 1000;
    }

    /** Refresh Token 만료 시간 (밀리초) */
    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpiration * 1000;
    }

    public RsaKeyProperties getRsa() {
        return rsa;
    }

    public void setRsa(RsaKeyProperties rsa) {
        this.rsa = rsa;
    }

    /**
     * RSA 키 설정
     *
     * <p>JWT 서명/검증에 사용할 RSA 키 쌍 설정입니다.
     */
    public static class RsaKeyProperties {
        private boolean enabled;
        private String keyId = "authhub-key-1";
        private String publicKeyPath;
        private String privateKeyPath;

        public RsaKeyProperties() {}

        public RsaKeyProperties(
                boolean enabled, String keyId, String publicKeyPath, String privateKeyPath) {
            this.enabled = enabled;
            this.keyId = keyId;
            this.publicKeyPath = publicKeyPath;
            this.privateKeyPath = privateKeyPath;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }

        public String getPublicKeyPath() {
            return publicKeyPath;
        }

        public void setPublicKeyPath(String publicKeyPath) {
            this.publicKeyPath = publicKeyPath;
        }

        public String getPrivateKeyPath() {
            return privateKeyPath;
        }

        public void setPrivateKeyPath(String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
        }
    }
}
