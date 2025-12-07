package com.ryuqq.setof.adapter.out.security.jwt;

import com.ryuqq.setof.adapter.out.security.config.JwtProperties;
import com.ryuqq.setof.application.member.dto.response.TokenPairResponse;
import com.ryuqq.setof.application.member.port.out.TokenProviderPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider Adapter
 *
 * <p>jjwt 라이브러리를 사용하여 TokenProviderPort를 구현합니다.
 *
 * <p><strong>토큰 구조:</strong>
 * <ul>
 *   <li>Access Token: 짧은 만료 시간 (기본 30분), 인증용</li>
 *   <li>Refresh Token: 긴 만료 시간 (기본 7일), Access Token 갱신용</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProviderAdapter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public TokenPairResponse generateTokenPair(String memberId) {
        long now = System.currentTimeMillis();

        String accessToken = createToken(
                memberId,
                ACCESS_TOKEN_TYPE,
                now,
                jwtProperties.getAccessTokenExpirationMs()
        );

        String refreshToken = createToken(
                memberId,
                REFRESH_TOKEN_TYPE,
                now,
                jwtProperties.getRefreshTokenExpirationMs()
        );

        return new TokenPairResponse(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpirationMs() / 1000,
                jwtProperties.getRefreshTokenExpirationMs() / 1000
        );
    }

    @Override
    public String extractMemberId(String accessToken) {
        return extractClaims(accessToken).getSubject();
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, ACCESS_TOKEN_TYPE);
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, REFRESH_TOKEN_TYPE);
    }

    @Override
    public String extractMemberIdFromRefreshToken(String refreshToken) {
        return extractClaims(refreshToken).getSubject();
    }

    @Override
    public boolean isAccessTokenExpired(String accessToken) {
        try {
            extractClaims(accessToken);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private String createToken(String memberId, String tokenType, long now, long expirationMs) {
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + expirationMs);

        return Jwts.builder()
                .subject(memberId)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .signWith(secretKey)
                .compact();
    }

    private boolean validateToken(String token, String expectedTokenType) {
        try {
            Claims claims = extractClaims(token);
            String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
            return expectedTokenType.equals(tokenType);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
