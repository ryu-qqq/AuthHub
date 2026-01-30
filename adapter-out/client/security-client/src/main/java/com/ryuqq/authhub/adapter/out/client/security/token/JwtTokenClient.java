package com.ryuqq.authhub.adapter.out.client.security.token;

import com.ryuqq.authhub.adapter.out.client.security.common.RsaKeyLoader;
import com.ryuqq.authhub.adapter.out.client.security.config.JwtProperties;
import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.application.token.dto.response.TokenResponse;
import com.ryuqq.authhub.application.token.port.out.client.TokenProviderClient;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * JwtTokenClient - JWT 토큰 생성 Client
 *
 * <p>jjwt 라이브러리를 사용하여 JWT 토큰을 생성합니다.
 *
 * <p><strong>토큰 구조:</strong>
 *
 * <ul>
 *   <li>Access Token: 짧은 만료 시간 (기본 1시간), 인증용
 *   <li>Refresh Token: 긴 만료 시간 (기본 7일), Access Token 갱신용
 * </ul>
 *
 * <p><strong>서명 알고리즘:</strong>
 *
 * <ul>
 *   <li>RS256 (RSA 비대칭키): security.jwt.rsa.enabled=true 시 사용
 *   <li>HS256 (HMAC 대칭키): security.jwt.rsa.enabled=false 시 사용
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class JwtTokenClient implements TokenProviderClient {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String TENANT_ID_CLAIM = "tid";
    private static final String TENANT_NAME_CLAIM = "tenant_name";
    private static final String ORGANIZATION_ID_CLAIM = "oid";
    private static final String ORGANIZATION_NAME_CLAIM = "org_name";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLES_CLAIM = "roles";
    private static final String PERMISSIONS_CLAIM = "permissions";
    private static final String PERMISSION_HASH_CLAIM = "permission_hash";
    private static final String MFA_VERIFIED_CLAIM = "mfa_verified";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    private static final String TOKEN_TYPE = "Bearer";

    private final JwtProperties jwtProperties;
    private final RsaKeyLoader rsaKeyLoader;
    private final Key signingKey;

    public JwtTokenClient(JwtProperties jwtProperties, RsaKeyLoader rsaKeyLoader) {
        this.jwtProperties = jwtProperties;
        this.rsaKeyLoader = rsaKeyLoader;
        this.signingKey = initializeSigningKey();
    }

    private Key initializeSigningKey() {
        if (rsaKeyLoader.isRsaEnabled()) {
            return rsaKeyLoader.loadPrivateKey();
        }
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public TokenResponse generateTokenPair(
            TokenClaimsComposite context, RolesAndPermissionsComposite rolesAndPermissions) {
        long now = System.currentTimeMillis();
        String userIdValue = context.userId().value().toString();

        String accessToken = createAccessToken(context, rolesAndPermissions, userIdValue, now);
        String refreshToken = createRefreshToken(userIdValue, now);

        return new TokenResponse(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpiration(),
                jwtProperties.getRefreshTokenExpiration(),
                TOKEN_TYPE);
    }

    private String createAccessToken(
            TokenClaimsComposite context,
            RolesAndPermissionsComposite rolesAndPermissions,
            String userId,
            long now) {
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + jwtProperties.getAccessTokenExpirationMs());

        Set<String> roles = rolesAndPermissions.roleNames();
        Set<String> permissions = rolesAndPermissions.permissionKeys();

        var builder =
                Jwts.builder()
                        .subject(userId)
                        .issuer(jwtProperties.getIssuer())
                        .issuedAt(issuedAt)
                        .expiration(expiration)
                        .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                        .claim(TENANT_ID_CLAIM, context.tenantId())
                        .claim(TENANT_NAME_CLAIM, context.tenantName())
                        .claim(ORGANIZATION_ID_CLAIM, context.organizationId())
                        .claim(ORGANIZATION_NAME_CLAIM, context.organizationName())
                        .claim(EMAIL_CLAIM, context.email())
                        .claim(ROLES_CLAIM, roles)
                        .claim(PERMISSIONS_CLAIM, permissions)
                        .claim(PERMISSION_HASH_CLAIM, calculatePermissionHash(permissions))
                        .claim(MFA_VERIFIED_CLAIM, context.mfaVerified());

        if (rsaKeyLoader.isRsaEnabled()) {
            builder.header().add("kid", rsaKeyLoader.getKeyId());
        }

        return builder.signWith(signingKey).compact();
    }

    private String createRefreshToken(String userId, long now) {
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + jwtProperties.getRefreshTokenExpirationMs());

        var builder =
                Jwts.builder()
                        .subject(userId)
                        .issuer(jwtProperties.getIssuer())
                        .issuedAt(issuedAt)
                        .expiration(expiration)
                        .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE);

        if (rsaKeyLoader.isRsaEnabled()) {
            builder.header().add("kid", rsaKeyLoader.getKeyId());
        }

        return builder.signWith(signingKey).compact();
    }

    private String calculatePermissionHash(Set<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "";
        }
        String sortedPermissions = permissions.stream().sorted().collect(Collectors.joining(","));
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sortedPermissions.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
