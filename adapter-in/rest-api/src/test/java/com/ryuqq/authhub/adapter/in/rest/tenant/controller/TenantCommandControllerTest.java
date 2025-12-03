package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantApiMapper;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.response.CreateTenantResponse;
import com.ryuqq.authhub.application.tenant.port.in.CreateTenantUseCase;

/**
 * TenantCommandController 단위 테스트
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
@WebMvcTest(TenantCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("TenantCommandController 테스트")
class TenantCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateTenantUseCase createTenantUseCase;

    @MockBean
    private TenantApiMapper tenantApiMapper;

    @MockBean
    private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/tenants 테스트")
    class CreateTenantTest {

        @Test
        @DisplayName("테넌트 생성 성공 (201 Created)")
        void givenValidRequest_whenCreateTenant_thenReturns201() throws Exception {
            // given
            CreateTenantApiRequest request = new CreateTenantApiRequest("Enterprise Tenant");
            Long tenantId = 1L;

            CreateTenantCommand command = new CreateTenantCommand("Enterprise Tenant");
            CreateTenantResponse useCaseResponse = new CreateTenantResponse(tenantId);

            given(tenantApiMapper.toCreateTenantCommand(any(CreateTenantApiRequest.class)))
                    .willReturn(command);
            given(createTenantUseCase.execute(any(CreateTenantCommand.class)))
                    .willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(post("/api/v1/tenants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId));

            verify(tenantApiMapper).toCreateTenantCommand(any(CreateTenantApiRequest.class));
            verify(createTenantUseCase).execute(any(CreateTenantCommand.class));
        }

        @Test
        @DisplayName("name 누락 시 Validation 실패 (400 Bad Request)")
        void givenNullName_whenCreateTenant_thenReturns400() throws Exception {
            // given
            String invalidRequest = """
                    {
                        "name": null
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/v1/tenants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("name이 빈 문자열이면 Validation 실패 (400 Bad Request)")
        void givenBlankName_whenCreateTenant_thenReturns400() throws Exception {
            // given
            String invalidRequest = """
                    {
                        "name": ""
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/v1/tenants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }
}
