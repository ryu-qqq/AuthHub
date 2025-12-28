package com.ryuqq.authhub.adapter.in.rest.role.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.mapper.RoleApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.dto.query.GetRoleQuery;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleUserResponse;
import com.ryuqq.authhub.application.role.port.in.query.GetRoleDetailUseCase;
import com.ryuqq.authhub.application.role.port.in.query.GetRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.query.SearchRoleUsersUseCase;
import com.ryuqq.authhub.application.role.port.in.query.SearchRolesAdminUseCase;
import com.ryuqq.authhub.application.role.port.in.query.SearchRolesUseCase;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * RoleQueryController 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>HTTP 요청/응답 매핑
 *   <li>Request 파라미터 바인딩
 *   <li>Response DTO 직렬화
 *   <li>HTTP Status Code
 *   <li>UseCase 호출 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(RoleQueryController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("RoleQueryController 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
class RoleQueryControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private GetRoleUseCase getRoleUseCase;

    @MockBean private GetRoleDetailUseCase getRoleDetailUseCase;

    @MockBean private SearchRolesUseCase searchRolesUseCase;

    @MockBean private SearchRolesAdminUseCase searchRolesAdminUseCase;

    @MockBean private SearchRoleUsersUseCase searchRoleUsersUseCase;

    @MockBean private RoleApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/roles/{roleId} - 역할 단건 조회")
    class GetRoleTest {

        @Test
        @DisplayName("[성공] 유효한 ID로 역할 조회 시 200 OK 반환")
        void getRole_withValidId_returns200Ok() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();
            RoleResponse useCaseResponse =
                    new RoleResponse(
                            roleId,
                            tenantId,
                            "TestRole",
                            "Test description",
                            "TENANT",
                            "CUSTOM",
                            now,
                            now);
            RoleApiResponse apiResponse =
                    new RoleApiResponse(
                            roleId.toString(),
                            tenantId.toString(),
                            "TestRole",
                            "Test description",
                            "TENANT",
                            "CUSTOM",
                            now,
                            now);

            given(mapper.toGetQuery(roleId.toString())).willReturn(new GetRoleQuery(roleId));
            given(getRoleUseCase.execute(any(GetRoleQuery.class))).willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(RoleResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/roles/{roleId}", roleId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roleId").value(roleId.toString()))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId.toString()))
                    .andExpect(jsonPath("$.data.name").value("TestRole"))
                    .andExpect(jsonPath("$.data.description").value("Test description"))
                    .andExpect(jsonPath("$.data.scope").value("TENANT"))
                    .andExpect(jsonPath("$.data.type").value("CUSTOM"));

            verify(getRoleUseCase).execute(any(GetRoleQuery.class));
        }

        @Test
        @DisplayName("[성공] 글로벌 역할 조회 시 tenantId가 null")
        void getRole_globalRole_returnsTenantIdNull() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            Instant now = Instant.now();
            RoleResponse useCaseResponse =
                    new RoleResponse(
                            roleId,
                            null,
                            "GlobalRole",
                            "Global role description",
                            "GLOBAL",
                            "SYSTEM",
                            now,
                            now);
            RoleApiResponse apiResponse =
                    new RoleApiResponse(
                            roleId.toString(),
                            null,
                            "GlobalRole",
                            "Global role description",
                            "GLOBAL",
                            "SYSTEM",
                            now,
                            now);

            given(mapper.toGetQuery(roleId.toString())).willReturn(new GetRoleQuery(roleId));
            given(getRoleUseCase.execute(any(GetRoleQuery.class))).willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(RoleResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/roles/{roleId}", roleId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roleId").value(roleId.toString()))
                    .andExpect(jsonPath("$.data.tenantId").isEmpty())
                    .andExpect(jsonPath("$.data.name").value("GlobalRole"))
                    .andExpect(jsonPath("$.data.scope").value("GLOBAL"))
                    .andExpect(jsonPath("$.data.type").value("SYSTEM"));

            verify(getRoleUseCase).execute(any(GetRoleQuery.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/roles - 역할 목록 검색")
    class SearchRolesTest {

        @Test
        @DisplayName("[성공] 필터 없이 전체 역할 목록 조회")
        void searchRoles_withoutFilters_returnsAll() throws Exception {
            // Given
            UUID roleId1 = UUID.randomUUID();
            UUID roleId2 = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();

            List<RoleResponse> useCaseResponses =
                    List.of(
                            new RoleResponse(
                                    roleId1,
                                    tenantId,
                                    "Role1",
                                    "Description1",
                                    "TENANT",
                                    "CUSTOM",
                                    now,
                                    now),
                            new RoleResponse(
                                    roleId2,
                                    null,
                                    "Role2",
                                    "Description2",
                                    "GLOBAL",
                                    "SYSTEM",
                                    now,
                                    now));
            List<RoleApiResponse> apiResponses =
                    List.of(
                            new RoleApiResponse(
                                    roleId1.toString(),
                                    tenantId.toString(),
                                    "Role1",
                                    "Description1",
                                    "TENANT",
                                    "CUSTOM",
                                    now,
                                    now),
                            new RoleApiResponse(
                                    roleId2.toString(),
                                    null,
                                    "Role2",
                                    "Description2",
                                    "GLOBAL",
                                    "SYSTEM",
                                    now,
                                    now));

            given(mapper.toQuery(any()))
                    .willReturn(SearchRolesQuery.of(null, null, null, null, 0, 20));
            given(searchRolesUseCase.execute(any(SearchRolesQuery.class)))
                    .willReturn(useCaseResponses);
            given(mapper.toApiResponseList(any())).willReturn(apiResponses);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/roles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].roleId").value(roleId1.toString()))
                    .andExpect(jsonPath("$.data[1].roleId").value(roleId2.toString()));

            verify(searchRolesUseCase).execute(any(SearchRolesQuery.class));
        }

        @Test
        @DisplayName("[성공] 테넌트 ID로 필터링")
        void searchRoles_withTenantIdFilter_returnsFiltered() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();

            List<RoleResponse> useCaseResponses =
                    List.of(
                            new RoleResponse(
                                    roleId,
                                    tenantId,
                                    "TenantRole",
                                    "Description",
                                    "TENANT",
                                    "CUSTOM",
                                    now,
                                    now));
            List<RoleApiResponse> apiResponses =
                    List.of(
                            new RoleApiResponse(
                                    roleId.toString(),
                                    tenantId.toString(),
                                    "TenantRole",
                                    "Description",
                                    "TENANT",
                                    "CUSTOM",
                                    now,
                                    now));

            given(mapper.toQuery(any()))
                    .willReturn(SearchRolesQuery.of(tenantId, null, null, null, 0, 20));
            given(searchRolesUseCase.execute(any(SearchRolesQuery.class)))
                    .willReturn(useCaseResponses);
            given(mapper.toApiResponseList(any())).willReturn(apiResponses);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/roles").param("tenantId", tenantId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].tenantId").value(tenantId.toString()));

            verify(searchRolesUseCase).execute(any(SearchRolesQuery.class));
        }

        @Test
        @DisplayName("[성공] 이름으로 필터링")
        void searchRoles_withNameFilter_returnsFiltered() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();

            List<RoleResponse> useCaseResponses =
                    List.of(
                            new RoleResponse(
                                    roleId,
                                    tenantId,
                                    "AdminRole",
                                    "Admin role",
                                    "TENANT",
                                    "CUSTOM",
                                    now,
                                    now));
            List<RoleApiResponse> apiResponses =
                    List.of(
                            new RoleApiResponse(
                                    roleId.toString(),
                                    tenantId.toString(),
                                    "AdminRole",
                                    "Admin role",
                                    "TENANT",
                                    "CUSTOM",
                                    now,
                                    now));

            given(mapper.toQuery(any()))
                    .willReturn(SearchRolesQuery.of(null, "Admin", null, null, 0, 20));
            given(searchRolesUseCase.execute(any(SearchRolesQuery.class)))
                    .willReturn(useCaseResponses);
            given(mapper.toApiResponseList(any())).willReturn(apiResponses);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/roles").param("name", "Admin"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("AdminRole"));

            verify(searchRolesUseCase).execute(any(SearchRolesQuery.class));
        }

        @Test
        @DisplayName("[성공] 범위(scope)로 필터링")
        void searchRoles_withScopeFilter_returnsFiltered() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            Instant now = Instant.now();

            List<RoleResponse> useCaseResponses =
                    List.of(
                            new RoleResponse(
                                    roleId,
                                    null,
                                    "GlobalRole",
                                    "Global role",
                                    "GLOBAL",
                                    "SYSTEM",
                                    now,
                                    now));
            List<RoleApiResponse> apiResponses =
                    List.of(
                            new RoleApiResponse(
                                    roleId.toString(),
                                    null,
                                    "GlobalRole",
                                    "Global role",
                                    "GLOBAL",
                                    "SYSTEM",
                                    now,
                                    now));

            given(mapper.toQuery(any()))
                    .willReturn(SearchRolesQuery.of(null, null, null, null, 0, 20));
            given(searchRolesUseCase.execute(any(SearchRolesQuery.class)))
                    .willReturn(useCaseResponses);
            given(mapper.toApiResponseList(any())).willReturn(apiResponses);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/roles").param("scope", "GLOBAL"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].scope").value("GLOBAL"));

            verify(searchRolesUseCase).execute(any(SearchRolesQuery.class));
        }

        @Test
        @DisplayName("[성공] 타입(type)으로 필터링")
        void searchRoles_withTypeFilter_returnsFiltered() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            Instant now = Instant.now();

            List<RoleResponse> useCaseResponses =
                    List.of(
                            new RoleResponse(
                                    roleId,
                                    null,
                                    "SystemRole",
                                    "System role",
                                    "GLOBAL",
                                    "SYSTEM",
                                    now,
                                    now));
            List<RoleApiResponse> apiResponses =
                    List.of(
                            new RoleApiResponse(
                                    roleId.toString(),
                                    null,
                                    "SystemRole",
                                    "System role",
                                    "GLOBAL",
                                    "SYSTEM",
                                    now,
                                    now));

            given(mapper.toQuery(any()))
                    .willReturn(SearchRolesQuery.of(null, null, null, null, 0, 20));
            given(searchRolesUseCase.execute(any(SearchRolesQuery.class)))
                    .willReturn(useCaseResponses);
            given(mapper.toApiResponseList(any())).willReturn(apiResponses);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/roles").param("type", "SYSTEM"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].type").value("SYSTEM"));

            verify(searchRolesUseCase).execute(any(SearchRolesQuery.class));
        }

        @Test
        @DisplayName("[성공] 페이징 파라미터 지원")
        void searchRoles_withPagination_returnsPaged() throws Exception {
            // Given
            given(mapper.toQuery(any()))
                    .willReturn(SearchRolesQuery.of(null, null, null, null, 1, 10));
            given(searchRolesUseCase.execute(any(SearchRolesQuery.class))).willReturn(List.of());
            given(mapper.toApiResponseList(any())).willReturn(List.of());

            // When & Then
            mockMvc.perform(get("/api/v1/auth/roles").param("page", "1").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());

            verify(searchRolesUseCase).execute(any(SearchRolesQuery.class));
        }

        @Test
        @DisplayName("[성공] 결과가 없으면 빈 배열 반환")
        void searchRoles_withNoResults_returnsEmptyArray() throws Exception {
            // Given
            given(mapper.toQuery(any()))
                    .willReturn(SearchRolesQuery.of(null, "nonexistent", null, null, 0, 20));
            given(searchRolesUseCase.execute(any(SearchRolesQuery.class))).willReturn(List.of());
            given(mapper.toApiResponseList(any())).willReturn(List.of());

            // When & Then
            mockMvc.perform(get("/api/v1/auth/roles").param("name", "nonexistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(searchRolesUseCase).execute(any(SearchRolesQuery.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/roles/{roleId}/users - 역할별 사용자 목록 조회")
    class GetRoleUsersTest {

        @Test
        @DisplayName("[성공] 유효한 역할 ID로 사용자 목록 조회 시 200 OK 반환")
        void getRoleUsers_withValidRoleId_returns200Ok() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            Instant now = Instant.now();

            RoleUserResponse useCaseResponse =
                    new RoleUserResponse(
                            userId.toString(),
                            "user@example.com",
                            tenantId.toString(),
                            organizationId.toString(),
                            now);
            PageResponse<RoleUserResponse> pageResponse =
                    PageResponse.of(List.of(useCaseResponse), 0, 20, 1L, 1, true, true);

            RoleUserApiResponse apiResponse =
                    new RoleUserApiResponse(
                            userId.toString(),
                            "user@example.com",
                            tenantId.toString(),
                            organizationId.toString(),
                            now);
            PageResponse<RoleUserApiResponse> apiPageResponse =
                    PageResponse.of(List.of(apiResponse), 0, 20, 1L, 1, true, true);

            given(searchRoleUsersUseCase.execute(any())).willReturn(pageResponse);
            given(mapper.toRoleUserApiPageResponse(any())).willReturn(apiPageResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/roles/{roleId}/users", roleId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.content[0].email").value("user@example.com"))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(1));

            verify(searchRoleUsersUseCase).execute(any());
        }

        @Test
        @DisplayName("[성공] 빈 사용자 목록 조회 시 빈 배열 반환")
        void getRoleUsers_withNoUsers_returnsEmptyArray() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();

            PageResponse<RoleUserResponse> emptyPageResponse =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);
            PageResponse<RoleUserApiResponse> emptyApiPageResponse =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(searchRoleUsersUseCase.execute(any())).willReturn(emptyPageResponse);
            given(mapper.toRoleUserApiPageResponse(any())).willReturn(emptyApiPageResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/roles/{roleId}/users", roleId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));

            verify(searchRoleUsersUseCase).execute(any());
        }

        @Test
        @DisplayName("[성공] 페이지네이션 파라미터로 조회 시 200 OK 반환")
        void getRoleUsers_withPagination_returns200Ok() throws Exception {
            // Given
            UUID roleId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();

            RoleUserResponse useCaseResponse =
                    new RoleUserResponse(
                            userId.toString(), "user@example.com", tenantId.toString(), null, now);
            PageResponse<RoleUserResponse> pageResponse =
                    PageResponse.of(List.of(useCaseResponse), 1, 10, 25L, 3, false, false);

            RoleUserApiResponse apiResponse =
                    new RoleUserApiResponse(
                            userId.toString(), "user@example.com", tenantId.toString(), null, now);
            PageResponse<RoleUserApiResponse> apiPageResponse =
                    PageResponse.of(List.of(apiResponse), 1, 10, 25L, 3, false, false);

            given(searchRoleUsersUseCase.execute(any())).willReturn(pageResponse);
            given(mapper.toRoleUserApiPageResponse(any())).willReturn(apiPageResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/roles/{roleId}/users", roleId)
                                    .param("page", "1")
                                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.page").value(1))
                    .andExpect(jsonPath("$.data.size").value(10))
                    .andExpect(jsonPath("$.data.totalElements").value(25))
                    .andExpect(jsonPath("$.data.totalPages").value(3))
                    .andExpect(jsonPath("$.data.first").value(false))
                    .andExpect(jsonPath("$.data.last").value(false));

            verify(searchRoleUsersUseCase).execute(any());
        }
    }
}
