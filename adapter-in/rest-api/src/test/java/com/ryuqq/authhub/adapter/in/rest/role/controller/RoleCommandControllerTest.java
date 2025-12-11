package com.ryuqq.authhub.adapter.in.rest.role.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.UpdateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.CreateRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.mapper.RoleApiMapper;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.port.in.command.CreateRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.command.DeleteRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.command.UpdateRoleUseCase;
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
 * RoleCommandController 단위 테스트
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
@WebMvcTest(RoleCommandController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("RoleCommandController 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
class RoleCommandControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private CreateRoleUseCase createRoleUseCase;

    @MockBean private UpdateRoleUseCase updateRoleUseCase;

    @MockBean private DeleteRoleUseCase deleteRoleUseCase;

    @MockBean private RoleApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/roles - 역할 생성")
    class CreateRoleTest {

        @Test
        @DisplayName("[성공] 유효한 요청으로 역할 생성 시 201 Created 반환")
        void createRole_withValidRequest_returns201Created() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            UUID roleId = UUID.randomUUID();
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(
                            tenantId.toString(), "TestRole", "Test description", "TENANT", false);
            RoleResponse useCaseResponse =
                    new RoleResponse(
                            roleId,
                            tenantId,
                            "TestRole",
                            "Test description",
                            "TENANT",
                            "CUSTOM",
                            Instant.now(),
                            Instant.now());
            CreateRoleApiResponse apiResponse = new CreateRoleApiResponse(roleId.toString());

            given(mapper.toCommand(any(CreateRoleApiRequest.class)))
                    .willReturn(
                            new CreateRoleCommand(
                                    tenantId, "TestRole", "Test description", "TENANT", false));
            given(createRoleUseCase.execute(any(CreateRoleCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(RoleResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            post("/api/v1/roles")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roleId").value(roleId.toString()));

            verify(createRoleUseCase).execute(any(CreateRoleCommand.class));
        }

        @Test
        @DisplayName("[성공] 시스템 역할 생성 시 201 Created 반환")
        void createRole_systemRole_returns201Created() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(
                            null, "SystemRole", "System role description", "GLOBAL", true);
            RoleResponse useCaseResponse =
                    new RoleResponse(
                            roleId,
                            null,
                            "SystemRole",
                            "System role description",
                            "GLOBAL",
                            "SYSTEM",
                            Instant.now(),
                            Instant.now());
            CreateRoleApiResponse apiResponse = new CreateRoleApiResponse(roleId.toString());

            given(mapper.toCommand(any(CreateRoleApiRequest.class)))
                    .willReturn(
                            new CreateRoleCommand(
                                    null, "SystemRole", "System role description", "GLOBAL", true));
            given(createRoleUseCase.execute(any(CreateRoleCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(RoleResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            post("/api/v1/roles")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roleId").value(roleId.toString()));

            verify(createRoleUseCase).execute(any(CreateRoleCommand.class));
        }

        @Test
        @DisplayName("[실패] 이름이 빈 문자열이면 400 Bad Request 반환")
        void createRole_withEmptyName_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(
                            tenantId.toString(), "", "Description", "TENANT", false);

            // When & Then
            mockMvc.perform(
                            post("/api/v1/roles")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(createRoleUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 null이면 400 Bad Request 반환")
        void createRole_withNullName_returns400BadRequest() throws Exception {
            // Given
            String invalidRequest =
                    """
                    {
                        "tenantId": "550e8400-e29b-41d4-a716-446655440000",
                        "name": null,
                        "scope": "TENANT"
                    }
                    """;

            // When & Then
            mockMvc.perform(
                            post("/api/v1/roles")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(createRoleUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 100자 초과면 400 Bad Request 반환")
        void createRole_withTooLongName_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            String longName = "A".repeat(101);
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(
                            tenantId.toString(), longName, "Description", "TENANT", false);

            // When & Then
            mockMvc.perform(
                            post("/api/v1/roles")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(createRoleUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 설명이 500자 초과면 400 Bad Request 반환")
        void createRole_withTooLongDescription_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            String longDescription = "A".repeat(501);
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(
                            tenantId.toString(), "ValidName", longDescription, "TENANT", false);

            // When & Then
            mockMvc.perform(
                            post("/api/v1/roles")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(createRoleUseCase, never()).execute(any());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/roles/{roleId} - 역할 수정")
    class UpdateRoleTest {

        @Test
        @DisplayName("[성공] 유효한 요청으로 역할 수정 시 200 OK 반환")
        void updateRole_withValidRequest_returns200Ok() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            UpdateRoleApiRequest request =
                    new UpdateRoleApiRequest("NewRoleName", "New description");

            given(mapper.toCommand(eq(roleId.toString()), any(UpdateRoleApiRequest.class)))
                    .willReturn(new UpdateRoleCommand(roleId, "NewRoleName", "New description"));
            given(updateRoleUseCase.execute(any(UpdateRoleCommand.class))).willReturn(null);

            // When & Then
            mockMvc.perform(
                            put("/api/v1/roles/{roleId}", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(updateRoleUseCase).execute(any(UpdateRoleCommand.class));
        }

        @Test
        @DisplayName("[실패] 이름이 빈 문자열이면 400 Bad Request 반환")
        void updateRole_withEmptyName_returns400BadRequest() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            UpdateRoleApiRequest request = new UpdateRoleApiRequest("", "Description");

            // When & Then
            mockMvc.perform(
                            put("/api/v1/roles/{roleId}", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateRoleUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 100자 초과면 400 Bad Request 반환")
        void updateRole_withTooLongName_returns400BadRequest() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            String longName = "A".repeat(101);
            UpdateRoleApiRequest request = new UpdateRoleApiRequest(longName, "Description");

            // When & Then
            mockMvc.perform(
                            put("/api/v1/roles/{roleId}", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateRoleUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 설명이 500자 초과면 400 Bad Request 반환")
        void updateRole_withTooLongDescription_returns400BadRequest() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            String longDescription = "A".repeat(501);
            UpdateRoleApiRequest request = new UpdateRoleApiRequest("ValidName", longDescription);

            // When & Then
            mockMvc.perform(
                            put("/api/v1/roles/{roleId}", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateRoleUseCase, never()).execute(any());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/roles/{roleId}/delete - 역할 삭제")
    class DeleteRoleTest {

        @Test
        @DisplayName("[성공] 역할 삭제 시 204 No Content 반환")
        void deleteRole_returns204NoContent() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            DeleteRoleCommand command = new DeleteRoleCommand(roleId);

            given(mapper.toDeleteCommand(any(String.class))).willReturn(command);
            willDoNothing().given(deleteRoleUseCase).execute(any(DeleteRoleCommand.class));

            // When & Then
            mockMvc.perform(patch("/api/v1/roles/{roleId}/delete", roleId))
                    .andExpect(status().isNoContent());

            verify(deleteRoleUseCase).execute(any(DeleteRoleCommand.class));
        }

        @Test
        @DisplayName("[실패] 잘못된 UUID 형식이면 400 Bad Request 반환")
        void deleteRole_withInvalidUuid_returns400BadRequest() throws Exception {
            // Given - mapper가 잘못된 UUID로 IllegalArgumentException 던지도록 설정
            given(mapper.toDeleteCommand(any(String.class)))
                    .willThrow(new IllegalArgumentException("Invalid UUID string: invalid-uuid"));

            // When & Then
            mockMvc.perform(patch("/api/v1/roles/{roleId}/delete", "invalid-uuid"))
                    .andExpect(status().isBadRequest());

            verify(deleteRoleUseCase, never()).execute(any());
        }
    }
}
