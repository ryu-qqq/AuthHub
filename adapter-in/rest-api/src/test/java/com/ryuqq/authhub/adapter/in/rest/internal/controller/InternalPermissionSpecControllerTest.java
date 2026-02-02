package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.internal.fixture.InternalApiFixture;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalPermissionSpecApiMapper;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecListResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
import com.ryuqq.authhub.application.permissionendpoint.port.in.query.GetEndpointPermissionSpecUseCase;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.JsonFieldType;

/**
 * InternalPermissionSpecController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(InternalPermissionSpecController.class)
@Import({ControllerTestSecurityConfig.class, InternalPermissionSpecApiMapper.class})
@DisplayName("InternalPermissionSpecController 테스트")
class InternalPermissionSpecControllerTest extends RestDocsTestSupport {

    @MockBean private GetEndpointPermissionSpecUseCase getEndpointPermissionSpecUseCase;

    @Nested
    @DisplayName("GET /api/v1/internal/endpoint-permissions/spec - 엔드포인트-권한 스펙 조회")
    class GetSpecTests {

        @Test
        @DisplayName("유효한 요청으로 엔드포인트-권한 스펙을 조회한다")
        void shouldGetSpecSuccessfully() throws Exception {
            // given
            Instant latestUpdatedAt = Instant.parse("2025-01-15T10:00:00Z");
            EndpointPermissionSpecResult specResult =
                    new EndpointPermissionSpecResult(
                            InternalApiFixture.defaultServiceName(),
                            InternalApiFixture.defaultPathPattern(),
                            InternalApiFixture.defaultHttpMethod(),
                            InternalApiFixture.defaultPermissionKey(),
                            false,
                            InternalApiFixture.defaultDescription());
            EndpointPermissionSpecListResult listResult =
                    EndpointPermissionSpecListResult.of(List.of(specResult), latestUpdatedAt);
            given(getEndpointPermissionSpecUseCase.getAll()).willReturn(listResult);

            // when & then
            mockMvc.perform(
                            get(
                                    InternalApiEndpoints.ENDPOINT_PERMISSIONS
                                            + InternalApiEndpoints.ENDPOINT_PERMISSIONS_SPEC))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.endpoints").isArray())
                    .andExpect(
                            jsonPath("$.data.endpoints[0].serviceName")
                                    .value(InternalApiFixture.defaultServiceName()))
                    .andExpect(
                            jsonPath("$.data.endpoints[0].pathPattern")
                                    .value(InternalApiFixture.defaultPathPattern()))
                    .andExpect(
                            jsonPath("$.data.endpoints[0].requiredPermissions[0]")
                                    .value(InternalApiFixture.defaultPermissionKey()))
                    .andDo(
                            document(
                                    "internal/permission-spec/get-all",
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.version")
                                                    .type(JsonFieldType.STRING)
                                                    .description("스펙 버전 (ETag용)"),
                                            fieldWithPath("data.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("마지막 수정 시간")
                                                    .optional(),
                                            fieldWithPath("data.endpoints")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("엔드포인트-권한 매핑 목록"),
                                            fieldWithPath("data.endpoints[].serviceName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("서비스 이름"),
                                            fieldWithPath("data.endpoints[].pathPattern")
                                                    .type(JsonFieldType.STRING)
                                                    .description("URL 패턴"),
                                            fieldWithPath("data.endpoints[].httpMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description("HTTP 메서드"),
                                            fieldWithPath("data.endpoints[].requiredPermissions")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("필요 권한 목록"),
                                            fieldWithPath("data.endpoints[].requiredRoles")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("필요 역할 목록"),
                                            fieldWithPath("data.endpoints[].isPublic")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("공개 엔드포인트 여부"),
                                            fieldWithPath("data.endpoints[].description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("빈 목록을 정상적으로 반환한다")
        void shouldReturnEmptyListSuccessfully() throws Exception {
            // given
            EndpointPermissionSpecListResult emptyResult = EndpointPermissionSpecListResult.empty();
            given(getEndpointPermissionSpecUseCase.getAll()).willReturn(emptyResult);

            // when & then
            mockMvc.perform(
                            get(
                                    InternalApiEndpoints.ENDPOINT_PERMISSIONS
                                            + InternalApiEndpoints.ENDPOINT_PERMISSIONS_SPEC))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.endpoints").isArray())
                    .andExpect(jsonPath("$.data.endpoints").isEmpty());
        }
    }
}
