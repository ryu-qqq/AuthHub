package com.ryuqq.authhub.adapter.in.rest.role.controller;

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
import com.ryuqq.authhub.adapter.in.rest.role.RoleApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.role.controller.query.RoleQueryController;
import com.ryuqq.authhub.adapter.in.rest.role.fixture.RoleApiFixture;
import com.ryuqq.authhub.adapter.in.rest.role.mapper.RoleQueryApiMapper;
import com.ryuqq.authhub.application.role.dto.response.RolePageResult;
import com.ryuqq.authhub.application.role.dto.response.RoleResult;
import com.ryuqq.authhub.application.role.port.in.query.SearchRolesUseCase;
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
 * RoleQueryController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(RoleQueryController.class)
@Import({ControllerTestSecurityConfig.class, RoleQueryApiMapper.class})
@DisplayName("RoleQueryController 테스트")
class RoleQueryControllerTest extends RestDocsTestSupport {

    @MockBean private SearchRolesUseCase searchRolesUseCase;

    @Nested
    @DisplayName("GET /api/v1/auth/roles - 역할 목록 검색")
    class SearchTests {

        @Test
        @DisplayName("유효한 요청으로 역할 목록을 조회한다")
        void shouldSearchRolesSuccessfully() throws Exception {
            // given
            Instant fixedTime = RoleApiFixture.fixedTime();
            RoleResult result =
                    new RoleResult(
                            RoleApiFixture.defaultRoleId(),
                            RoleApiFixture.defaultTenantId(),
                            RoleApiFixture.defaultName(),
                            RoleApiFixture.defaultDisplayName(),
                            RoleApiFixture.defaultDescription(),
                            RoleApiFixture.defaultType(),
                            fixedTime,
                            fixedTime);
            RolePageResult pageResult = RolePageResult.of(List.of(result), 0, 20, 1L);
            given(searchRolesUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(get(RoleApiEndpoints.ROLES).param("page", "0").param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(
                            jsonPath("$.data.content[0].roleId")
                                    .value(RoleApiFixture.defaultRoleId()))
                    .andExpect(
                            jsonPath("$.data.content[0].name").value(RoleApiFixture.defaultName()))
                    .andExpect(
                            jsonPath("$.data.content[0].type").value(RoleApiFixture.defaultType()))
                    .andDo(
                            document(
                                    "role/search",
                                    queryParameters(
                                            parameterWithName("tenantId")
                                                    .description("테넌트 ID 필터 (null이면 Global만)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (NAME, DISPLAY_NAME,"
                                                                    + " DESCRIPTION)")
                                                    .optional(),
                                            parameterWithName("types")
                                                    .description("역할 유형 필터 (SYSTEM, CUSTOM)")
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
                                                    .description("역할 목록"),
                                            fieldWithPath("data.content[].roleId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("Role ID"),
                                            fieldWithPath("data.content[].tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 ID (null이면 Global)"),
                                            fieldWithPath("data.content[].name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("역할 이름"),
                                            fieldWithPath("data.content[].displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시 이름"),
                                            fieldWithPath("data.content[].description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("역할 설명"),
                                            fieldWithPath("data.content[].type")
                                                    .type(JsonFieldType.STRING)
                                                    .description("역할 유형 (SYSTEM, CUSTOM)"),
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
            RolePageResult pageResult = RolePageResult.empty(10);
            given(searchRolesUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            get(RoleApiEndpoints.ROLES)
                                    .param("tenantId", RoleApiFixture.defaultTenantId())
                                    .param("searchWord", "USER")
                                    .param("searchField", "NAME")
                                    .param("types", "CUSTOM")
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
            mockMvc.perform(get(RoleApiEndpoints.ROLES).param("page", "0").param("size", "101"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400 Bad Request")
        void shouldFailWhenPageIsNegative() throws Exception {
            // when & then
            mockMvc.perform(get(RoleApiEndpoints.ROLES).param("page", "-1").param("size", "20"))
                    .andExpect(status().isBadRequest());
        }
    }
}
