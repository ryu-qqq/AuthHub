package com.ryuqq.authhub.adapter.in.rest.role.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.UpdateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.query.SearchRolesAdminApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.query.SearchRolesApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.CreateRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RolePermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RolePermissionSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.mapper.RoleApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.command.RevokeRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.application.role.dto.query.GetRoleQuery;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleDetailResponse;
import com.ryuqq.authhub.application.role.dto.response.RolePermissionResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleSummaryResponse;
import com.ryuqq.authhub.application.role.port.in.command.CreateRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.command.DeleteRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.command.GrantRolePermissionUseCase;
import com.ryuqq.authhub.application.role.port.in.command.RevokeRolePermissionUseCase;
import com.ryuqq.authhub.application.role.port.in.command.UpdateRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.query.GetRoleDetailUseCase;
import com.ryuqq.authhub.application.role.port.in.query.GetRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.query.SearchRoleUsersUseCase;
import com.ryuqq.authhub.application.role.port.in.query.SearchRolesAdminUseCase;
import com.ryuqq.authhub.application.role.port.in.query.SearchRolesUseCase;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

/**
 * RoleController REST Docs 문서화 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>API 엔드포인트 문서화
 *   <li>Request/Response 필드 문서화
 *   <li>Path Parameter 문서화
 *   <li>Query Parameter 문서화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest({
    RoleCommandController.class,
    RoleQueryController.class,
    RolePermissionController.class
})
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("docs")
@DisplayName("Role Controller REST Docs")
class RoleControllerDocsTest extends RestDocsTestSupport {

    @MockBean private CreateRoleUseCase createRoleUseCase;

    @MockBean private UpdateRoleUseCase updateRoleUseCase;

    @MockBean private DeleteRoleUseCase deleteRoleUseCase;

    @MockBean private GetRoleUseCase getRoleUseCase;

    @MockBean private GetRoleDetailUseCase getRoleDetailUseCase;

    @MockBean private SearchRolesUseCase searchRolesUseCase;

    @MockBean private SearchRolesAdminUseCase searchRolesAdminUseCase;

    @MockBean private SearchRoleUsersUseCase searchRoleUsersUseCase;

    @MockBean private GrantRolePermissionUseCase grantRolePermissionUseCase;

    @MockBean private RevokeRolePermissionUseCase revokeRolePermissionUseCase;

    @MockBean private RoleApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/roles - 역할 생성")
    class CreateRoleDocs {

        @Test
        @DisplayName("역할 생성 API 문서")
        void createRole() throws Exception {
            // given
            String tenantId = UUID.randomUUID().toString();
            String name = "TENANT_ADMIN";
            String description = "테넌트 관리자 역할";
            String scope = "TENANT";
            Boolean isSystem = false;
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(tenantId, name, description, scope, isSystem);

            UUID roleId = UUID.randomUUID();
            Instant now = Instant.now();
            CreateRoleCommand command =
                    new CreateRoleCommand(
                            UUID.fromString(tenantId), name, description, scope, isSystem);
            RoleResponse useCaseResponse =
                    new RoleResponse(
                            roleId,
                            UUID.fromString(tenantId),
                            name,
                            description,
                            scope,
                            "CUSTOM",
                            now,
                            now);
            CreateRoleApiResponse apiResponse = new CreateRoleApiResponse(roleId.toString());

            given(mapper.toCommand(any(CreateRoleApiRequest.class))).willReturn(command);
            given(createRoleUseCase.execute(any(CreateRoleCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(RoleResponse.class))).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/roles")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andDo(
                            document(
                                    "role-create",
                                    requestFields(
                                            fieldWithPath("tenantId")
                                                    .description("테넌트 ID (선택, null인 경우 시스템 역할)")
                                                    .optional(),
                                            fieldWithPath("name").description("역할 이름 (1~100자, 필수)"),
                                            fieldWithPath("description")
                                                    .description("역할 설명 (최대 500자, 선택)")
                                                    .optional(),
                                            fieldWithPath("scope")
                                                    .description(
                                                            "역할 범위 (TENANT/ORGANIZATION/SYSTEM,"
                                                                    + " 선택)")
                                                    .optional(),
                                            fieldWithPath("isSystem")
                                                    .description("시스템 역할 여부 (선택)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.roleId").description("생성된 역할 ID"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/roles/{roleId} - 역할 수정")
    class UpdateRoleDocs {

        @Test
        @DisplayName("역할 수정 API 문서")
        void updateRole() throws Exception {
            // given
            String roleId = UUID.randomUUID().toString();
            String name = "UPDATED_ROLE";
            String description = "수정된 역할 설명";
            UpdateRoleApiRequest request = new UpdateRoleApiRequest(name, description);

            UpdateRoleCommand command =
                    new UpdateRoleCommand(UUID.fromString(roleId), name, description);

            given(mapper.toCommand(any(String.class), any(UpdateRoleApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            put("/api/v1/auth/roles/{roleId}", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "role-update",
                                    pathParameters(
                                            parameterWithName("roleId").description("역할 ID")),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("역할 이름 (1~100자, 선택)")
                                                    .optional(),
                                            fieldWithPath("description")
                                                    .description("역할 설명 (최대 500자, 선택)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터 (null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/roles/{roleId}/delete - 역할 삭제")
    class DeleteRoleDocs {

        @Test
        @DisplayName("역할 삭제 API 문서")
        void deleteRole() throws Exception {
            // given
            String roleId = UUID.randomUUID().toString();
            DeleteRoleCommand command = new DeleteRoleCommand(UUID.fromString(roleId));

            given(mapper.toDeleteCommand(any(String.class))).willReturn(command);

            // when & then
            mockMvc.perform(patch("/api/v1/auth/roles/{roleId}/delete", roleId))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "role-delete",
                                    pathParameters(
                                            parameterWithName("roleId").description("역할 ID"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/roles/{roleId} - 역할 단건 조회")
    class GetRoleDocs {

        @Test
        @DisplayName("역할 단건 조회 API 문서")
        void getRole() throws Exception {
            // given
            String roleId = UUID.randomUUID().toString();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();
            RoleResponse useCaseResponse =
                    new RoleResponse(
                            UUID.fromString(roleId),
                            tenantId,
                            "TENANT_ADMIN",
                            "테넌트 관리자 역할",
                            "TENANT",
                            "CUSTOM",
                            now,
                            now);
            RoleApiResponse apiResponse =
                    new RoleApiResponse(
                            roleId,
                            tenantId.toString(),
                            "TENANT_ADMIN",
                            "테넌트 관리자 역할",
                            "TENANT",
                            "CUSTOM",
                            now,
                            now);

            given(mapper.toGetQuery(roleId)).willReturn(new GetRoleQuery(UUID.fromString(roleId)));
            given(getRoleUseCase.execute(any(GetRoleQuery.class))).willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(RoleResponse.class))).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            get("/api/v1/auth/roles/{roleId}", roleId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "role-get",
                                    pathParameters(
                                            parameterWithName("roleId").description("역할 ID")),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.roleId").description("역할 ID"),
                                            fieldWithPath("data.tenantId").description("테넌트 ID"),
                                            fieldWithPath("data.name").description("역할 이름"),
                                            fieldWithPath("data.description").description("역할 설명"),
                                            fieldWithPath("data.scope")
                                                    .description(
                                                            "역할 범위 (TENANT/ORGANIZATION/SYSTEM)"),
                                            fieldWithPath("data.type")
                                                    .description("역할 유형 (SYSTEM/CUSTOM)"),
                                            fieldWithPath("data.createdAt").description("생성 일시"),
                                            fieldWithPath("data.updatedAt").description("수정 일시"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/roles - 역할 목록 검색")
    class SearchRolesDocs {

        @Test
        @DisplayName("역할 목록 검색 API 문서")
        void searchRoles() throws Exception {
            // given
            UUID roleId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();
            RoleResponse response =
                    new RoleResponse(
                            roleId,
                            tenantId,
                            "TENANT_ADMIN",
                            "테넌트 관리자 역할",
                            "TENANT",
                            "CUSTOM",
                            now,
                            now);
            RoleApiResponse apiResponse =
                    new RoleApiResponse(
                            roleId.toString(),
                            tenantId.toString(),
                            "TENANT_ADMIN",
                            "테넌트 관리자 역할",
                            "TENANT",
                            "CUSTOM",
                            now,
                            now);

            given(mapper.toQuery(any(SearchRolesApiRequest.class)))
                    .willReturn(SearchRolesQuery.of(tenantId, "ADMIN", null, null, 0, 20));
            given(searchRolesUseCase.execute(any(SearchRolesQuery.class)))
                    .willReturn(List.of(response));
            given(mapper.toApiResponseList(any())).willReturn(List.of(apiResponse));

            // when & then
            mockMvc.perform(
                            get("/api/v1/auth/roles")
                                    .param("tenantId", tenantId.toString())
                                    .param("name", "ADMIN")
                                    .param("scope", "TENANT")
                                    .param("type", "CUSTOM")
                                    .param("page", "0")
                                    .param("size", "20")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "role-search",
                                    queryParameters(
                                            parameterWithName("tenantId")
                                                    .description("테넌트 ID 필터 (선택)")
                                                    .optional(),
                                            parameterWithName("name")
                                                    .description("역할 이름 필터 (선택)")
                                                    .optional(),
                                            parameterWithName("scope")
                                                    .description(
                                                            "범위 필터 (TENANT/ORGANIZATION/SYSTEM,"
                                                                    + " 선택)")
                                                    .optional(),
                                            parameterWithName("type")
                                                    .description("역할 유형 필터 (SYSTEM/CUSTOM, 선택)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (기본값: 0)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (기본값: 20)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data[]").description("역할 목록"),
                                            fieldWithPath("data[].roleId").description("역할 ID"),
                                            fieldWithPath("data[].tenantId").description("테넌트 ID"),
                                            fieldWithPath("data[].name").description("역할 이름"),
                                            fieldWithPath("data[].description")
                                                    .description("역할 설명"),
                                            fieldWithPath("data[].scope")
                                                    .description(
                                                            "역할 범위 (TENANT/ORGANIZATION/SYSTEM)"),
                                            fieldWithPath("data[].type")
                                                    .description("역할 유형 (SYSTEM/CUSTOM)"),
                                            fieldWithPath("data[].createdAt").description("생성 일시"),
                                            fieldWithPath("data[].updatedAt").description("수정 일시"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/roles/{roleId}/permissions - 역할에 권한 부여")
    class GrantRolePermissionDocs {

        @Test
        @DisplayName("역할에 권한 부여 API 문서")
        void grantPermission() throws Exception {
            // given
            String roleId = UUID.randomUUID().toString();
            UUID permissionId = UUID.randomUUID();
            GrantRolePermissionApiRequest request = new GrantRolePermissionApiRequest(permissionId);

            Instant now = Instant.now();
            GrantRolePermissionCommand command =
                    new GrantRolePermissionCommand(UUID.fromString(roleId), permissionId);
            RolePermissionResponse useCaseResponse =
                    new RolePermissionResponse(UUID.fromString(roleId), permissionId, now);
            RolePermissionApiResponse apiResponse =
                    new RolePermissionApiResponse(UUID.fromString(roleId), permissionId, now);

            given(
                            mapper.toGrantPermissionCommand(
                                    any(String.class), any(GrantRolePermissionApiRequest.class)))
                    .willReturn(command);
            given(grantRolePermissionUseCase.execute(any(GrantRolePermissionCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toRolePermissionApiResponse(any(RolePermissionResponse.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/roles/{roleId}/permissions", roleId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andDo(
                            document(
                                    "role-permission-grant",
                                    pathParameters(
                                            parameterWithName("roleId").description("역할 ID")),
                                    requestFields(
                                            fieldWithPath("permissionId")
                                                    .description("부여할 권한 ID (필수)")),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.roleId").description("역할 ID"),
                                            fieldWithPath("data.permissionId").description("권한 ID"),
                                            fieldWithPath("data.grantedAt").description("권한 부여 일시"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/roles/{roleId}/permissions/{permissionId}/revoke - 역할에서 권한 해제")
    class RevokeRolePermissionDocs {

        @Test
        @DisplayName("역할에서 권한 해제 API 문서")
        void revokePermission() throws Exception {
            // given
            String roleId = UUID.randomUUID().toString();
            String permissionId = UUID.randomUUID().toString();
            RevokeRolePermissionCommand command =
                    new RevokeRolePermissionCommand(
                            UUID.fromString(roleId), UUID.fromString(permissionId));

            given(mapper.toRevokePermissionCommand(any(String.class), any(String.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            patch(
                                    "/api/v1/auth/roles/{roleId}/permissions/{permissionId}/revoke",
                                    roleId,
                                    permissionId))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "role-permission-revoke",
                                    pathParameters(
                                            parameterWithName("roleId").description("역할 ID"),
                                            parameterWithName("permissionId")
                                                    .description("권한 ID"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/roles/admin/search - 역할 목록 검색 (Admin)")
    class SearchRolesAdminDocs {

        @Test
        @DisplayName("역할 목록 검색 (Admin) API 문서")
        void searchRolesAdmin() throws Exception {
            // given
            UUID roleId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();

            RoleSummaryResponse summaryResponse =
                    new RoleSummaryResponse(
                            roleId,
                            tenantId,
                            "테넌트A",
                            "TENANT_ADMIN",
                            "테넌트 관리자 역할",
                            "TENANT",
                            "CUSTOM",
                            5,
                            10,
                            now,
                            now);

            PageResponse<RoleSummaryResponse> pageResponse =
                    new PageResponse<>(List.of(summaryResponse), 0, 20, 1L, 1, true, true);

            RoleSummaryApiResponse apiResponse =
                    new RoleSummaryApiResponse(
                            roleId.toString(),
                            tenantId.toString(),
                            "테넌트A",
                            "TENANT_ADMIN",
                            "테넌트 관리자 역할",
                            "TENANT",
                            "CUSTOM",
                            5,
                            10,
                            now,
                            now);

            PageApiResponse<RoleSummaryApiResponse> pageApiResponse =
                    new PageApiResponse<>(List.of(apiResponse), 0, 20, 1L, 1, true, true);

            SearchRolesQuery query =
                    new SearchRolesQuery(
                            tenantId,
                            "ADMIN",
                            RoleScope.TENANT,
                            RoleType.CUSTOM,
                            null,
                            null,
                            "createdAt",
                            "DESC",
                            0,
                            20);

            given(mapper.toAdminQuery(any(SearchRolesAdminApiRequest.class))).willReturn(query);
            given(searchRolesAdminUseCase.execute(any(SearchRolesQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toSummaryApiResponse(any(RoleSummaryResponse.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            get("/api/v1/auth/roles/admin/search")
                                    .param("tenantId", tenantId.toString())
                                    .param("name", "ADMIN")
                                    .param("scope", "TENANT")
                                    .param("type", "CUSTOM")
                                    .param("createdFrom", "2024-01-01T00:00:00Z")
                                    .param("createdTo", "2024-12-31T23:59:59Z")
                                    .param("sortBy", "createdAt")
                                    .param("sortDirection", "DESC")
                                    .param("page", "0")
                                    .param("size", "20")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "role-admin-search",
                                    queryParameters(
                                            parameterWithName("tenantId")
                                                    .description("테넌트 ID 필터 (선택)")
                                                    .optional(),
                                            parameterWithName("name")
                                                    .description("역할 이름 필터 (부분 일치, 선택)")
                                                    .optional(),
                                            parameterWithName("scope")
                                                    .description(
                                                            "범위 필터 (TENANT/ORGANIZATION/SYSTEM,"
                                                                    + " 선택)")
                                                    .optional(),
                                            parameterWithName("type")
                                                    .description("역할 유형 필터 (SYSTEM/CUSTOM, 선택)")
                                                    .optional(),
                                            parameterWithName("createdFrom")
                                                    .description("생성일 시작 (ISO 8601 형식, 선택)")
                                                    .optional(),
                                            parameterWithName("createdTo")
                                                    .description("생성일 종료 (ISO 8601 형식, 선택)")
                                                    .optional(),
                                            parameterWithName("sortBy")
                                                    .description(
                                                            "정렬 기준 (createdAt/updatedAt/name, 기본값:"
                                                                    + " createdAt)")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC/DESC, 기본값: DESC)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (기본값: 0)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (기본값: 20, 최대: 100)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("content").description("역할 목록"),
                                            fieldWithPath("content[].roleId").description("역할 ID"),
                                            fieldWithPath("content[].tenantId")
                                                    .description("테넌트 ID"),
                                            fieldWithPath("content[].tenantName")
                                                    .description("테넌트 이름"),
                                            fieldWithPath("content[].name").description("역할 이름"),
                                            fieldWithPath("content[].description")
                                                    .description("역할 설명"),
                                            fieldWithPath("content[].scope").description("역할 범위"),
                                            fieldWithPath("content[].type").description("역할 유형"),
                                            fieldWithPath("content[].permissionCount")
                                                    .description("할당된 권한 수"),
                                            fieldWithPath("content[].userCount")
                                                    .description("역할이 할당된 사용자 수"),
                                            fieldWithPath("content[].createdAt")
                                                    .description("생성 일시"),
                                            fieldWithPath("content[].updatedAt")
                                                    .description("수정 일시"),
                                            fieldWithPath("page").description("현재 페이지 번호"),
                                            fieldWithPath("size").description("페이지 크기"),
                                            fieldWithPath("totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("totalPages").description("전체 페이지 수"),
                                            fieldWithPath("first").description("첫 페이지 여부"),
                                            fieldWithPath("last").description("마지막 페이지 여부"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/roles/{roleId}/admin/detail - 역할 상세 조회 (Admin)")
    class GetRoleDetailDocs {

        @Test
        @DisplayName("역할 상세 조회 (Admin) API 문서")
        void getRoleDetail() throws Exception {
            // given
            UUID roleId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            UUID permissionId = UUID.randomUUID();
            Instant now = Instant.now();

            RoleDetailResponse.RolePermissionSummary permissionSummary =
                    new RoleDetailResponse.RolePermissionSummary(
                            permissionId, "USER_READ", "사용자 조회 권한", "USER", "READ");

            RoleDetailResponse detailResponse =
                    new RoleDetailResponse(
                            roleId,
                            tenantId,
                            "테넌트A",
                            "TENANT_ADMIN",
                            "테넌트 관리자 역할",
                            "TENANT",
                            "CUSTOM",
                            List.of(permissionSummary),
                            10,
                            now,
                            now);

            RolePermissionSummaryApiResponse permissionApiResponse =
                    new RolePermissionSummaryApiResponse(
                            permissionId.toString(), "USER_READ", "사용자 조회 권한", "USER", "READ");

            RoleDetailApiResponse apiResponse =
                    new RoleDetailApiResponse(
                            roleId.toString(),
                            tenantId.toString(),
                            "테넌트A",
                            "TENANT_ADMIN",
                            "테넌트 관리자 역할",
                            "TENANT",
                            "CUSTOM",
                            List.of(permissionApiResponse),
                            10,
                            now,
                            now);

            GetRoleQuery query = new GetRoleQuery(roleId);

            given(mapper.toGetQuery(any(String.class))).willReturn(query);
            given(getRoleDetailUseCase.execute(any(GetRoleQuery.class))).willReturn(detailResponse);
            given(mapper.toDetailApiResponse(any(RoleDetailResponse.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            get("/api/v1/auth/roles/{roleId}/admin/detail", roleId.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "role-admin-detail",
                                    pathParameters(
                                            parameterWithName("roleId")
                                                    .description("역할 ID (UUID 형식)")),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.roleId").description("역할 ID"),
                                            fieldWithPath("data.tenantId").description("테넌트 ID"),
                                            fieldWithPath("data.tenantName").description("테넌트 이름"),
                                            fieldWithPath("data.name").description("역할 이름"),
                                            fieldWithPath("data.description").description("역할 설명"),
                                            fieldWithPath("data.scope")
                                                    .description(
                                                            "역할 범위 (GLOBAL/TENANT/ORGANIZATION)"),
                                            fieldWithPath("data.type")
                                                    .description("역할 유형 (SYSTEM/CUSTOM)"),
                                            fieldWithPath("data.permissions")
                                                    .description("할당된 권한 목록"),
                                            fieldWithPath("data.permissions[].permissionId")
                                                    .description("권한 ID"),
                                            fieldWithPath("data.permissions[].permissionKey")
                                                    .description("권한 키"),
                                            fieldWithPath("data.permissions[].description")
                                                    .description("권한 설명"),
                                            fieldWithPath("data.permissions[].resource")
                                                    .description("리소스"),
                                            fieldWithPath("data.permissions[].action")
                                                    .description("액션"),
                                            fieldWithPath("data.userCount")
                                                    .description("역할이 할당된 사용자 수"),
                                            fieldWithPath("data.createdAt").description("생성 일시"),
                                            fieldWithPath("data.updatedAt").description("수정 일시"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }
}
