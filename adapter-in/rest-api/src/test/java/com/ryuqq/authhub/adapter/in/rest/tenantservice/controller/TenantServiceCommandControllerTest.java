package com.ryuqq.authhub.adapter.in.rest.tenantservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
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
import com.ryuqq.authhub.adapter.in.rest.common.fixture.ErrorMapperApiFixture;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.TenantServiceApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.controller.command.TenantServiceCommandController;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.SubscribeTenantServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.UpdateTenantServiceStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.fixture.TenantServiceApiFixture;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.mapper.TenantServiceCommandApiMapper;
import com.ryuqq.authhub.application.tenantservice.port.in.command.SubscribeTenantServiceUseCase;
import com.ryuqq.authhub.application.tenantservice.port.in.command.UpdateTenantServiceStatusUseCase;
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
 * TenantServiceCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(TenantServiceCommandController.class)
@Import({ControllerTestSecurityConfig.class, TenantServiceCommandApiMapper.class})
@DisplayName("TenantServiceCommandController 테스트")
class TenantServiceCommandControllerTest extends RestDocsTestSupport {

    @MockBean private SubscribeTenantServiceUseCase subscribeTenantServiceUseCase;

    @MockBean private UpdateTenantServiceStatusUseCase updateTenantServiceStatusUseCase;

    @Nested
    @DisplayName("POST /api/v1/auth/tenant-services - 테넌트-서비스 구독")
    class SubscribeTests {

        @Test
        @DisplayName("유효한 요청으로 테넌트-서비스를 구독한다")
        void shouldSubscribeSuccessfully() throws Exception {
            // given
            SubscribeTenantServiceApiRequest request = TenantServiceApiFixture.subscribeRequest();
            Long tenantServiceId = TenantServiceApiFixture.defaultTenantServiceId();
            given(subscribeTenantServiceUseCase.execute(any())).willReturn(tenantServiceId);

            // when & then
            mockMvc.perform(
                            post(TenantServiceApiEndpoints.TENANT_SERVICES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantServiceId").value(tenantServiceId))
                    .andDo(
                            document(
                                    "tenant-service/subscribe",
                                    requestFields(
                                            fieldWithPath("tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 ID (필수)"),
                                            fieldWithPath("serviceId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("서비스 ID (필수)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.tenantServiceId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 TenantService ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("tenantId가 빈 문자열이면 400 Bad Request")
        void shouldFailWhenTenantIdIsBlank() throws Exception {
            // given
            SubscribeTenantServiceApiRequest request =
                    TenantServiceApiFixture.subscribeRequest("", 1L);

            // when & then
            mockMvc.perform(
                            post(TenantServiceApiEndpoints.TENANT_SERVICES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("serviceId가 null이면 400 Bad Request")
        void shouldFailWhenServiceIdIsNull() throws Exception {
            // given
            SubscribeTenantServiceApiRequest request =
                    TenantServiceApiFixture.subscribeRequest(
                            TenantServiceApiFixture.defaultTenantId(), null);

            // when & then
            mockMvc.perform(
                            post(TenantServiceApiEndpoints.TENANT_SERVICES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("이미 구독된 테넌트-서비스이면 409 Conflict")
        void shouldFailWhenDuplicateTenantService() throws Exception {
            // given
            SubscribeTenantServiceApiRequest request = TenantServiceApiFixture.subscribeRequest();
            willThrow(ErrorMapperApiFixture.duplicateTenantServiceException())
                    .given(subscribeTenantServiceUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(
                            post(TenantServiceApiEndpoints.TENANT_SERVICES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(409));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/tenant-services/{tenantServiceId}/status - 상태 변경")
    class UpdateStatusTests {

        @Test
        @DisplayName("유효한 요청으로 상태를 변경한다")
        void shouldUpdateStatusSuccessfully() throws Exception {
            // given
            Long tenantServiceId = TenantServiceApiFixture.defaultTenantServiceId();
            UpdateTenantServiceStatusApiRequest request =
                    TenantServiceApiFixture.updateStatusRequest();
            doNothing().when(updateTenantServiceStatusUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(
                                            TenantServiceApiEndpoints.TENANT_SERVICES
                                                    + "/{tenantServiceId}/status",
                                            tenantServiceId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantServiceId").value(tenantServiceId))
                    .andDo(
                            document(
                                    "tenant-service/update-status",
                                    pathParameters(
                                            parameterWithName("tenantServiceId")
                                                    .description("TenantService ID")),
                                    requestFields(
                                            fieldWithPath("status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "변경할 상태 (ACTIVE, INACTIVE,"
                                                                    + " SUSPENDED)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.tenantServiceId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("변경된 TenantService ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("status가 빈 문자열이면 400 Bad Request")
        void shouldFailWhenStatusIsBlank() throws Exception {
            // given
            Long tenantServiceId = TenantServiceApiFixture.defaultTenantServiceId();
            UpdateTenantServiceStatusApiRequest request =
                    TenantServiceApiFixture.updateStatusRequest("");

            // when & then
            mockMvc.perform(
                            put(
                                            TenantServiceApiEndpoints.TENANT_SERVICES
                                                    + "/{tenantServiceId}/status",
                                            tenantServiceId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("status가 유효하지 않은 값이면 400 Bad Request")
        void shouldFailWhenStatusIsInvalid() throws Exception {
            // given
            Long tenantServiceId = TenantServiceApiFixture.defaultTenantServiceId();
            UpdateTenantServiceStatusApiRequest request =
                    TenantServiceApiFixture.updateStatusRequest("INVALID_STATUS");

            // when & then
            mockMvc.perform(
                            put(
                                            TenantServiceApiEndpoints.TENANT_SERVICES
                                                    + "/{tenantServiceId}/status",
                                            tenantServiceId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TenantService가 존재하지 않으면 404 Not Found")
        void shouldFailWhenTenantServiceNotFound() throws Exception {
            // given
            Long tenantServiceId = TenantServiceApiFixture.defaultTenantServiceId();
            UpdateTenantServiceStatusApiRequest request =
                    TenantServiceApiFixture.updateStatusRequest();
            willThrow(ErrorMapperApiFixture.tenantServiceNotFoundException())
                    .given(updateTenantServiceStatusUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(
                            put(
                                            TenantServiceApiEndpoints.TENANT_SERVICES
                                                    + "/{tenantServiceId}/status",
                                            tenantServiceId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
