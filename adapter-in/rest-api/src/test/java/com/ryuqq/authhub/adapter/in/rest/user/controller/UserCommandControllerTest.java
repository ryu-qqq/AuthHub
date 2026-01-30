package com.ryuqq.authhub.adapter.in.rest.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.user.UserApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangePasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.fixture.UserApiFixture;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserCommandApiMapper;
import com.ryuqq.authhub.application.user.port.in.command.ChangePasswordUseCase;
import com.ryuqq.authhub.application.user.port.in.command.CreateUserUseCase;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserUseCase;
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
 * UserCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(UserCommandController.class)
@Import({ControllerTestSecurityConfig.class, UserCommandApiMapper.class})
@DisplayName("UserCommandController 테스트")
class UserCommandControllerTest extends RestDocsTestSupport {

    @MockBean private CreateUserUseCase createUserUseCase;

    @MockBean private UpdateUserUseCase updateUserUseCase;

    @MockBean private ChangePasswordUseCase changePasswordUseCase;

    @Nested
    @DisplayName("POST /api/v1/auth/users - 사용자 생성")
    class CreateUserTests {

        @Test
        @DisplayName("유효한 요청으로 사용자를 생성한다")
        void shouldCreateUserSuccessfully() throws Exception {
            // given
            CreateUserApiRequest request = UserApiFixture.createUserRequest();
            String expectedUserId = UserApiFixture.defaultUserId();
            given(createUserUseCase.execute(any())).willReturn(expectedUserId);

            // when & then
            mockMvc.perform(
                            post(UserApiEndpoints.USERS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(expectedUserId))
                    .andDo(
                            document(
                                    "user/create",
                                    requestFields(
                                            fieldWithPath("organizationId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("소속 조직 ID (필수)"),
                                            fieldWithPath("identifier")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "로그인 식별자 - 이메일 또는 사용자명 (필수, 4-100자)"),
                                            fieldWithPath("phoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호 (선택)")
                                                    .optional(),
                                            fieldWithPath("password")
                                                    .type(JsonFieldType.STRING)
                                                    .description("비밀번호 (필수, 8-100자)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data.userId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성된 사용자 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("식별자가 없으면 400 Bad Request")
        void shouldReturn400WhenIdentifierIsBlank() throws Exception {
            // given
            CreateUserApiRequest request =
                    new CreateUserApiRequest(
                            UserApiFixture.defaultOrganizationId(),
                            "",
                            "010-1234-5678",
                            "password123!");

            // when & then
            mockMvc.perform(
                            post(UserApiEndpoints.USERS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("비밀번호가 8자 미만이면 400 Bad Request")
        void shouldReturn400WhenPasswordTooShort() throws Exception {
            // given
            CreateUserApiRequest request =
                    new CreateUserApiRequest(
                            UserApiFixture.defaultOrganizationId(),
                            "testuser@example.com",
                            "010-1234-5678",
                            "short");

            // when & then
            mockMvc.perform(
                            post(UserApiEndpoints.USERS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/users/{userId} - 사용자 정보 수정")
    class UpdateUserTests {

        @Test
        @DisplayName("유효한 요청으로 사용자 정보를 수정한다")
        void shouldUpdateUserSuccessfully() throws Exception {
            // given
            String userId = UserApiFixture.defaultUserId();
            UpdateUserApiRequest request = UserApiFixture.updateUserRequest();
            willDoNothing().given(updateUserUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(UserApiEndpoints.USERS + "/{userId}", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andDo(
                            document(
                                    "user/update",
                                    pathParameters(
                                            parameterWithName("userId").description("수정할 사용자 ID")),
                                    requestFields(
                                            fieldWithPath("phoneNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("전화번호 (null이면 변경 안 함)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data.userId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정된 사용자 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/users/{userId}/password - 비밀번호 변경")
    class ChangePasswordTests {

        @Test
        @DisplayName("유효한 요청으로 비밀번호를 변경한다")
        void shouldChangePasswordSuccessfully() throws Exception {
            // given
            String userId = UserApiFixture.defaultUserId();
            ChangePasswordApiRequest request = UserApiFixture.changePasswordRequest();
            willDoNothing().given(changePasswordUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(UserApiEndpoints.USERS + "/{userId}/password", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(
                            document(
                                    "user/change-password",
                                    pathParameters(
                                            parameterWithName("userId")
                                                    .description("비밀번호를 변경할 사용자 ID")),
                                    requestFields(
                                            fieldWithPath("currentPassword")
                                                    .type(JsonFieldType.STRING)
                                                    .description("현재 비밀번호 (필수)"),
                                            fieldWithPath("newPassword")
                                                    .type(JsonFieldType.STRING)
                                                    .description("새 비밀번호 (필수, 8-100자)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("데이터 없음"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("현재 비밀번호가 없으면 400 Bad Request")
        void shouldReturn400WhenCurrentPasswordIsBlank() throws Exception {
            // given
            String userId = UserApiFixture.defaultUserId();
            ChangePasswordApiRequest request = new ChangePasswordApiRequest("", "newPassword123!");

            // when & then
            mockMvc.perform(
                            put(UserApiEndpoints.USERS + "/{userId}/password", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("새 비밀번호가 8자 미만이면 400 Bad Request")
        void shouldReturn400WhenNewPasswordTooShort() throws Exception {
            // given
            String userId = UserApiFixture.defaultUserId();
            ChangePasswordApiRequest request =
                    new ChangePasswordApiRequest("currentPassword123!", "short");

            // when & then
            mockMvc.perform(
                            put(UserApiEndpoints.USERS + "/{userId}/password", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
