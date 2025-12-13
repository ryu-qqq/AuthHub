package com.ryuqq.authhub.adapter.in.rest.auth.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.auth.config.JwtValidationProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DefaultJwtClaimsExtractor 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DefaultJwtClaimsExtractor 단위 테스트")
class DefaultJwtClaimsExtractorTest {

    private static final String SECRET =
            "this-is-a-very-secure-secret-key-for-testing-purpose-256-bits";
    private static final String ISSUER = "authhub";
    private static final String USER_ID = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1b";
    private static final String TENANT_ID = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1c";
    private static final String ORG_ID = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1d";

    private DefaultJwtClaimsExtractor extractor;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        JwtValidationProperties properties = createTestProperties();
        extractor = new DefaultJwtClaimsExtractor(properties);
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private JwtValidationProperties createTestProperties() {
        JwtValidationProperties properties = new JwtValidationProperties();
        properties.setSecret(SECRET);
        properties.setIssuer(ISSUER);
        JwtValidationProperties.RsaProperties rsa = new JwtValidationProperties.RsaProperties();
        rsa.setEnabled(false);
        properties.setRsa(rsa);
        return properties;
    }

    @Nested
    @DisplayName("extractClaims() 테스트")
    class ExtractClaimsTest {

        @Test
        @DisplayName("유효한 JWT 토큰에서 Claims를 추출한다")
        void extractClaims_withValidToken_shouldReturnClaims() {
            // given
            String token = createValidToken();

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isPresent();
            JwtClaimsExtractor.JwtClaims claims = result.get();
            assertThat(claims.userId()).isEqualTo(USER_ID);
            assertThat(claims.tenantId()).isEqualTo(TENANT_ID);
            assertThat(claims.organizationId()).isEqualTo(ORG_ID);
            assertThat(claims.roles()).containsExactlyInAnyOrder("TENANT_ADMIN", "USER");
            assertThat(claims.permissions()).containsExactlyInAnyOrder("user:read", "user:create");
        }

