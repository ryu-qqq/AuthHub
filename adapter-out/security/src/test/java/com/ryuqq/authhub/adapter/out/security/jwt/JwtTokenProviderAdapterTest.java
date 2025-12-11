package com.ryuqq.authhub.adapter.out.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.out.security.config.JwtProperties;
import com.ryuqq.authhub.application.auth.dto.command.TokenClaimsContext;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * JwtTokenProviderAdapter 단위 테스트
 *
 * <p>JWT 토큰 생성 및 Hybrid Payload 검증
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("JwtTokenProviderAdapter 단위 테스트")
class JwtTokenProviderAdapterTest {

    private static final String TEST_SECRET =
            "test-secret-key-for-jwt-token-generation-must-be-at-least-256-bits";
    private static final long ACCESS_TOKEN_EXPIRATION = 3600L;
    private static final long REFRESH_TOKEN_EXPIRATION = 604800L;
    private static final String ISSUER = "test-authhub";

    private JwtTokenProviderAdapter adapter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(TEST_SECRET);
        properties.setAccessTokenExpiration(ACCESS_TOKEN_EXPIRATION);
        properties.setRefreshTokenExpiration(REFRESH_TOKEN_EXPIRATION);
        properties.setIssuer(ISSUER);

        adapter = new JwtTokenProviderAdapter(properties);
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("generateTokenPair 메서드")
    class GenerateTokenPairTest {

        @Test
        @DisplayName("토큰 쌍을 성공적으로 생성한다")
        void shouldGenerateTokenPairSuccessfully() {
            // given
            TokenClaimsContext context = createTestContext();

            // when
            TokenResponse response = adapter.generateTokenPair(context);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isNotBlank();
            assertThat(response.refreshToken()).isNotBlank();
            assertThat(response.accessTokenExpiresIn()).isEqualTo(ACCESS_TOKEN_EXPIRATION);
            assertThat(response.refreshTokenExpiresIn()).isEqualTo(REFRESH_TOKEN_EXPIRATION);
            assertThat(response.tokenType()).isEqualTo("Bearer");
        }

        @Test
        @DisplayName("Access Token과 Refresh Token은 서로 다르다")
        void shouldGenerateDifferentTokens() {
            // given
            TokenClaimsContext context = createTestContext();

            // when
            TokenResponse response = adapter.generateTokenPair(context);

            // then
            assertThat(response.accessToken()).isNotEqualTo(response.refreshToken());
        }
    }

    @Nested
    @DisplayName("Hybrid JWT Payload 검증")
    class HybridJwtPayloadTest {

        @Test
        @DisplayName("Access Token Payload에 tenant_name이 포함된다")
        void shouldIncludeTenantNameInAccessToken() throws Exception {
            // given
            String tenantName = "테스트 테넌트";
            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(UserId.of(UUID.randomUUID()))
                            .tenantId(UUID.randomUUID())
                            .tenantName(tenantName)
                            .organizationId(UUID.randomUUID())
                            .organizationName("테스트 조직")
                            .email("test@example.com")
                            .roles(Set.of("ROLE_USER"))
                            .permissions(Set.of("READ_USER"))
                            .build();

            // when
            TokenResponse response = adapter.generateTokenPair(context);
            JsonNode payload = decodeJwtPayload(response.accessToken());

            // then
            assertThat(payload.get("tenant_name").asText()).isEqualTo(tenantName);
        }

        @Test
        @DisplayName("Access Token Payload에 organization_name이 포함된다")
        void shouldIncludeOrganizationNameInAccessToken() throws Exception {
            // given
            String organizationName = "테스트 조직";
            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(UserId.of(UUID.randomUUID()))
                            .tenantId(UUID.randomUUID())
                            .tenantName("테스트 테넌트")
                            .organizationId(UUID.randomUUID())
                            .organizationName(organizationName)
                            .email("test@example.com")
                            .roles(Set.of("ROLE_USER"))
                            .permissions(Set.of("READ_USER"))
                            .build();

            // when
            TokenResponse response = adapter.generateTokenPair(context);
            JsonNode payload = decodeJwtPayload(response.accessToken());

            // then
            assertThat(payload.get("org_name").asText()).isEqualTo(organizationName);
        }

        @Test
        @DisplayName("Access Token Payload에 email이 포함된다")
        void shouldIncludeEmailInAccessToken() throws Exception {
            // given
            String email = "user@example.com";
            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(UserId.of(UUID.randomUUID()))
                            .tenantId(UUID.randomUUID())
                            .tenantName("테스트 테넌트")
                            .organizationId(UUID.randomUUID())
                            .organizationName("테스트 조직")
                            .email(email)
                            .roles(Set.of("ROLE_USER"))
                            .permissions(Set.of("READ_USER"))
                            .build();

            // when
            TokenResponse response = adapter.generateTokenPair(context);
            JsonNode payload = decodeJwtPayload(response.accessToken());

            // then
            assertThat(payload.get("email").asText()).isEqualTo(email);
        }

