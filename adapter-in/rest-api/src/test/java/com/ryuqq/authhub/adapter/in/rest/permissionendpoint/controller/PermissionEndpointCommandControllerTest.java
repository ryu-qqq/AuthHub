package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.controller;

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
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.PermissionEndpointApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.CreatePermissionEndpointApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.UpdatePermissionEndpointApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.fixture.PermissionEndpointApiFixture;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.mapper.PermissionEndpointCommandApiMapper;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.CreatePermissionEndpointUseCase;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.DeletePermissionEndpointUseCase;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.UpdatePermissionEndpointUseCase;
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
 * PermissionEndpointCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(PermissionEndpointCommandController.class)
@Import({ControllerTestSecurityConfig.class, PermissionEndpointCommandApiMapper.class})
@DisplayName("PermissionEndpointCommandController 테스트")
class PermissionEndpointCommandControllerTest extends RestDocsTestSupport {

    @MockBean private CreatePermissionEndpointUseCase createPermissionEndpointUseCase;

    @MockBean private UpdatePermissionEndpointUseCase updatePermissionEndpointUseCase;

    @MockBean private DeletePermissionEndpointUseCase deletePermissionEndpointUseCase;

    @Nested
    @DisplayName("POST /api/v1/permission-endpoints - PermissionEndpoint 생성")
    class CreateTests {

