package com.ryuqq.authhub.adapter.in.rest.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LogoutApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.RefreshTokenApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.mapper.AuthApiMapper;
import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.application.auth.dto.command.LoginCommand;
import com.ryuqq.authhub.application.auth.dto.command.LogoutCommand;
import com.ryuqq.authhub.application.auth.dto.command.RefreshTokenCommand;
import com.ryuqq.authhub.application.auth.dto.response.LoginResponse;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import com.ryuqq.authhub.application.auth.port.in.LogoutUseCase;
import com.ryuqq.authhub.application.auth.port.in.RefreshTokenUseCase;
import java.util.UUID;
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
 * AuthCommandController 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>HTTP 요청/응답 매핑
 *   <li>Request DTO Validation
 *   <li>Response DTO 직렬화
 *   <li>HTTP Status Code
 *   <li>UseCase 호출 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(AuthCommandController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("AuthCommandController 테스트")
class AuthCommandControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private LoginUseCase loginUseCase;

    @MockBean private RefreshTokenUseCase refreshTokenUseCase;

    @MockBean private LogoutUseCase logoutUseCase;

    @MockBean private AuthApiMapper authApiMapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/auth/login 테스트")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공 (201 Created)")
        void givenValidLoginRequest_whenLogin_thenReturns201() throws Exception {
            // given
            UUID tenantId = UUID.randomUUID();
            String identifier = "user@example.com";
            String password = "password123";
            LoginApiRequest request = new LoginApiRequest(tenantId, identifier, password);

            UUID userId = UUID.randomUUID();
            String accessToken = "accessToken123";
            String refreshToken = "refreshToken456";
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
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.accessToken").value(accessToken))
                    .andExpect(jsonPath("$.data.refreshToken").value(refreshToken))
                    .andExpect(jsonPath("$.data.expiresIn").value(expiresIn))
                    .andExpect(jsonPath("$.data.tokenType").value(tokenType));

            verify(authApiMapper).toLoginCommand(any(LoginApiRequest.class));
            verify(loginUseCase).execute(any(LoginCommand.class));
        }

        @Test
        @DisplayName("tenantId 누락 시 Validation 실패 (400 Bad Request)")
        void givenNullTenantId_whenLogin_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "tenantId": null,
                        "identifier": "user@example.com",
                        "password": "password123"
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("identifier 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankIdentifier_whenLogin_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "tenantId": 1,
                        "identifier": "",
                        "password": "password123"
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("password 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankPassword_whenLogin_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "tenantId": 1,
                        "identifier": "user@example.com",
                        "password": ""
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/refresh 테스트")
    class RefreshTokenTest {

        @Test
        @DisplayName("토큰 갱신 성공 (200 OK)")
        void givenValidRefreshRequest_whenRefresh_thenReturns200() throws Exception {
            // given
            String inputRefreshToken = "validRefreshToken";
            RefreshTokenApiRequest request = new RefreshTokenApiRequest(inputRefreshToken);

            String newAccessToken = "newAccessToken123";
            String newRefreshToken = "newRefreshToken456";
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
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").value(newAccessToken))
                    .andExpect(jsonPath("$.data.refreshToken").value(newRefreshToken))
                    .andExpect(jsonPath("$.data.accessTokenExpiresIn").value(accessTokenExpiresIn))
                    .andExpect(
                            jsonPath("$.data.refreshTokenExpiresIn").value(refreshTokenExpiresIn))
                    .andExpect(jsonPath("$.data.tokenType").value(tokenType));

            verify(authApiMapper).toRefreshTokenCommand(any(RefreshTokenApiRequest.class));
            verify(refreshTokenUseCase).execute(any(RefreshTokenCommand.class));
        }

        @Test
        @DisplayName("refreshToken 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankRefreshToken_whenRefresh_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "refreshToken": ""
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/refresh")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/logout 테스트")
    class LogoutTest {

        @Test
        @DisplayName("로그아웃 성공 (200 OK)")
        void givenValidLogoutRequest_whenLogout_thenReturns200() throws Exception {
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
                    .andExpect(jsonPath("$.success").value(true));

            verify(authApiMapper).toLogoutCommand(any(LogoutApiRequest.class));
            verify(logoutUseCase).execute(any(LogoutCommand.class));
        }

        @Test
        @DisplayName("userId 누락 시 Validation 실패 (400 Bad Request)")
        void givenNullUserId_whenLogout_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "userId": null
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/logout")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }
}
