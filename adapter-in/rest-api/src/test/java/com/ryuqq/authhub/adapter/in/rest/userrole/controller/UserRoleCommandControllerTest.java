package com.ryuqq.authhub.adapter.in.rest.userrole.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.userrole.UserRoleApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.userrole.dto.request.AssignUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.userrole.dto.request.RevokeUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.userrole.fixture.UserRoleApiFixture;
import com.ryuqq.authhub.adapter.in.rest.userrole.mapper.UserRoleCommandApiMapper;
import com.ryuqq.authhub.application.userrole.port.in.command.AssignUserRoleUseCase;
import com.ryuqq.authhub.application.userrole.port.in.command.RevokeUserRoleUseCase;
import java.util.List;
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
 * UserRoleCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(UserRoleCommandController.class)
@Import({ControllerTestSecurityConfig.class, UserRoleCommandApiMapper.class})
@DisplayName("UserRoleCommandController 테스트")
class UserRoleCommandControllerTest extends RestDocsTestSupport {

    @MockBean private AssignUserRoleUseCase assignUserRoleUseCase;

    @MockBean private RevokeUserRoleUseCase revokeUserRoleUseCase;

    @Nested
    @DisplayName("POST /api/v1/auth/users/{userId}/roles - 사용자에게 역할 할당")
    class AssignRolesTests {

        @Test
        @DisplayName("유효한 요청으로 사용자에게 역할을 할당한다")
        void shouldAssignRolesSuccessfully() throws Exception {
            // given
            String userId = UserRoleApiFixture.defaultUserId();
            AssignUserRoleApiRequest request = UserRoleApiFixture.assignUserRoleRequest();
            doNothing().when(assignUserRoleUseCase).assign(any());

            // when & then
            mockMvc.perform(
                            post(UserRoleApiEndpoints.BASE + "/{userId}/roles", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(
                            document(
                                    "user-role/assign",
                                    pathParameters(
                                            parameterWithName("userId").description("사용자 ID")),
                                    requestFields(
                                            fieldWithPath("roleIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("할당할 역할 ID 목록 (필수, 1개 이상)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (없음)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("역할 ID 목록이 비어있으면 400 Bad Request")
        void shouldFailWhenRoleIdsIsEmpty() throws Exception {
            // given
            String userId = UserRoleApiFixture.defaultUserId();
            AssignUserRoleApiRequest request = new AssignUserRoleApiRequest(List.of());

            // when & then
            mockMvc.perform(
                            post(UserRoleApiEndpoints.BASE + "/{userId}/roles", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/auth/users/{userId}/roles - 사용자로부터 역할 철회")
    class RevokeRolesTests {

        @Test
        @DisplayName("유효한 요청으로 사용자로부터 역할을 철회한다")
        void shouldRevokeRolesSuccessfully() throws Exception {
            // given
            String userId = UserRoleApiFixture.defaultUserId();
            RevokeUserRoleApiRequest request = UserRoleApiFixture.revokeUserRoleRequest();
            doNothing().when(revokeUserRoleUseCase).revoke(any());

            // when & then
            mockMvc.perform(
                            delete(UserRoleApiEndpoints.BASE + "/{userId}/roles", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "user-role/revoke",
                                    pathParameters(
                                            parameterWithName("userId").description("사용자 ID")),
                                    requestFields(
                                            fieldWithPath("roleIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("철회할 역할 ID 목록 (필수, 1개 이상)"))));
        }

        @Test
        @DisplayName("역할 ID 목록이 비어있으면 400 Bad Request")
        void shouldFailWhenRoleIdsIsEmpty() throws Exception {
            // given
            String userId = UserRoleApiFixture.defaultUserId();
            RevokeUserRoleApiRequest request = new RevokeUserRoleApiRequest(List.of());

            // when & then
            mockMvc.perform(
                            delete(UserRoleApiEndpoints.BASE + "/{userId}/roles", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
