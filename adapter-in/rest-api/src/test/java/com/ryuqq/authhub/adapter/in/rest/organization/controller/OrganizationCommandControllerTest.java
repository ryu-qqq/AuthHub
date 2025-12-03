package com.ryuqq.authhub.adapter.in.rest.organization.controller;

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
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationApiMapper;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.CreateOrganizationResponse;
import com.ryuqq.authhub.application.organization.port.in.CreateOrganizationUseCase;

/**
 * OrganizationCommandController 단위 테스트
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
@WebMvcTest(OrganizationCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("OrganizationCommandController 테스트")
class OrganizationCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateOrganizationUseCase createOrganizationUseCase;

    @MockBean
    private OrganizationApiMapper organizationApiMapper;

    @MockBean
    private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/organizations 테스트")
    class CreateOrganizationTest {

        @Test
        @DisplayName("조직 생성 성공 (201 Created)")
        void givenValidRequest_whenCreateOrganization_thenReturns201() throws Exception {
            // given
            CreateOrganizationApiRequest request = new CreateOrganizationApiRequest(1L, "Engineering Team");
            Long organizationId = 100L;

            CreateOrganizationCommand command = new CreateOrganizationCommand(1L, "Engineering Team");
            CreateOrganizationResponse useCaseResponse = new CreateOrganizationResponse(organizationId);

            given(organizationApiMapper.toCreateOrganizationCommand(any(CreateOrganizationApiRequest.class)))
                    .willReturn(command);
            given(createOrganizationUseCase.execute(any(CreateOrganizationCommand.class)))
                    .willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(post("/api/v1/organizations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.organizationId").value(organizationId));

            verify(organizationApiMapper).toCreateOrganizationCommand(any(CreateOrganizationApiRequest.class));
            verify(createOrganizationUseCase).execute(any(CreateOrganizationCommand.class));
        }

        @Test
        @DisplayName("tenantId 누락 시 Validation 실패 (400 Bad Request)")
        void givenNullTenantId_whenCreateOrganization_thenReturns400() throws Exception {
            // given
            String invalidRequest = """
                    {
                        "tenantId": null,
                        "name": "Engineering Team"
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/v1/organizations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("name 누락 시 Validation 실패 (400 Bad Request)")
        void givenBlankName_whenCreateOrganization_thenReturns400() throws Exception {
            // given
            String invalidRequest = """
                    {
                        "tenantId": 1,
                        "name": ""
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/v1/organizations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }
}
