package com.ryuqq.authhub.adapter.in.rest.organization.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.organization.OrganizationApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.fixture.OrganizationApiFixture;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationCommandApiMapper;
import com.ryuqq.authhub.application.organization.port.in.command.CreateOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationNameUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationStatusUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

/**
 * OrganizationCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(OrganizationCommandController.class)
@Import({ControllerTestSecurityConfig.class, OrganizationCommandApiMapper.class})
@DisplayName("OrganizationCommandController 테스트")
class OrganizationCommandControllerTest extends RestDocsTestSupport {

    @MockBean private CreateOrganizationUseCase createOrganizationUseCase;

    @MockBean private UpdateOrganizationNameUseCase updateOrganizationNameUseCase;

    @MockBean private UpdateOrganizationStatusUseCase updateOrganizationStatusUseCase;

    @Nested
    @DisplayName("POST /api/v1/auth/organizations - 조직 생성")
    class CreateTests {

        @Test
        @DisplayName("유효한 요청으로 조직을 생성한다")
        void shouldCreateOrganizationSuccessfully() throws Exception {
            // given
            CreateOrganizationApiRequest request =
                    OrganizationApiFixture.createOrganizationRequest();
            String organizationId = OrganizationApiFixture.defaultOrganizationId();
            given(createOrganizationUseCase.execute(any())).willReturn(organizationId);

            // when & then
            mockMvc.perform(
                            post(OrganizationApiEndpoints.ORGANIZATIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.organizationId").value(organizationId))
                    .andDo(
                            document(
                                    "organization/create",
                                    requestFields(
                                            fieldWithPath("tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 ID (필수)"),
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조직 이름 (필수, 2~100자)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data.organizationId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성된 조직 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("테넌트 ID가 없으면 400 Bad Request")
        void shouldFailWhenTenantIdIsBlank() throws Exception {
            // given
            CreateOrganizationApiRequest request =
                    new CreateOrganizationApiRequest("", OrganizationApiFixture.defaultName());

            // when & then
            mockMvc.perform(
                            post(OrganizationApiEndpoints.ORGANIZATIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("조직 이름이 2자 미만이면 400 Bad Request")
        void shouldFailWhenNameIsTooShort() throws Exception {
            // given
            CreateOrganizationApiRequest request =
                    OrganizationApiFixture.createOrganizationRequestWithName("A");

            // when & then
            mockMvc.perform(
                            post(OrganizationApiEndpoints.ORGANIZATIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/organizations/{organizationId}/name - 조직 이름 수정")
    class UpdateNameTests {

        @Test
        @DisplayName("유효한 요청으로 조직 이름을 수정한다")
        void shouldUpdateOrganizationNameSuccessfully() throws Exception {
            // given
            String organizationId = OrganizationApiFixture.defaultOrganizationId();
            UpdateOrganizationNameApiRequest request =
                    OrganizationApiFixture.updateOrganizationNameRequest();
            doNothing().when(updateOrganizationNameUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(
                                            OrganizationApiEndpoints.ORGANIZATIONS
                                                    + "/{organizationId}/name",
                                            organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.organizationId").value(organizationId))
                    .andDo(
                            document(
                                    "organization/update-name",
                                    pathParameters(
                                            parameterWithName("organizationId")
                                                    .description("수정할 조직 ID")),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변경할 조직 이름 (필수, 2~100자)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data.organizationId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정된 조직 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("조직 이름이 없으면 400 Bad Request")
        void shouldFailWhenNameIsBlank() throws Exception {
            // given
            String organizationId = OrganizationApiFixture.defaultOrganizationId();
            UpdateOrganizationNameApiRequest request = new UpdateOrganizationNameApiRequest("");

            // when & then
            mockMvc.perform(
                            put(
                                            OrganizationApiEndpoints.ORGANIZATIONS
                                                    + "/{organizationId}/name",
                                            organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/auth/organizations/{organizationId}/status - 조직 상태 수정")
    class UpdateStatusTests {

        @Test
        @DisplayName("유효한 요청으로 조직 상태를 수정한다")
        void shouldUpdateOrganizationStatusSuccessfully() throws Exception {
            // given
            String organizationId = OrganizationApiFixture.defaultOrganizationId();
            UpdateOrganizationStatusApiRequest request =
                    OrganizationApiFixture.updateOrganizationStatusRequest();
            doNothing().when(updateOrganizationStatusUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            patch(
                                            OrganizationApiEndpoints.ORGANIZATIONS
                                                    + "/{organizationId}/status",
                                            organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.organizationId").value(organizationId))
                    .andDo(
                            document(
                                    "organization/update-status",
                                    pathParameters(
                                            parameterWithName("organizationId")
                                                    .description("수정할 조직 ID")),
                                    requestFields(
                                            fieldWithPath("status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "변경할 상태 (필수, ACTIVE 또는 INACTIVE)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data.organizationId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정된 조직 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("상태 값이 유효하지 않으면 400 Bad Request")
        void shouldFailWhenStatusIsInvalid() throws Exception {
            // given
            String organizationId = OrganizationApiFixture.defaultOrganizationId();
            UpdateOrganizationStatusApiRequest request =
                    new UpdateOrganizationStatusApiRequest("INVALID_STATUS");

            // when & then
            mockMvc.perform(
                            patch(
                                            OrganizationApiEndpoints.ORGANIZATIONS
                                                    + "/{organizationId}/status",
                                            organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("상태 값이 없으면 400 Bad Request")
        void shouldFailWhenStatusIsBlank() throws Exception {
            // given
            String organizationId = OrganizationApiFixture.defaultOrganizationId();
            UpdateOrganizationStatusApiRequest request = new UpdateOrganizationStatusApiRequest("");

            // when & then
            mockMvc.perform(
                            patch(
                                            OrganizationApiEndpoints.ORGANIZATIONS
                                                    + "/{organizationId}/status",
                                            organizationId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
