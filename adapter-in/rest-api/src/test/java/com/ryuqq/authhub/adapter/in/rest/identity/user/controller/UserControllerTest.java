package com.ryuqq.authhub.adapter.in.rest.identity.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.identity.user.dto.request.RegisterUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.identity.user.dto.response.RegisterUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.identity.user.mapper.UserApiMapper;
import com.ryuqq.authhub.application.identity.port.in.RegisterUserUseCase;
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
 * UserController 단위 테스트 - Mockito 기반.
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
 *   <li>정상 케이스: 201 Created 응답, userId와 credentialId 반환</li>
 *   <li>Validation 실패: 400 Bad Request 응답 (필수 필드, 길이 제한)</li>
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
@DisplayName("UserController 단위 테스트")
class UserControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @Mock
    private UserApiMapper userApiMapper;

    @InjectMocks
    private UserController userController;

    /**
     * 각 테스트 실행 전 MockMvc 초기화.
     * Standalone 모드로 UserController만 등록하여 테스트합니다.
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 사용자 등록 성공 시 201 Created와 userId, credentialId를 반환하는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 실행 중 발생하는 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Test
    @DisplayName("사용자 등록 성공 시 201 Created와 사용자 정보를 반환한다")
    void register_Success_ReturnsCreatedWithUserInfo() throws Exception {
        // Given
        final RegisterUserApiRequest request = new RegisterUserApiRequest(
                "EMAIL",
                "test@example.com",
                "password123!",
                "testuser"
        );

        final RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "EMAIL",
                "test@example.com",
                "password123!",
                "testuser"
        );

        final RegisterUserUseCase.Response useCaseResponse = new RegisterUserUseCase.Response(
                "550e8400-e29b-41d4-a716-446655440000",
                "660e8400-e29b-41d4-a716-446655440111"
        );

        final RegisterUserApiResponse apiResponse = new RegisterUserApiResponse(
                "550e8400-e29b-41d4-a716-446655440000",
                "660e8400-e29b-41d4-a716-446655440111"
        );

        given(userApiMapper.toCommand(any(RegisterUserApiRequest.class))).willReturn(command);
        given(registerUserUseCase.register(any(RegisterUserUseCase.Command.class))).willReturn(useCaseResponse);
        given(userApiMapper.toApiResponse(any(RegisterUserUseCase.Response.class))).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.credentialId").value("660e8400-e29b-41d4-a716-446655440111"));
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
    void register_NullCredentialType_ReturnsBadRequest() throws Exception {
        // Given
        final RegisterUserApiRequest request = new RegisterUserApiRequest(
                null,  // credentialType이 null
                "test@example.com",
                "password123!",
                "testuser"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
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
    void register_BlankIdentifier_ReturnsBadRequest() throws Exception {
        // Given
        final RegisterUserApiRequest request = new RegisterUserApiRequest(
                "EMAIL",
                "   ",  // identifier가 blank
                "password123!",
                "testuser"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * password가 8자 미만이면 400 Bad Request를 반환하는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 실행 중 발생하는 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Test
    @DisplayName("password가 8자 미만이면 400 Bad Request를 반환한다")
    void register_PasswordTooShort_ReturnsBadRequest() throws Exception {
        // Given
        final RegisterUserApiRequest request = new RegisterUserApiRequest(
                "EMAIL",
                "test@example.com",
                "pass1",  // password가 8자 미만
                "testuser"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * nickname이 2자 미만이면 400 Bad Request를 반환하는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 실행 중 발생하는 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Test
    @DisplayName("nickname이 2자 미만이면 400 Bad Request를 반환한다")
    void register_NicknameTooShort_ReturnsBadRequest() throws Exception {
        // Given
        final RegisterUserApiRequest request = new RegisterUserApiRequest(
                "EMAIL",
                "test@example.com",
                "password123!",
                "a"  // nickname이 2자 미만
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * nickname이 20자를 초과하면 400 Bad Request를 반환하는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 실행 중 발생하는 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Test
    @DisplayName("nickname이 20자를 초과하면 400 Bad Request를 반환한다")
    void register_NicknameTooLong_ReturnsBadRequest() throws Exception {
        // Given
        final RegisterUserApiRequest request = new RegisterUserApiRequest(
                "EMAIL",
                "test@example.com",
                "password123!",
                "a".repeat(21)  // nickname이 20자 초과
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 모든 필드가 null이면 400 Bad Request를 반환하는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 실행 중 발생하는 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Test
    @DisplayName("모든 필드가 null이면 400 Bad Request를 반환한다")
    void register_AllFieldsNull_ReturnsBadRequest() throws Exception {
        // Given
        final RegisterUserApiRequest request = new RegisterUserApiRequest(
                null,
                null,
                null,
                null
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * credentialType이 허용되지 않은 값이면 400 Bad Request를 반환하는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 실행 중 발생하는 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Test
    @DisplayName("credentialType이 허용되지 않은 값이면 400 Bad Request를 반환한다")
    void register_InvalidCredentialType_ReturnsBadRequest() throws Exception {
        // Given
        final RegisterUserApiRequest request = new RegisterUserApiRequest(
                "INVALID_TYPE",  // 허용되지 않은 credentialType
                "test@example.com",
                "password123!",
                "testuser"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
