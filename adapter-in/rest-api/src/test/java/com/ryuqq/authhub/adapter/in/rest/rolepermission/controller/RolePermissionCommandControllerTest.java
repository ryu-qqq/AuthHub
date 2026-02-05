package com.ryuqq.authhub.adapter.in.rest.rolepermission.controller;

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
import com.ryuqq.authhub.adapter.in.rest.rolepermission.RolePermissionApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.dto.request.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.dto.request.RevokeRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.fixture.RolePermissionApiFixture;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.mapper.RolePermissionCommandApiMapper;
import com.ryuqq.authhub.application.rolepermission.port.in.command.GrantRolePermissionUseCase;
import com.ryuqq.authhub.application.rolepermission.port.in.command.RevokeRolePermissionUseCase;
import java.util.List;
import java.util.stream.IntStream;
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
 * RolePermissionCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(RolePermissionCommandController.class)
@Import({ControllerTestSecurityConfig.class, RolePermissionCommandApiMapper.class})
@DisplayName("RolePermissionCommandController 테스트")
class RolePermissionCommandControllerTest extends RestDocsTestSupport {

    @MockBean private GrantRolePermissionUseCase grantRolePermissionUseCase;

    @MockBean private RevokeRolePermissionUseCase revokeRolePermissionUseCase;

    @Nested
    @DisplayName("POST /api/v1/auth/roles/{roleId}/permissions - 역할에 권한 부여")
    class GrantPermissionsTests {

        @Test
        @DisplayName("유효한 요청으로 역할에 권한을 부여한다")
        void shouldGrantPermissionsSuccessfully() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            GrantRolePermissionApiRequest request =
                    RolePermissionApiFixture.grantRolePermissionRequest();
            doNothing().when(grantRolePermissionUseCase).grant(any());

