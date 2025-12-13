package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

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
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantApiMapper;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.DeleteTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.port.in.command.CreateTenantUseCase;
import com.ryuqq.authhub.application.tenant.port.in.command.DeleteTenantUseCase;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantNameUseCase;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantStatusUseCase;
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
 * TenantCommandController 단위 테스트
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
@WebMvcTest(TenantCommandController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TenantCommandController 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
class TenantCommandControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private CreateTenantUseCase createTenantUseCase;

    @MockBean private UpdateTenantNameUseCase updateTenantNameUseCase;

    @MockBean private UpdateTenantStatusUseCase updateTenantStatusUseCase;

    @MockBean private DeleteTenantUseCase deleteTenantUseCase;

    @MockBean private TenantApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/tenants - 테넌트 생성")
    class CreateTenantTest {

        @Test
        @DisplayName("[성공] 유효한 요청으로 테넌트 생성 시 201 Created 반환")
        void createTenant_withValidRequest_returns201Created() throws Exception {
            // Given
            CreateTenantApiRequest request = new CreateTenantApiRequest("TestTenant");
            UUID tenantId = UUID.randomUUID();
            TenantResponse useCaseResponse =
                    new TenantResponse(
                            tenantId, "TestTenant", "ACTIVE", Instant.now(), Instant.now());
            CreateTenantApiResponse apiResponse = new CreateTenantApiResponse(tenantId.toString());

            given(mapper.toCommand(any(CreateTenantApiRequest.class)))
                    .willReturn(new CreateTenantCommand("TestTenant"));
            given(createTenantUseCase.execute(any(CreateTenantCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(TenantResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            post("/api/v1/auth/tenants")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId.toString()));

            verify(createTenantUseCase).execute(any(CreateTenantCommand.class));
        }

        @Test
        @DisplayName("[실패] 이름이 null이면 400 Bad Request 반환")
        void createTenant_withNullName_returns400BadRequest() throws Exception {
            // Given
            String invalidRequest =
                    """
                    {
                        "name": null
                    }
                    """;

            // When & Then
            // GlobalExceptionHandler가 ProblemDetail (RFC 7807) 형식으로 반환
            mockMvc.perform(
                            post("/api/v1/auth/tenants")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.title").value("Bad Request"));

            verify(createTenantUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 빈 문자열이면 400 Bad Request 반환")
        void createTenant_withEmptyName_returns400BadRequest() throws Exception {
            // Given
            CreateTenantApiRequest request = new CreateTenantApiRequest("");

            // When & Then
            mockMvc.perform(
                            post("/api/v1/auth/tenants")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(createTenantUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 1자 이하면 400 Bad Request 반환")
        void createTenant_withTooShortName_returns400BadRequest() throws Exception {
            // Given
            CreateTenantApiRequest request = new CreateTenantApiRequest("A");

            // When & Then
            mockMvc.perform(
                            post("/api/v1/auth/tenants")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(createTenantUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 100자 초과면 400 Bad Request 반환")
        void createTenant_withTooLongName_returns400BadRequest() throws Exception {
            // Given
            String longName = "A".repeat(101);
            CreateTenantApiRequest request = new CreateTenantApiRequest(longName);

            // When & Then
            mockMvc.perform(
                            post("/api/v1/auth/tenants")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(createTenantUseCase, never()).execute(any());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/tenants/{id}/name - 테넌트 이름 변경")
    class UpdateTenantNameTest {

        @Test
        @DisplayName("[성공] 유효한 요청으로 이름 변경 시 200 OK 반환")
        void updateTenantName_withValidRequest_returns200Ok() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            UpdateTenantNameApiRequest request = new UpdateTenantNameApiRequest("NewTenantName");
            TenantResponse useCaseResponse =
                    new TenantResponse(
                            tenantId, "NewTenantName", "ACTIVE", Instant.now(), Instant.now());
            TenantApiResponse apiResponse =
                    new TenantApiResponse(
                            tenantId.toString(),
                            "NewTenantName",
                            "ACTIVE",
                            Instant.now(),
                            Instant.now());

            given(mapper.toCommand(eq(tenantId), any(UpdateTenantNameApiRequest.class)))
                    .willReturn(new UpdateTenantNameCommand(tenantId, "NewTenantName"));
            given(updateTenantNameUseCase.execute(any(UpdateTenantNameCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(TenantResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            put("/api/v1/auth/tenants/{id}/name", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId.toString()))
                    .andExpect(jsonPath("$.data.name").value("NewTenantName"));

            verify(updateTenantNameUseCase).execute(any(UpdateTenantNameCommand.class));
        }

        @Test
        @DisplayName("[실패] 이름이 빈 문자열이면 400 Bad Request 반환")
        void updateTenantName_withEmptyName_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            UpdateTenantNameApiRequest request = new UpdateTenantNameApiRequest("");

            // When & Then
            mockMvc.perform(
                            put("/api/v1/auth/tenants/{id}/name", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateTenantNameUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 1자면 400 Bad Request 반환")
        void updateTenantName_withTooShortName_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            UpdateTenantNameApiRequest request = new UpdateTenantNameApiRequest("A");

            // When & Then
            mockMvc.perform(
                            put("/api/v1/auth/tenants/{id}/name", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateTenantNameUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 이름이 100자 초과면 400 Bad Request 반환")
        void updateTenantName_withTooLongName_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            String longName = "A".repeat(101);
            UpdateTenantNameApiRequest request = new UpdateTenantNameApiRequest(longName);

            // When & Then
            mockMvc.perform(
                            put("/api/v1/auth/tenants/{id}/name", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateTenantNameUseCase, never()).execute(any());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/tenants/{id}/status - 테넌트 상태 변경")
    class UpdateTenantStatusTest {

        @Test
        @DisplayName("[성공] ACTIVE 상태로 변경 시 200 OK 반환")
        void updateTenantStatus_toActive_returns200Ok() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            UpdateTenantStatusApiRequest request = new UpdateTenantStatusApiRequest("ACTIVE");
            TenantResponse useCaseResponse =
                    new TenantResponse(
                            tenantId, "TestTenant", "ACTIVE", Instant.now(), Instant.now());
            TenantApiResponse apiResponse =
                    new TenantApiResponse(
                            tenantId.toString(),
                            "TestTenant",
                            "ACTIVE",
                            Instant.now(),
                            Instant.now());

            given(mapper.toCommand(eq(tenantId), any(UpdateTenantStatusApiRequest.class)))
                    .willReturn(new UpdateTenantStatusCommand(tenantId, "ACTIVE"));
            given(updateTenantStatusUseCase.execute(any(UpdateTenantStatusCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(TenantResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            patch("/api/v1/auth/tenants/{id}/status", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));

            verify(updateTenantStatusUseCase).execute(any(UpdateTenantStatusCommand.class));
        }

        @Test
        @DisplayName("[성공] INACTIVE 상태로 변경 시 200 OK 반환")
        void updateTenantStatus_toInactive_returns200Ok() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            UpdateTenantStatusApiRequest request = new UpdateTenantStatusApiRequest("INACTIVE");
            TenantResponse useCaseResponse =
                    new TenantResponse(
                            tenantId, "TestTenant", "INACTIVE", Instant.now(), Instant.now());
            TenantApiResponse apiResponse =
                    new TenantApiResponse(
                            tenantId.toString(),
                            "TestTenant",
                            "INACTIVE",
                            Instant.now(),
                            Instant.now());

            given(mapper.toCommand(eq(tenantId), any(UpdateTenantStatusApiRequest.class)))
                    .willReturn(new UpdateTenantStatusCommand(tenantId, "INACTIVE"));
            given(updateTenantStatusUseCase.execute(any(UpdateTenantStatusCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(TenantResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            patch("/api/v1/auth/tenants/{id}/status", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.status").value("INACTIVE"));

            verify(updateTenantStatusUseCase).execute(any(UpdateTenantStatusCommand.class));
        }

        @Test
        @DisplayName("[실패] 상태가 빈 문자열이면 400 Bad Request 반환")
        void updateTenantStatus_withEmptyStatus_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            UpdateTenantStatusApiRequest request = new UpdateTenantStatusApiRequest("");

            // When & Then
            mockMvc.perform(
                            patch("/api/v1/auth/tenants/{id}/status", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateTenantStatusUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] 잘못된 상태값이면 400 Bad Request 반환")
        void updateTenantStatus_withInvalidStatus_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            UpdateTenantStatusApiRequest request = new UpdateTenantStatusApiRequest("INVALID");

            // When & Then
            mockMvc.perform(
                            patch("/api/v1/auth/tenants/{id}/status", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateTenantStatusUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("[실패] DELETED 상태로 변경 시도하면 400 Bad Request 반환")
        void updateTenantStatus_toDeleted_returns400BadRequest() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            UpdateTenantStatusApiRequest request = new UpdateTenantStatusApiRequest("DELETED");

            // When & Then
            mockMvc.perform(
                            patch("/api/v1/auth/tenants/{id}/status", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(updateTenantStatusUseCase, never()).execute(any());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/tenants/{id}/delete - 테넌트 삭제")
    class DeleteTenantTest {

        @Test
        @DisplayName("[성공] 테넌트 삭제 시 204 No Content 반환")
        void deleteTenant_returns204NoContent() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            DeleteTenantCommand command = new DeleteTenantCommand(tenantId);

            given(mapper.toDeleteCommand(any(UUID.class))).willReturn(command);
            willDoNothing().given(deleteTenantUseCase).execute(any(DeleteTenantCommand.class));

            // When & Then
            mockMvc.perform(patch("/api/v1/auth/tenants/{id}/delete", tenantId))
                    .andExpect(status().isNoContent());

            verify(deleteTenantUseCase).execute(any(DeleteTenantCommand.class));
        }

        @Test
        @DisplayName("[실패] 잘못된 UUID 형식이면 400 Bad Request 반환")
        void deleteTenant_withInvalidUuid_returns400BadRequest() throws Exception {
            // When & Then
            // @PathVariable UUID id 사용으로 Spring이 MethodArgumentTypeMismatchException 발생
            mockMvc.perform(patch("/api/v1/auth/tenants/{id}/delete", "invalid-uuid"))
                    .andExpect(status().isBadRequest());

            verify(deleteTenantUseCase, never()).execute(any());
        }
    }
}
