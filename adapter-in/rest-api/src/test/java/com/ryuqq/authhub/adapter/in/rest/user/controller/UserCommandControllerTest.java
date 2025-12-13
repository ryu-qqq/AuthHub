package com.ryuqq.authhub.adapter.in.rest.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserPasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserApiMapper;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.DeleteUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserPasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserStatusCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.in.command.CreateUserUseCase;
import com.ryuqq.authhub.application.user.port.in.command.DeleteUserUseCase;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserPasswordUseCase;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserStatusUseCase;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserUseCase;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * UserCommandController 단위 테스트
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
@WebMvcTest(UserCommandController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("UserCommandController 테스트")
class UserCommandControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private CreateUserUseCase createUserUseCase;

    @MockBean private UpdateUserUseCase updateUserUseCase;

    @MockBean private UpdateUserStatusUseCase updateUserStatusUseCase;

    @MockBean private UpdateUserPasswordUseCase updateUserPasswordUseCase;

    @MockBean private DeleteUserUseCase deleteUserUseCase;

    @MockBean private UserApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/users 테스트")
    class CreateUserTest {

        @Test
        @DisplayName("사용자 생성 성공 (201 Created)")
        void givenValidCreateRequest_whenCreateUser_thenReturns201() throws Exception {
            // given
            String tenantId = UUID.randomUUID().toString();
            String organizationId = UUID.randomUUID().toString();
            String identifier = "user@example.com";
            String password = "password123";
            CreateUserApiRequest request =
                    new CreateUserApiRequest(tenantId, organizationId, identifier, password);

            UUID userId = UUID.randomUUID();
            CreateUserCommand command =
                    new CreateUserCommand(
                            UUID.fromString(tenantId),
                            UUID.fromString(organizationId),
                            identifier,
                            password);
            Instant now = Instant.now();
            UserResponse useCaseResponse =
                    new UserResponse(
                            userId,
                            UUID.fromString(tenantId),
                            UUID.fromString(organizationId),
                            identifier,
                            "ACTIVE",
                            now,
                            now);

            given(mapper.toCommand(any(CreateUserApiRequest.class))).willReturn(command);
            given(createUserUseCase.execute(any(CreateUserCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(UserResponse.class)))
                    .willReturn(
                            new com.ryuqq.authhub.adapter.in.rest.user.dto.response
                                    .CreateUserApiResponse(userId.toString()));

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()));

            verify(mapper).toCommand(any(CreateUserApiRequest.class));
            verify(createUserUseCase).execute(any(CreateUserCommand.class));
            verify(mapper).toCreateResponse(any(UserResponse.class));
        }

        @Test
        @DisplayName("tenantId 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankTenantId_whenCreateUser_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "tenantId": "",
                        "organizationId": "550e8400-e29b-41d4-a716-446655440000",
                        "identifier": "user@example.com",
                        "password": "password123"
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("organizationId 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankOrganizationId_whenCreateUser_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "tenantId": "550e8400-e29b-41d4-a716-446655440000",
                        "organizationId": "",
                        "identifier": "user@example.com",
                        "password": "password123"
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/users")
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
            String invalidRequest =
                    """
                    {
                        "tenantId": "550e8400-e29b-41d4-a716-446655440000",
                        "organizationId": "550e8400-e29b-41d4-a716-446655440000",
                        "identifier": "",
                        "password": "password123"
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("password 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankPassword_whenCreateUser_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "tenantId": "550e8400-e29b-41d4-a716-446655440000",
                        "organizationId": "550e8400-e29b-41d4-a716-446655440000",
                        "identifier": "user@example.com",
                        "password": ""
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("password가 8자 미만일 때 Validation 실패 (400 Bad Request)")
        void givenShortPassword_whenCreateUser_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "tenantId": "550e8400-e29b-41d4-a716-446655440000",
                        "organizationId": "550e8400-e29b-41d4-a716-446655440000",
                        "identifier": "user@example.com",
                        "password": "short"
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/users/{userId} 테스트")
    class UpdateUserTest {

        @Test
        @DisplayName("사용자 정보 수정 성공 (200 OK)")
        void givenValidUpdateRequest_whenUpdateUser_thenReturns200() throws Exception {
            // given
            String userId = UUID.randomUUID().toString();
            String newIdentifier = "newuser@example.com";
            UpdateUserApiRequest request = new UpdateUserApiRequest(newIdentifier);

            UpdateUserCommand command =
                    new UpdateUserCommand(UUID.fromString(userId), newIdentifier);

            given(mapper.toCommand(any(String.class), any(UpdateUserApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            put("/api/v1/auth/users/{userId}", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(mapper).toCommand(any(String.class), any(UpdateUserApiRequest.class));
            verify(updateUserUseCase).execute(any(UpdateUserCommand.class));
        }

        @Test
        @DisplayName("identifier가 256자를 초과할 때 Validation 실패 (400 Bad Request)")
        void givenTooLongIdentifier_whenUpdateUser_thenReturns400() throws Exception {
            // given
            String userId = UUID.randomUUID().toString();
            String tooLongIdentifier = "a".repeat(256);
            String invalidRequest =
                    String.format(
                            """
                            {
                                "identifier": "%s"
                            }
                            """,
                            tooLongIdentifier);

            // when & then
            mockMvc.perform(
                            put("/api/v1/auth/users/{userId}", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/users/{userId}/status 테스트")
    class UpdateUserStatusTest {

        @Test
        @DisplayName("사용자 상태 변경 성공 (200 OK)")
        void givenValidStatusRequest_whenUpdateUserStatus_thenReturns200() throws Exception {
            // given
            String userId = UUID.randomUUID().toString();
            String status = "INACTIVE";
            UpdateUserStatusApiRequest request = new UpdateUserStatusApiRequest(status);

            UpdateUserStatusCommand command =
                    new UpdateUserStatusCommand(UUID.fromString(userId), status);

            given(mapper.toStatusCommand(any(String.class), any(UpdateUserStatusApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            patch("/api/v1/auth/users/{userId}/status", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(mapper)
                    .toStatusCommand(any(String.class), any(UpdateUserStatusApiRequest.class));
            verify(updateUserStatusUseCase).execute(any(UpdateUserStatusCommand.class));
        }

        @Test
        @DisplayName("status 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankStatus_whenUpdateUserStatus_thenReturns400() throws Exception {
            // given
            String userId = UUID.randomUUID().toString();
            String invalidRequest =
                    """
                    {
                        "status": ""
                    }
                    """;

            // when & then
            mockMvc.perform(
                            patch("/api/v1/auth/users/{userId}/status", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/users/{userId}/password 테스트")
    class UpdateUserPasswordTest {

        @Test
        @DisplayName("사용자 비밀번호 변경 성공 (200 OK)")
        void givenValidPasswordRequest_whenUpdateUserPassword_thenReturns200() throws Exception {
            // given
            String userId = UUID.randomUUID().toString();
            String currentPassword = "oldPassword123";
            String newPassword = "newPassword456";
            UpdateUserPasswordApiRequest request =
                    new UpdateUserPasswordApiRequest(currentPassword, newPassword);

            UpdateUserPasswordCommand command =
                    new UpdateUserPasswordCommand(
                            UUID.fromString(userId), currentPassword, newPassword);

            given(
                            mapper.toPasswordCommand(
                                    any(String.class), any(UpdateUserPasswordApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            patch("/api/v1/auth/users/{userId}/password", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(mapper)
                    .toPasswordCommand(any(String.class), any(UpdateUserPasswordApiRequest.class));
            verify(updateUserPasswordUseCase).execute(any(UpdateUserPasswordCommand.class));
        }

        @Test
        @DisplayName("currentPassword 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankCurrentPassword_whenUpdateUserPassword_thenReturns400() throws Exception {
            // given
            String userId = UUID.randomUUID().toString();
            String invalidRequest =
                    """
                    {
                        "currentPassword": "",
                        "newPassword": "newPassword456"
                    }
                    """;

            // when & then
            mockMvc.perform(
                            patch("/api/v1/auth/users/{userId}/password", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("newPassword 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankNewPassword_whenUpdateUserPassword_thenReturns400() throws Exception {
            // given
            String userId = UUID.randomUUID().toString();
            String invalidRequest =
                    """
                    {
                        "currentPassword": "oldPassword123",
                        "newPassword": ""
                    }
                    """;

            // when & then
            mockMvc.perform(
                            patch("/api/v1/auth/users/{userId}/password", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("newPassword가 8자 미만일 때 Validation 실패 (400 Bad Request)")
        void givenShortNewPassword_whenUpdateUserPassword_thenReturns400() throws Exception {
            // given
            String userId = UUID.randomUUID().toString();
            String invalidRequest =
                    """
                    {
                        "currentPassword": "oldPassword123",
                        "newPassword": "short"
                    }
                    """;

            // when & then
            mockMvc.perform(
                            patch("/api/v1/auth/users/{userId}/password", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/users/{userId}/delete 테스트")
    class DeleteUserTest {

        @Test
        @DisplayName("사용자 삭제 성공 (204 No Content)")
        void givenValidUserId_whenDeleteUser_thenReturns204() throws Exception {
            // given
            String userId = UUID.randomUUID().toString();
            DeleteUserCommand command = new DeleteUserCommand(UUID.fromString(userId));

            given(mapper.toDeleteCommand(any(String.class))).willReturn(command);

            // when & then
            mockMvc.perform(patch("/api/v1/auth/users/{userId}/delete", userId))
                    .andExpect(status().isNoContent());

            verify(mapper).toDeleteCommand(any(String.class));
            verify(deleteUserUseCase).execute(any(DeleteUserCommand.class));
        }
    }
}
