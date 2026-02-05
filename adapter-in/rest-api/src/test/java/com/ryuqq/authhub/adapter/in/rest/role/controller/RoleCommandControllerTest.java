package com.ryuqq.authhub.adapter.in.rest.role.controller;

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
import com.ryuqq.authhub.adapter.in.rest.role.RoleApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.role.controller.command.RoleCommandController;
import com.ryuqq.authhub.adapter.in.rest.role.dto.request.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.request.UpdateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.fixture.RoleApiFixture;
import com.ryuqq.authhub.adapter.in.rest.role.mapper.RoleCommandApiMapper;
import com.ryuqq.authhub.application.role.port.in.command.CreateRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.command.DeleteRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.command.UpdateRoleUseCase;
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
 * RoleCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(RoleCommandController.class)
@Import({ControllerTestSecurityConfig.class, RoleCommandApiMapper.class})
@DisplayName("RoleCommandController 테스트")
class RoleCommandControllerTest extends RestDocsTestSupport {

    @MockBean private CreateRoleUseCase createRoleUseCase;

    @MockBean private UpdateRoleUseCase updateRoleUseCase;

    @MockBean private DeleteRoleUseCase deleteRoleUseCase;

    @Nested
    @DisplayName("POST /api/v1/auth/roles - 역할 생성")
    class CreateTests {

