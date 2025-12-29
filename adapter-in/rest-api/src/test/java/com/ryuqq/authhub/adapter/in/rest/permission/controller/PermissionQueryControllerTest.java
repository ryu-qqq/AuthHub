package com.ryuqq.authhub.adapter.in.rest.permission.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.UserPermissionsApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.mapper.PermissionApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.permission.dto.query.GetPermissionQuery;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.port.in.query.GetPermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.query.SearchPermissionsUseCase;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.role.port.in.GetUserRolesUseCase;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * PermissionQueryController 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>HTTP 요청/응답 매핑
 *   <li>Query Parameter 바인딩
 *   <li>Response DTO 직렬화
 *   <li>HTTP Status Code
 *   <li>UseCase 호출 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(PermissionQueryController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PermissionQueryController 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
class PermissionQueryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private GetPermissionUseCase getPermissionUseCase;

    @MockBean private SearchPermissionsUseCase searchPermissionsUseCase;

    @MockBean private GetUserRolesUseCase getUserRolesUseCase;

    @MockBean private PermissionApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/permissions/{permissionId} - 권한 단건 조회")
    class GetPermissionTest {

        @Test
        @DisplayName("[성공] 존재하는 권한 ID로 조회 시 200 OK 반환")
        void getPermission_withExistingId_returns200Ok() throws Exception {
            // Given
            String permissionId = UUID.randomUUID().toString();
            PermissionResponse useCaseResponse =
                    new PermissionResponse(
                            UUID.fromString(permissionId),
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            Instant.now(),
                            Instant.now());
            PermissionApiResponse apiResponse =
                    new PermissionApiResponse(
                            permissionId,
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            Instant.now(),
                            Instant.now());

            given(mapper.toGetQuery(permissionId))
                    .willReturn(new GetPermissionQuery(UUID.fromString(permissionId)));
            given(getPermissionUseCase.execute(any(GetPermissionQuery.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(PermissionResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/permissions/{permissionId}", permissionId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.permissionId").value(permissionId))
                    .andExpect(jsonPath("$.data.key").value("tenant:read"))
                    .andExpect(jsonPath("$.data.resource").value("tenant"))
                    .andExpect(jsonPath("$.data.action").value("read"))
                    .andExpect(jsonPath("$.data.type").value("SYSTEM"));

            verify(getPermissionUseCase).execute(any(GetPermissionQuery.class));
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 권한 ID로 조회 시 404 Not Found 반환")
        void getPermission_withNonExistingId_returns404NotFound() throws Exception {
            // Given
            String permissionId = UUID.randomUUID().toString();
            given(mapper.toGetQuery(permissionId))
                    .willReturn(new GetPermissionQuery(UUID.fromString(permissionId)));
            given(getPermissionUseCase.execute(any(GetPermissionQuery.class)))
                    .willThrow(
                            new PermissionNotFoundException(
                                    PermissionId.of(UUID.fromString(permissionId))));

            ErrorMapper.MappedError mappedError =
                    new ErrorMapper.MappedError(
                            HttpStatus.NOT_FOUND,
                            "Permission Not Found",
                            "Permission not found",
                            URI.create("https://authhub.ryuqq.com/errors/permission-not-found"));
            given(errorMapperRegistry.map(any(), any())).willReturn(Optional.of(mappedError));

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/permissions/{permissionId}", permissionId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.title").value("Permission Not Found"));
        }

        @Test
        @DisplayName("[실패] 잘못된 UUID 형식으로 조회 시 400 Bad Request 반환")
        void getPermission_withInvalidUuid_returns400BadRequest() throws Exception {
            // Given
            String invalidUuid = "not-a-valid-uuid";

            given(mapper.toGetQuery(any(String.class)))
                    .willThrow(new IllegalArgumentException("Invalid UUID format"));

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/permissions/{permissionId}", invalidUuid)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/permissions - 권한 목록 검색")
    class SearchPermissionsTest {

        private static final Instant CREATED_FROM = Instant.parse("2024-01-01T00:00:00Z");
        private static final Instant CREATED_TO = Instant.parse("2024-12-31T23:59:59Z");

        @Test
        @DisplayName("[성공] 필터 없이 조회 시 기본값으로 200 OK 반환")
        void searchPermissions_withoutFilters_returns200Ok() throws Exception {
            // Given
            UUID permissionId = UUID.randomUUID();
            Instant now = Instant.now();
            PermissionResponse response =
                    new PermissionResponse(
                            permissionId,
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PermissionApiResponse apiResponse =
                    new PermissionApiResponse(
                            permissionId.toString(),
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PageResponse<PermissionResponse> pageResponse =
                    PageResponse.of(List.of(response), 0, 20, 1, 1, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchPermissionsQuery.of(
                                    null, null, null, CREATED_FROM, CREATED_TO, 0, 20));
            given(searchPermissionsUseCase.execute(any(SearchPermissionsQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toApiResponse(any(PermissionResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/permissions")
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(
                            jsonPath("$.data.content[0].permissionId")
                                    .value(permissionId.toString()))
                    .andExpect(jsonPath("$.data.content[0].key").value("tenant:read"));

            verify(searchPermissionsUseCase).execute(any(SearchPermissionsQuery.class));
        }

        @Test
        @DisplayName("[성공] 리소스 필터로 조회 시 200 OK 반환")
        void searchPermissions_withResourceFilter_returns200Ok() throws Exception {
            // Given
            UUID permissionId = UUID.randomUUID();
            Instant now = Instant.now();
            PermissionResponse response =
                    new PermissionResponse(
                            permissionId,
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PermissionApiResponse apiResponse =
                    new PermissionApiResponse(
                            permissionId.toString(),
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PageResponse<PermissionResponse> pageResponse =
                    PageResponse.of(List.of(response), 0, 20, 1, 1, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchPermissionsQuery.of(
                                    "tenant", null, null, CREATED_FROM, CREATED_TO, 0, 20));
            given(searchPermissionsUseCase.execute(any(SearchPermissionsQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toApiResponse(any(PermissionResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/permissions")
                                    .param("resource", "tenant")
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].resource").value("tenant"));

            verify(searchPermissionsUseCase).execute(any(SearchPermissionsQuery.class));
        }

        @Test
        @DisplayName("[성공] 액션 필터로 조회 시 200 OK 반환")
        void searchPermissions_withActionFilter_returns200Ok() throws Exception {
            // Given
            UUID permissionId = UUID.randomUUID();
            Instant now = Instant.now();
            PermissionResponse response =
                    new PermissionResponse(
                            permissionId,
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PermissionApiResponse apiResponse =
                    new PermissionApiResponse(
                            permissionId.toString(),
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PageResponse<PermissionResponse> pageResponse =
                    PageResponse.of(List.of(response), 0, 20, 1, 1, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchPermissionsQuery.of(
                                    null, "read", null, CREATED_FROM, CREATED_TO, 0, 20));
            given(searchPermissionsUseCase.execute(any(SearchPermissionsQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toApiResponse(any(PermissionResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/permissions")
                                    .param("action", "read")
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].action").value("read"));

            verify(searchPermissionsUseCase).execute(any(SearchPermissionsQuery.class));
        }

        @Test
        @DisplayName("[성공] 권한 유형 필터로 조회 시 200 OK 반환")
        void searchPermissions_withTypesFilter_returns200Ok() throws Exception {
            // Given
            UUID permissionId = UUID.randomUUID();
            Instant now = Instant.now();
            PermissionResponse response =
                    new PermissionResponse(
                            permissionId,
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PermissionApiResponse apiResponse =
                    new PermissionApiResponse(
                            permissionId.toString(),
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PageResponse<PermissionResponse> pageResponse =
                    PageResponse.of(List.of(response), 0, 20, 1, 1, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchPermissionsQuery.of(
                                    null,
                                    null,
                                    List.of("SYSTEM"),
                                    CREATED_FROM,
                                    CREATED_TO,
                                    0,
                                    20));
            given(searchPermissionsUseCase.execute(any(SearchPermissionsQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toApiResponse(any(PermissionResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/permissions")
                                    .param("types", "SYSTEM")
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].type").value("SYSTEM"));

            verify(searchPermissionsUseCase).execute(any(SearchPermissionsQuery.class));
        }

        @Test
        @DisplayName("[성공] 복합 필터로 조회 시 200 OK 반환")
        void searchPermissions_withMultipleFilters_returns200Ok() throws Exception {
            // Given
            UUID permissionId = UUID.randomUUID();
            Instant now = Instant.now();
            PermissionResponse response =
                    new PermissionResponse(
                            permissionId,
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PermissionApiResponse apiResponse =
                    new PermissionApiResponse(
                            permissionId.toString(),
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PageResponse<PermissionResponse> pageResponse =
                    PageResponse.of(List.of(response), 0, 10, 1, 1, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchPermissionsQuery.of(
                                    "tenant",
                                    "read",
                                    List.of("SYSTEM"),
                                    CREATED_FROM,
                                    CREATED_TO,
                                    0,
                                    10));
            given(searchPermissionsUseCase.execute(any(SearchPermissionsQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toApiResponse(any(PermissionResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/permissions")
                                    .param("resource", "tenant")
                                    .param("action", "read")
                                    .param("types", "SYSTEM")
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .param("page", "0")
                                    .param("size", "10")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray());

            verify(searchPermissionsUseCase).execute(any(SearchPermissionsQuery.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/permissions/users/{userId} - 사용자 권한 조회")
    class GetUserPermissionsTest {

        @Test
        @DisplayName("[성공] 존재하는 사용자 ID로 조회 시 200 OK 반환")
        void getUserPermissions_withExistingUserId_returns200Ok() throws Exception {
            // Given
            UUID userId = UUID.randomUUID();
            UserRolesResponse useCaseResponse =
                    new UserRolesResponse(
                            userId,
                            Set.of("TENANT_ADMIN", "USER"),
                            Set.of("tenant:read", "user:read"));
            UserPermissionsApiResponse apiResponse =
                    new UserPermissionsApiResponse(
                            userId.toString(),
                            Set.of("TENANT_ADMIN", "USER"),
                            Set.of("tenant:read", "user:read"));

            given(getUserRolesUseCase.execute(userId)).willReturn(useCaseResponse);
            given(mapper.toUserPermissionsApiResponse(any(UserRolesResponse.class)))
                    .willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/permissions/users/{userId}", userId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.roles").isArray())
                    .andExpect(jsonPath("$.data.permissions").isArray());

            verify(getUserRolesUseCase).execute(userId);
        }

        @Test
        @DisplayName("[실패] 잘못된 UUID 형식으로 조회 시 400 Bad Request 반환")
        void getUserPermissions_withInvalidUuid_returns400BadRequest() throws Exception {
            // Given
            String invalidUuid = "not-a-valid-uuid";

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/permissions/users/{userId}", invalidUuid)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }
}
