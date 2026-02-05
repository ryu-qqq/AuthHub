package com.ryuqq.authhub.adapter.in.rest.permission.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
import com.ryuqq.authhub.adapter.in.rest.common.fixture.ErrorMapperApiFixture;
import com.ryuqq.authhub.adapter.in.rest.permission.PermissionApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.permission.controller.command.PermissionCommandController;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.CreatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.UpdatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.fixture.PermissionApiFixture;
import com.ryuqq.authhub.adapter.in.rest.permission.mapper.PermissionCommandApiMapper;
import com.ryuqq.authhub.application.permission.port.in.command.CreatePermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.command.DeletePermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.command.UpdatePermissionUseCase;
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
 * PermissionCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(PermissionCommandController.class)
@Import({ControllerTestSecurityConfig.class, PermissionCommandApiMapper.class})
@DisplayName("PermissionCommandController 테스트")
class PermissionCommandControllerTest extends RestDocsTestSupport {

    @MockBean private CreatePermissionUseCase createPermissionUseCase;

    @MockBean private UpdatePermissionUseCase updatePermissionUseCase;

    @MockBean private DeletePermissionUseCase deletePermissionUseCase;

    @Nested
    @DisplayName("POST /api/v1/auth/permissions - 권한 생성")
    class CreateTests {

