package com.ryuqq.authhub.adapter.in.rest.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.user.UserApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.user.fixture.UserApiFixture;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserQueryApiMapper;
import com.ryuqq.authhub.application.user.dto.response.UserPageResult;
import com.ryuqq.authhub.application.user.dto.response.UserResult;
import com.ryuqq.authhub.application.user.port.in.query.GetUserUseCase;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersUseCase;
import com.ryuqq.authhub.domain.common.vo.PageMeta;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
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
 * UserQueryController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(UserQueryController.class)
@Import({ControllerTestSecurityConfig.class, UserQueryApiMapper.class})
@DisplayName("UserQueryController 테스트")
class UserQueryControllerTest extends RestDocsTestSupport {

    @MockBean private GetUserUseCase getUserUseCase;

    @MockBean private SearchUsersUseCase searchUsersUseCase;

    @Nested
    @DisplayName("GET /api/v1/auth/users/{userId} - 사용자 단건 조회")
    class GetByIdTests {

        @Test
        @DisplayName("유효한 ID로 사용자를 조회한다")
        void shouldGetUserByIdSuccessfully() throws Exception {
            // given
            String userId = UserApiFixture.defaultUserId();
            UserResult result =
                    new UserResult(
                            userId,
                            UserApiFixture.defaultOrganizationId(),
                            UserApiFixture.defaultIdentifier(),
                            "010-1234-5678",
                            "ACTIVE",
                            UserApiFixture.fixedTime(),
                            UserApiFixture.fixedTime());
            given(getUserUseCase.execute(anyString())).willReturn(result);

            // when & then
            mockMvc.perform(get(UserApiEndpoints.USERS + "/{userId}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(
                            jsonPath("$.data.identifier").value(UserApiFixture.defaultIdentifier()))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                    .andDo(
                            document(
                                    "user/get-by-id",
                                    pathParameters(
                                            parameterWithName("userId").description("조회할 사용자 ID")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data.userId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 ID"),
                                            fieldWithPath("data.organizationId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("소속 조직 ID"),
                                            fieldWithPath("data.identifier")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로그인 식별자"),
                                            fieldWithPath("data.phoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호"),
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "사용자 상태 (ACTIVE, INACTIVE, SUSPENDED)"),
                                            fieldWithPath("data.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성 시각"),
                                            fieldWithPath("data.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정 시각"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("사용자를 찾을 수 없으면 404 Not Found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // given
            String userId = UserApiFixture.defaultUserId();
            willThrow(new UserNotFoundException(userId)).given(getUserUseCase).execute(anyString());

            // when & then
            mockMvc.perform(get(UserApiEndpoints.USERS + "/{userId}", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/users - 사용자 목록 조회")
    class SearchTests {

        @Test
        @DisplayName("검색 조건으로 사용자 목록을 조회한다")
        void shouldSearchUsersSuccessfully() throws Exception {
            // given
            String userId = UserApiFixture.defaultUserId();
            UserResult user =
                    new UserResult(
                            userId,
                            UserApiFixture.defaultOrganizationId(),
                            UserApiFixture.defaultIdentifier(),
                            "010-1234-5678",
                            "ACTIVE",
                            UserApiFixture.fixedTime(),
                            UserApiFixture.fixedTime());
            PageMeta pageMeta = PageMeta.of(0, 20, 1L);
            UserPageResult pageResult = new UserPageResult(List.of(user), pageMeta);
            given(searchUsersUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            get(UserApiEndpoints.USERS)
                                    .param("organizationId", UserApiFixture.defaultOrganizationId())
                                    .param("status", "ACTIVE")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].userId").value(userId))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andDo(
                            document(
                                    "user/search",
                                    queryParameters(
                                            parameterWithName("organizationId")
                                                    .description("소속 조직 ID (선택)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어 - 식별자 또는 전화번호 (선택)")
                                                    .optional(),
                                            parameterWithName("status")
                                                    .description(
                                                            "상태 필터 - ACTIVE, INACTIVE, SUSPENDED"
                                                                    + " (선택)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터 시작, 기본값: 0)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (기본값: 20)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data.content")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("사용자 목록"),
                                            fieldWithPath("data.content[].userId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 ID"),
                                            fieldWithPath("data.content[].organizationId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("소속 조직 ID"),
                                            fieldWithPath("data.content[].identifier")
                                                    .type(JsonFieldType.STRING)
                                                    .description("로그인 식별자"),
                                            fieldWithPath("data.content[].phoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 상태"),
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
        void shouldSearchAllUsersWithoutCondition() throws Exception {
            // given
            PageMeta pageMeta = PageMeta.of(0, 20, 0L);
            UserPageResult pageResult = new UserPageResult(List.of(), pageMeta);
            given(searchUsersUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(get(UserApiEndpoints.USERS))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과 조회를 처리한다")
        void shouldHandleEmptyResult() throws Exception {
            // given
            PageMeta pageMeta = PageMeta.of(0, 20, 0L);
            UserPageResult pageResult = new UserPageResult(List.of(), pageMeta);
            given(searchUsersUseCase.execute(any())).willReturn(pageResult);

            // when & then
            mockMvc.perform(
                            get(UserApiEndpoints.USERS)
                                    .param("organizationId", UserApiFixture.defaultOrganizationId())
                                    .param("status", "ACTIVE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }

        @Test
        @DisplayName("다양한 파라미터 조합으로 검색한다")
        void shouldSearchWithVariousParameterCombinations() throws Exception {
            // given
            String userId = UserApiFixture.defaultUserId();
            UserResult user =
                    new UserResult(
                            userId,
                            UserApiFixture.defaultOrganizationId(),
                            UserApiFixture.defaultIdentifier(),
                            "010-1234-5678",
                            "ACTIVE",
                            UserApiFixture.fixedTime(),
                            UserApiFixture.fixedTime());
            PageMeta pageMeta = PageMeta.of(0, 20, 1L);
            UserPageResult pageResult = new UserPageResult(List.of(user), pageMeta);
            given(searchUsersUseCase.execute(any())).willReturn(pageResult);

            // when & then - organizationId + status 조합
            mockMvc.perform(
                            get(UserApiEndpoints.USERS)
                                    .param("organizationId", UserApiFixture.defaultOrganizationId())
                                    .param("status", "ACTIVE")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].userId").value(userId));

            // when & then - searchWord 조합
            mockMvc.perform(
                            get(UserApiEndpoints.USERS)
                                    .param("searchWord", UserApiFixture.defaultIdentifier())
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("page/size 경계값을 처리한다")
        void shouldHandlePageSizeBoundaryValues() throws Exception {
            // given
            PageMeta pageMetaFirst = PageMeta.of(0, 1, 1L);
            UserResult user =
                    new UserResult(
                            UserApiFixture.defaultUserId(),
                            UserApiFixture.defaultOrganizationId(),
                            UserApiFixture.defaultIdentifier(),
                            "010-1234-5678",
                            "ACTIVE",
                            UserApiFixture.fixedTime(),
                            UserApiFixture.fixedTime());
            UserPageResult pageResultFirst = new UserPageResult(List.of(user), pageMetaFirst);
            given(searchUsersUseCase.execute(any())).willReturn(pageResultFirst);

            // when & then - page=0, size=1 (최소값)
            mockMvc.perform(get(UserApiEndpoints.USERS).param("page", "0").param("size", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(1));

            // given - size=100 (최대값)
            PageMeta pageMetaMax = PageMeta.of(0, 100, 1L);
            UserPageResult pageResultMax = new UserPageResult(List.of(user), pageMetaMax);
            given(searchUsersUseCase.execute(any())).willReturn(pageResultMax);

            // when & then - size=100
            mockMvc.perform(get(UserApiEndpoints.USERS).param("page", "0").param("size", "100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.size").value(100));
        }
    }
}
