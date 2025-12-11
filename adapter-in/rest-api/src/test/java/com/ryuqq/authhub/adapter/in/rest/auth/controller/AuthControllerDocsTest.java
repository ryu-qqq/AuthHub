package com.ryuqq.authhub.adapter.in.rest.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LogoutApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.RefreshTokenApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.mapper.AuthApiMapper;
import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.application.auth.dto.command.LoginCommand;
import com.ryuqq.authhub.application.auth.dto.command.LogoutCommand;
import com.ryuqq.authhub.application.auth.dto.command.RefreshTokenCommand;
import com.ryuqq.authhub.application.auth.dto.response.JwkResponse;
import com.ryuqq.authhub.application.auth.dto.response.JwksResponse;
import com.ryuqq.authhub.application.auth.dto.response.LoginResponse;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import com.ryuqq.authhub.application.auth.port.in.LogoutUseCase;
import com.ryuqq.authhub.application.auth.port.in.RefreshTokenUseCase;
import com.ryuqq.authhub.application.auth.port.in.query.GetJwksUseCase;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

/**
 * Auth Controller REST Docs 테스트
 *
 * <p>AuthCommandController와 AuthQueryController의 API 문서화 테스트입니다.
 *
 * <p>문서화 대상:
 *
 * <ul>
 *   <li>POST /api/v1/auth/login - 로그인
 *   <li>POST /api/v1/auth/refresh - 토큰 갱신
 *   <li>POST /api/v1/auth/logout - 로그아웃
 *   <li>GET /api/v1/auth/jwks - JWKS 조회
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest({AuthCommandController.class, AuthQueryController.class})
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("docs")
@DisplayName("Auth Controller REST Docs")
class AuthControllerDocsTest extends RestDocsTestSupport {

    @MockBean private LoginUseCase loginUseCase;

    @MockBean private RefreshTokenUseCase refreshTokenUseCase;

    @MockBean private LogoutUseCase logoutUseCase;

    @MockBean private GetJwksUseCase getJwksUseCase;

    @MockBean private AuthApiMapper authApiMapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/auth/login - 로그인")
    class LoginDocsTest {

        @Test
        @DisplayName("로그인 API 문서화")
        void loginDocs() throws Exception {
            // given
            UUID tenantId = UUID.randomUUID();
            String identifier = "user@example.com";
            String password = "password123!";
            LoginApiRequest request = new LoginApiRequest(tenantId, identifier, password);

            UUID userId = UUID.randomUUID();
            String accessToken =
                    "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0.signature";
            String refreshToken = "refresh_token_abc123";
            Long expiresIn = 3600L;
            String tokenType = "Bearer";

            LoginCommand command = new LoginCommand(tenantId, identifier, password);
            LoginResponse useCaseResponse =
                    new LoginResponse(userId, accessToken, refreshToken, expiresIn, tokenType);

            given(authApiMapper.toLoginCommand(any(LoginApiRequest.class))).willReturn(command);
            given(loginUseCase.execute(any(LoginCommand.class))).willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andDo(
                            document(
                                    "auth-login",
                                    requestFields(
                                            fieldWithPath("tenantId")
                                                    .description("테넌트 ID (UUID 형식)"),
                                            fieldWithPath("identifier")
                                                    .description("사용자 식별자 (이메일)"),
                                            fieldWithPath("password").description("비밀번호")),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data.userId").description("사용자 ID"),
                                            fieldWithPath("data.accessToken")
                                                    .description("액세스 토큰 (JWT)"),
                                            fieldWithPath("data.refreshToken")
                                                    .description("리프레시 토큰"),
                                            fieldWithPath("data.expiresIn")
                                                    .description("액세스 토큰 만료 시간 (초)"),
                                            fieldWithPath("data.tokenType")
                                                    .description("토큰 타입 (Bearer)"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간 (ISO-8601 형식)"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/refresh - 토큰 갱신")
    class RefreshTokenDocsTest {

        @Test
        @DisplayName("토큰 갱신 API 문서화")
        void refreshTokenDocs() throws Exception {
            // given
            String inputRefreshToken = "refresh_token_abc123";
            RefreshTokenApiRequest request = new RefreshTokenApiRequest(inputRefreshToken);

            String newAccessToken =
                    "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0.new_signature";
            String newRefreshToken = "refresh_token_def456";
            long accessTokenExpiresIn = 3_600L;
            long refreshTokenExpiresIn = 86_400L;
            String tokenType = "Bearer";

            RefreshTokenCommand command = new RefreshTokenCommand(inputRefreshToken);
            TokenResponse useCaseResponse =
                    new TokenResponse(
                            newAccessToken,
                            newRefreshToken,
                            accessTokenExpiresIn,
                            refreshTokenExpiresIn,
                            tokenType);

            given(authApiMapper.toRefreshTokenCommand(any(RefreshTokenApiRequest.class)))
                    .willReturn(command);
            given(refreshTokenUseCase.execute(any(RefreshTokenCommand.class)))
                    .willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/refresh")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "auth-refresh",
                                    requestFields(
                                            fieldWithPath("refreshToken").description("리프레시 토큰")),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data.accessToken")
                                                    .description("새로운 액세스 토큰 (JWT)"),
                                            fieldWithPath("data.refreshToken")
                                                    .description("새로운 리프레시 토큰"),
                                            fieldWithPath("data.accessTokenExpiresIn")
                                                    .description("액세스 토큰 만료 시간 (초)"),
                                            fieldWithPath("data.refreshTokenExpiresIn")
                                                    .description("리프레시 토큰 만료 시간 (초)"),
                                            fieldWithPath("data.tokenType")
                                                    .description("토큰 타입 (Bearer)"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간 (ISO-8601 형식)"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/logout - 로그아웃")
    class LogoutDocsTest {

        @Test
        @DisplayName("로그아웃 API 문서화")
        void logoutDocs() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            LogoutApiRequest request = new LogoutApiRequest(userId);

            LogoutCommand command = new LogoutCommand(userId);

            given(authApiMapper.toLogoutCommand(any(LogoutApiRequest.class))).willReturn(command);

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/logout")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "auth-logout",
                                    requestFields(
                                            fieldWithPath("userId").description("사용자 ID (UUID)")),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터 (성공 시 null)"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간 (ISO-8601 형식)"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/jwks - JWKS 조회")
    class GetJwksDocsTest {

        @Test
        @DisplayName("JWKS 조회 API 문서화")
        void getJwksDocs() throws Exception {
            // given
            String kid = "2024-12-10-key-id";
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
                    .andDo(
                            document(
                                    "auth-jwks",
                                    responseFields(
                                            fieldWithPath("keys").description("공개키 목록"),
                                            fieldWithPath("keys[].kid")
                                                    .description("키 ID (Key Identifier)"),
                                            fieldWithPath("keys[].kty")
                                                    .description("키 타입 (RSA, EC 등)"),
                                            fieldWithPath("keys[].use")
                                                    .description("키 용도 (sig=서명, enc=암호화)"),
                                            fieldWithPath("keys[].alg")
                                                    .description("알고리즘 (RS256, RS512 등)"),
                                            fieldWithPath("keys[].n")
                                                    .description("RSA Modulus (Base64URL 인코딩)"),
                                            fieldWithPath("keys[].e")
                                                    .description("RSA Exponent (Base64URL 인코딩)"))));
        }
    }
}
