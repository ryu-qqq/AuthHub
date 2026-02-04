package com.ryuqq.authhub.adapter.in.rest.service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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
import com.ryuqq.authhub.adapter.in.rest.service.ServiceApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.service.controller.command.ServiceCommandController;
import com.ryuqq.authhub.adapter.in.rest.service.dto.request.CreateServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.service.dto.request.UpdateServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.service.fixture.ServiceApiFixture;
import com.ryuqq.authhub.adapter.in.rest.service.mapper.ServiceCommandApiMapper;
import com.ryuqq.authhub.application.service.port.in.command.CreateServiceUseCase;
import com.ryuqq.authhub.application.service.port.in.command.UpdateServiceUseCase;
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
 * ServiceCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(ServiceCommandController.class)
@Import({ControllerTestSecurityConfig.class, ServiceCommandApiMapper.class})
@DisplayName("ServiceCommandController 테스트")
class ServiceCommandControllerTest extends RestDocsTestSupport {

    @MockBean private CreateServiceUseCase createServiceUseCase;

    @MockBean private UpdateServiceUseCase updateServiceUseCase;

    @Nested
    @DisplayName("POST /api/v1/auth/services - 서비스 생성")
    class CreateTests {

        @Test
        @DisplayName("유효한 요청으로 서비스를 생성한다")
        void shouldCreateServiceSuccessfully() throws Exception {
            // given
            CreateServiceApiRequest request = ServiceApiFixture.createServiceRequest();
            Long serviceId = ServiceApiFixture.defaultServiceId();
            given(createServiceUseCase.execute(any())).willReturn(serviceId);

            // when & then
            mockMvc.perform(
                            post(ServiceApiEndpoints.SERVICES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.serviceId").value(serviceId))
                    .andDo(
                            document(
                                    "service/create",
                                    requestFields(
                                            fieldWithPath("serviceCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "서비스 코드 (필수, UPPER_SNAKE_CASE, 2~50자)"),
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("서비스 이름 (필수, 2~100자)"),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("서비스 설명 (선택, 500자 이하)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.serviceId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 Service ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("serviceCode가 빈 문자열이면 400 Bad Request")
        void shouldFailWhenServiceCodeIsBlank() throws Exception {
            // given
            CreateServiceApiRequest request = ServiceApiFixture.createServiceRequest("", "자사몰");

            // when & then
            mockMvc.perform(
                            post(ServiceApiEndpoints.SERVICES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("serviceCode가 UPPER_SNAKE_CASE 형식이 아니면 400 Bad Request")
        void shouldFailWhenServiceCodePatternIsInvalid() throws Exception {
            // given
            CreateServiceApiRequest request =
                    ServiceApiFixture.createServiceRequest("invalid-code", "자사몰");

            // when & then
            mockMvc.perform(
                            post(ServiceApiEndpoints.SERVICES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("name이 빈 문자열이면 400 Bad Request")
        void shouldFailWhenNameIsBlank() throws Exception {
            // given
            CreateServiceApiRequest request =
                    ServiceApiFixture.createServiceRequest("SVC_STORE", "");

            // when & then
            mockMvc.perform(
                            post(ServiceApiEndpoints.SERVICES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("name이 100자 초과이면 400 Bad Request")
        void shouldFailWhenNameExceedsMaxLength() throws Exception {
            // given
            String longName = "a".repeat(101);
            CreateServiceApiRequest request =
                    ServiceApiFixture.createServiceRequest("SVC_STORE", longName);

            // when & then
            mockMvc.perform(
                            post(ServiceApiEndpoints.SERVICES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("description이 500자 초과이면 400 Bad Request")
        void shouldFailWhenDescriptionExceedsMaxLength() throws Exception {
            // given
            String longDescription = "a".repeat(501);
            CreateServiceApiRequest request =
                    ServiceApiFixture.createServiceRequest("SVC_STORE", "자사몰", longDescription);

            // when & then
            mockMvc.perform(
                            post(ServiceApiEndpoints.SERVICES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/services/{serviceId} - 서비스 수정")
    class UpdateTests {

        @Test
        @DisplayName("유효한 요청으로 서비스를 수정한다")
        void shouldUpdateServiceSuccessfully() throws Exception {
            // given
            Long serviceId = ServiceApiFixture.defaultServiceId();
            UpdateServiceApiRequest request = ServiceApiFixture.updateServiceRequest();
            doNothing().when(updateServiceUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(ServiceApiEndpoints.SERVICES + "/{serviceId}", serviceId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.serviceId").value(serviceId))
                    .andDo(
                            document(
                                    "service/update",
                                    pathParameters(
                                            parameterWithName("serviceId")
                                                    .description("수정할 Service ID")),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("서비스 이름 (필수, 2~100자)"),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("서비스 설명 (필수, 500자 이하)"),
                                            fieldWithPath("status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "서비스 상태 (필수, ACTIVE 또는 INACTIVE)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.serviceId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수정된 Service ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("name이 빈 문자열이면 400 Bad Request")
        void shouldFailWhenNameIsBlank() throws Exception {
            // given
            Long serviceId = ServiceApiFixture.defaultServiceId();
            UpdateServiceApiRequest request =
                    ServiceApiFixture.updateServiceRequest("", "설명", "ACTIVE");

            // when & then
            mockMvc.perform(
                            put(ServiceApiEndpoints.SERVICES + "/{serviceId}", serviceId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("status가 빈 문자열이면 400 Bad Request")
        void shouldFailWhenStatusIsBlank() throws Exception {
            // given
            Long serviceId = ServiceApiFixture.defaultServiceId();
            UpdateServiceApiRequest request =
                    ServiceApiFixture.updateServiceRequest("자사몰", "설명", "");

            // when & then
            mockMvc.perform(
                            put(ServiceApiEndpoints.SERVICES + "/{serviceId}", serviceId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("description이 null이면 400 Bad Request")
        void shouldFailWhenDescriptionIsNull() throws Exception {
            // given
            Long serviceId = ServiceApiFixture.defaultServiceId();
            UpdateServiceApiRequest request =
                    ServiceApiFixture.updateServiceRequest("자사몰", null, "ACTIVE");

            // when & then
            mockMvc.perform(
                            put(ServiceApiEndpoints.SERVICES + "/{serviceId}", serviceId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("name이 100자 초과이면 400 Bad Request")
        void shouldFailWhenNameExceedsMaxLength() throws Exception {
            // given
            Long serviceId = ServiceApiFixture.defaultServiceId();
            String longName = "a".repeat(101);
            UpdateServiceApiRequest request =
                    ServiceApiFixture.updateServiceRequest(longName, "설명", "ACTIVE");

            // when & then
            mockMvc.perform(
                            put(ServiceApiEndpoints.SERVICES + "/{serviceId}", serviceId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
