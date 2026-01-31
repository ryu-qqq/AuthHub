package com.ryuqq.authhub.adapter.in.rest.organization.controller;

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
import com.ryuqq.authhub.adapter.in.rest.organization.OrganizationApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.organization.fixture.OrganizationApiFixture;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationQueryApiMapper;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationPageResult;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResult;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsByOffsetUseCase;
import com.ryuqq.authhub.domain.common.vo.PageMeta;
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
 * OrganizationQueryController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(OrganizationQueryController.class)
@Import({ControllerTestSecurityConfig.class, OrganizationQueryApiMapper.class})
@DisplayName("OrganizationQueryController 테스트")
class OrganizationQueryControllerTest extends RestDocsTestSupport {

    @MockBean private SearchOrganizationsByOffsetUseCase searchOrganizationsByOffsetUseCase;

    @Nested
    @DisplayName("GET /api/v1/auth/organizations - 조직 목록 조회")
    class SearchTests {

        @Test
        @DisplayName("검색 조건으로 조직 목록을 조회한다")
        void shouldSearchOrganizationsSuccessfully() throws Exception {
            // given
            String organizationId = OrganizationApiFixture.defaultOrganizationId();
            OrganizationResult organization =
                    new OrganizationResult(
                            organizationId,
                            OrganizationApiFixture.defaultTenantId(),
                            OrganizationApiFixture.defaultName(),
                            "ACTIVE",
                            OrganizationApiFixture.fixedTime(),
                            OrganizationApiFixture.fixedTime());
            PageMeta pageMeta = PageMeta.of(0, 20, 1L);
            OrganizationPageResult pageResult =
                    new OrganizationPageResult(List.of(organization), pageMeta);
            given(searchOrganizationsByOffsetUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            get(OrganizationApiEndpoints.ORGANIZATIONS)
                                    .param("tenantIds", OrganizationApiFixture.defaultTenantId())
                                    .param("searchWord", "개발")
                                    .param("searchField", "NAME")
                                    .param("statuses", "ACTIVE")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].organizationId").value(organizationId))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andDo(
                            document(
                                    "organization/search",
                                    queryParameters(
                                            parameterWithName("tenantIds")
                                                    .description("테넌트 ID 목록 (선택, 쉼표로 구분)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어 - 조직 이름 검색 (선택)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 - NAME (선택, 기본값: NAME)")
                                                    .optional(),
                                            parameterWithName("statuses")
                                                    .description(
                                                            "상태 필터 - ACTIVE, INACTIVE, DELETED (선택,"
                                                                    + " 다중 선택 가능)")
                                                    .optional(),
                                            parameterWithName("startDate")
                                                    .description("조회 시작일 (선택, 형식: yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("endDate")
                                                    .description("조회 종료일 (선택, 형식: yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터 시작, 기본값: 0)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (1~100, 기본값: 20)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data.content")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("조직 목록"),
                                            fieldWithPath("data.content[].organizationId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조직 ID"),
                                            fieldWithPath("data.content[].tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 ID"),
                                            fieldWithPath("data.content[].name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조직 이름"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "조직 상태 (ACTIVE, INACTIVE, DELETED)"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성 시각"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정 시각"),
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
                                                    .description("첫 페이지 여부"),
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
        @DisplayName("검색 조건 없이 전체 목록을 조회한다")
        void shouldSearchAllOrganizationsWithoutCondition() throws Exception {
            // given
            PageMeta pageMeta = PageMeta.of(0, 20, 0L);
            OrganizationPageResult pageResult = new OrganizationPageResult(List.of(), pageMeta);
            given(searchOrganizationsByOffsetUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(get(OrganizationApiEndpoints.ORGANIZATIONS))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("페이지 크기가 100을 초과하면 400 Bad Request")
        void shouldFailWhenPageSizeExceedsMax() throws Exception {
            // when & then
            mockMvc.perform(get(OrganizationApiEndpoints.ORGANIZATIONS).param("size", "101"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 크기가 0이면 400 Bad Request")
        void shouldFailWhenPageSizeIsZero() throws Exception {
            // when & then
            mockMvc.perform(get(OrganizationApiEndpoints.ORGANIZATIONS).param("size", "0"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400 Bad Request")
        void shouldFailWhenPageNumberIsNegative() throws Exception {
            // when & then
            mockMvc.perform(get(OrganizationApiEndpoints.ORGANIZATIONS).param("page", "-1"))
                    .andExpect(status().isBadRequest());
        }
    }
}
