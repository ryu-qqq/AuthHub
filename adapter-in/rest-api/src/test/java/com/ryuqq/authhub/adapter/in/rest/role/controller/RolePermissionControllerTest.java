package com.ryuqq.authhub.adapter.in.rest.role.controller;

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
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RolePermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.mapper.RoleApiMapper;
import com.ryuqq.authhub.application.role.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.command.RevokeRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.response.RolePermissionResponse;
import com.ryuqq.authhub.application.role.port.in.command.GrantRolePermissionUseCase;
import com.ryuqq.authhub.application.role.port.in.command.RevokeRolePermissionUseCase;
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
 * RolePermissionController 단위 테스트
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
@WebMvcTest(RolePermissionController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("RolePermissionController 테스트")
class RolePermissionControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private GrantRolePermissionUseCase grantRolePermissionUseCase;

    @MockBean private RevokeRolePermissionUseCase revokeRolePermissionUseCase;

    @MockBean private RoleApiMapper roleApiMapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/roles/{roleId}/permissions 테스트")
    class GrantPermissionTest {

        @Test
        @DisplayName("권한 부여 성공 (201 Created)")
        void givenValidRequest_whenGrantPermission_thenReturns201() throws Exception {
            // given
            UUID roleId = UUID.randomUUID();
            UUID permissionId = UUID.randomUUID();
            GrantRolePermissionApiRequest request = new GrantRolePermissionApiRequest(permissionId);

            GrantRolePermissionCommand command =
                    new GrantRolePermissionCommand(roleId, permissionId);
            Instant grantedAt = Instant.now();
            RolePermissionResponse useCaseResponse =
                    new RolePermissionResponse(roleId, permissionId, grantedAt);

            RolePermissionApiResponse apiResponse =
                    new RolePermissionApiResponse(roleId, permissionId, grantedAt);

            given(roleApiMapper.toGrantPermissionCommand(roleId.toString(), request))
                    .willReturn(command);
            given(grantRolePermissionUseCase.execute(any(GrantRolePermissionCommand.class)))
                    .willReturn(useCaseResponse);
            given(roleApiMapper.toRolePermissionApiResponse(any(RolePermissionResponse.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/roles/{roleId}/permissions", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roleId").value(roleId.toString()))
                    .andExpect(jsonPath("$.data.permissionId").value(permissionId.toString()))
                    .andExpect(jsonPath("$.data.grantedAt").exists());

            verify(roleApiMapper).toGrantPermissionCommand(roleId.toString(), request);
            verify(grantRolePermissionUseCase).execute(any(GrantRolePermissionCommand.class));
        }

        @Test
        @DisplayName("permissionId 누락 시 Validation 실패 (400 Bad Request)")
        void givenNullPermissionId_whenGrantPermission_thenReturns400() throws Exception {
            // given
            UUID roleId = UUID.randomUUID();
            String invalidRequest =
                    """
                    {
                        "permissionId": null
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/roles/{roleId}/permissions", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("roleId가 유효하지 않은 UUID 형식일 때 400 Bad Request")
        void givenInvalidRoleIdFormat_whenGrantPermission_thenReturns400() throws Exception {
            // given
            String invalidRoleId = "invalid-uuid";
            UUID permissionId = UUID.randomUUID();
            GrantRolePermissionApiRequest request = new GrantRolePermissionApiRequest(permissionId);

            given(
                            roleApiMapper.toGrantPermissionCommand(
                                    any(String.class), any(GrantRolePermissionApiRequest.class)))
                    .willThrow(new IllegalArgumentException("Invalid UUID format"));

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/roles/{roleId}/permissions", invalidRoleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/roles/{roleId}/permissions/{permissionId}/revoke 테스트")
    class RevokePermissionTest {

        @Test
        @DisplayName("권한 해제 성공 (204 No Content)")
        void givenValidIds_whenRevokePermission_thenReturns204() throws Exception {
            // given
            UUID roleId = UUID.randomUUID();
            UUID permissionId = UUID.randomUUID();

            RevokeRolePermissionCommand command =
                    new RevokeRolePermissionCommand(roleId, permissionId);

            given(
                            roleApiMapper.toRevokePermissionCommand(
                                    roleId.toString(), permissionId.toString()))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            patch(
                                            "/api/v1/auth/roles/{roleId}/permissions/{permissionId}/revoke",
                                            roleId,
                                            permissionId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(roleApiMapper)
                    .toRevokePermissionCommand(roleId.toString(), permissionId.toString());
            verify(revokeRolePermissionUseCase).execute(any(RevokeRolePermissionCommand.class));
        }

        @Test
        @DisplayName("roleId가 유효하지 않은 UUID 형식일 때 400 Bad Request")
        void givenInvalidRoleIdFormat_whenRevokePermission_thenReturns400() throws Exception {
            // given
            String invalidRoleId = "invalid-uuid";
            UUID permissionId = UUID.randomUUID();

            given(roleApiMapper.toRevokePermissionCommand(any(String.class), any(String.class)))
                    .willThrow(new IllegalArgumentException("Invalid UUID format"));

            // when & then
            mockMvc.perform(
                            patch(
                                            "/api/v1/auth/roles/{roleId}/permissions/{permissionId}/revoke",
                                            invalidRoleId,
                                            permissionId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("permissionId가 유효하지 않은 UUID 형식일 때 400 Bad Request")
        void givenInvalidPermissionIdFormat_whenRevokePermission_thenReturns400() throws Exception {
            // given
            UUID roleId = UUID.randomUUID();
            String invalidPermissionId = "invalid-uuid";

            given(roleApiMapper.toRevokePermissionCommand(any(String.class), any(String.class)))
                    .willThrow(new IllegalArgumentException("Invalid UUID format"));

            // when & then
            mockMvc.perform(
                            patch(
                                            "/api/v1/auth/roles/{roleId}/permissions/{permissionId}/revoke",
                                            roleId,
                                            invalidPermissionId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }
}