        @Test
        @DisplayName("유효한 요청으로 권한을 생성한다")
        void shouldCreatePermissionSuccessfully() throws Exception {
            // given
            CreatePermissionApiRequest request = PermissionApiFixture.createPermissionRequest();
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            given(createPermissionUseCase.execute(any())).willReturn(permissionId);

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.permissionId").value(permissionId))
                    .andDo(
                            document(
                                    "permission/create",
                                    requestFields(
                                            fieldWithPath("serviceId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("서비스 ID (null이면 서비스 무관)")
                                                    .optional(),
                                            fieldWithPath("resource")
                                                    .type(JsonFieldType.STRING)
                                                    .description("리소스명 (필수, 2~50자, 소문자와 하이픈으로 구성)"),
                                            fieldWithPath("action")
                                                    .type(JsonFieldType.STRING)
                                                    .description("행위명 (필수, 2~50자, 소문자와 하이픈으로 구성)"),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("권한 설명 (선택, 500자 이하)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.permissionId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 Permission ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("resource가 빈 문자열이면 400 Bad Request")
        void shouldFailWhenResourceIsBlank() throws Exception {
            // given
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest("", "read");

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("action이 빈 문자열이면 400 Bad Request")
        void shouldFailWhenActionIsBlank() throws Exception {
            // given
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest("user", "");

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("resource가 소문자-하이픈 패턴이 아니면 400 Bad Request")
        void shouldFailWhenResourcePatternIsInvalid() throws Exception {
            // given
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest("USER_RESOURCE", "read");

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("action이 소문자-하이픈 패턴이 아니면 400 Bad Request")
        void shouldFailWhenActionPatternIsInvalid() throws Exception {
            // given
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest("user", "CREATE_ACTION");

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("resource가 최소 길이(2자)이면 성공한다")
        void shouldSucceedWhenResourceIsMinLength() throws Exception {
            // given
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest("ab", "read");
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            given(createPermissionUseCase.execute(any())).willReturn(permissionId);

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("resource가 최대 길이(50자)이면 성공한다")
        void shouldSucceedWhenResourceIsMaxLength() throws Exception {
            // given
            String maxLengthResource = "a".repeat(50);
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest(maxLengthResource, "read");
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            given(createPermissionUseCase.execute(any())).willReturn(permissionId);

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("resource가 최대 길이(50자)를 초과하면 400 Bad Request")
        void shouldFailWhenResourceExceedsMaxLength() throws Exception {
            // given
            String tooLongResource = "a".repeat(51);
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest(tooLongResource, "read");

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("action이 최소 길이(2자)이면 성공한다")
        void shouldSucceedWhenActionIsMinLength() throws Exception {
            // given
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest("user", "ab");
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            given(createPermissionUseCase.execute(any())).willReturn(permissionId);

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("action이 최대 길이(50자)이면 성공한다")
        void shouldSucceedWhenActionIsMaxLength() throws Exception {
            // given
            String maxLengthAction = "a".repeat(50);
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest("user", maxLengthAction);
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            given(createPermissionUseCase.execute(any())).willReturn(permissionId);

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("action이 최대 길이(50자)를 초과하면 400 Bad Request")
        void shouldFailWhenActionExceedsMaxLength() throws Exception {
            // given
            String tooLongAction = "a".repeat(51);
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest("user", tooLongAction);

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("resource가 숫자로 시작하면 400 Bad Request")
        void shouldFailWhenResourceStartsWithNumber() throws Exception {
            // given
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest("1user", "read");

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("action이 숫자로 시작하면 400 Bad Request")
        void shouldFailWhenActionStartsWithNumber() throws Exception {
            // given
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest("user", "1read");

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("description이 최대 길이(500자)이면 성공한다")
        void shouldSucceedWhenDescriptionIsMaxLength() throws Exception {
            // given
            String maxLengthDescription = "a".repeat(500);
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest(
                            "user", "read", maxLengthDescription);
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            given(createPermissionUseCase.execute(any())).willReturn(permissionId);

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("description이 최대 길이(500자)를 초과하면 400 Bad Request")
        void shouldFailWhenDescriptionExceedsMaxLength() throws Exception {
            // given
            String tooLongDescription = "a".repeat(501);
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest(
                            "user", "read", tooLongDescription);

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("serviceId가 null인 요청으로 권한을 생성한다")
        void shouldCreatePermissionWhenServiceIdIsNull() throws Exception {
            // given
            CreatePermissionApiRequest request =
                    new CreatePermissionApiRequest(
                            null,
                            PermissionApiFixture.defaultResource(),
                            PermissionApiFixture.defaultAction(),
                            PermissionApiFixture.defaultDescription());
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            given(createPermissionUseCase.execute(any())).willReturn(permissionId);

            // when & then
            mockMvc.perform(
                            post(PermissionApiEndpoints.PERMISSIONS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.permissionId").value(permissionId));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/permissions/{permissionId} - 권한 수정")
    class UpdateTests {

        @Test
        @DisplayName("유효한 요청으로 권한을 수정한다")
        void shouldUpdatePermissionSuccessfully() throws Exception {
            // given
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            UpdatePermissionApiRequest request = PermissionApiFixture.updatePermissionRequest();
            doNothing().when(updatePermissionUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(
                                            PermissionApiEndpoints.PERMISSIONS + "/{permissionId}",
                                            permissionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.permissionId").value(permissionId))
                    .andDo(
                            document(
                                    "permission/update",
                                    pathParameters(
                                            parameterWithName("permissionId")
                                                    .description("수정할 Permission ID")),
                                    requestFields(
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("권한 설명 (null이면 변경 안 함, 500자 이하)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.permissionId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수정된 Permission ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("description이 500자 초과이면 400 Bad Request")
        void shouldFailWhenDescriptionExceedsMaxLength() throws Exception {
            // given
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            String longDescription = "a".repeat(501);
            UpdatePermissionApiRequest request =
                    PermissionApiFixture.updatePermissionRequest(longDescription);

            // when & then
            mockMvc.perform(
                            put(
                                            PermissionApiEndpoints.PERMISSIONS + "/{permissionId}",
                                            permissionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("description이 null인 요청으로 권한을 수정한다")
        void shouldUpdatePermissionWhenDescriptionIsNull() throws Exception {
            // given
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            UpdatePermissionApiRequest request = new UpdatePermissionApiRequest(null);
            doNothing().when(updatePermissionUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(
                                            PermissionApiEndpoints.PERMISSIONS + "/{permissionId}",
                                            permissionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.permissionId").value(permissionId));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/auth/permissions/{permissionId} - 권한 삭제")
    class DeleteTests {

        @Test
        @DisplayName("유효한 요청으로 권한을 삭제한다")
        void shouldDeletePermissionSuccessfully() throws Exception {
            // given
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            doNothing().when(deletePermissionUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            delete(
                                    PermissionApiEndpoints.PERMISSIONS + "/{permissionId}",
                                    permissionId))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "permission/delete",
                                    pathParameters(
                                            parameterWithName("permissionId")
                                                    .description("삭제할 Permission ID"))));
        }

        @Test
        @DisplayName("권한이 존재하지 않으면 404 Not Found")
        void shouldFailWhenPermissionNotFound() throws Exception {
            // given
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            willThrow(ErrorMapperApiFixture.permissionNotFoundException())
                    .given(deletePermissionUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(
                            delete(
                                    PermissionApiEndpoints.PERMISSIONS + "/{permissionId}",
                                    permissionId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("시스템 권한은 삭제할 수 없어 403 Forbidden")
        void shouldFailWhenSystemPermissionNotDeletable() throws Exception {
            // given
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            willThrow(ErrorMapperApiFixture.systemPermissionNotDeletableException())
                    .given(deletePermissionUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(
                            delete(
                                    PermissionApiEndpoints.PERMISSIONS + "/{permissionId}",
                                    permissionId))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(403));
        }

        @Test
        @DisplayName("권한이 사용 중이면 409 Conflict")
        void shouldFailWhenPermissionInUse() throws Exception {
            // given
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            willThrow(ErrorMapperApiFixture.permissionInUseException())
                    .given(deletePermissionUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(
                            delete(
                                    PermissionApiEndpoints.PERMISSIONS + "/{permissionId}",
                                    permissionId))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(409));
        }
    }
}
