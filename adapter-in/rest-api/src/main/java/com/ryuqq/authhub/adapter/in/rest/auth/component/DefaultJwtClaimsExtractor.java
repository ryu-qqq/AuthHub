package com.ryuqq.authhub.adapter.in.rest.auth.component;

import com.ryuqq.authhub.adapter.in.rest.auth.config.JwtValidationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * JWT Claims 추출기 기본 구현
 *
 * <p>jjwt 라이브러리를 사용하여 JWT 토큰을 검증하고 Claims를 추출합니다.
 *
 * <p>지원 알고리즘:
 *
 * <ul>
 *   <li>RS256: RSA 공개키로 검증 (security.jwt.rsa.enabled=true)
 *   <li>HS256: HMAC secret으로 검증 (security.jwt.rsa.enabled=false)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class DefaultJwtClaimsExtractor implements JwtClaimsExtractor {

    private static final Logger log = LoggerFactory.getLogger(DefaultJwtClaimsExtractor.class);

    private static final String TENANT_ID_CLAIM = "tid";
    private static final String ORGANIZATION_ID_CLAIM = "oid";
    private static final String ROLES_CLAIM = "roles";
    private static final String PERMISSIONS_CLAIM = "permissions";

    private final RSAPublicKey rsaPublicKey;
    private final SecretKey secretKey;
    private final String issuer;

    public DefaultJwtClaimsExtractor(JwtValidationProperties properties) {
        this.issuer = properties.getIssuer();
        this.rsaPublicKey = initializeRsaKey(properties);
        this.secretKey = initializeSecretKey(properties);
    }

    private RSAPublicKey initializeRsaKey(JwtValidationProperties properties) {
        if (properties.getRsa().isEnabled()) {
            String publicKeyPath = properties.getRsa().getPublicKeyPath();
            if (StringUtils.hasText(publicKeyPath)) {
                return loadRsaPublicKey(publicKeyPath);
            }
            log.warn("RSA enabled but public key path not configured. Falling back to HMAC.");
        }
        return null;
    }

    private SecretKey initializeSecretKey(JwtValidationProperties properties) {
        if (rsaPublicKey != null) {
            return null;
        }
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    private RSAPublicKey loadRsaPublicKey(String path) {
        try {
            String keyContent = Files.readString(Path.of(path));
            String publicKeyPem =
                    keyContent
                            .replace("-----BEGIN PUBLIC KEY-----", "")
                            .replace("-----END PUBLIC KEY-----", "")
                            .replaceAll("\\s", "");

            byte[] encoded = Base64.getDecoder().decode(publicKeyPem);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Failed to load RSA public key from path: {}", path, e);
            throw new IllegalStateException("Failed to load RSA public key", e);
        }
    }

    @Override
    public Optional<JwtClaims> extractClaims(String token) {
        if (!StringUtils.hasText(token)) {
            return Optional.empty();
        }

        try {
            JwtParserBuilder parserBuilder = Jwts.parser().requireIssuer(issuer);

            if (rsaPublicKey != null) {
                parserBuilder.verifyWith(rsaPublicKey);
            } else if (secretKey != null) {
                parserBuilder.verifyWith(secretKey);
            } else {
                log.error("No verification key configured for JWT validation");
                return Optional.empty();
            }

            Claims claims = parserBuilder.build().parseSignedClaims(token).getPayload();

            String userId = claims.getSubject();
            String tenantId = claims.get(TENANT_ID_CLAIM, String.class);
            String organizationId = claims.get(ORGANIZATION_ID_CLAIM, String.class);
            Set<String> roles = extractStringSet(claims, ROLES_CLAIM);
            Set<String> permissions = extractStringSet(claims, PERMISSIONS_CLAIM);

            return Optional.of(new JwtClaims(userId, tenantId, organizationId, roles, permissions));

        } catch (JwtException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Unexpected error during JWT parsing: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractStringSet(Claims claims, String claimName) {
        Object value = claims.get(claimName);
        if (value == null) {
            return Set.of();
        }
        if (value instanceof List) {
            return new HashSet<>((List<String>) value);
        }
        return Set.of();
    }
}
