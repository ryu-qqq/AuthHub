package com.ryuqq.authhub.adapter.out.security.jwt;

import com.ryuqq.authhub.adapter.out.security.config.JwtProperties;
import com.ryuqq.authhub.application.auth.dto.command.TokenClaimsContext;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.auth.port.out.client.TokenProviderPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
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
 * <p><strong>Access Token Payload (하이브리드 JWT):</strong>
 *
 * <ul>
 *   <li>sub: userId (UUIDv7)
 *   <li>tid: tenantId (UUIDv7)
 *   <li>tenant_name: 테넌트 이름
 *   <li>oid: organizationId (UUIDv7)
 *   <li>org_name: 조직 이름
 *   <li>email: 사용자 이메일
 *   <li>roles: 역할 목록
 *   <li>permissions: 권한 목록
 * </ul>
 *
 * <p><strong>디코딩 안내:</strong> 다른 서비스에서 키 없이 Base64 디코딩으로 Payload 확인 가능
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String TENANT_ID_CLAIM = "tid";
    private static final String TENANT_NAME_CLAIM = "tenant_name";
    private static final String ORGANIZATION_ID_CLAIM = "oid";
    private static final String ORGANIZATION_NAME_CLAIM = "org_name";
    private static final String EMAIL_CLAIM = "email";
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
    public TokenResponse generateTokenPair(TokenClaimsContext context) {
        long now = System.currentTimeMillis();
        String userIdValue = context.userId().value().toString();

        String accessToken = createAccessToken(context, userIdValue, now);
        String refreshToken = createRefreshToken(userIdValue, now);

        return new TokenResponse(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpiration(),
                jwtProperties.getRefreshTokenExpiration(),
                TOKEN_TYPE);
    }

    private String createAccessToken(TokenClaimsContext context, String userId, long now) {
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + jwtProperties.getAccessTokenExpirationMs());

        return Jwts.builder()
                .subject(userId)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .claim(TENANT_ID_CLAIM, uuidToString(context.tenantId()))
                .claim(TENANT_NAME_CLAIM, context.tenantName())
                .claim(ORGANIZATION_ID_CLAIM, uuidToString(context.organizationId()))
                .claim(ORGANIZATION_NAME_CLAIM, context.organizationName())
                .claim(EMAIL_CLAIM, context.email())
                .claim(ROLES_CLAIM, context.roles())
                .claim(PERMISSIONS_CLAIM, context.permissions())
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

    private String uuidToString(java.util.UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
}