        @Test
        @DisplayName("유효한 요청으로 PermissionEndpoint를 생성한다")
        void shouldCreatePermissionEndpointSuccessfully() throws Exception {
            // given
            CreatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture.createPermissionEndpointRequest();
            Long createdId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            given(createPermissionEndpointUseCase.create(any())).willReturn(createdId);

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(createdId))
                    .andDo(
                            document(
                                    "permission-endpoint/create",
                                    requestFields(
                                            fieldWithPath("permissionId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("연결할 권한 ID (필수, 양수)"),
                                            fieldWithPath("serviceName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("서비스 이름 (필수, 100자 이하)"),
                                            fieldWithPath("urlPattern")
                                                    .type(JsonFieldType.STRING)
                                                    .description("URL 패턴 (필수, '/'로 시작, 500자 이하)"),
                                            fieldWithPath("httpMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "HTTP 메서드 (필수,"
                                                                + " GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS)"),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명 (선택, 500자 이하)")
                                                    .optional(),
                                            fieldWithPath("isPublic")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("공개 엔드포인트 여부 (선택, 기본값 false)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 PermissionEndpoint ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("permissionId가 null이면 400 Bad Request")
        void shouldFailWhenPermissionIdIsNull() throws Exception {
            // given
            CreatePermissionEndpointApiRequest request =
                    new CreatePermissionEndpointApiRequest(
                            null,
                            PermissionEndpointApiFixture.defaultServiceName(),
                            PermissionEndpointApiFixture.defaultUrlPattern(),
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            PermissionEndpointApiFixture.defaultDescription(),
                            PermissionEndpointApiFixture.defaultIsPublic());

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("urlPattern이 빈 문자열이면 400 Bad Request")
        void shouldFailWhenUrlPatternIsBlank() throws Exception {
            // given
            CreatePermissionEndpointApiRequest request =
                    new CreatePermissionEndpointApiRequest(
                            PermissionEndpointApiFixture.defaultPermissionId(),
                            PermissionEndpointApiFixture.defaultServiceName(),
                            "",
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            PermissionEndpointApiFixture.defaultDescription(),
                            PermissionEndpointApiFixture.defaultIsPublic());

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("urlPattern이 '/'로 시작하지 않으면 400 Bad Request")
        void shouldFailWhenUrlPatternDoesNotStartWithSlash() throws Exception {
            // given
            CreatePermissionEndpointApiRequest request =
                    new CreatePermissionEndpointApiRequest(
                            PermissionEndpointApiFixture.defaultPermissionId(),
                            PermissionEndpointApiFixture.defaultServiceName(),
                            "api/v1/users",
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            PermissionEndpointApiFixture.defaultDescription(),
                            PermissionEndpointApiFixture.defaultIsPublic());

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("httpMethod가 유효하지 않으면 400 Bad Request")
        void shouldFailWhenHttpMethodIsInvalid() throws Exception {
            // given
            CreatePermissionEndpointApiRequest request =
                    new CreatePermissionEndpointApiRequest(
                            PermissionEndpointApiFixture.defaultPermissionId(),
                            PermissionEndpointApiFixture.defaultServiceName(),
                            PermissionEndpointApiFixture.defaultUrlPattern(),
                            "INVALID",
                            PermissionEndpointApiFixture.defaultDescription(),
                            PermissionEndpointApiFixture.defaultIsPublic());

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("isPublic이 제공되지 않으면 기본값 false로 처리한다")
        void shouldUseDefaultFalseWhenIsPublicNotProvided() throws Exception {
            // given
            CreatePermissionEndpointApiRequest request =
                    new CreatePermissionEndpointApiRequest(
                            PermissionEndpointApiFixture.defaultPermissionId(),
                            PermissionEndpointApiFixture.defaultServiceName(),
                            PermissionEndpointApiFixture.defaultUrlPattern(),
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            PermissionEndpointApiFixture.defaultDescription(),
                            false); // isPublic is boolean primitive, defaults to false
            Long createdId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            given(createPermissionEndpointUseCase.create(any())).willReturn(createdId);

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(createdId));
        }

        @Test
        @DisplayName("serviceName이 100자이면 유효하다")
        void shouldAcceptServiceNameWithMaxLength() throws Exception {
            // given
            String maxLengthServiceName = "a".repeat(100);
            CreatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture
                            .createPermissionEndpointRequestWithMaxLengthServiceName(
                                    maxLengthServiceName);
            Long createdId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            given(createPermissionEndpointUseCase.create(any())).willReturn(createdId);

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(createdId));
        }

        @Test
        @DisplayName("serviceName이 101자이면 400 Bad Request")
        void shouldFailWhenServiceNameExceedsMaxLength() throws Exception {
            // given
            String tooLongServiceName = "a".repeat(101);
            CreatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture
                            .createPermissionEndpointRequestWithMaxLengthServiceName(
                                    tooLongServiceName);

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("urlPattern이 500자이면 유효하다")
        void shouldAcceptUrlPatternWithMaxLength() throws Exception {
            // given
            String maxLengthUrlPattern = "/" + "a".repeat(499);
            CreatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture
                            .createPermissionEndpointRequestWithMaxLengthUrlPattern(
                                    maxLengthUrlPattern);
            Long createdId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            given(createPermissionEndpointUseCase.create(any())).willReturn(createdId);

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(createdId));
        }

        @Test
        @DisplayName("urlPattern이 501자이면 400 Bad Request")
        void shouldFailWhenUrlPatternExceedsMaxLength() throws Exception {
            // given
            String tooLongUrlPattern = "/" + "a".repeat(500);
            CreatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture
                            .createPermissionEndpointRequestWithMaxLengthUrlPattern(
                                    tooLongUrlPattern);

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("description이 500자이면 유효하다")
        void shouldAcceptDescriptionWithMaxLength() throws Exception {
            // given
            String maxLengthDescription = "a".repeat(500);
            CreatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture
                            .createPermissionEndpointRequestWithMaxLengthDescription(
                                    maxLengthDescription);
            Long createdId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            given(createPermissionEndpointUseCase.create(any())).willReturn(createdId);

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(createdId));
        }

        @Test
        @DisplayName("description이 501자이면 400 Bad Request")
        void shouldFailWhenDescriptionExceedsMaxLength() throws Exception {
            // given
            String tooLongDescription = "a".repeat(501);
            CreatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture
                            .createPermissionEndpointRequestWithMaxLengthDescription(
                                    tooLongDescription);

            // when & then
            mockMvc.perform(
                            post(PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/permission-endpoints/{permissionEndpointId} - PermissionEndpoint 수정")
    class UpdateTests {

        @Test
        @DisplayName("유효한 요청으로 PermissionEndpoint를 수정한다")
        void shouldUpdatePermissionEndpointSuccessfully() throws Exception {
            // given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            UpdatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture.updatePermissionEndpointRequest();
            doNothing().when(updatePermissionEndpointUseCase).update(any());

            // when & then
            mockMvc.perform(
                            put(
                                            PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS
                                                    + "/{permissionEndpointId}",
                                            permissionEndpointId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(
                            document(
                                    "permission-endpoint/update",
                                    pathParameters(
                                            parameterWithName("permissionEndpointId")
                                                    .description("수정할 PermissionEndpoint ID")),
                                    requestFields(
                                            fieldWithPath("serviceName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("서비스 이름 (선택, 100자 이하)")
                                                    .optional(),
                                            fieldWithPath("urlPattern")
                                                    .type(JsonFieldType.STRING)
                                                    .description("URL 패턴 (선택, '/'로 시작, 500자 이하)")
                                                    .optional(),
                                            fieldWithPath("httpMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "HTTP 메서드 (선택,"
                                                                + " GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS)")
                                                    .optional(),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("설명 (선택, 500자 이하)")
                                                    .optional(),
                                            fieldWithPath("isPublic")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("공개 엔드포인트 여부 (선택)")
                                                    .optional()),
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
        @DisplayName("urlPattern이 '/'로 시작하지 않으면 400 Bad Request")
        void shouldFailWhenUrlPatternDoesNotStartWithSlash() throws Exception {
            // given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            UpdatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture.updateUrlPatternRequest("api/v1/invalid");

            // when & then
            mockMvc.perform(
                            put(
                                            PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS
                                                    + "/{permissionEndpointId}",
                                            permissionEndpointId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("httpMethod가 유효하지 않으면 400 Bad Request")
        void shouldFailWhenHttpMethodIsInvalid() throws Exception {
            // given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            UpdatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture.updateHttpMethodRequest("INVALID");

            // when & then
            mockMvc.perform(
                            put(
                                            PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS
                                                    + "/{permissionEndpointId}",
                                            permissionEndpointId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("부분 업데이트 시나리오 - description과 isPublic만 수정")
        void shouldUpdatePartially() throws Exception {
            // given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            UpdatePermissionEndpointApiRequest request =
                    new UpdatePermissionEndpointApiRequest(
                            PermissionEndpointApiFixture.defaultServiceName(),
                            PermissionEndpointApiFixture.defaultUrlPattern(),
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            "수정된 설명만",
                            true); // isPublic만 변경
            doNothing().when(updatePermissionEndpointUseCase).update(any());

            // when & then
            mockMvc.perform(
                            put(
                                            PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS
                                                    + "/{permissionEndpointId}",
                                            permissionEndpointId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("description이 null이면 유효하다 (부분 업데이트)")
        void shouldAcceptNullDescription() throws Exception {
            // given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            UpdatePermissionEndpointApiRequest request =
                    new UpdatePermissionEndpointApiRequest(
                            PermissionEndpointApiFixture.defaultServiceName(),
                            PermissionEndpointApiFixture.defaultUrlPattern(),
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            null, // description null 허용
                            PermissionEndpointApiFixture.defaultIsPublic());
            doNothing().when(updatePermissionEndpointUseCase).update(any());

            // when & then
            mockMvc.perform(
                            put(
                                            PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS
                                                    + "/{permissionEndpointId}",
                                            permissionEndpointId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName(
            "DELETE /api/v1/permission-endpoints/{permissionEndpointId} - PermissionEndpoint 삭제")
    class DeleteTests {

        @Test
        @DisplayName("유효한 요청으로 PermissionEndpoint를 삭제한다")
        void shouldDeletePermissionEndpointSuccessfully() throws Exception {
            // given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            doNothing().when(deletePermissionEndpointUseCase).delete(any());

            // when & then
            mockMvc.perform(
                            delete(
                                    PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS
                                            + "/{permissionEndpointId}",
                                    permissionEndpointId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(
                            document(
                                    "permission-endpoint/delete",
                                    pathParameters(
                                            parameterWithName("permissionEndpointId")
                                                    .description("삭제할 PermissionEndpoint ID")),
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
        @DisplayName("PermissionEndpoint가 존재하지 않으면 404 Not Found")
        void shouldFailWhenPermissionEndpointNotFound() throws Exception {
            // given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            willThrow(ErrorMapperApiFixture.permissionEndpointNotFoundException())
                    .given(deletePermissionEndpointUseCase)
                    .delete(any());

            // when & then
            mockMvc.perform(
                            delete(
                                    PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS
                                            + "/{permissionEndpointId}",
                                    permissionEndpointId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("중복된 PermissionEndpoint 삭제 시도 시 409 Conflict")
        void shouldFailWhenDuplicatePermissionEndpoint() throws Exception {
            // given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            willThrow(ErrorMapperApiFixture.duplicatePermissionEndpointException())
                    .given(deletePermissionEndpointUseCase)
                    .delete(any());

            // when & then
            mockMvc.perform(
                            delete(
                                    PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS
                                            + "/{permissionEndpointId}",
                                    permissionEndpointId))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(409));
        }
    }
}
