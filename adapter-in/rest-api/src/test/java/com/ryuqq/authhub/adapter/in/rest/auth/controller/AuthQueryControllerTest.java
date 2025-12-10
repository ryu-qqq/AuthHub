package com.ryuqq.authhub.adapter.in.rest.auth.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.application.auth.dto.response.JwkResponse;
import com.ryuqq.authhub.application.auth.dto.response.JwksResponse;
import com.ryuqq.authhub.application.auth.port.in.query.GetJwksUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * AuthQueryController 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>HTTP 요청/응답 매핑
 *   <li>Response DTO 직렬화
 *   <li>HTTP Status Code
 *   <li>UseCase 호출 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(AuthQueryController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("AuthQueryController 테스트")
class AuthQueryControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private GetJwksUseCase getJwksUseCase;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/auth/jwks 테스트")
    class GetJwksTest {

        @Test
        @DisplayName("JWKS 조회 성공 - 단일 키 (200 OK)")
        void givenSingleKey_whenGetJwks_thenReturns200() throws Exception {
            // given
            String kid = "key-1";
            String kty = "RSA";
            String use = "sig";
            String alg = "RS256";
            String n = "modulus-base64url-encoded-string";
            String e = "AQAB";

            JwkResponse jwkResponse = new JwkResponse(kid, kty, use, alg, n, e);
            JwksResponse useCaseResponse = new JwksResponse(List.of(jwkResponse));

            given(getJwksUseCase.execute()).willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(get("/api/v1/auth/jwks").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys").isArray())
                    .andExpect(jsonPath("$.keys.length()").value(1))
                    .andExpect(jsonPath("$.keys[0].kid").value(kid))
                    .andExpect(jsonPath("$.keys[0].kty").value(kty))
                    .andExpect(jsonPath("$.keys[0].use").value(use))
                    .andExpect(jsonPath("$.keys[0].alg").value(alg))
                    .andExpect(jsonPath("$.keys[0].n").value(n))
                    .andExpect(jsonPath("$.keys[0].e").value(e));

            verify(getJwksUseCase).execute();
        }

        @Test
        @DisplayName("JWKS 조회 성공 - 다중 키 (200 OK)")
        void givenMultipleKeys_whenGetJwks_thenReturns200() throws Exception {
            // given
            JwkResponse jwk1 =
                    new JwkResponse(
                            "key-1",
                            "RSA",
                            "sig",
                            "RS256",
                            "modulus1-base64url-encoded-string",
                            "AQAB");

            JwkResponse jwk2 =
                    new JwkResponse(
                            "key-2",
                            "RSA",
                            "sig",
                            "RS256",
                            "modulus2-base64url-encoded-string",
                            "AQAB");

            JwkResponse jwk3 =
                    new JwkResponse(
                            "key-3",
                            "RSA",
                            "sig",
                            "RS256",
                            "modulus3-base64url-encoded-string",
                            "AQAB");

            JwksResponse useCaseResponse = new JwksResponse(List.of(jwk1, jwk2, jwk3));

            given(getJwksUseCase.execute()).willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(get("/api/v1/auth/jwks").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys").isArray())
                    .andExpect(jsonPath("$.keys.length()").value(3))
                    .andExpect(jsonPath("$.keys[0].kid").value("key-1"))
                    .andExpect(jsonPath("$.keys[1].kid").value("key-2"))
                    .andExpect(jsonPath("$.keys[2].kid").value("key-3"))
                    .andExpect(jsonPath("$.keys[0].kty").value("RSA"))
                    .andExpect(jsonPath("$.keys[0].use").value("sig"))
                    .andExpect(jsonPath("$.keys[0].alg").value("RS256"))
                    .andExpect(jsonPath("$.keys[0].e").value("AQAB"));

            verify(getJwksUseCase).execute();
        }

        @Test
        @DisplayName("JWKS 조회 성공 - 빈 키 목록 (200 OK)")
        void givenEmptyKeys_whenGetJwks_thenReturns200() throws Exception {
            // given
            JwksResponse useCaseResponse = new JwksResponse(List.of());

            given(getJwksUseCase.execute()).willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(get("/api/v1/auth/jwks").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys").isArray())
                    .andExpect(jsonPath("$.keys.length()").value(0));

            verify(getJwksUseCase).execute();
        }

        @Test
        @DisplayName("JWKS 조회 성공 - RFC 7517 형식 준수 검증 (200 OK)")
        void givenValidRequest_whenGetJwks_thenReturnsRfc7517CompliantFormat() throws Exception {
            // given
            String kid = "2024-12-10-key";
            String kty = "RSA";
            String use = "sig";
            String alg = "RS256";
            String n =
                    "0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx4cbbfAAtVT86zwu1RK7aP"
                        + "FFxuhDR1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMstn64tZ_2W-5JsGY4Hc5n9yBXArwl9"
                        + "3lqt7_RN5w6Cf0h4QyQ5v-65YGjQR0_FDW2QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdA"
                        + "ZHzu6qMQvRL5hajrn1n91CbOpbISD08qNLyrdkt-bFTWhAI4vMQFh6WeZu0fM4lFd2NcRwr3"
                        + "XPksINHaQ-G_xBniIqbw0Ls1jF44-csFCur-kEgU8awapJzKnqDKgw";
            String e = "AQAB";

            JwkResponse jwkResponse = new JwkResponse(kid, kty, use, alg, n, e);
            JwksResponse useCaseResponse = new JwksResponse(List.of(jwkResponse));

            given(getJwksUseCase.execute()).willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(get("/api/v1/auth/jwks").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.keys").exists())
                    .andExpect(jsonPath("$.keys").isArray())
                    .andExpect(jsonPath("$.keys[0].kid").exists())
                    .andExpect(jsonPath("$.keys[0].kty").value("RSA"))
                    .andExpect(jsonPath("$.keys[0].use").value("sig"))
                    .andExpect(jsonPath("$.keys[0].alg").value("RS256"))
                    .andExpect(jsonPath("$.keys[0].n").exists())
                    .andExpect(jsonPath("$.keys[0].e").value("AQAB"));

            verify(getJwksUseCase).execute();
        }
    }
}
