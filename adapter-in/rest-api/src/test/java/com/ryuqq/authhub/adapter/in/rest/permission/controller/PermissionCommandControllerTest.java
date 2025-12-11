package com.ryuqq.authhub.adapter.in.rest.permission.controller;

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
import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.CreatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.UpdatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.CreatePermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.mapper.PermissionApiMapper;
import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.port.in.command.CreatePermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.command.DeletePermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.command.UpdatePermissionUseCase;
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
 * PermissionCommandController 단위 테스트
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
@WebMvcTest(PermissionCommandController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("PermissionCommandController 테스트")
class PermissionCommandControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private CreatePermissionUseCase createPermissionUseCase;

    @MockBean private UpdatePermissionUseCase updatePermissionUseCase;

    @MockBean private DeletePermissionUseCase deletePermissionUseCase;

    @MockBean private PermissionApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/permissions 테스트")
    class CreatePermissionTest {

        @Test
        @DisplayName("권한 생성 성공 (201 Created)")
        void givenValidCreateRequest_whenCreatePermission_thenReturns201() throws Exception {
            // given
            String resource = "USER";
            String action = "READ";
            String description = "사용자 조회 권한";
            Boolean isSystem = false;
            CreatePermissionApiRequest request =
                    new CreatePermissionApiRequest(resource, action, description, isSystem);

            UUID permissionId = UUID.randomUUID();
            String key = "USER:READ";
            String type = "CUSTOM";
            Instant now = Instant.now();
            CreatePermissionCommand command =
                    new CreatePermissionCommand(resource, action, description, isSystem);
            PermissionResponse useCaseResponse =
                    new PermissionResponse(
                            permissionId, key, resource, action, description, type, now, now);

            CreatePermissionApiResponse apiResponse =
                    new CreatePermissionApiResponse(permissionId.toString());

            given(mapper.toCommand(any(CreatePermissionApiRequest.class))).willReturn(command);
            given(createPermissionUseCase.execute(any(CreatePermissionCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(PermissionResponse.class))).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            post("/api/v1/permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.permissionId").value(permissionId.toString()));

            verify(mapper).toCommand(any(CreatePermissionApiRequest.class));
            verify(createPermissionUseCase).execute(any(CreatePermissionCommand.class));
        }

        @Test
        @DisplayName("resource 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankResource_whenCreatePermission_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "resource": "",
                        "action": "READ",
                        "description": "사용자 조회 권한",
                        "isSystem": false
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("action 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankAction_whenCreatePermission_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "resource": "USER",
                        "action": "",
                        "description": "사용자 조회 권한",
                        "isSystem": false
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("resource 길이 초과 시 Validation 실패 (400 Bad Request)")
        void givenTooLongResource_whenCreatePermission_thenReturns400() throws Exception {
            // given
            String tooLongResource = "A".repeat(51);
            String invalidRequest =
                    """
                    {
                        "resource": "%s",
                        "action": "READ",
                        "description": "사용자 조회 권한",
                        "isSystem": false
                    }
                    """
                            .formatted(tooLongResource);

            // when & then
            mockMvc.perform(
                            post("/api/v1/permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("description 길이 초과 시 Validation 실패 (400 Bad Request)")
        void givenTooLongDescription_whenCreatePermission_thenReturns400() throws Exception {
            // given
            String tooLongDescription = "A".repeat(501);
            String invalidRequest =
                    """
                    {
                        "resource": "USER",
                        "action": "READ",
                        "description": "%s",
                        "isSystem": false
                    }
                    """
                            .formatted(tooLongDescription);

            // when & then
            mockMvc.perform(
                            post("/api/v1/permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/permissions/{permissionId} 테스트")
    class UpdatePermissionTest {

        @Test
        @DisplayName("권한 수정 성공 (200 OK)")
        void givenValidUpdateRequest_whenUpdatePermission_thenReturns200() throws Exception {
            // given
            String permissionId = UUID.randomUUID().toString();
            String description = "수정된 권한 설명";
            UpdatePermissionApiRequest request = new UpdatePermissionApiRequest(description);

            UpdatePermissionCommand command =
                    new UpdatePermissionCommand(UUID.fromString(permissionId), description);

            given(mapper.toCommand(any(String.class), any(UpdatePermissionApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            put("/api/v1/permissions/{permissionId}", permissionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(mapper).toCommand(any(String.class), any(UpdatePermissionApiRequest.class));
            verify(updatePermissionUseCase).execute(any(UpdatePermissionCommand.class));
        }

        @Test
        @DisplayName("description 길이 초과 시 Validation 실패 (400 Bad Request)")
        void givenTooLongDescription_whenUpdatePermission_thenReturns400() throws Exception {
            // given
            String permissionId = UUID.randomUUID().toString();
            String tooLongDescription = "A".repeat(501);
            String invalidRequest =
                    """
                    {
                        "description": "%s"
                    }
                    """
                            .formatted(tooLongDescription);

            // when & then
            mockMvc.perform(
                            put("/api/v1/permissions/{permissionId}", permissionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("description null 허용 (200 OK)")
        void givenNullDescription_whenUpdatePermission_thenReturns200() throws Exception {
            // given
            String permissionId = UUID.randomUUID().toString();
            String requestBody =
                    """
                    {
                        "description": null
                    }
                    """;

            UpdatePermissionCommand command =
                    new UpdatePermissionCommand(UUID.fromString(permissionId), null);

            given(mapper.toCommand(any(String.class), any(UpdatePermissionApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            put("/api/v1/permissions/{permissionId}", permissionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(mapper).toCommand(any(String.class), any(UpdatePermissionApiRequest.class));
            verify(updatePermissionUseCase).execute(any(UpdatePermissionCommand.class));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/permissions/{permissionId}/delete 테스트")
    class DeletePermissionTest {

        @Test
        @DisplayName("권한 삭제 성공 (204 No Content)")
        void givenValidPermissionId_whenDeletePermission_thenReturns204() throws Exception {
            // given
            String permissionId = UUID.randomUUID().toString();
            DeletePermissionCommand command =
                    new DeletePermissionCommand(UUID.fromString(permissionId));

            given(mapper.toDeleteCommand(any(String.class))).willReturn(command);

            // when & then
            mockMvc.perform(patch("/api/v1/permissions/{permissionId}/delete", permissionId))
                    .andExpect(status().isNoContent());

            verify(mapper).toDeleteCommand(any(String.class));
            verify(deletePermissionUseCase).execute(any(DeletePermissionCommand.class));
        }
    }
}
