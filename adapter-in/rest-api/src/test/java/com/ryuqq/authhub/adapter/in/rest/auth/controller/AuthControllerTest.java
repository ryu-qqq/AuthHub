package com.ryuqq.authhub.adapter.in.rest.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.request.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.response.LoginApiResponse;
import com.ryuqq.authhub.adapter.in.rest.auth.mapper.AuthApiMapper;
import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController 단위 테스트 - Mockito 기반.
 *
 * <p>REST API 엔드포인트의 요청/응답을 검증하는 단위 테스트입니다.
 * MockMvc를 사용하여 실제 HTTP 요청을 시뮬레이션하고 결과를 검증합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>MockitoExtension: Spring 컨텍스트 없이 Mockito만 사용하여 빠른 테스트</li>
 *   <li>Mock: UseCase와 Mapper는 Mocking하여 Controller 로직에만 집중</li>
 *   <li>MockMvc Standalone: 컨트롤러만 독립적으로 테스트 (Bean Validation 자동 적용)</li>
 * </ul>
 *
 * <p><strong>검증 항목:</strong></p>
 * <ul>
 *   <li>정상 케이스: 200 OK 응답, JWT 토큰 정보 반환</li>
 *   <li>Validation 실패: 400 Bad Request 응답 (credentialType, identifier, password, platform)</li>
 *   <li>JSON 직렬화/역직렬화 정상 동작</li>
 * </ul>
 *
 * <p><strong>참고:</strong></p>
 * <p>이 테스트는 Spring 컨텍스트 없이 순수 Mockito로 실행되지만,
 * MockMvc Standalone 모드에서 Bean Validation이 자동으로 적용됩니다.
 * 전체 애플리케이션 컨텍스트 통합 테스트는 Bootstrap 모듈에서 제공됩니다.</p>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 단위 테스트")
class AuthControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private LoginUseCase loginUseCase;

    @Mock
    private AuthApiMapper authApiMapper;

    @InjectMocks
    private AuthController authController;

    /**
     * 각 테스트 실행 전 MockMvc 초기화.
     * Standalone 모드로 AuthController만 등록하여 테스트합니다.
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 로그인 성공 시 200 OK와 JWT 토큰을 반환하는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 실행 중 발생하는 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
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

    /**
     * credentialType이 null이면 400 Bad Request를 반환하는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 실행 중 발생하는 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
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

    /**
     * identifier가 blank이면 400 Bad Request를 반환하는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 실행 중 발생하는 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
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

    /**
     * password가 null이면 400 Bad Request를 반환하는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 실행 중 발생하는 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
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

    /**
     * platform이 blank이면 400 Bad Request를 반환하는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 실행 중 발생하는 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
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
