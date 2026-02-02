package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.PermissionEndpointApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.fixture.PermissionEndpointApiFixture;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.mapper.PermissionEndpointQueryApiMapper;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointPageResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointResult;
import com.ryuqq.authhub.application.permissionendpoint.port.in.query.SearchPermissionEndpointsUseCase;
import com.ryuqq.authhub.domain.common.vo.PageMeta;
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
 * PermissionEndpointQueryController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(PermissionEndpointQueryController.class)
@Import({ControllerTestSecurityConfig.class, PermissionEndpointQueryApiMapper.class})
@DisplayName("PermissionEndpointQueryController 테스트")
class PermissionEndpointQueryControllerTest extends RestDocsTestSupport {

    @MockBean private SearchPermissionEndpointsUseCase searchPermissionEndpointsUseCase;

    @Nested
    @DisplayName("GET /api/v1/permission-endpoints - PermissionEndpoint 목록 검색")
    class SearchTests {

        @Test
        @DisplayName("유효한 요청으로 PermissionEndpoint 목록을 조회한다")
        void shouldSearchPermissionEndpointsSuccessfully() throws Exception {
            // given
            Instant fixedTime = PermissionEndpointApiFixture.fixedTime();
            PermissionEndpointResult result =
                    new PermissionEndpointResult(
                            PermissionEndpointApiFixture.defaultPermissionEndpointId(),
                            PermissionEndpointApiFixture.defaultPermissionId(),
                            PermissionEndpointApiFixture.defaultServiceName(),
                            PermissionEndpointApiFixture.defaultUrlPattern(),
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            PermissionEndpointApiFixture.defaultDescription(),
                            PermissionEndpointApiFixture.defaultIsPublic(),
                            fixedTime,
                            fixedTime);
            PageMeta pageMeta = new PageMeta(0, 20, 1L, 1);
            PermissionEndpointPageResult pageResult =
                    new PermissionEndpointPageResult(List.of(result), pageMeta);
            given(searchPermissionEndpointsUseCase.search(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            get(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].permissionEndpointId").value(1))
                    .andExpect(jsonPath("$.data.content[0].permissionId").value(1))
                    .andExpect(
                            jsonPath("$.data.content[0].urlPattern")
                                    .value(PermissionEndpointApiFixture.defaultUrlPattern()))
                    .andExpect(
                            jsonPath("$.data.content[0].httpMethod")
                                    .value(PermissionEndpointApiFixture.defaultHttpMethod()))
                    .andDo(
                            document(
                                    "permission-endpoint/search",
                                    queryParameters(
                                            parameterWithName("permissionIds")
                                                    .description("권한 ID 필터 목록")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (URL_PATTERN, HTTP_METHOD,"
                                                                    + " DESCRIPTION)")
                                                    .optional(),
                                            parameterWithName("httpMethods")
                                                    .description("HTTP 메서드 필터 목록")
                                                    .optional(),
                                            parameterWithName("startDate")
                                                    .description("조회 시작일 (YYYY-MM-DD)")
                                                    .optional(),
                                            parameterWithName("endDate")
                                                    .description("조회 종료일 (YYYY-MM-DD)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (기본값: 0, 0 이상)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (기본값: 20, 1~100)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("페이지 응답 데이터"),
                                            fieldWithPath("data.content")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("PermissionEndpoint 목록"),
                                            fieldWithPath("data.content[].permissionEndpointId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("PermissionEndpoint ID"),
                                            fieldWithPath("data.content[].permissionId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("권한 ID"),
                                            fieldWithPath("data.content[].serviceName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("서비스 이름"),
                                            fieldWithPath("data.content[].urlPattern")
                                                    .type(JsonFieldType.STRING)
                                                    .description("URL 패턴"),
                                            fieldWithPath("data.content[].httpMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description("HTTP 메서드"),
                                            fieldWithPath("data.content[].description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명"),
                                            fieldWithPath("data.content[].isPublic")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("공개 엔드포인트 여부"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시 (ISO 8601)"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시 (ISO 8601)"),
                                            fieldWithPath("data.page")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지 번호"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 요소 수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.first")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("첫 번째 페이지 여부"),
                                            fieldWithPath("data.last")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("마지막 페이지 여부"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("다양한 검색 필터를 적용하여 조회한다")
        void shouldSearchWithFilters() throws Exception {
            // given
            PageMeta pageMeta = new PageMeta(0, 20, 0L, 0);
            PermissionEndpointPageResult pageResult =
                    new PermissionEndpointPageResult(List.of(), pageMeta);
            given(searchPermissionEndpointsUseCase.search(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            get(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .param("permissionIds", "1", "2")
                                    .param("searchWord", "users")
                                    .param("searchField", "URL_PATTERN")
                                    .param("httpMethods", "GET", "POST")
                                    .param("startDate", "2024-01-01")
                                    .param("endDate", "2024-12-31")
                                    .param("page", "0")
                                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("페이지 크기가 최대값을 초과하면 400 Bad Request")
        void shouldFailWhenSizeExceedsMax() throws Exception {
            // when & then
            mockMvc.perform(
                            get(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .param("page", "0")
                                    .param("size", "101"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400 Bad Request")
        void shouldFailWhenPageIsNegative() throws Exception {
            // when & then
            mockMvc.perform(
                            get(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .param("page", "-1")
                                    .param("size", "20"))
                    .andExpect(status().isBadRequest());
        }
    }
}
