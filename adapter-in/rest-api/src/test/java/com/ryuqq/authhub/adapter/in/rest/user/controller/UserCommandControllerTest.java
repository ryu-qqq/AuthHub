package com.ryuqq.authhub.adapter.in.rest.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangePasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangeUserStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserApiMapper;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.ChangeUserStatusCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.in.ChangePasswordUseCase;
import com.ryuqq.authhub.application.user.port.in.ChangeUserStatusUseCase;
import com.ryuqq.authhub.application.user.port.in.CreateUserUseCase;
import com.ryuqq.authhub.application.user.port.in.UpdateUserUseCase;

/**
 * UserCommandController 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>HTTP 요청/응답 매핑</li>
 *   <li>Request DTO Validation</li>
 *   <li>Response DTO 직렬화</li>
 *   <li>HTTP Status Code</li>
 *   <li>UseCase 호출 검증</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(UserCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("UserCommandController 테스트")
class UserCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateUserUseCase createUserUseCase;

    @MockBean
    private UpdateUserUseCase updateUserUseCase;

    @MockBean
    private ChangePasswordUseCase changePasswordUseCase;

    @MockBean
    private ChangeUserStatusUseCase changeUserStatusUseCase;

    @MockBean
    private UserApiMapper userApiMapper;

    @MockBean
    private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/users 테스트")
    class CreateUserTest {

        @Test
        @DisplayName("사용자 생성 성공 (201 Created)")
        void givenValidRequest_whenCreateUser_thenReturns201() throws Exception {
            // given
            CreateUserApiRequest request = new CreateUserApiRequest(
                    1L, 10L, "user@example.com", "password123",
                    "PUBLIC", "홍길동", "010-1234-5678");

            UUID userId = UUID.randomUUID();
            Instant createdAt = Instant.now();

            CreateUserCommand command = new CreateUserCommand(
                    1L, 10L, "user@example.com", "password123",
                    "PUBLIC", "홍길동", "010-1234-5678");
            CreateUserResponse useCaseResponse = new CreateUserResponse(userId, createdAt);

            given(userApiMapper.toCreateUserCommand(any(CreateUserApiRequest.class)))
                    .willReturn(command);
            given(createUserUseCase.execute(any(CreateUserCommand.class)))
                    .willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()));

            verify(userApiMapper).toCreateUserCommand(any(CreateUserApiRequest.class));
            verify(createUserUseCase).execute(any(CreateUserCommand.class));
        }

        @Test
        @DisplayName("tenantId 누락 시 Validation 실패 (400 Bad Request)")
        void givenNullTenantId_whenCreateUser_thenReturns400() throws Exception {
            // given
            String invalidRequest = """
                    {
                        "tenantId": null,
                        "organizationId": 10,
                        "identifier": "user@example.com",
                        "password": "password123"
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("identifier 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankIdentifier_whenCreateUser_thenReturns400() throws Exception {
            // given
            String invalidRequest = """
                    {
                        "tenantId": 1,
                        "organizationId": 10,
                        "identifier": "",
                        "password": "password123"
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/users/{userId} 테스트")
    class UpdateUserTest {

        @Test
        @DisplayName("사용자 정보 수정 성공 (200 OK)")
        void givenValidRequest_whenUpdateUser_thenReturns200() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            UpdateUserApiRequest request = new UpdateUserApiRequest("홍길동", "010-1234-5678");

            Instant now = Instant.now();
            UpdateUserCommand command = new UpdateUserCommand(userId, "홍길동", "010-1234-5678");
            UserResponse useCaseResponse = new UserResponse(
                    userId, 1L, 10L, "PUBLIC", "ACTIVE",
                    "홍길동", "010-1234-5678", now, now);

            given(userApiMapper.toUpdateUserCommand(eq(userId), any(UpdateUserApiRequest.class)))
                    .willReturn(command);
            given(updateUserUseCase.execute(any(UpdateUserCommand.class)))
                    .willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(patch("/api/v1/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.name").value("홍길동"))
                    .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"));

            verify(userApiMapper).toUpdateUserCommand(eq(userId), any(UpdateUserApiRequest.class));
            verify(updateUserUseCase).execute(any(UpdateUserCommand.class));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/users/{userId}/password 테스트")
    class ChangePasswordTest {

        @Test
        @DisplayName("비밀번호 변경 성공 (200 OK)")
        void givenValidRequest_whenChangePassword_thenReturns200() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            ChangePasswordApiRequest request = new ChangePasswordApiRequest(
                    "oldPassword123", "newPassword456", false);

            ChangePasswordCommand command = new ChangePasswordCommand(
                    userId, "oldPassword123", "newPassword456", false);

            given(userApiMapper.toChangePasswordCommand(eq(userId), any(ChangePasswordApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(patch("/api/v1/users/{userId}/password", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(userApiMapper).toChangePasswordCommand(eq(userId), any(ChangePasswordApiRequest.class));
            verify(changePasswordUseCase).execute(any(ChangePasswordCommand.class));
        }

        @Test
        @DisplayName("newPassword 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankNewPassword_whenChangePassword_thenReturns400() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            String invalidRequest = """
                    {
                        "currentPassword": "oldPassword123",
                        "newPassword": "",
                        "verified": false
                    }
                    """;

            // when & then
            mockMvc.perform(patch("/api/v1/users/{userId}/password", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/users/{userId}/status 테스트")
    class ChangeUserStatusTest {

        @Test
        @DisplayName("상태 변경 성공 (200 OK)")
        void givenValidRequest_whenChangeStatus_thenReturns200() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            ChangeUserStatusApiRequest request = new ChangeUserStatusApiRequest(
                    "INACTIVE", "휴면 계정 전환");

            ChangeUserStatusCommand command = new ChangeUserStatusCommand(
                    userId, "INACTIVE", "휴면 계정 전환");

            given(userApiMapper.toChangeUserStatusCommand(eq(userId), any(ChangeUserStatusApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(patch("/api/v1/users/{userId}/status", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(userApiMapper).toChangeUserStatusCommand(eq(userId), any(ChangeUserStatusApiRequest.class));
            verify(changeUserStatusUseCase).execute(any(ChangeUserStatusCommand.class));
        }

        @Test
        @DisplayName("targetStatus 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankTargetStatus_whenChangeStatus_thenReturns400() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            String invalidRequest = """
                    {
                        "targetStatus": "",
                        "reason": "사유"
                    }
                    """;

            // when & then
            mockMvc.perform(patch("/api/v1/users/{userId}/status", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }
}
