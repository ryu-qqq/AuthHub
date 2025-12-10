package com.ryuqq.authhub.adapter.out.security.jwt;

import com.ryuqq.authhub.adapter.out.security.config.JwtProperties;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.auth.port.out.client.TokenProviderPort;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

/**
 * JWT Token Provider Adapter
 *
 * <p>jjwt 라이브러리를 사용하여 TokenProviderPort를 구현합니다.
 *
 * <p><strong>토큰 구조:</strong>
 *
 * <ul>
 *   <li>Access Token: 짧은 만료 시간 (기본 1시간), 인증용
 *   <li>Refresh Token: 긴 만료 시간 (기본 7일), Access Token 갱신용
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ROLES_CLAIM = "roles";
    private static final String PERMISSIONS_CLAIM = "permissions";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    private static final String TOKEN_TYPE = "Bearer";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProviderAdapter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey =
                Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public TokenResponse generateTokenPair(
            UserId userId, Set<String> roles, Set<String> permissions) {
        long now = System.currentTimeMillis();
        String userIdValue = userId.value().toString();

        String accessToken = createAccessToken(userIdValue, roles, permissions, now);
        String refreshToken = createRefreshToken(userIdValue, now);

        return new TokenResponse(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpiration(),
                jwtProperties.getRefreshTokenExpiration(),
                TOKEN_TYPE);
    }

    private String createAccessToken(
            String userId, Set<String> roles, Set<String> permissions, long now) {
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + jwtProperties.getAccessTokenExpirationMs());

        return Jwts.builder()
                .subject(userId)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .claim(ROLES_CLAIM, roles)
                .claim(PERMISSIONS_CLAIM, permissions)
                .signWith(secretKey)
                .compact();
    }

    private String createRefreshToken(String userId, long now) {
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + jwtProperties.getRefreshTokenExpirationMs());

        return Jwts.builder()
                .subject(userId)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .signWith(secretKey)
                .compact();
    }
}
