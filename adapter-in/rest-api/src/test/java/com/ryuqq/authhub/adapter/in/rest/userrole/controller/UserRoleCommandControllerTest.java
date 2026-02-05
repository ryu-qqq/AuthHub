package com.ryuqq.authhub.adapter.in.rest.userrole.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
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
import com.ryuqq.authhub.adapter.in.rest.common.fixture.ErrorMapperApiFixture;
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

        @Test
        @DisplayName("사용자-역할 관계를 찾을 수 없으면 404 Not Found")
        void shouldFailWhenUserRoleNotFound() throws Exception {
            // given
            String userId = UserRoleApiFixture.defaultUserId();
            AssignUserRoleApiRequest request = UserRoleApiFixture.assignUserRoleRequest();
            willThrow(ErrorMapperApiFixture.userRoleNotFoundException())
                    .given(assignUserRoleUseCase)
                    .assign(any());

            // when & then
            mockMvc.perform(
                            post(UserRoleApiEndpoints.BASE + "/{userId}/roles", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("이미 할당된 역할이면 409 Conflict")
        void shouldFailWhenDuplicateUserRole() throws Exception {
            // given
            String userId = UserRoleApiFixture.defaultUserId();
            AssignUserRoleApiRequest request = UserRoleApiFixture.assignUserRoleRequest();
            willThrow(ErrorMapperApiFixture.duplicateUserRoleException())
                    .given(assignUserRoleUseCase)
                    .assign(any());

            // when & then
            mockMvc.perform(
                            post(UserRoleApiEndpoints.BASE + "/{userId}/roles", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(409));
        }

        // Note: @PreAuthorize("@access.hasPermission('user', 'update')") 테스트는
        // ControllerTestSecurityConfig의 TestAccessChecker가 항상 true를 반환하므로
        // 단위 테스트에서는 테스트 불가. 통합 테스트에서 검증 필요.
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

        @Test
        @DisplayName("사용자-역할 관계를 찾을 수 없으면 404 Not Found")
        void shouldFailWhenUserRoleNotFound() throws Exception {
            // given
            String userId = UserRoleApiFixture.defaultUserId();
            RevokeUserRoleApiRequest request = UserRoleApiFixture.revokeUserRoleRequest();
            willThrow(ErrorMapperApiFixture.userRoleNotFoundException())
                    .given(revokeUserRoleUseCase)
                    .revoke(any());

            // when & then
            mockMvc.perform(
                            delete(UserRoleApiEndpoints.BASE + "/{userId}/roles", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryValueTests {

        @Test
        @DisplayName("매우 긴 userId로도 정상 처리된다")
        void shouldHandleVeryLongUserId() throws Exception {
            // given
            String veryLongUserId = "a".repeat(100); // 매우 긴 userId
            AssignUserRoleApiRequest request = UserRoleApiFixture.assignUserRoleRequest();
            doNothing().when(assignUserRoleUseCase).assign(any());

            // when & then
            mockMvc.perform(
                            post(UserRoleApiEndpoints.BASE + "/{userId}/roles", veryLongUserId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("큰 roleIds 리스트로도 정상 처리된다")
        void shouldHandleLargeRoleIdsList() throws Exception {
            // given
            String userId = UserRoleApiFixture.defaultUserId();
            List<Long> largeRoleIdsList = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
            AssignUserRoleApiRequest request =
                    UserRoleApiFixture.assignUserRoleRequest(largeRoleIdsList);
            doNothing().when(assignUserRoleUseCase).assign(any());

            // when & then
            mockMvc.perform(
                            post(UserRoleApiEndpoints.BASE + "/{userId}/roles", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }
}
