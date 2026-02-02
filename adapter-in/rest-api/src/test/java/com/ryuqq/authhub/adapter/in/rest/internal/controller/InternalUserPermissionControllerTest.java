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
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalUserPermissionApiMapper;
import com.ryuqq.authhub.application.userrole.dto.response.UserPermissionsResult;
import com.ryuqq.authhub.application.userrole.port.in.query.GetUserPermissionsUseCase;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.JsonFieldType;

/**
 * InternalUserPermissionController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(InternalUserPermissionController.class)
@Import({ControllerTestSecurityConfig.class, InternalUserPermissionApiMapper.class})
@DisplayName("InternalUserPermissionController 테스트")
class InternalUserPermissionControllerTest extends RestDocsTestSupport {

    @MockBean private GetUserPermissionsUseCase getUserPermissionsUseCase;

    @Nested
    @DisplayName("GET /api/v1/internal/users/{userId}/permissions - 사용자 권한 조회")
    class GetPermissionsTests {

        @Test
        @DisplayName("유효한 요청으로 역할/권한이 있는 사용자 권한을 조회한다")
        void shouldGetPermissionsSuccessfully() throws Exception {
            // given
            String userId = InternalApiFixture.defaultUserId();
            Set<String> roles = InternalApiFixture.defaultRoles();
            Set<String> permissions = InternalApiFixture.defaultPermissions();

            UserPermissionsResult result = new UserPermissionsResult(userId, roles, permissions);
            given(getUserPermissionsUseCase.getByUserId(userId)).willReturn(result);

            // when & then
            mockMvc.perform(
                            get(
                                    InternalApiEndpoints.USERS
                                            + InternalApiEndpoints.USER_PERMISSIONS,
                                    userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(jsonPath("$.data.roles").isArray())
                    .andExpect(jsonPath("$.data.permissions").isArray())
                    .andDo(
                            document(
                                    "internal/user-permissions/get",
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
                                            fieldWithPath("data.roles")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("역할 이름 목록"),
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
        void shouldGetEmptyPermissionsSuccessfully() throws Exception {
            // given
            String userId = InternalApiFixture.defaultUserId();
            UserPermissionsResult result = new UserPermissionsResult(userId, Set.of(), Set.of());
            given(getUserPermissionsUseCase.getByUserId(userId)).willReturn(result);

            // when & then
            mockMvc.perform(
                            get(
                                    InternalApiEndpoints.USERS
                                            + InternalApiEndpoints.USER_PERMISSIONS,
                                    userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(jsonPath("$.data.roles").isEmpty())
                    .andExpect(jsonPath("$.data.permissions").isEmpty());
        }

        @Test
        @DisplayName("단일 역할만 있는 사용자 권한을 조회한다")
        void shouldGetSingleRolePermissionsSuccessfully() throws Exception {
            // given
            String userId = InternalApiFixture.defaultUserId();
            Set<String> roles = Set.of("VIEWER");
            Set<String> permissions = Set.of("product:read");

            UserPermissionsResult result = new UserPermissionsResult(userId, roles, permissions);
            given(getUserPermissionsUseCase.getByUserId(userId)).willReturn(result);

            // when & then
            mockMvc.perform(
                            get(
                                    InternalApiEndpoints.USERS
                                            + InternalApiEndpoints.USER_PERMISSIONS,
                                    userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(jsonPath("$.data.roles.length()").value(1))
                    .andExpect(jsonPath("$.data.permissions.length()").value(1));
        }
    }
}