        @Test
        @DisplayName("Access Token Payload에 모든 Hybrid Claims가 포함된다")
        void shouldIncludeAllHybridClaimsInAccessToken() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            String tenantName = "테스트 테넌트";
            String organizationName = "테스트 조직";
            String email = "test@example.com";
            Set<String> roles = Set.of("ROLE_ADMIN", "ROLE_USER");
            Set<String> permissions = Set.of("READ_USER", "WRITE_USER");

            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(UserId.of(userId))
                            .tenantId(tenantId)
                            .tenantName(tenantName)
                            .organizationId(organizationId)
                            .organizationName(organizationName)
                            .email(email)
                            .roles(roles)
                            .permissions(permissions)
                            .build();

            // when
            TokenResponse response = adapter.generateTokenPair(context);
            JsonNode payload = decodeJwtPayload(response.accessToken());

            // then
            assertThat(payload.get("sub").asText()).isEqualTo(userId.toString());
            assertThat(payload.get("tid").asText()).isEqualTo(tenantId.toString());
            assertThat(payload.get("tenant_name").asText()).isEqualTo(tenantName);
            assertThat(payload.get("oid").asText()).isEqualTo(organizationId.toString());
            assertThat(payload.get("org_name").asText()).isEqualTo(organizationName);
            assertThat(payload.get("email").asText()).isEqualTo(email);
            assertThat(payload.get("token_type").asText()).isEqualTo("access");
            assertThat(payload.get("iss").asText()).isEqualTo(ISSUER);
            assertThat(payload.has("roles")).isTrue();
            assertThat(payload.has("permissions")).isTrue();
        }

        @Test
        @DisplayName("Refresh Token Payload에는 Hybrid Claims가 포함되지 않는다")
        void shouldNotIncludeHybridClaimsInRefreshToken() throws Exception {
            // given
            TokenClaimsContext context = createTestContext();

            // when
            TokenResponse response = adapter.generateTokenPair(context);
            JsonNode payload = decodeJwtPayload(response.refreshToken());

            // then
            assertThat(payload.get("token_type").asText()).isEqualTo("refresh");
            assertThat(payload.has("tenant_name")).isFalse();
            assertThat(payload.has("org_name")).isFalse();
            assertThat(payload.has("email")).isFalse();
            assertThat(payload.has("roles")).isFalse();
            assertThat(payload.has("permissions")).isFalse();
        }
    }

    @Nested
    @DisplayName("Base64 디코딩 검증 (키 없이 디코딩 가능)")
    class Base64DecodingTest {

        @Test
        @DisplayName("JWT Payload를 키 없이 Base64 디코딩으로 읽을 수 있다")
        void shouldDecodePayloadWithoutKey() throws Exception {
            // given
            String tenantName = "테스트 테넌트";
            String email = "test@example.com";
            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(UserId.of(UUID.randomUUID()))
                            .tenantId(UUID.randomUUID())
                            .tenantName(tenantName)
                            .organizationId(UUID.randomUUID())
                            .organizationName("테스트 조직")
                            .email(email)
                            .roles(Set.of("ROLE_USER"))
                            .permissions(Set.of("READ_USER"))
                            .build();

            // when
            TokenResponse response = adapter.generateTokenPair(context);

            // Base64 디코딩만으로 payload 확인 (키 불필요)
            String[] parts = response.accessToken().split("\\.");
            assertThat(parts).hasSize(3);

            String payloadJson =
                    new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode payload = objectMapper.readTree(payloadJson);

            // then - 다른 서비스에서 키 없이도 정보 확인 가능
            assertThat(payload.get("tenant_name").asText()).isEqualTo(tenantName);
            assertThat(payload.get("email").asText()).isEqualTo(email);
        }
    }

    @Nested
    @DisplayName("토큰 구조 검증")
    class TokenStructureTest {

        @Test
        @DisplayName("JWT 토큰은 Header.Payload.Signature 구조를 가진다")
        void shouldHaveValidJwtStructure() {
            // given
            TokenClaimsContext context = createTestContext();

            // when
            TokenResponse response = adapter.generateTokenPair(context);

            // then
            String[] accessParts = response.accessToken().split("\\.");
            String[] refreshParts = response.refreshToken().split("\\.");

            assertThat(accessParts).hasSize(3);
            assertThat(refreshParts).hasSize(3);
        }
    }

    private TokenClaimsContext createTestContext() {
        return TokenClaimsContext.builder()
                .userId(UserId.of(UUID.randomUUID()))
                .tenantId(UUID.randomUUID())
                .tenantName("테스트 테넌트")
                .organizationId(UUID.randomUUID())
                .organizationName("테스트 조직")
                .email("test@example.com")
                .roles(Set.of("ROLE_USER"))
                .permissions(Set.of("READ_USER"))
                .build();
    }

    private JsonNode decodeJwtPayload(String token) throws Exception {
        String[] parts = token.split("\\.");
        String payloadJson =
                new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        return objectMapper.readTree(payloadJson);
    }
}
