package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantApiMapper;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.port.in.command.CreateTenantUseCase;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TenantCommandController 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
class TenantCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateTenantUseCase createTenantUseCase;

    @MockBean
    private TenantApiMapper mapper;

    @MockBean
    private ErrorMapperRegistry errorMapperRegistry;

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
                    new TenantResponse(tenantId, "TestTenant", "ACTIVE", Instant.now(), Instant.now());
            CreateTenantApiResponse apiResponse = new CreateTenantApiResponse(tenantId.toString());

            given(mapper.toCommand(any(CreateTenantApiRequest.class)))
                    .willReturn(new CreateTenantCommand("TestTenant"));
            given(createTenantUseCase.execute(any(CreateTenantCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(TenantResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            post("/api/v1/tenants")
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
            String invalidRequest = """
                    {
                        "name": null
                    }
                    """;

            // When & Then
            // GlobalExceptionHandler가 ProblemDetail (RFC 7807) 형식으로 반환
            mockMvc.perform(
                            post("/api/v1/tenants")
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
                            post("/api/v1/tenants")
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
                            post("/api/v1/tenants")
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
                            post("/api/v1/tenants")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));

            verify(createTenantUseCase, never()).execute(any());
        }
    }
}
