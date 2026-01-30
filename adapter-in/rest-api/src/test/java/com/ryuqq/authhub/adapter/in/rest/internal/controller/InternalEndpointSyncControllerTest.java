package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.EndpointSyncApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.fixture.InternalApiFixture;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalEndpointSyncApiMapper;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.SyncEndpointsResult;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.SyncEndpointsUseCase;
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
 * InternalEndpointSyncController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(InternalEndpointSyncController.class)
@Import({ControllerTestSecurityConfig.class, InternalEndpointSyncApiMapper.class})
@DisplayName("InternalEndpointSyncController 테스트")
class InternalEndpointSyncControllerTest extends RestDocsTestSupport {

    @MockBean private SyncEndpointsUseCase syncEndpointsUseCase;

    @Nested
    @DisplayName("POST /api/v1/internal/endpoints/sync - 엔드포인트 동기화")
    class SyncTests {

        @Test
        @DisplayName("유효한 요청으로 엔드포인트를 동기화한다")
        void shouldSyncEndpointsSuccessfully() throws Exception {
            // given
            EndpointSyncApiRequest request =
                    InternalApiFixture.endpointSyncRequestWithMultipleEndpoints();
            SyncEndpointsResult result =
                    SyncEndpointsResult.of(InternalApiFixture.defaultServiceName(), 4, 2, 3, 1);
            given(syncEndpointsUseCase.sync(any())).willReturn(result);

            // when & then
            mockMvc.perform(
                            post(InternalApiEndpoints.ENDPOINTS
                                            + InternalApiEndpoints.ENDPOINTS_SYNC)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(
                            jsonPath("$.data.serviceName")
                                    .value(InternalApiFixture.defaultServiceName()))
                    .andExpect(jsonPath("$.data.totalEndpoints").value(4))
                    .andExpect(jsonPath("$.data.createdPermissions").value(2))
                    .andExpect(jsonPath("$.data.createdEndpoints").value(3))
                    .andExpect(jsonPath("$.data.skippedEndpoints").value(1))
                    .andDo(
                            document(
                                    "internal/endpoint-sync/sync",
                                    requestFields(
                                            fieldWithPath("serviceName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("서비스 이름 (필수)"),
                                            fieldWithPath("endpoints")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("엔드포인트 목록 (필수, 비어있으면 안됨)"),
                                            fieldWithPath("endpoints[].httpMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description("HTTP 메서드 (필수)"),
                                            fieldWithPath("endpoints[].pathPattern")
                                                    .type(JsonFieldType.STRING)
                                                    .description("URL 패턴 (필수)"),
                                            fieldWithPath("endpoints[].permissionKey")
                                                    .type(JsonFieldType.STRING)
                                                    .description("권한 키 (필수)"),
                                            fieldWithPath("endpoints[].description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명 (선택)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.serviceName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("서비스 이름"),
                                            fieldWithPath("data.totalEndpoints")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 엔드포인트 수"),
                                            fieldWithPath("data.createdPermissions")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 권한 수"),
                                            fieldWithPath("data.createdEndpoints")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 엔드포인트 수"),
                                            fieldWithPath("data.skippedEndpoints")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("스킵된 엔드포인트 수 (이미 존재)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("serviceName이 빈 문자열이면 400 Bad Request")
        void shouldFailWhenServiceNameIsBlank() throws Exception {
            // given
            EndpointSyncApiRequest request = InternalApiFixture.endpointSyncRequest("");

            // when & then
            mockMvc.perform(
                            post(InternalApiEndpoints.ENDPOINTS
                                            + InternalApiEndpoints.ENDPOINTS_SYNC)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("endpoints가 비어있으면 400 Bad Request")
        void shouldFailWhenEndpointsIsEmpty() throws Exception {
            // given
            EndpointSyncApiRequest request =
                    InternalApiFixture.endpointSyncRequestWithEmptyEndpoints("marketplace");

            // when & then
            mockMvc.perform(
                            post(InternalApiEndpoints.ENDPOINTS
                                            + InternalApiEndpoints.ENDPOINTS_SYNC)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
