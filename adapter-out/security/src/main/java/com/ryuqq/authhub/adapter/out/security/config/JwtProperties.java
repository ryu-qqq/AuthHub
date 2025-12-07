package com.ryuqq.setof.adapter.out.security.config;

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
    private String issuer = "setof-commerce";

    public JwtProperties() {
    }

    public JwtProperties(String secret, long accessTokenExpiration,
                         long refreshTokenExpiration, String issuer) {
        this.secret = secret;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.issuer = issuer;
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

    /**
     * Access Token 만료 시간 (밀리초)
     */
    public long getAccessTokenExpirationMs() {
        return accessTokenExpiration * 1000;
    }

    /**
     * Refresh Token 만료 시간 (밀리초)
     */
    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpiration * 1000;
    }
}
