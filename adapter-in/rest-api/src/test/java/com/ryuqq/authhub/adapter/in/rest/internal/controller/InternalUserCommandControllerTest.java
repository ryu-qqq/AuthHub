package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalUserApiMapper;
import com.ryuqq.authhub.adapter.in.rest.user.dto.request.ChangePasswordApiRequest;
import com.ryuqq.authhub.application.user.port.in.command.ChangePasswordUseCase;
import com.ryuqq.authhub.application.user.port.in.command.CreateUserWithRolesUseCase;
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
 * InternalUserCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(InternalUserCommandController.class)
@Import({ControllerTestSecurityConfig.class, InternalUserApiMapper.class})
@DisplayName("InternalUserCommandController 테스트")
class InternalUserCommandControllerTest extends RestDocsTestSupport {

    @MockBean private CreateUserWithRolesUseCase createUserWithRolesUseCase;

    @MockBean private ChangePasswordUseCase changePasswordUseCase;

    @Nested
    @DisplayName("PUT /api/v1/internal/users/{userId}/password - 비밀번호 변경")
    class ChangePasswordTests {

        @Test
        @DisplayName("유효한 요청으로 비밀번호를 변경한다")
        void shouldChangePasswordSuccessfully() throws Exception {
            // given
            String userId = "test-user-id";
            ChangePasswordApiRequest request =
                    new ChangePasswordApiRequest("currentPassword123", "newPassword456");
            willDoNothing().given(changePasswordUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(
                                            InternalApiEndpoints.USERS
                                                    + InternalApiEndpoints.USER_PASSWORD,
                                            userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(
                            document(
                                    "internal/user-command/change-password",
                                    pathParameters(
                                            parameterWithName("userId")
                                                    .description("사용자 ID (UUID)")),
                                    requestFields(
                                            fieldWithPath("currentPassword")
                                                    .type(JsonFieldType.STRING)
                                                    .description("현재 비밀번호"),
                                            fieldWithPath("newPassword")
                                                    .type(JsonFieldType.STRING)
                                                    .description("새 비밀번호 (8자 이상)"))));
        }

        @Test
        @DisplayName("현재 비밀번호가 비어있으면 400을 반환한다")
        void shouldReturn400WhenCurrentPasswordIsBlank() throws Exception {
            // given
            String userId = "test-user-id";
            String requestBody = "{\"currentPassword\": \"\", \"newPassword\": \"newPassword456\"}";

            // when & then
            mockMvc.perform(
                            put(
                                            InternalApiEndpoints.USERS
                                                    + InternalApiEndpoints.USER_PASSWORD,
                                            userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("새 비밀번호가 8자 미만이면 400을 반환한다")
        void shouldReturn400WhenNewPasswordIsTooShort() throws Exception {
            // given
            String userId = "test-user-id";
            String requestBody =
                    "{\"currentPassword\": \"currentPassword123\", \"newPassword\": \"short\"}";

            // when & then
            mockMvc.perform(
                            put(
                                            InternalApiEndpoints.USERS
                                                    + InternalApiEndpoints.USER_PASSWORD,
                                            userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }
}