        @Test
        @DisplayName("유효한 요청으로 역할을 생성한다")
        void shouldCreateRoleSuccessfully() throws Exception {
            // given
            CreateRoleApiRequest request = RoleApiFixture.createRoleRequest();
            Long roleId = RoleApiFixture.defaultRoleId();
            given(createRoleUseCase.execute(any())).willReturn(roleId);

            // when & then
            mockMvc.perform(
                            post(RoleApiEndpoints.ROLES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roleId").value(roleId))
                    .andDo(
                            document(
                                    "role/create",
                                    requestFields(
                                            fieldWithPath("tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 ID (null이면 Global 역할)")
                                                    .optional(),
                                            fieldWithPath("serviceId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("서비스 ID (null이면 서비스 무관)")
                                                    .optional(),
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "역할 이름 (필수, UPPER_SNAKE_CASE, 2~50자)"),
                                            fieldWithPath("displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시 이름 (선택, 100자 이하)")
                                                    .optional(),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("역할 설명 (선택, 500자 이하)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.roleId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 Role ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("name이 빈 문자열이면 400 Bad Request")
        void shouldFailWhenNameIsBlank() throws Exception {
            // given
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(
                            RoleApiFixture.defaultTenantId(),
                            RoleApiFixture.defaultServiceId(),
                            "",
                            RoleApiFixture.defaultDisplayName(),
                            RoleApiFixture.defaultDescription());

            // when & then
            mockMvc.perform(
                            post(RoleApiEndpoints.ROLES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("name이 UPPER_SNAKE_CASE가 아니면 400 Bad Request")
        void shouldFailWhenNameIsNotUpperSnakeCase() throws Exception {
            // given
            CreateRoleApiRequest request = RoleApiFixture.createRoleRequest("invalid_name");

            // when & then
            mockMvc.perform(
                            post(RoleApiEndpoints.ROLES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Global 역할을 생성한다 (tenantId=null, serviceId=null)")
        void shouldCreateGlobalRole() throws Exception {
            // given
            CreateRoleApiRequest request = RoleApiFixture.createGlobalRoleRequest();
            Long roleId = RoleApiFixture.defaultRoleId();
            given(createRoleUseCase.execute(any())).willReturn(roleId);

            // when & then
            mockMvc.perform(
                            post(RoleApiEndpoints.ROLES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roleId").value(roleId));
        }

        @Test
        @DisplayName("name이 2자 미만이면 400 Bad Request")
        void shouldFailWhenNameIsTooShort() throws Exception {
            // given
            CreateRoleApiRequest request = RoleApiFixture.createRoleRequest("A");

            // when & then
            mockMvc.perform(
                            post(RoleApiEndpoints.ROLES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("name이 50자를 초과하면 400 Bad Request")
        void shouldFailWhenNameIsTooLong() throws Exception {
            // given
            String longName = "A".repeat(51);
            CreateRoleApiRequest request = RoleApiFixture.createRoleRequest(longName);

            // when & then
            mockMvc.perform(
                            post(RoleApiEndpoints.ROLES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("displayName이 100자를 초과하면 400 Bad Request")
        void shouldFailWhenDisplayNameIsTooLong() throws Exception {
            // given
            String longDisplayName = "가".repeat(101);
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(
                            RoleApiFixture.defaultTenantId(),
                            RoleApiFixture.defaultServiceId(),
                            RoleApiFixture.defaultName(),
                            longDisplayName,
                            RoleApiFixture.defaultDescription());

            // when & then
            mockMvc.perform(
                            post(RoleApiEndpoints.ROLES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("description이 500자를 초과하면 400 Bad Request")
        void shouldFailWhenDescriptionIsTooLong() throws Exception {
            // given
            String longDescription = "가".repeat(501);
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(
                            RoleApiFixture.defaultTenantId(),
                            RoleApiFixture.defaultServiceId(),
                            RoleApiFixture.defaultName(),
                            RoleApiFixture.defaultDisplayName(),
                            longDescription);

            // when & then
            mockMvc.perform(
                            post(RoleApiEndpoints.ROLES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/roles/{roleId} - 역할 수정")
    class UpdateTests {

        @Test
        @DisplayName("유효한 요청으로 역할을 수정한다")
        void shouldUpdateRoleSuccessfully() throws Exception {
            // given
            Long roleId = RoleApiFixture.defaultRoleId();
            UpdateRoleApiRequest request = RoleApiFixture.updateRoleRequest();
            doNothing().when(updateRoleUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(RoleApiEndpoints.ROLES + "/{roleId}", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roleId").value(roleId))
                    .andDo(
                            document(
                                    "role/update",
                                    pathParameters(
                                            parameterWithName("roleId").description("수정할 Role ID")),
                                    requestFields(
                                            fieldWithPath("displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("표시 이름 (null이면 변경 안 함, 100자 이하)")
                                                    .optional(),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.STRING)
                                                    .description("역할 설명 (null이면 변경 안 함, 500자 이하)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.roleId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수정된 Role ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("displayName만 수정한다")
        void shouldUpdateDisplayNameOnly() throws Exception {
            // given
            Long roleId = RoleApiFixture.defaultRoleId();
            UpdateRoleApiRequest request = RoleApiFixture.updateDisplayNameRequest("새로운 표시 이름");
            doNothing().when(updateRoleUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(RoleApiEndpoints.ROLES + "/{roleId}", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roleId").value(roleId));
        }

        @Test
        @DisplayName("description만 수정한다")
        void shouldUpdateDescriptionOnly() throws Exception {
            // given
            Long roleId = RoleApiFixture.defaultRoleId();
            UpdateRoleApiRequest request = RoleApiFixture.updateDescriptionRequest("새로운 설명");
            doNothing().when(updateRoleUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(RoleApiEndpoints.ROLES + "/{roleId}", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roleId").value(roleId));
        }

        @Test
        @DisplayName("모든 필드가 null인 요청도 정상 처리한다")
        void shouldUpdateWithAllNullFields() throws Exception {
            // given
            Long roleId = RoleApiFixture.defaultRoleId();
            UpdateRoleApiRequest request = new UpdateRoleApiRequest(null, null);
            doNothing().when(updateRoleUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(RoleApiEndpoints.ROLES + "/{roleId}", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roleId").value(roleId));
        }

        @Test
        @DisplayName("시스템 역할은 수정할 수 없어 403 Forbidden")
        void shouldFailWhenSystemRoleNotModifiable() throws Exception {
            // given
            Long roleId = RoleApiFixture.defaultRoleId();
            UpdateRoleApiRequest request = RoleApiFixture.updateRoleRequest();
            willThrow(ErrorMapperApiFixture.systemRoleNotModifiableException())
                    .given(updateRoleUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(
                            put(RoleApiEndpoints.ROLES + "/{roleId}", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(403));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/auth/roles/{roleId} - 역할 삭제")
    class DeleteTests {

        @Test
        @DisplayName("유효한 요청으로 역할을 삭제한다")
        void shouldDeleteRoleSuccessfully() throws Exception {
            // given
            Long roleId = RoleApiFixture.defaultRoleId();
            doNothing().when(deleteRoleUseCase).execute(any());

            // when & then
            mockMvc.perform(delete(RoleApiEndpoints.ROLES + "/{roleId}", roleId))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "role/delete",
                                    pathParameters(
                                            parameterWithName("roleId")
                                                    .description("삭제할 Role ID"))));
        }

        @Test
        @DisplayName("역할을 찾을 수 없으면 404 Not Found")
        void shouldFailWhenRoleNotFound() throws Exception {
            // given
            Long roleId = RoleApiFixture.defaultRoleId();
            willThrow(ErrorMapperApiFixture.roleNotFoundException())
                    .given(deleteRoleUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(delete(RoleApiEndpoints.ROLES + "/{roleId}", roleId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("시스템 역할은 삭제할 수 없어 403 Forbidden")
        void shouldFailWhenSystemRoleNotDeletable() throws Exception {
            // given
            Long roleId = RoleApiFixture.defaultRoleId();
            willThrow(ErrorMapperApiFixture.systemRoleNotDeletableException())
                    .given(deleteRoleUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(delete(RoleApiEndpoints.ROLES + "/{roleId}", roleId))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(403));
        }

        @Test
        @DisplayName("역할이 사용 중이면 409 Conflict")
        void shouldFailWhenRoleInUse() throws Exception {
            // given
            Long roleId = RoleApiFixture.defaultRoleId();
            willThrow(ErrorMapperApiFixture.roleInUseException())
                    .given(deleteRoleUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(delete(RoleApiEndpoints.ROLES + "/{roleId}", roleId))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(409));
        }
    }
}
