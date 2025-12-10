package com.ryuqq.authhub.adapter.in.rest.endpointpermission.controller;

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
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command.CreateEndpointPermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command.UpdateEndpointPermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response.CreateEndpointPermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.mapper.EndpointPermissionApiMapper;
import com.ryuqq.authhub.application.endpointpermission.dto.command.CreateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.command.DeleteEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.command.UpdateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.CreateEndpointPermissionUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.DeleteEndpointPermissionUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.UpdateEndpointPermissionUseCase;
import java.time.Instant;
import java.util.Set;
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
 * EndpointPermissionCommandController 단위 테스트
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
@WebMvcTest(EndpointPermissionCommandController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("EndpointPermissionCommandController 테스트")
class EndpointPermissionCommandControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private CreateEndpointPermissionUseCase createEndpointPermissionUseCase;

    @MockBean private UpdateEndpointPermissionUseCase updateEndpointPermissionUseCase;

    @MockBean private DeleteEndpointPermissionUseCase deleteEndpointPermissionUseCase;

    @MockBean private EndpointPermissionApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/endpoint-permissions 테스트")
    class CreateEndpointPermissionTest {

        @Test
        @DisplayName("엔드포인트 권한 생성 성공 (201 Created)")
        void givenValidCreateRequest_whenCreateEndpointPermission_thenReturns201()
                throws Exception {
            // given
            String serviceName = "auth-hub";
            String path = "/api/v1/users/{userId}";
            String method = "GET";
            String description = "사용자 상세 조회 엔드포인트";
            boolean isPublic = false;
            Set<String> requiredPermissions = Set.of("user:read");
            Set<String> requiredRoles = Set.of("ADMIN");
            CreateEndpointPermissionApiRequest request =
                    new CreateEndpointPermissionApiRequest(
                            serviceName,
                            path,
                            method,
                            description,
                            isPublic,
                            requiredPermissions,
                            requiredRoles);

            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            CreateEndpointPermissionCommand command =
                    new CreateEndpointPermissionCommand(
                            serviceName,
                            path,
                            method,
                            description,
                            isPublic,
                            requiredPermissions,
                            requiredRoles);
            EndpointPermissionResponse useCaseResponse =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            serviceName,
                            path,
                            method,
                            description,
                            isPublic,
                            requiredPermissions,
                            requiredRoles,
                            0L,
                            now,
                            now);

            CreateEndpointPermissionApiResponse apiResponse =
                    new CreateEndpointPermissionApiResponse(endpointPermissionId);

            given(mapper.toCommand(any(CreateEndpointPermissionApiRequest.class)))
                    .willReturn(command);
            given(
                            createEndpointPermissionUseCase.execute(
                                    any(CreateEndpointPermissionCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(EndpointPermissionResponse.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            post("/api/v1/endpoint-permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(endpointPermissionId));

            verify(mapper).toCommand(any(CreateEndpointPermissionApiRequest.class));
            verify(createEndpointPermissionUseCase)
                    .execute(any(CreateEndpointPermissionCommand.class));
        }

        @Test
        @DisplayName("serviceName 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankServiceName_whenCreateEndpointPermission_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "serviceName": "",
                        "path": "/api/v1/users",
                        "method": "GET",
                        "description": "사용자 목록 조회",
                        "isPublic": false,
                        "requiredPermissions": ["user:read"],
                        "requiredRoles": ["ADMIN"]
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/endpoint-permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("path 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankPath_whenCreateEndpointPermission_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "serviceName": "auth-hub",
                        "path": "",
                        "method": "GET",
                        "description": "사용자 목록 조회",
                        "isPublic": false,
                        "requiredPermissions": ["user:read"],
                        "requiredRoles": ["ADMIN"]
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/endpoint-permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("method 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankMethod_whenCreateEndpointPermission_thenReturns400() throws Exception {
            // given
            String invalidRequest =
                    """
                    {
                        "serviceName": "auth-hub",
                        "path": "/api/v1/users",
                        "method": "",
                        "description": "사용자 목록 조회",
                        "isPublic": false,
                        "requiredPermissions": ["user:read"],
                        "requiredRoles": ["ADMIN"]
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/endpoint-permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("serviceName 길이 초과 시 Validation 실패 (400 Bad Request)")
        void givenTooLongServiceName_whenCreateEndpointPermission_thenReturns400()
                throws Exception {
            // given
            String tooLongServiceName = "A".repeat(101);
            String invalidRequest =
                    """
                    {
                        "serviceName": "%s",
                        "path": "/api/v1/users",
                        "method": "GET",
                        "description": "사용자 목록 조회",
                        "isPublic": false,
                        "requiredPermissions": ["user:read"],
                        "requiredRoles": ["ADMIN"]
                    }
                    """
                            .formatted(tooLongServiceName);

            // when & then
            mockMvc.perform(
                            post("/api/v1/endpoint-permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("description 길이 초과 시 Validation 실패 (400 Bad Request)")
        void givenTooLongDescription_whenCreateEndpointPermission_thenReturns400()
                throws Exception {
            // given
            String tooLongDescription = "A".repeat(501);
            String invalidRequest =
                    """
                    {
                        "serviceName": "auth-hub",
                        "path": "/api/v1/users",
                        "method": "GET",
                        "description": "%s",
                        "isPublic": false,
                        "requiredPermissions": ["user:read"],
                        "requiredRoles": ["ADMIN"]
                    }
                    """
                            .formatted(tooLongDescription);

            // when & then
            mockMvc.perform(
                            post("/api/v1/endpoint-permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("공개 엔드포인트 생성 성공 (201 Created)")
        void givenPublicEndpoint_whenCreateEndpointPermission_thenReturns201() throws Exception {
            // given
            String serviceName = "auth-hub";
            String path = "/api/v1/health";
            String method = "GET";
            String description = "Health check endpoint";
            boolean isPublic = true;
            Set<String> requiredPermissions = Set.of();
            Set<String> requiredRoles = Set.of();
            CreateEndpointPermissionApiRequest request =
                    new CreateEndpointPermissionApiRequest(
                            serviceName,
                            path,
                            method,
                            description,
                            isPublic,
                            requiredPermissions,
                            requiredRoles);

            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            CreateEndpointPermissionCommand command =
                    new CreateEndpointPermissionCommand(
                            serviceName,
                            path,
                            method,
                            description,
                            isPublic,
                            requiredPermissions,
                            requiredRoles);
            EndpointPermissionResponse useCaseResponse =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            serviceName,
                            path,
                            method,
                            description,
                            isPublic,
                            requiredPermissions,
                            requiredRoles,
                            0L,
                            now,
                            now);

            CreateEndpointPermissionApiResponse apiResponse =
                    new CreateEndpointPermissionApiResponse(endpointPermissionId);

            given(mapper.toCommand(any(CreateEndpointPermissionApiRequest.class)))
                    .willReturn(command);
            given(
                            createEndpointPermissionUseCase.execute(
                                    any(CreateEndpointPermissionCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(EndpointPermissionResponse.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            post("/api/v1/endpoint-permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(endpointPermissionId));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/endpoint-permissions/{endpointPermissionId} 테스트")
    class UpdateEndpointPermissionTest {

        @Test
        @DisplayName("엔드포인트 권한 수정 성공 (200 OK)")
        void givenValidUpdateRequest_whenUpdateEndpointPermission_thenReturns200()
                throws Exception {
            // given
            String endpointPermissionId = UUID.randomUUID().toString();
            String description = "수정된 엔드포인트 설명";
            boolean isPublic = false;
            Set<String> requiredPermissions = Set.of("user:write");
            Set<String> requiredRoles = Set.of("ADMIN", "USER_MANAGER");
            UpdateEndpointPermissionApiRequest request =
                    new UpdateEndpointPermissionApiRequest(
                            description, isPublic, requiredPermissions, requiredRoles);

            UpdateEndpointPermissionCommand command =
                    new UpdateEndpointPermissionCommand(
                            endpointPermissionId,
                            description,
                            isPublic,
                            requiredPermissions,
                            requiredRoles);

            given(
                            mapper.toCommand(
                                    any(String.class),
                                    any(UpdateEndpointPermissionApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            put(
                                            "/api/v1/endpoint-permissions/{endpointPermissionId}",
                                            endpointPermissionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(mapper)
                    .toCommand(any(String.class), any(UpdateEndpointPermissionApiRequest.class));
            verify(updateEndpointPermissionUseCase)
                    .execute(any(UpdateEndpointPermissionCommand.class));
        }

        @Test
        @DisplayName("description 길이 초과 시 Validation 실패 (400 Bad Request)")
        void givenTooLongDescription_whenUpdateEndpointPermission_thenReturns400()
                throws Exception {
            // given
            String endpointPermissionId = UUID.randomUUID().toString();
            String tooLongDescription = "A".repeat(501);
            String invalidRequest =
                    """
                    {
                        "description": "%s",
                        "isPublic": false,
                        "requiredPermissions": ["user:read"],
                        "requiredRoles": ["ADMIN"]
                    }
                    """
                            .formatted(tooLongDescription);

            // when & then
            mockMvc.perform(
                            put(
                                            "/api/v1/endpoint-permissions/{endpointPermissionId}",
                                            endpointPermissionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("description null 허용 (200 OK)")
        void givenNullDescription_whenUpdateEndpointPermission_thenReturns200() throws Exception {
            // given
            String endpointPermissionId = UUID.randomUUID().toString();
            String requestBody =
                    """
                    {
                        "description": null,
                        "isPublic": true,
                        "requiredPermissions": [],
                        "requiredRoles": []
                    }
                    """;

            UpdateEndpointPermissionCommand command =
                    new UpdateEndpointPermissionCommand(
                            endpointPermissionId, null, true, Set.of(), Set.of());

            given(
                            mapper.toCommand(
                                    any(String.class),
                                    any(UpdateEndpointPermissionApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            put(
                                            "/api/v1/endpoint-permissions/{endpointPermissionId}",
                                            endpointPermissionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(mapper)
                    .toCommand(any(String.class), any(UpdateEndpointPermissionApiRequest.class));
            verify(updateEndpointPermissionUseCase)
                    .execute(any(UpdateEndpointPermissionCommand.class));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/endpoint-permissions/{endpointPermissionId}/delete 테스트")
    class DeleteEndpointPermissionTest {

        @Test
        @DisplayName("엔드포인트 권한 삭제 성공 (204 No Content)")
        void givenValidEndpointPermissionId_whenDeleteEndpointPermission_thenReturns204()
                throws Exception {
            // given
            String endpointPermissionId = UUID.randomUUID().toString();
            DeleteEndpointPermissionCommand command =
                    new DeleteEndpointPermissionCommand(endpointPermissionId);

            given(mapper.toDeleteCommand(any(String.class))).willReturn(command);

            // when & then
            mockMvc.perform(
                            patch(
                                    "/api/v1/endpoint-permissions/{endpointPermissionId}/delete",
                                    endpointPermissionId))
                    .andExpect(status().isNoContent());

            verify(mapper).toDeleteCommand(any(String.class));
            verify(deleteEndpointPermissionUseCase)
                    .execute(any(DeleteEndpointPermissionCommand.class));
        }
    }
}
