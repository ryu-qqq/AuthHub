package com.ryuqq.authhub.adapter.in.rest.tenantservice.controller;

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
import com.ryuqq.authhub.adapter.in.rest.tenantservice.TenantServiceApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.controller.query.TenantServiceQueryController;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.fixture.TenantServiceApiFixture;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.mapper.TenantServiceQueryApiMapper;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServicePageResult;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServiceResult;
import com.ryuqq.authhub.application.tenantservice.port.in.query.SearchTenantServicesUseCase;
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
 * TenantServiceQueryController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(TenantServiceQueryController.class)
@Import({ControllerTestSecurityConfig.class, TenantServiceQueryApiMapper.class})
@DisplayName("TenantServiceQueryController 테스트")
class TenantServiceQueryControllerTest extends RestDocsTestSupport {

    @MockBean private SearchTenantServicesUseCase searchTenantServicesUseCase;

    @Nested
    @DisplayName("GET /api/v1/auth/tenant-services - 테넌트-서비스 구독 목록 조회")
    class SearchTests {

        @Test
        @DisplayName("유효한 요청으로 테넌트-서비스 목록을 조회한다")
        void shouldSearchTenantServicesSuccessfully() throws Exception {
            // given
            Instant fixedTime = TenantServiceApiFixture.fixedTime();
            TenantServiceResult result =
                    new TenantServiceResult(
                            TenantServiceApiFixture.defaultTenantServiceId(),
                            TenantServiceApiFixture.defaultTenantId(),
                            TenantServiceApiFixture.defaultServiceId(),
                            TenantServiceApiFixture.defaultStatus(),
                            fixedTime,
                            fixedTime,
                            fixedTime);
            TenantServicePageResult pageResult =
                    TenantServicePageResult.of(List.of(result), 0, 20, 1L);
            given(searchTenantServicesUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            get(TenantServiceApiEndpoints.TENANT_SERVICES)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(
                            jsonPath("$.data.content[0].tenantServiceId")
                                    .value(TenantServiceApiFixture.defaultTenantServiceId()))
                    .andExpect(
                            jsonPath("$.data.content[0].tenantId")
                                    .value(TenantServiceApiFixture.defaultTenantId()))
                    .andExpect(
                            jsonPath("$.data.content[0].serviceId")
                                    .value(TenantServiceApiFixture.defaultServiceId()))
                    .andExpect(
                            jsonPath("$.data.content[0].status")
                                    .value(TenantServiceApiFixture.defaultStatus()))
                    .andDo(
                            document(
                                    "tenant-service/search",
                                    queryParameters(
                                            parameterWithName("tenantId")
                                                    .description("테넌트 ID 필터")
                                                    .optional(),
                                            parameterWithName("serviceId")
                                                    .description("서비스 ID 필터")
                                                    .optional(),
                                            parameterWithName("statuses")
                                                    .description(
                                                            "상태 필터 (ACTIVE, INACTIVE,"
                                                                    + " SUSPENDED)")
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
                                                    .description("테넌트-서비스 목록"),
                                            fieldWithPath("data.content[].tenantServiceId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("TenantService ID"),
                                            fieldWithPath("data.content[].tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 ID"),
                                            fieldWithPath("data.content[].serviceId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("서비스 ID"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "구독 상태 (ACTIVE, INACTIVE,"
                                                                    + " SUSPENDED)"),
                                            fieldWithPath("data.content[].subscribedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구독 일시 (ISO 8601)"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성 시각 (ISO 8601)"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정 시각 (ISO 8601)"),
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
            TenantServicePageResult pageResult = TenantServicePageResult.empty(10);
            given(searchTenantServicesUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            get(TenantServiceApiEndpoints.TENANT_SERVICES)
                                    .param("tenantId", TenantServiceApiFixture.defaultTenantId())
                                    .param("serviceId", "1")
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
            mockMvc.perform(
                            get(TenantServiceApiEndpoints.TENANT_SERVICES)
                                    .param("page", "0")
                                    .param("size", "101"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400 Bad Request")
        void shouldFailWhenPageIsNegative() throws Exception {
            // when & then
            mockMvc.perform(
                            get(TenantServiceApiEndpoints.TENANT_SERVICES)
                                    .param("page", "-1")
                                    .param("size", "20"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("빈 결과를 반환한다")
        void shouldReturnEmptyResult() throws Exception {
            // given
            TenantServicePageResult pageResult = TenantServicePageResult.empty(20);
            given(searchTenantServicesUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            get(TenantServiceApiEndpoints.TENANT_SERVICES)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0))
                    .andExpect(jsonPath("$.data.totalPages").value(0))
                    .andExpect(jsonPath("$.data.first").value(true))
                    .andExpect(jsonPath("$.data.last").value(true));
        }
    }
}
