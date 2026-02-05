package com.ryuqq.authhub.adapter.in.rest.token.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContext;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContextHolder;
import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.token.TokenApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.token.mapper.TokenApiMapper;
import com.ryuqq.authhub.application.token.dto.response.JwkResponse;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import com.ryuqq.authhub.application.token.dto.response.PublicKeysResponse;
import com.ryuqq.authhub.application.token.port.in.query.GetMyContextUseCase;
import com.ryuqq.authhub.application.token.port.in.query.GetPublicKeyUseCase;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.JsonFieldType;

/**
 * TokenQueryController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(TokenQueryController.class)
@Import({ControllerTestSecurityConfig.class, TokenApiMapper.class})
@DisplayName("TokenQueryController 테스트")
class TokenQueryControllerTest extends RestDocsTestSupport {

    @MockBean private GetPublicKeyUseCase getPublicKeyUseCase;
    @MockBean private GetMyContextUseCase getMyContextUseCase;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("GET /api/v1/auth/jwks - 공개키 목록 조회")
    class GetPublicKeysTests {

        @Test
        @DisplayName("유효한 요청으로 공개키 목록을 조회한다")
        void shouldGetPublicKeysSuccessfully() throws Exception {
            // given
            String modulus =
                    "0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx4cbbfAAtVT86zwu1RK7aPFFxuhD"
                        + "R1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMstn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6C"
                        + "f0h4QyQ5v-65YGjQR0_FDW2QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1"
                        + "n91CbOpbISD08qNLyrdkt-bFTWhAI4vMQFh6WeZu0fM4lFd2NcRwr3XPksINHaQ-G_xBniIqbw0Ls1"
                        + "jF44-csFCur-kEgU8awapJzKnqDKgw";
            JwkResponse jwk = new JwkResponse("key-id-1", "RSA", "sig", "RS256", modulus, "AQAB");
            PublicKeysResponse response = new PublicKeysResponse(List.of(jwk));
            given(getPublicKeyUseCase.execute()).willReturn(response);

            // when & then
            mockMvc.perform(get(TokenApiEndpoints.BASE + TokenApiEndpoints.JWKS))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys").isArray())
                    .andExpect(jsonPath("$.keys[0].kid").value("key-id-1"))
                    .andExpect(jsonPath("$.keys[0].kty").value("RSA"))
                    .andExpect(jsonPath("$.keys[0].use").value("sig"))
                    .andExpect(jsonPath("$.keys[0].alg").value("RS256"))
                    .andDo(
                            document(
                                    "token/jwks",
                                    responseFields(
                                            fieldWithPath("keys")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("JWK 목록"),
                                            fieldWithPath("keys[].kid")
                                                    .type(JsonFieldType.STRING)
                                                    .description("Key ID - 키 식별자"),
                                            fieldWithPath("keys[].kty")
                                                    .type(JsonFieldType.STRING)
                                                    .description("Key Type (RSA)"),
                                            fieldWithPath("keys[].use")
                                                    .type(JsonFieldType.STRING)
                                                    .description("Public Key Use (sig - 서명용)"),
                                            fieldWithPath("keys[].alg")
                                                    .type(JsonFieldType.STRING)
                                                    .description("Algorithm (RS256)"),
                                            fieldWithPath("keys[].n")
                                                    .type(JsonFieldType.STRING)
                                                    .description("RSA modulus (Base64URL encoded)"),
                                            fieldWithPath("keys[].e")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "RSA public exponent (Base64URL"
                                                                    + " encoded)"))));
        }

        @Test
        @DisplayName("빈 공개키 목록을 조회한다")
        void shouldGetEmptyPublicKeysSuccessfully() throws Exception {
            // given
            PublicKeysResponse response = new PublicKeysResponse(List.of());
            given(getPublicKeyUseCase.execute()).willReturn(response);

            // when & then
            mockMvc.perform(get(TokenApiEndpoints.BASE + TokenApiEndpoints.JWKS))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys").isArray())
                    .andExpect(jsonPath("$.keys").isEmpty());
        }

        @Test
        @DisplayName("다중 공개키를 조회한다")
        void shouldGetMultiplePublicKeysSuccessfully() throws Exception {
            // given
            String modulus1 =
                    "0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx4cbbfAAtVT86zwu1RK7aPFFxuhD"
                        + "R1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMstn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6C"
                        + "f0h4QyQ5v-65YGjQR0_FDW2QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1"
                        + "n91CbOpbISD08qNLyrdkt-bFTWhAI4vMQFh6WeZu0fM4lFd2NcRwr3XPksINHaQ-G_xBniIqbw0Ls1"
                        + "jF44-csFCur-kEgU8awapJzKnqDKgw";
            String modulus2 =
                    "1wx8bhpfcHdTRvvQjKYaYquO0onndrQmbXEps2aiAFbWhM78LhWx4cbbfAAtVT86zwu1RK7aPFFxuhD"
                        + "R1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMstn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6C"
                        + "f0h4QyQ5v-65YGjQR0_FDW2QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1"
                        + "n91CbOpbISD08qNLyrdkt-bFTWhAI4vMQFh6WeZu0fM4lFd2NcRwr3XPksINHaQ-G_xBniIqbw0Ls1"
                        + "jF44-csFCur-kEgU8awapJzKnqDKgw";
            JwkResponse jwk1 = new JwkResponse("key-id-1", "RSA", "sig", "RS256", modulus1, "AQAB");
            JwkResponse jwk2 = new JwkResponse("key-id-2", "RSA", "sig", "RS256", modulus2, "AQAB");
            PublicKeysResponse response = new PublicKeysResponse(List.of(jwk1, jwk2));
            given(getPublicKeyUseCase.execute()).willReturn(response);

            // when & then
            mockMvc.perform(get(TokenApiEndpoints.BASE + TokenApiEndpoints.JWKS))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys").isArray())
                    .andExpect(jsonPath("$.keys").isNotEmpty())
                    .andExpect(jsonPath("$.keys[0].kid").value("key-id-1"))
                    .andExpect(jsonPath("$.keys[1].kid").value("key-id-2"));
        }

        @Test
        @DisplayName("UseCase에서 예외가 발생하면 500 Internal Server Error")
        void shouldReturn500WhenUseCaseThrowsException() throws Exception {
            // given
            willThrow(new RuntimeException("Internal error")).given(getPublicKeyUseCase).execute();

            // when & then
            mockMvc.perform(get(TokenApiEndpoints.BASE + TokenApiEndpoints.JWKS))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/me - 내 정보 조회")
    class GetMyContextTests {

        @Test
        @DisplayName("성공: 인증된 사용자의 전체 컨텍스트를 조회한다")
        void shouldGetMyContextSuccessfully() throws Exception {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000001";
            SecurityContext securityContext =
                    SecurityContext.builder()
                            .userId(userId)
                            .tenantId("tenant-123")
                            .organizationId("org-456")
                            .roles(Set.of("ADMIN"))
                            .permissions(Set.of("user:read", "user:write"))
                            .build();
            SecurityContextHolder.setContext(securityContext);

            MyContextResponse useCaseResponse =
                    new MyContextResponse(
                            userId,
                            "test@example.com",
                            "Test User",
                            "tenant-123",
                            "Test Tenant",
                            "org-456",
                            "Test Organization",
                            List.of(new MyContextResponse.RoleInfo("role-1", "ADMIN")),
                            List.of("user:read", "user:write"));

            given(getMyContextUseCase.execute(userId)).willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(get(TokenApiEndpoints.BASE + TokenApiEndpoints.ME))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"))
                    .andExpect(jsonPath("$.data.name").value("Test User"))
                    .andExpect(jsonPath("$.data.tenant.id").value("tenant-123"))
                    .andExpect(jsonPath("$.data.tenant.name").value("Test Tenant"))
                    .andExpect(jsonPath("$.data.organization.id").value("org-456"))
                    .andExpect(jsonPath("$.data.organization.name").value("Test Organization"))
                    .andExpect(jsonPath("$.data.roles[0].id").value("role-1"))
                    .andExpect(jsonPath("$.data.roles[0].name").value("ADMIN"))
                    .andExpect(jsonPath("$.data.permissions[0]").value("user:read"))
                    .andExpect(jsonPath("$.data.permissions[1]").value("user:write"))
                    .andDo(
                            document(
                                    "token/me",
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.userId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 ID (UUIDv7)"),
                                            fieldWithPath("data.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이메일"),
                                            fieldWithPath("data.name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 이름"),
                                            fieldWithPath("data.tenant")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("테넌트 정보"),
                                            fieldWithPath("data.tenant.id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 ID"),
                                            fieldWithPath("data.tenant.name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 이름"),
                                            fieldWithPath("data.organization")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("조직 정보"),
                                            fieldWithPath("data.organization.id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조직 ID"),
                                            fieldWithPath("data.organization.name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조직 이름"),
                                            fieldWithPath("data.roles")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("역할 목록"),
                                            fieldWithPath("data.roles[].id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("역할 ID"),
                                            fieldWithPath("data.roles[].name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("역할 이름"),
                                            fieldWithPath("data.permissions")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("권한 키 목록"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("성공: 역할과 권한이 없는 사용자의 컨텍스트를 조회한다")
        void shouldGetMyContextWithEmptyRolesAndPermissions() throws Exception {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000002";
            SecurityContext securityContext =
                    SecurityContext.builder()
                            .userId(userId)
                            .tenantId("tenant-123")
                            .organizationId("org-456")
                            .roles(Set.of())
                            .permissions(Set.of())
                            .build();
            SecurityContextHolder.setContext(securityContext);

            MyContextResponse useCaseResponse =
                    new MyContextResponse(
                            userId,
                            "newuser@example.com",
                            "New User",
                            "tenant-123",
                            "Test Tenant",
                            "org-456",
                            "Test Organization",
                            List.of(),
                            List.of());

            given(getMyContextUseCase.execute(userId)).willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(get(TokenApiEndpoints.BASE + TokenApiEndpoints.ME))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(jsonPath("$.data.email").value("newuser@example.com"))
                    .andExpect(jsonPath("$.data.roles").isArray())
                    .andExpect(jsonPath("$.data.roles").isEmpty())
                    .andExpect(jsonPath("$.data.permissions").isArray())
                    .andExpect(jsonPath("$.data.permissions").isEmpty());
        }

        @Test
        @DisplayName("성공: 다중 역할과 권한을 가진 사용자의 컨텍스트를 조회한다")
        void shouldGetMyContextWithMultipleRolesAndPermissions() throws Exception {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000003";
            SecurityContext securityContext =
                    SecurityContext.builder()
                            .userId(userId)
                            .tenantId("tenant-123")
                            .organizationId("org-456")
                            .roles(Set.of("ADMIN", "USER", "GUEST"))
                            .permissions(
                                    Set.of(
                                            "user:read",
                                            "user:write",
                                            "user:delete",
                                            "admin:read",
                                            "admin:write"))
                            .build();
            SecurityContextHolder.setContext(securityContext);

            List<MyContextResponse.RoleInfo> roles =
                    List.of(
                            new MyContextResponse.RoleInfo("role-1", "ADMIN"),
                            new MyContextResponse.RoleInfo("role-2", "USER"),
                            new MyContextResponse.RoleInfo("role-3", "GUEST"));
            List<String> permissions =
                    List.of("user:read", "user:write", "user:delete", "admin:read", "admin:write");

            MyContextResponse useCaseResponse =
                    new MyContextResponse(
                            userId,
                            "multiuser@example.com",
                            "Multi User",
                            "tenant-123",
                            "Test Tenant",
                            "org-456",
                            "Test Organization",
                            roles,
                            permissions);

            given(getMyContextUseCase.execute(userId)).willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(get(TokenApiEndpoints.BASE + TokenApiEndpoints.ME))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(jsonPath("$.data.roles").isArray())
                    .andExpect(jsonPath("$.data.roles.length()").value(3))
                    .andExpect(jsonPath("$.data.permissions").isArray())
                    .andExpect(jsonPath("$.data.permissions.length()").value(5));
        }

        @Test
        @DisplayName("SecurityContextHolder에 사용자 ID가 없으면 400 Bad Request")
        void shouldFailWhenSecurityContextHasNoUserId() throws Exception {
            // given
            SecurityContextHolder.clearContext(); // Anonymous context (userId = null)
            // UserId.of(null)이 IllegalArgumentException을 던지므로, UseCase에서 예외 발생
            // GlobalExceptionHandler가 IllegalArgumentException을 400 Bad Request로 처리
            willThrow(new IllegalArgumentException("UserId는 null이거나 빈 값일 수 없습니다"))
                    .given(getMyContextUseCase)
                    .execute(null);

            // when & then
            mockMvc.perform(get(TokenApiEndpoints.BASE + TokenApiEndpoints.ME))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 404 Not Found")
        void shouldFailWhenUserNotFound() throws Exception {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000999";
            SecurityContext securityContext =
                    SecurityContext.builder()
                            .userId(userId)
                            .tenantId("tenant-123")
                            .organizationId("org-456")
                            .roles(Set.of())
                            .permissions(Set.of())
                            .build();
            SecurityContextHolder.setContext(securityContext);

            willThrow(new UserNotFoundException(userId)).given(getMyContextUseCase).execute(userId);

            // when & then
            mockMvc.perform(get(TokenApiEndpoints.BASE + TokenApiEndpoints.ME))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("UseCase에서 예외가 발생하면 500 Internal Server Error")
        void shouldReturn500WhenUseCaseThrowsException() throws Exception {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000001";
            SecurityContext securityContext =
                    SecurityContext.builder()
                            .userId(userId)
                            .tenantId("tenant-123")
                            .organizationId("org-456")
                            .roles(Set.of())
                            .permissions(Set.of())
                            .build();
            SecurityContextHolder.setContext(securityContext);

            willThrow(new RuntimeException("Internal error"))
                    .given(getMyContextUseCase)
                    .execute(userId);

            // when & then
            mockMvc.perform(get(TokenApiEndpoints.BASE + TokenApiEndpoints.ME))
                    .andExpect(status().isInternalServerError());
        }
    }
}
