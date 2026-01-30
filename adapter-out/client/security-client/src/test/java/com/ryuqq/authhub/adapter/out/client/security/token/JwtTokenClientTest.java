package com.ryuqq.authhub.adapter.out.client.security.token;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.out.client.security.common.RsaKeyLoader;
import com.ryuqq.authhub.adapter.out.client.security.config.JwtProperties;
import com.ryuqq.authhub.adapter.out.client.security.fixture.SecurityClientFixtures;
import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.application.token.dto.response.TokenResponse;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("JwtTokenClient")
class JwtTokenClientTest {

    private JwtTokenClient sut;
    private JwtProperties jwtProperties;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeEach
    void setUp() {
        jwtProperties = SecurityClientFixtures.jwtPropertiesHs256();
        sut = new JwtTokenClient(jwtProperties, createDisabledRsaKeyLoader());
    }

    @Nested
    @DisplayName("generateTokenPair - HS256")
    class GenerateTokenPairHs256 {

        @BeforeEach
        void setUpHs256() {
            jwtProperties = SecurityClientFixtures.jwtPropertiesHs256();
            sut = new JwtTokenClient(jwtProperties, createDisabledRsaKeyLoader());
        }

        @Test
        @DisplayName("Access Token과 Refresh Token 생성")
        void generatesTokenPair() {
            TokenClaimsComposite claims = SecurityClientFixtures.tokenClaimsComposite();
            RolesAndPermissionsComposite rolesAndPerms =
                    SecurityClientFixtures.rolesAndPermissionsComposite();

            TokenResponse result = sut.generateTokenPair(claims, rolesAndPerms);

            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isNotBlank();
            assertThat(result.refreshToken()).isNotBlank();
            assertThat(result.accessTokenExpiresIn()).isEqualTo(3600L);
            assertThat(result.refreshTokenExpiresIn()).isEqualTo(604800L);
            assertThat(result.tokenType()).isEqualTo("Bearer");
        }

        @Test
        @DisplayName("생성된 Access Token 파싱 및 검증")
        void generatedAccessTokenCanBeParsed() {
            TokenClaimsComposite claims = SecurityClientFixtures.tokenClaimsComposite();
            RolesAndPermissionsComposite rolesAndPerms =
                    SecurityClientFixtures.rolesAndPermissionsComposite();

            TokenResponse result = sut.generateTokenPair(claims, rolesAndPerms);

            Claims parsed =
                    Jwts.parser()
                            .verifyWith(
                                    Keys.hmacShaKeyFor(
                                            jwtProperties
                                                    .getSecret()
                                                    .getBytes(StandardCharsets.UTF_8)))
                            .build()
                            .parseSignedClaims(result.accessToken())
                            .getPayload();

            assertThat(parsed.getSubject()).isEqualTo(claims.userId().value().toString());
            assertThat(parsed.getIssuer()).isEqualTo("authhub");
            assertThat(parsed.get("token_type")).isEqualTo("access");
            assertThat(parsed.get("tid")).isEqualTo(claims.tenantId());
            assertThat(parsed.get("email")).isEqualTo(claims.email());
            assertThat(parsed.get("roles")).isInstanceOf(List.class);
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) parsed.get("roles");
            assertThat(roles).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        }

        @Test
        @DisplayName("생성된 Refresh Token 파싱 및 검증")
        void generatedRefreshTokenCanBeParsed() {
            TokenClaimsComposite claims = SecurityClientFixtures.tokenClaimsComposite();
            RolesAndPermissionsComposite rolesAndPerms =
                    SecurityClientFixtures.rolesAndPermissionsComposite();

            TokenResponse result = sut.generateTokenPair(claims, rolesAndPerms);

            Claims parsed =
                    Jwts.parser()
                            .verifyWith(
                                    Keys.hmacShaKeyFor(
                                            jwtProperties
                                                    .getSecret()
                                                    .getBytes(StandardCharsets.UTF_8)))
                            .build()
                            .parseSignedClaims(result.refreshToken())
                            .getPayload();

            assertThat(parsed.getSubject()).isEqualTo(claims.userId().value().toString());
            assertThat(parsed.get("token_type")).isEqualTo("refresh");
        }

        @Test
        @DisplayName("빈 역할/권한으로 토큰 생성")
        void generatesTokenWithEmptyRolesAndPermissions() {
            TokenClaimsComposite claims = SecurityClientFixtures.tokenClaimsComposite();
            RolesAndPermissionsComposite rolesAndPerms =
                    SecurityClientFixtures.emptyRolesAndPermissions();

            TokenResponse result = sut.generateTokenPair(claims, rolesAndPerms);

            assertThat(result.accessToken()).isNotBlank();
            assertThat(result.refreshToken()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("generateTokenPair - RS256")
    class GenerateTokenPairRs256 {

        @BeforeEach
        void setUpRs256() throws Exception {
            jwtProperties = SecurityClientFixtures.jwtPropertiesRsaWithContent();
            sut = new JwtTokenClient(jwtProperties, new RsaKeyLoader(jwtProperties));
        }

        @Test
        @DisplayName("RSA로 Access Token과 Refresh Token 생성")
        void generatesTokenPairWithRsa() {
            TokenClaimsComposite claims = SecurityClientFixtures.tokenClaimsComposite();
            RolesAndPermissionsComposite rolesAndPerms =
                    SecurityClientFixtures.rolesAndPermissionsComposite();

            TokenResponse result = sut.generateTokenPair(claims, rolesAndPerms);

            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isNotBlank();
            assertThat(result.refreshToken()).isNotBlank();
        }

        @Test
        @DisplayName("RSA 토큰에 kid 헤더 포함")
        void rsaTokenContainsKidHeader() throws Exception {
            TokenClaimsComposite claims = SecurityClientFixtures.tokenClaimsComposite();
            RolesAndPermissionsComposite rolesAndPerms =
                    SecurityClientFixtures.rolesAndPermissionsComposite();

            TokenResponse result = sut.generateTokenPair(claims, rolesAndPerms);

            String[] parts = result.accessToken().split("\\.");
            assertThat(parts).hasSize(3);

            byte[] headerBytes = Base64.getUrlDecoder().decode(parts[0]);
            JsonNode header =
                    OBJECT_MAPPER.readTree(new String(headerBytes, StandardCharsets.UTF_8));
            assertThat(header.get("kid").asText()).isEqualTo("test-key-1");
        }
    }

    private RsaKeyLoader createDisabledRsaKeyLoader() {
        JwtProperties props = new JwtProperties();
        props.getRsa().setEnabled(false);
        return new RsaKeyLoader(props);
    }
}
