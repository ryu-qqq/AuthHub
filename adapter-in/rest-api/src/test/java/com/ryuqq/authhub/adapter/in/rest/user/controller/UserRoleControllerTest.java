package com.ryuqq.authhub.adapter.in.rest.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.AssignUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserApiMapper;
import com.ryuqq.authhub.application.user.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.user.dto.command.RevokeUserRoleCommand;
import com.ryuqq.authhub.application.user.dto.response.UserRoleResponse;
import com.ryuqq.authhub.application.user.port.in.command.AssignUserRoleUseCase;
import com.ryuqq.authhub.application.user.port.in.command.RevokeUserRoleUseCase;
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
 * UserRoleController 단위 테스트
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
@WebMvcTest(UserRoleController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("UserRoleController 테스트")
class UserRoleControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private AssignUserRoleUseCase assignUserRoleUseCase;

    @MockBean private RevokeUserRoleUseCase revokeUserRoleUseCase;

    @MockBean private UserApiMapper userApiMapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/users/{userId}/roles 테스트")
    class AssignRoleTest {

        @Test
        @DisplayName("역할 할당 성공 (201 Created)")
        void givenValidRequest_whenAssignRole_thenReturns201() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            UUID roleId = UUID.randomUUID();
            AssignUserRoleApiRequest request = new AssignUserRoleApiRequest(roleId);

            AssignUserRoleCommand command = new AssignUserRoleCommand(userId, roleId);
            Instant assignedAt = Instant.now();
            UserRoleResponse useCaseResponse = new UserRoleResponse(userId, roleId, assignedAt);
            UserRoleApiResponse apiResponse = new UserRoleApiResponse(userId, roleId, assignedAt);

            given(
                            userApiMapper.toAssignRoleCommand(
                                    any(String.class), any(AssignUserRoleApiRequest.class)))
                    .willReturn(command);
            given(assignUserRoleUseCase.execute(any(AssignUserRoleCommand.class)))
                    .willReturn(useCaseResponse);
            given(userApiMapper.toUserRoleApiResponse(any(UserRoleResponse.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/users/{userId}/roles", userId.toString())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.roleId").value(roleId.toString()))
                    .andExpect(jsonPath("$.data.assignedAt").exists());

            verify(userApiMapper)
                    .toAssignRoleCommand(any(String.class), any(AssignUserRoleApiRequest.class));
            verify(assignUserRoleUseCase).execute(any(AssignUserRoleCommand.class));
            verify(userApiMapper).toUserRoleApiResponse(any(UserRoleResponse.class));
        }

        @Test
        @DisplayName("roleId 누락 시 Validation 실패 (400 Bad Request)")
        void givenNullRoleId_whenAssignRole_thenReturns400() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            String invalidRequest =
                    """
                    {
                        "roleId": null
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/users/{userId}/roles", userId.toString())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("잘못된 roleId 형식 시 Validation 실패 (400 Bad Request)")
        void givenInvalidRoleIdFormat_whenAssignRole_thenReturns400() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            String invalidRequest =
                    """
                    {
                        "roleId": "invalid-uuid-format"
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/users/{userId}/roles", userId.toString())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/users/{userId}/roles/{roleId}/revoke 테스트")
    class RevokeRoleTest {

        @Test
        @DisplayName("역할 해제 성공 (204 No Content)")
        void givenValidIds_whenRevokeRole_thenReturns204() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            UUID roleId = UUID.randomUUID();

            RevokeUserRoleCommand command = new RevokeUserRoleCommand(userId, roleId);

            given(userApiMapper.toRevokeRoleCommand(any(String.class), any(String.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            patch(
                                    "/api/v1/auth/users/{userId}/roles/{roleId}/revoke",
                                    userId.toString(),
                                    roleId.toString()))
                    .andExpect(status().isNoContent());

            verify(userApiMapper).toRevokeRoleCommand(any(String.class), any(String.class));
            verify(revokeUserRoleUseCase).execute(any(RevokeUserRoleCommand.class));
        }

        @Test
        @DisplayName("잘못된 userId 형식으로 역할 해제 시도")
        void givenInvalidUserIdFormat_whenRevokeRole_thenMapperThrowsException() throws Exception {
            // given
            String invalidUserId = "invalid-uuid";
            UUID roleId = UUID.randomUUID();

            given(userApiMapper.toRevokeRoleCommand(any(String.class), any(String.class)))
                    .willThrow(new IllegalArgumentException("Invalid UUID format"));

            // when & then
            mockMvc.perform(
                            patch(
                                    "/api/v1/auth/users/{userId}/roles/{roleId}/revoke",
                                    invalidUserId,
                                    roleId.toString()))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("잘못된 roleId 형식으로 역할 해제 시도")
        void givenInvalidRoleIdFormat_whenRevokeRole_thenMapperThrowsException() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            String invalidRoleId = "invalid-uuid";

            given(userApiMapper.toRevokeRoleCommand(any(String.class), any(String.class)))
                    .willThrow(new IllegalArgumentException("Invalid UUID format"));

            // when & then
            mockMvc.perform(
                            patch(
                                    "/api/v1/auth/users/{userId}/roles/{roleId}/revoke",
                                    userId.toString(),
                                    invalidRoleId))
                    .andExpect(status().is4xxClientError());
        }
    }
}
