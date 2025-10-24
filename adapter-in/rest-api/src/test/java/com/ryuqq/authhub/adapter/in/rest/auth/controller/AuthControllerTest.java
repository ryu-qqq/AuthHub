package com.ryuqq.authhub.adapter.in.rest.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.request.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.response.LoginApiResponse;
import com.ryuqq.authhub.adapter.in.rest.auth.mapper.AuthApiMapper;
import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController 통합 테스트 - MockMvc 기반.
 *
 * <p>REST API 엔드포인트의 요청/응답을 검증하는 통합 테스트입니다.
 * MockMvc를 사용하여 실제 HTTP 요청을 시뮬레이션하고 결과를 검증합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>WebMvcTest: Controller Layer만 로드하여 테스트 속도 향상</li>
 *   <li>MockBean: UseCase는 Mocking하여 Controller 로직에만 집중</li>
 *   <li>MockMvc: 실제 HTTP 요청/응답 시뮬레이션</li>
 * </ul>
 *
 * <p><strong>검증 항목:</strong></p>
 * <ul>
 *   <li>정상 케이스: 200 OK 응답, JWT 토큰 정보 반환</li>
 *   <li>Validation 실패: 400 Bad Request 응답</li>
 *   <li>JSON 직렬화/역직렬화 정상 동작</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@WebMvcTest(AuthController.class)
@DisplayName("AuthController 통합 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private AuthApiMapper authApiMapper;

    @Test
    @DisplayName("로그인 성공 시 200 OK와 JWT 토큰을 반환한다")
    void login_Success_ReturnsJwtTokens() throws Exception {
        // Given
        final LoginApiRequest request = new LoginApiRequest(
                "EMAIL",
                "test@example.com",
                "password123!",
                "WEB"
        );

        final LoginUseCase.Command command = new LoginUseCase.Command(
                "EMAIL",
                "test@example.com",
                "password123!",
                "WEB"
        );

        final LoginUseCase.Response useCaseResponse = new LoginUseCase.Response(
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.access",
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.refresh",
                "Bearer",
                900
        );

        final LoginApiResponse apiResponse = new LoginApiResponse(
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.access",
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.refresh",
                "Bearer",
                900
        );

        given(authApiMapper.toCommand(any(LoginApiRequest.class))).willReturn(command);
        given(loginUseCase.login(any(LoginUseCase.Command.class))).willReturn(useCaseResponse);
        given(authApiMapper.toApiResponse(any(LoginUseCase.Response.class))).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.access"))
                .andExpect(jsonPath("$.refreshToken").value("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.refresh"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    @DisplayName("credentialType이 null이면 400 Bad Request를 반환한다")
    void login_NullCredentialType_ReturnsBadRequest() throws Exception {
        // Given
        final LoginApiRequest request = new LoginApiRequest(
                null,  // credentialType이 null
                "test@example.com",
                "password123!",
                "WEB"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("identifier가 blank이면 400 Bad Request를 반환한다")
    void login_BlankIdentifier_ReturnsBadRequest() throws Exception {
        // Given
        final LoginApiRequest request = new LoginApiRequest(
                "EMAIL",
                "   ",  // identifier가 blank
                "password123!",
                "WEB"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("password가 null이면 400 Bad Request를 반환한다")
    void login_NullPassword_ReturnsBadRequest() throws Exception {
        // Given
        final LoginApiRequest request = new LoginApiRequest(
                "EMAIL",
                "test@example.com",
                null,  // password가 null
                "WEB"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("platform이 blank이면 400 Bad Request를 반환한다")
    void login_BlankPlatform_ReturnsBadRequest() throws Exception {
        // Given
        final LoginApiRequest request = new LoginApiRequest(
                "EMAIL",
                "test@example.com",
                "password123!",
                ""  // platform이 blank
        );

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
