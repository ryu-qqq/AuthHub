package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.internal.fixture.InternalApiFixture;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalUserContextApiMapper;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import com.ryuqq.authhub.application.token.port.in.query.GetMyContextUseCase;
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
 * InternalUserContextController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(InternalUserContextController.class)
@Import({ControllerTestSecurityConfig.class, InternalUserContextApiMapper.class})
@DisplayName("InternalUserContextController 테스트")
class InternalUserContextControllerTest extends RestDocsTestSupport {

    @MockBean private GetMyContextUseCase getMyContextUseCase;

    @Nested
    @DisplayName("GET /api/v1/internal/users/{userId}/context - 사용자 컨텍스트 조회")
    class GetUserContextTests {

        @Test
        @DisplayName("유효한 요청으로 사용자 컨텍스트를 조회한다")
        void shouldGetUserContextSuccessfully() throws Exception {
            // given
            String userId = InternalApiFixture.defaultUserId();
            MyContextResponse result = InternalApiFixture.createMyContextResponse(userId);
            given(getMyContextUseCase.execute(userId)).willReturn(result);

            // when & then
            mockMvc.perform(
                            get(
                                    InternalApiEndpoints.USERS + InternalApiEndpoints.USER_CONTEXT,
                                    userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"))
                    .andExpect(jsonPath("$.data.name").value("테스트 사용자"))
                    .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
                    .andExpect(jsonPath("$.data.tenant.id").exists())
                    .andExpect(jsonPath("$.data.tenant.name").exists())
                    .andExpect(jsonPath("$.data.organization.id").exists())
                    .andExpect(jsonPath("$.data.organization.name").exists())
                    .andExpect(jsonPath("$.data.roles").isArray())
                    .andExpect(jsonPath("$.data.permissions").isArray())
                    .andDo(
                            document(
                                    "internal/user-context/get",
                                    pathParameters(
                                            parameterWithName("userId")
                                                    .description("사용자 ID (UUID)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.userId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 ID"),
                                            fieldWithPath("data.email")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이메일"),
                                            fieldWithPath("data.name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사용자 이름"),
                                            fieldWithPath("data.phoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호"),
                                            fieldWithPath("data.tenant")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("테넌트 정보"),
                                            fieldWithPath("data.tenant.id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 ID"),
                                            fieldWithPath("data.tenant.name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 이름"),
                                            fieldWithPath("data.organization")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("조직 정보"),
                                            fieldWithPath("data.organization.id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조직 ID"),
                                            fieldWithPath("data.organization.name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조직 이름"),
                                            fieldWithPath("data.roles")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("역할 목록"),
                                            fieldWithPath("data.roles[].id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("역할 ID"),
                                            fieldWithPath("data.roles[].name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("역할 이름"),
                                            fieldWithPath("data.permissions")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("권한 키 목록"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("역할/권한이 없는 사용자는 빈 배열을 반환한다")
        void shouldGetEmptyContextSuccessfully() throws Exception {
            // given
            String userId = InternalApiFixture.defaultUserId();
            MyContextResponse result =
                    new MyContextResponse(
                            userId,
                            "empty@example.com",
                            "빈 사용자",
                            InternalApiFixture.defaultTenantId(),
                            InternalApiFixture.defaultTenantName(),
                            "org-001",
                            InternalApiFixture.defaultOrgName(),
                            null,
                            List.of(),
                            List.of());
            given(getMyContextUseCase.execute(userId)).willReturn(result);

            // when & then
            mockMvc.perform(
                            get(
                                    InternalApiEndpoints.USERS + InternalApiEndpoints.USER_CONTEXT,
                                    userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(jsonPath("$.data.roles").isEmpty())
                    .andExpect(jsonPath("$.data.permissions").isEmpty());
        }
    }
}
