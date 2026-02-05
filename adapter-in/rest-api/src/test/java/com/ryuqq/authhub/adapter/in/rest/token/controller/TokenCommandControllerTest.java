package com.ryuqq.authhub.adapter.in.rest.token.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.token.TokenApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.LogoutApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.RefreshTokenApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.fixture.TokenApiFixture;
import com.ryuqq.authhub.adapter.in.rest.token.mapper.TokenApiMapper;
import com.ryuqq.authhub.application.token.dto.response.LoginResponse;
import com.ryuqq.authhub.application.token.dto.response.TokenResponse;
import com.ryuqq.authhub.application.token.port.in.command.LoginUseCase;
import com.ryuqq.authhub.application.token.port.in.command.LogoutUseCase;
import com.ryuqq.authhub.application.token.port.in.command.RefreshTokenUseCase;
import com.ryuqq.authhub.domain.token.exception.InvalidCredentialsException;
import com.ryuqq.authhub.domain.token.exception.InvalidRefreshTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

/**
 * TokenCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(TokenCommandController.class)
@Import({ControllerTestSecurityConfig.class, TokenApiMapper.class})
@DisplayName("TokenCommandController 테스트")
class TokenCommandControllerTest extends RestDocsTestSupport {

    @MockBean private LoginUseCase loginUseCase;

    @MockBean private RefreshTokenUseCase refreshTokenUseCase;

    @MockBean private LogoutUseCase logoutUseCase;

    @Nested
    @DisplayName("POST /api/v1/auth/login - 로그인")
    class LoginTests {

        @Test
        @DisplayName("유효한 요청으로 로그인한다")
        void shouldLoginSuccessfully() throws Exception {
            // given
            LoginApiRequest request = TokenApiFixture.loginRequest();
            LoginResponse response =
                    new LoginResponse(
                            TokenApiFixture.defaultUserId(),
                            TokenApiFixture.defaultAccessToken(),
                            TokenApiFixture.defaultRefreshToken(),
                            3600L,
                            "Bearer");
            given(loginUseCase.execute(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.LOGIN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(TokenApiFixture.defaultUserId()))
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists())
                    .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                    .andDo(
                            document(
                                    "token/login",
                                    requestFields(
                                            fieldWithPath("identifier")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 식별자 (이메일 또는 사용자명) - 필수"),
                                            fieldWithPath("password")
                                                    .type(JsonFieldType.STRING)
                                                    .description("비밀번호 - 필수")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data.userId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 ID"),
                                            fieldWithPath("data.accessToken")
                                                    .type(JsonFieldType.STRING)
                                                    .description("액세스 토큰"),
                                            fieldWithPath("data.refreshToken")
                                                    .type(JsonFieldType.STRING)
                                                    .description("리프레시 토큰"),
                                            fieldWithPath("data.expiresIn")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("액세스 토큰 만료 시간(초)"),
                                            fieldWithPath("data.tokenType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("토큰 타입 (Bearer)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("식별자가 없으면 400 Bad Request")
        void shouldFailWhenIdentifierIsBlank() throws Exception {
            // given
            LoginApiRequest request = new LoginApiRequest("", TokenApiFixture.defaultPassword());

            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.LOGIN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("비밀번호가 없으면 400 Bad Request")
        void shouldFailWhenPasswordIsBlank() throws Exception {
            // given
            LoginApiRequest request = new LoginApiRequest(TokenApiFixture.defaultIdentifier(), "");

            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.LOGIN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("잘못된 자격증명으로 로그인하면 401 Unauthorized")
        void shouldFailWhenCredentialsAreInvalid() throws Exception {
            // given
            LoginApiRequest request = TokenApiFixture.loginRequest();
            willThrow(new InvalidCredentialsException()).given(loginUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.LOGIN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(401));
        }

        @Test
        @DisplayName("null request body면 400 Bad Request")
        void shouldFailWhenRequestBodyIsNull() throws Exception {
            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.LOGIN)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("잘못된 JSON 형식이면 400 Bad Request")
        void shouldFailWhenJsonIsInvalid() throws Exception {
            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.LOGIN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{invalid json}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/refresh - 토큰 갱신")
    class RefreshTests {

        @Test
        @DisplayName("유효한 리프레시 토큰으로 새 토큰을 발급받는다")
        void shouldRefreshTokenSuccessfully() throws Exception {
            // given
            RefreshTokenApiRequest request = TokenApiFixture.refreshTokenRequest();
            TokenResponse response =
                    new TokenResponse(
                            TokenApiFixture.defaultAccessToken(),
                            TokenApiFixture.defaultRefreshToken(),
                            3600L,
                            604800L,
                            "Bearer");
            given(refreshTokenUseCase.execute(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.REFRESH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists())
                    .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                    .andDo(
                            document(
                                    "token/refresh",
                                    requestFields(
                                            fieldWithPath("refreshToken")
                                                    .type(JsonFieldType.STRING)
                                                    .description("리프레시 토큰 - 필수")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data.accessToken")
                                                    .type(JsonFieldType.STRING)
                                                    .description("새로운 액세스 토큰"),
                                            fieldWithPath("data.refreshToken")
                                                    .type(JsonFieldType.STRING)
                                                    .description("새로운 리프레시 토큰"),
                                            fieldWithPath("data.accessTokenExpiresIn")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("액세스 토큰 만료 시간(초)"),
                                            fieldWithPath("data.refreshTokenExpiresIn")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("리프레시 토큰 만료 시간(초)"),
                                            fieldWithPath("data.tokenType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("토큰 타입 (Bearer)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("리프레시 토큰이 없으면 400 Bad Request")
        void shouldFailWhenRefreshTokenIsBlank() throws Exception {
            // given
            RefreshTokenApiRequest request = new RefreshTokenApiRequest("");

            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.REFRESH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("만료되거나 무효한 리프레시 토큰이면 401 Unauthorized")
        void shouldFailWhenRefreshTokenIsInvalid() throws Exception {
            // given
            RefreshTokenApiRequest request = TokenApiFixture.refreshTokenRequest();
            willThrow(new InvalidRefreshTokenException()).given(refreshTokenUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.REFRESH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(401));
        }

        @Test
        @DisplayName("null request body면 400 Bad Request")
        void shouldFailWhenRequestBodyIsNull() throws Exception {
            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.REFRESH)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/logout - 로그아웃")
    class LogoutTests {

        @Test
        @DisplayName("유효한 요청으로 로그아웃한다")
        void shouldLogoutSuccessfully() throws Exception {
            // given
            LogoutApiRequest request = TokenApiFixture.logoutRequest();
            doNothing().when(logoutUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.LOGOUT)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(
                            document(
                                    "token/logout",
                                    requestFields(
                                            fieldWithPath("userId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 ID - 필수")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (없음)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("사용자 ID가 없으면 400 Bad Request")
        void shouldFailWhenUserIdIsBlank() throws Exception {
            // given
            LogoutApiRequest request = new LogoutApiRequest("");

            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.LOGOUT)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("null request body면 400 Bad Request")
        void shouldFailWhenRequestBodyIsNull() throws Exception {
            // when & then
            mockMvc.perform(
                            post(TokenApiEndpoints.BASE + TokenApiEndpoints.LOGOUT)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }
}
