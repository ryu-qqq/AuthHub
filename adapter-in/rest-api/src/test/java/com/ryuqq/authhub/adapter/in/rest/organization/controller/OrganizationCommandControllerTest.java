package com.ryuqq.authhub.adapter.in.rest.organization.controller;

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
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.CreateOrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationApiMapper;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.DeleteOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.port.in.command.CreateOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.DeleteOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationStatusUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationUseCase;
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
 * OrganizationCommandController 단위 테스트
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
@WebMvcTest(OrganizationCommandController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("OrganizationCommandController 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
class OrganizationCommandControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private CreateOrganizationUseCase createOrganizationUseCase;

    @MockBean private UpdateOrganizationUseCase updateOrganizationUseCase;

    @MockBean private UpdateOrganizationStatusUseCase updateOrganizationStatusUseCase;

    @MockBean private DeleteOrganizationUseCase deleteOrganizationUseCase;

    @MockBean private OrganizationApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/organizations - 조직 생성")
    class CreateOrganizationTest {

        @Test
        @DisplayName("[성공] 유효한 요청으로 조직 생성 시 201 Created 반환")
        void createOrganization_withValidRequest_returns201Created() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            CreateOrganizationApiRequest request =
                    new CreateOrganizationApiRequest(tenantId.toString(), "TestOrganization");
            OrganizationResponse useCaseResponse =
                    new OrganizationResponse(
                            organizationId,
                            tenantId,
                            "TestOrganization",
                            "ACTIVE",
                            Instant.now(),
                            Instant.now());
            CreateOrganizationApiResponse apiResponse =
                    new CreateOrganizationApiResponse(organizationId.toString());

            given(mapper.toCommand(any(CreateOrganizationApiRequest.class)))
                    .willReturn(new CreateOrganizationCommand(tenantId, "TestOrganization"));
            given(createOrganizationUseCase.execute(any(CreateOrganizationCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(OrganizationResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            post("/api/v1/organizations")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.organizationId").value(organizationId.toString()));

            verify(createOrganizationUseCase).execute(any(CreateOrganizationCommand.class));
        }

        @Test
        @DisplayName("[실패] 테넌트 ID가 null이면 400 Bad Request 반환")
        void createOrganization_withNullTenantId_returns400BadRequest() throws Exception {
            // Given
            String invalidRequest =
                    """
                    {
                        "tenantId": null,
                        "name": "TestOrganization"
                    }
                    """;

            // When & Then
            mockMvc.perform(
                            post("/api/v1/organizations")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.title").value("Bad Request"));

            verify(createOrganizationUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 빈 문자열이면 400 Bad Request 반환")
        void createOrganization_withEmptyName_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            CreateOrganizationApiRequest request =
                    new CreateOrganizationApiRequest(tenantId.toString(), "");

            // When & Then
            mockMvc.perform(
                            post("/api/v1/organizations")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(createOrganizationUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 1자 이하면 400 Bad Request 반환")
        void createOrganization_withTooShortName_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            CreateOrganizationApiRequest request =
                    new CreateOrganizationApiRequest(tenantId.toString(), "A");

            // When & Then
            mockMvc.perform(
                            post("/api/v1/organizations")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(createOrganizationUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 100자 초과면 400 Bad Request 반환")
        void createOrganization_withTooLongName_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            String longName = "A".repeat(101);
            CreateOrganizationApiRequest request =
                    new CreateOrganizationApiRequest(tenantId.toString(), longName);

            // When & Then
            mockMvc.perform(
                            post("/api/v1/organizations")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(createOrganizationUseCase, never()).execute(any());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/organizations/{organizationId} - 조직 수정")
    class UpdateOrganizationTest {

        @Test
        @DisplayName("[성공] 유효한 요청으로 조직 수정 시 200 OK 반환")
        void updateOrganization_withValidRequest_returns200Ok() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UpdateOrganizationApiRequest request = new UpdateOrganizationApiRequest("NewOrgName");

            given(
                            mapper.toCommand(
                                    eq(organizationId.toString()),
                                    any(UpdateOrganizationApiRequest.class)))
                    .willReturn(new UpdateOrganizationCommand(organizationId, "NewOrgName"));
            given(updateOrganizationUseCase.execute(any(UpdateOrganizationCommand.class)))
                    .willReturn(null);

            // When & Then
            mockMvc.perform(
                            put("/api/v1/organizations/{organizationId}", organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(updateOrganizationUseCase).execute(any(UpdateOrganizationCommand.class));
        }

        @Test
        @DisplayName("[실패] 이름이 빈 문자열이면 400 Bad Request 반환")
        void updateOrganization_withEmptyName_returns400BadRequest() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UpdateOrganizationApiRequest request = new UpdateOrganizationApiRequest("");

            // When & Then
            mockMvc.perform(
                            put("/api/v1/organizations/{organizationId}", organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateOrganizationUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 1자면 400 Bad Request 반환")
        void updateOrganization_withTooShortName_returns400BadRequest() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UpdateOrganizationApiRequest request = new UpdateOrganizationApiRequest("A");

            // When & Then
            mockMvc.perform(
                            put("/api/v1/organizations/{organizationId}", organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateOrganizationUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 100자 초과면 400 Bad Request 반환")
        void updateOrganization_withTooLongName_returns400BadRequest() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            String longName = "A".repeat(101);
            UpdateOrganizationApiRequest request = new UpdateOrganizationApiRequest(longName);

            // When & Then
            mockMvc.perform(
                            put("/api/v1/organizations/{organizationId}", organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateOrganizationUseCase, never()).execute(any());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/organizations/{organizationId}/status - 조직 상태 변경")
    class UpdateOrganizationStatusTest {

        @Test
        @DisplayName("[성공] ACTIVE 상태로 변경 시 200 OK 반환")
        void updateOrganizationStatus_toActive_returns200Ok() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            UpdateOrganizationStatusApiRequest request =
                    new UpdateOrganizationStatusApiRequest("ACTIVE");
            OrganizationResponse useCaseResponse =
                    new OrganizationResponse(
                            organizationId,
                            tenantId,
                            "TestOrg",
                            "ACTIVE",
                            Instant.now(),
                            Instant.now());
            OrganizationApiResponse apiResponse =
                    new OrganizationApiResponse(
                            organizationId.toString(),
                            tenantId.toString(),
                            "TestOrg",
                            "ACTIVE",
                            Instant.now(),
                            Instant.now());

            given(
                            mapper.toStatusCommand(
                                    eq(organizationId.toString()),
                                    any(UpdateOrganizationStatusApiRequest.class)))
                    .willReturn(new UpdateOrganizationStatusCommand(organizationId, "ACTIVE"));
            given(
                            updateOrganizationStatusUseCase.execute(
                                    any(UpdateOrganizationStatusCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(OrganizationResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            patch("/api/v1/organizations/{organizationId}/status", organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));

            verify(updateOrganizationStatusUseCase)
                    .execute(any(UpdateOrganizationStatusCommand.class));
        }

        @Test
        @DisplayName("[성공] INACTIVE 상태로 변경 시 200 OK 반환")
        void updateOrganizationStatus_toInactive_returns200Ok() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            UpdateOrganizationStatusApiRequest request =
                    new UpdateOrganizationStatusApiRequest("INACTIVE");
            OrganizationResponse useCaseResponse =
                    new OrganizationResponse(
                            organizationId,
                            tenantId,
                            "TestOrg",
                            "INACTIVE",
                            Instant.now(),
                            Instant.now());
            OrganizationApiResponse apiResponse =
                    new OrganizationApiResponse(
                            organizationId.toString(),
                            tenantId.toString(),
                            "TestOrg",
                            "INACTIVE",
                            Instant.now(),
                            Instant.now());

            given(
                            mapper.toStatusCommand(
                                    eq(organizationId.toString()),
                                    any(UpdateOrganizationStatusApiRequest.class)))
                    .willReturn(new UpdateOrganizationStatusCommand(organizationId, "INACTIVE"));
            given(
                            updateOrganizationStatusUseCase.execute(
                                    any(UpdateOrganizationStatusCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(OrganizationResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            patch("/api/v1/organizations/{organizationId}/status", organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.status").value("INACTIVE"));

            verify(updateOrganizationStatusUseCase)
                    .execute(any(UpdateOrganizationStatusCommand.class));
        }

        @Test
        @DisplayName("[실패] 상태가 빈 문자열이면 400 Bad Request 반환")
        void updateOrganizationStatus_withEmptyStatus_returns400BadRequest() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UpdateOrganizationStatusApiRequest request = new UpdateOrganizationStatusApiRequest("");

            // When & Then
            mockMvc.perform(
                            patch("/api/v1/organizations/{organizationId}/status", organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateOrganizationStatusUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 잘못된 상태값이면 400 Bad Request 반환")
        void updateOrganizationStatus_withInvalidStatus_returns400BadRequest() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UpdateOrganizationStatusApiRequest request =
                    new UpdateOrganizationStatusApiRequest("INVALID");

            // When & Then
            mockMvc.perform(
                            patch("/api/v1/organizations/{organizationId}/status", organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateOrganizationStatusUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] DELETED 상태로 변경 시도하면 400 Bad Request 반환")
        void updateOrganizationStatus_toDeleted_returns400BadRequest() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UpdateOrganizationStatusApiRequest request =
                    new UpdateOrganizationStatusApiRequest("DELETED");

            // When & Then
            mockMvc.perform(
                            patch("/api/v1/organizations/{organizationId}/status", organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateOrganizationStatusUseCase, never()).execute(any());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/organizations/{organizationId}/delete - 조직 삭제")
    class DeleteOrganizationTest {

        @Test
        @DisplayName("[성공] 조직 삭제 시 204 No Content 반환")
        void deleteOrganization_returns204NoContent() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            DeleteOrganizationCommand command = new DeleteOrganizationCommand(organizationId);

            given(mapper.toDeleteCommand(any(String.class))).willReturn(command);
            willDoNothing()
                    .given(deleteOrganizationUseCase)
                    .execute(any(DeleteOrganizationCommand.class));

            // When & Then
            mockMvc.perform(patch("/api/v1/organizations/{organizationId}/delete", organizationId))
                    .andExpect(status().isNoContent());

            verify(deleteOrganizationUseCase).execute(any(DeleteOrganizationCommand.class));
        }

        @Test
        @DisplayName("[실패] 잘못된 UUID 형식이면 400 Bad Request 반환")
        void deleteOrganization_withInvalidUuid_returns400BadRequest() throws Exception {
            // Given - mapper가 잘못된 UUID로 IllegalArgumentException 던지도록 설정
            given(mapper.toDeleteCommand(any(String.class)))
                    .willThrow(new IllegalArgumentException("Invalid UUID string: invalid-uuid"));

            // When & Then
            mockMvc.perform(patch("/api/v1/organizations/{organizationId}/delete", "invalid-uuid"))
                    .andExpect(status().isBadRequest());

            verify(deleteOrganizationUseCase, never()).execute(any());
        }
    }
}