        @Test
        @DisplayName("null 토큰이면 empty를 반환한다")
        void extractClaims_withNullToken_shouldReturnEmpty() {
            // given
            String token = null;

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 토큰이면 empty를 반환한다")
        void extractClaims_withEmptyToken_shouldReturnEmpty() {
            // given
            String token = "";

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("공백 토큰이면 empty를 반환한다")
        void extractClaims_withBlankToken_shouldReturnEmpty() {
            // given
            String token = "   ";

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("만료된 토큰이면 empty를 반환한다")
        void extractClaims_withExpiredToken_shouldReturnEmpty() {
            // given
            String token = createExpiredToken();

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("잘못된 서명의 토큰이면 empty를 반환한다")
        void extractClaims_withInvalidSignature_shouldReturnEmpty() {
            // given
            SecretKey wrongKey =
                    Keys.hmacShaKeyFor(
                            "wrong-secret-key-for-testing-purpose-256-bits-long"
                                    .getBytes(StandardCharsets.UTF_8));
            String token =
                    Jwts.builder()
                            .subject(USER_ID)
                            .issuer(ISSUER)
                            .expiration(new Date(System.currentTimeMillis() + 3600000))
                            .signWith(wrongKey)
                            .compact();

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("잘못된 issuer의 토큰이면 empty를 반환한다")
        void extractClaims_withWrongIssuer_shouldReturnEmpty() {
            // given
            String token =
                    Jwts.builder()
                            .subject(USER_ID)
                            .issuer("wrong-issuer")
                            .expiration(new Date(System.currentTimeMillis() + 3600000))
                            .signWith(secretKey)
                            .compact();

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("잘못된 형식의 토큰이면 empty를 반환한다")
        void extractClaims_withMalformedToken_shouldReturnEmpty() {
            // given
            String token = "not.a.valid.jwt.token";

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("roles가 없으면 빈 Set을 반환한다")
        void extractClaims_withNoRoles_shouldReturnEmptyRoles() {
            // given
            String token =
                    Jwts.builder()
                            .subject(USER_ID)
                            .issuer(ISSUER)
                            .claim("tid", TENANT_ID)
                            .expiration(new Date(System.currentTimeMillis() + 3600000))
                            .signWith(secretKey)
                            .compact();

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().roles()).isEmpty();
        }

        @Test
        @DisplayName("permissions가 없으면 빈 Set을 반환한다")
        void extractClaims_withNoPermissions_shouldReturnEmptyPermissions() {
            // given
            String token =
                    Jwts.builder()
                            .subject(USER_ID)
                            .issuer(ISSUER)
                            .claim("tid", TENANT_ID)
                            .expiration(new Date(System.currentTimeMillis() + 3600000))
                            .signWith(secretKey)
                            .compact();

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().permissions()).isEmpty();
        }

        @Test
        @DisplayName("tenantId가 없으면 null을 반환한다")
        void extractClaims_withNoTenantId_shouldReturnNullTenantId() {
            // given
            String token =
                    Jwts.builder()
                            .subject(USER_ID)
                            .issuer(ISSUER)
                            .expiration(new Date(System.currentTimeMillis() + 3600000))
                            .signWith(secretKey)
                            .compact();

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().tenantId()).isNull();
        }

        @Test
        @DisplayName("organizationId가 없으면 null을 반환한다")
        void extractClaims_withNoOrganizationId_shouldReturnNullOrganizationId() {
            // given
            String token =
                    Jwts.builder()
                            .subject(USER_ID)
                            .issuer(ISSUER)
                            .claim("tid", TENANT_ID)
                            .expiration(new Date(System.currentTimeMillis() + 3600000))
                            .signWith(secretKey)
                            .compact();

            // when
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().organizationId()).isNull();
        }
    }

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("RSA가 비활성화되면 HMAC 키를 사용한다")
        void constructor_withRsaDisabled_shouldUseHmacKey() {
            // given
            JwtValidationProperties properties = createTestProperties();
            properties.getRsa().setEnabled(false);

            // when
            DefaultJwtClaimsExtractor extractor = new DefaultJwtClaimsExtractor(properties);
            String token = createValidToken();
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("RSA가 활성화되었지만 경로가 없으면 HMAC으로 폴백한다")
        void constructor_withRsaEnabledButNoPath_shouldFallbackToHmac() {
            // given
            JwtValidationProperties properties = createTestProperties();
            properties.getRsa().setEnabled(true);
            properties.getRsa().setPublicKeyPath(null);

            // when
            DefaultJwtClaimsExtractor extractor = new DefaultJwtClaimsExtractor(properties);
            String token = createValidToken();
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("RSA가 활성화되었지만 빈 경로면 HMAC으로 폴백한다")
        void constructor_withRsaEnabledButEmptyPath_shouldFallbackToHmac() {
            // given
            JwtValidationProperties properties = createTestProperties();
            properties.getRsa().setEnabled(true);
            properties.getRsa().setPublicKeyPath("");

            // when
            DefaultJwtClaimsExtractor extractor = new DefaultJwtClaimsExtractor(properties);
            String token = createValidToken();
            Optional<JwtClaimsExtractor.JwtClaims> result = extractor.extractClaims(token);

            // then
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("RSA가 활성화되고 잘못된 경로면 예외가 발생한다")
        void constructor_withRsaEnabledAndInvalidPath_shouldThrowException() {
            // given
            JwtValidationProperties properties = createTestProperties();
            properties.getRsa().setEnabled(true);
            properties.getRsa().setPublicKeyPath("/invalid/path/to/public.pem");

            // when & then
            assertThatThrownBy(() -> new DefaultJwtClaimsExtractor(properties))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Failed to load RSA public key");
        }
    }

    private String createValidToken() {
        return Jwts.builder()
                .subject(USER_ID)
                .issuer(ISSUER)
                .claim("tid", TENANT_ID)
                .claim("oid", ORG_ID)
                .claim("roles", List.of("TENANT_ADMIN", "USER"))
                .claim("permissions", List.of("user:read", "user:create"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();
    }

    private String createExpiredToken() {
        return Jwts.builder()
                .subject(USER_ID)
                .issuer(ISSUER)
                .claim("tid", TENANT_ID)
                .issuedAt(new Date(System.currentTimeMillis() - 7200000))
                .expiration(new Date(System.currentTimeMillis() - 3600000))
                .signWith(secretKey)
                .compact();
    }
}
