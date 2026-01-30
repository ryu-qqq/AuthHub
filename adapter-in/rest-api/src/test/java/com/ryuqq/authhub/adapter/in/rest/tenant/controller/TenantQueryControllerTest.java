package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

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
import com.ryuqq.authhub.adapter.in.rest.tenant.TenantApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.tenant.controller.query.TenantQueryController;
import com.ryuqq.authhub.adapter.in.rest.tenant.fixture.TenantApiFixture;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantQueryApiMapper;
import com.ryuqq.authhub.application.tenant.dto.response.TenantPageResult;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResult;
import com.ryuqq.authhub.application.tenant.port.in.query.SearchTenantsByOffsetUseCase;
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
 * TenantQueryController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(TenantQueryController.class)
@Import({ControllerTestSecurityConfig.class, TenantQueryApiMapper.class})
@DisplayName("TenantQueryController 테스트")
class TenantQueryControllerTest extends RestDocsTestSupport {

    @MockBean private SearchTenantsByOffsetUseCase searchTenantsByOffsetUseCase;

    @Nested
    @DisplayName("GET /api/v1/auth/tenants - 테넌트 목록 검색")
    class SearchTests {

        @Test
        @DisplayName("유효한 요청으로 테넌트 목록을 조회한다")
        void shouldSearchTenantsSuccessfully() throws Exception {
            // given
            Instant fixedTime = TenantApiFixture.fixedTime();
            TenantResult result =
                    new TenantResult(
                            TenantApiFixture.defaultTenantIdString(),
                            TenantApiFixture.defaultTenantName(),
                            TenantApiFixture.defaultStatus(),
                            fixedTime,
                            fixedTime);
            TenantPageResult pageResult = TenantPageResult.of(List.of(result), 0, 20, 1L);
            given(searchTenantsByOffsetUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(get(TenantApiEndpoints.TENANTS).param("page", "0").param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(
                            jsonPath("$.data.content[0].tenantId")
                                    .value(TenantApiFixture.defaultTenantIdString()))
                    .andExpect(
                            jsonPath("$.data.content[0].name")
                                    .value(TenantApiFixture.defaultTenantName()))
                    .andExpect(
                            jsonPath("$.data.content[0].status")
                                    .value(TenantApiFixture.defaultStatus()))
                    .andDo(
                            document(
                                    "tenant/search",
                                    queryParameters(
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (NAME)")
                                                    .optional(),
                                            parameterWithName("statuses")
                                                    .description(
                                                            "상태 필터 목록 (ACTIVE, INACTIVE, DELETED)")
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
                                                    .description("테넌트 목록"),
                                            fieldWithPath("data.content[].tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("Tenant ID (UUID)"),
                                            fieldWithPath("data.content[].name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 이름"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 상태"),
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
            TenantPageResult pageResult = TenantPageResult.empty(10);
            given(searchTenantsByOffsetUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            get(TenantApiEndpoints.TENANTS)
                                    .param("searchWord", "테넌트")
                                    .param("searchField", "NAME")
                                    .param("statuses", "ACTIVE", "INACTIVE")
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
            mockMvc.perform(get(TenantApiEndpoints.TENANTS).param("page", "0").param("size", "101"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400 Bad Request")
        void shouldFailWhenPageIsNegative() throws Exception {
            // when & then
            mockMvc.perform(get(TenantApiEndpoints.TENANTS).param("page", "-1").param("size", "20"))
                    .andExpect(status().isBadRequest());
        }
    }
}