            // when & then
            mockMvc.perform(
                            post(RolePermissionApiEndpoints.BASE + "/{roleId}/permissions", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(
                            document(
                                    "role-permission/grant",
                                    pathParameters(
                                            parameterWithName("roleId").description("역할 ID")),
                                    requestFields(
                                            fieldWithPath("permissionIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("부여할 권한 ID 목록 (필수, 1~100개)")),
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
        @DisplayName("권한 ID 목록이 비어있으면 400 Bad Request")
        void shouldFailWhenPermissionIdsIsEmpty() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            GrantRolePermissionApiRequest request = new GrantRolePermissionApiRequest(List.of());

            // when & then
            mockMvc.perform(
                            post(RolePermissionApiEndpoints.BASE + "/{roleId}/permissions", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("역할 또는 권한을 찾을 수 없으면 404 Not Found")
        void shouldReturn404WhenRolePermissionNotFound() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            GrantRolePermissionApiRequest request =
                    RolePermissionApiFixture.grantRolePermissionRequest();
            willThrow(ErrorMapperApiFixture.rolePermissionNotFoundException())
                    .given(grantRolePermissionUseCase)
                    .grant(any());

            // when & then
            mockMvc.perform(
                            post(RolePermissionApiEndpoints.BASE + "/{roleId}/permissions", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("이미 부여된 권한이면 409 Conflict")
        void shouldReturn409WhenDuplicateRolePermission() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            GrantRolePermissionApiRequest request =
                    RolePermissionApiFixture.grantRolePermissionRequest();
            willThrow(ErrorMapperApiFixture.duplicateRolePermissionException())
                    .given(grantRolePermissionUseCase)
                    .grant(any());

            // when & then
            mockMvc.perform(
                            post(RolePermissionApiEndpoints.BASE + "/{roleId}/permissions", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        @DisplayName("권한 ID 목록이 1개이면 성공한다")
        void shouldSuccessWhenPermissionIdsHasOne() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            GrantRolePermissionApiRequest request =
                    RolePermissionApiFixture.grantSinglePermissionRequest(1L);
            doNothing().when(grantRolePermissionUseCase).grant(any());

            // when & then
            mockMvc.perform(
                            post(RolePermissionApiEndpoints.BASE + "/{roleId}/permissions", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("권한 ID 목록이 100개이면 성공한다")
        void shouldSuccessWhenPermissionIdsHasMaxSize() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            List<Long> maxSizePermissionIds =
                    IntStream.rangeClosed(1, 100).mapToLong(i -> (long) i).boxed().toList();
            GrantRolePermissionApiRequest request =
                    RolePermissionApiFixture.grantRolePermissionRequest(maxSizePermissionIds);
            doNothing().when(grantRolePermissionUseCase).grant(any());

            // when & then
            mockMvc.perform(
                            post(RolePermissionApiEndpoints.BASE + "/{roleId}/permissions", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("권한 ID 목록이 101개이면 400 Bad Request")
        void shouldFailWhenPermissionIdsExceedsMaxSize() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            List<Long> exceededPermissionIds =
                    IntStream.rangeClosed(1, 101).mapToLong(i -> (long) i).boxed().toList();
            GrantRolePermissionApiRequest request =
                    RolePermissionApiFixture.grantRolePermissionRequest(exceededPermissionIds);

            // when & then
            mockMvc.perform(
                            post(RolePermissionApiEndpoints.BASE + "/{roleId}/permissions", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/auth/roles/{roleId}/permissions - 역할에서 권한 제거")
    class RevokePermissionsTests {

        @Test
        @DisplayName("유효한 요청으로 역할에서 권한을 제거한다")
        void shouldRevokePermissionsSuccessfully() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            RevokeRolePermissionApiRequest request =
                    RolePermissionApiFixture.revokeRolePermissionRequest();
            doNothing().when(revokeRolePermissionUseCase).revoke(any());

            // when & then
            mockMvc.perform(
                            delete(
                                            RolePermissionApiEndpoints.BASE
                                                    + "/{roleId}/permissions",
                                            roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "role-permission/revoke",
                                    pathParameters(
                                            parameterWithName("roleId").description("역할 ID")),
                                    requestFields(
                                            fieldWithPath("permissionIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("제거할 권한 ID 목록 (필수, 1~100개)"))));
        }

        @Test
        @DisplayName("권한 ID 목록이 비어있으면 400 Bad Request")
        void shouldFailWhenPermissionIdsIsEmpty() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            RevokeRolePermissionApiRequest request = new RevokeRolePermissionApiRequest(List.of());

            // when & then
            mockMvc.perform(
                            delete(
                                            RolePermissionApiEndpoints.BASE
                                                    + "/{roleId}/permissions",
                                            roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("역할 또는 역할-권한 관계를 찾을 수 없으면 404 Not Found")
        void shouldReturn404WhenRolePermissionNotFound() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            RevokeRolePermissionApiRequest request =
                    RolePermissionApiFixture.revokeRolePermissionRequest();
            willThrow(ErrorMapperApiFixture.rolePermissionNotFoundException())
                    .given(revokeRolePermissionUseCase)
                    .revoke(any());

            // when & then
            mockMvc.perform(
                            delete(
                                            RolePermissionApiEndpoints.BASE
                                                    + "/{roleId}/permissions",
                                            roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("권한 ID 목록이 1개이면 성공한다")
        void shouldSuccessWhenPermissionIdsHasOne() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            RevokeRolePermissionApiRequest request =
                    RolePermissionApiFixture.revokeSinglePermissionRequest(1L);
            doNothing().when(revokeRolePermissionUseCase).revoke(any());

            // when & then
            mockMvc.perform(
                            delete(
                                            RolePermissionApiEndpoints.BASE
                                                    + "/{roleId}/permissions",
                                            roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("권한 ID 목록이 100개이면 성공한다")
        void shouldSuccessWhenPermissionIdsHasMaxSize() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            List<Long> maxSizePermissionIds =
                    IntStream.rangeClosed(1, 100).mapToLong(i -> (long) i).boxed().toList();
            RevokeRolePermissionApiRequest request =
                    RolePermissionApiFixture.revokeRolePermissionRequest(maxSizePermissionIds);
            doNothing().when(revokeRolePermissionUseCase).revoke(any());

            // when & then
            mockMvc.perform(
                            delete(
                                            RolePermissionApiEndpoints.BASE
                                                    + "/{roleId}/permissions",
                                            roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("권한 ID 목록이 101개이면 400 Bad Request")
        void shouldFailWhenPermissionIdsExceedsMaxSize() throws Exception {
            // given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            List<Long> exceededPermissionIds =
                    IntStream.rangeClosed(1, 101).mapToLong(i -> (long) i).boxed().toList();
            RevokeRolePermissionApiRequest request =
                    RolePermissionApiFixture.revokeRolePermissionRequest(exceededPermissionIds);

            // when & then
            mockMvc.perform(
                            delete(
                                            RolePermissionApiEndpoints.BASE
                                                    + "/{roleId}/permissions",
                                            roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
