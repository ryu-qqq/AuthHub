package com.ryuqq.authhub.adapter.in.rest.endpointpermission.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response.EndpointPermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.mapper.EndpointPermissionApiMapper;
import com.ryuqq.authhub.application.endpointpermission.dto.query.GetEndpointPermissionQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.query.GetServiceEndpointPermissionSpecQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.query.SearchEndpointPermissionsQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionSpecItemResponse;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionSpecListResponse;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.GetAllEndpointPermissionSpecUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.GetEndpointPermissionUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.SearchEndpointPermissionsUseCase;
import com.ryuqq.authhub.domain.endpointpermission.exception.EndpointPermissionNotFoundException;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
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
 * EndpointPermissionQueryController 단위 테스트
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
@WebMvcTest(EndpointPermissionQueryController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("EndpointPermissionQueryController 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
class EndpointPermissionQueryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private GetEndpointPermissionUseCase getEndpointPermissionUseCase;

    @MockBean private GetAllEndpointPermissionSpecUseCase getAllEndpointPermissionSpecUseCase;

    @MockBean private SearchEndpointPermissionsUseCase searchEndpointPermissionsUseCase;

    @MockBean private EndpointPermissionApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/endpoint-permissions/spec - 서비스별 엔드포인트 권한 스펙 조회")
    class GetEndpointPermissionSpecTest {

        @Test
        @DisplayName("[성공] 서비스별 엔드포인트 권한 스펙 조회 시 200 OK 반환")
        void getEndpointPermissionSpec_returns200Ok() throws Exception {
            // Given
            EndpointPermissionSpecItemResponse item1 =
                    EndpointPermissionSpecItemResponse.of(
                            "GET",
                            "/api/v1/users/{userId}",
                            "auth-hub",
                            Set.of("ADMIN"),
                            Set.of("user:read"),
                            false);
            EndpointPermissionSpecItemResponse item2 =
                    EndpointPermissionSpecItemResponse.of(
                            "GET", "/api/v1/health", "auth-hub", Set.of(), Set.of(), true);
            EndpointPermissionSpecListResponse response =
                    EndpointPermissionSpecListResponse.of(
                            List.of(item1, item2), "2025-12-11T05:30:00Z");

            given(
                            getAllEndpointPermissionSpecUseCase.execute(
                                    any(GetServiceEndpointPermissionSpecQuery.class)))
                    .willReturn(response);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions/spec")
                                    .param("serviceName", "auth-hub")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.endpoints").isArray())
                    .andExpect(jsonPath("$.data.endpoints.length()").value(2))
                    .andExpect(jsonPath("$.data.endpoints[0].method").value("GET"))
                    .andExpect(
                            jsonPath("$.data.endpoints[0].pattern").value("/api/v1/users/{userId}"))
                    .andExpect(jsonPath("$.data.endpoints[0].service").value("auth-hub"))
                    .andExpect(jsonPath("$.data.endpoints[0].isPublic").value(false))
                    .andExpect(jsonPath("$.data.endpoints[1].isPublic").value(true))
                    .andExpect(jsonPath("$.data.version").value("2025-12-11T05:30:00Z"));

            verify(getAllEndpointPermissionSpecUseCase)
                    .execute(any(GetServiceEndpointPermissionSpecQuery.class));
        }

        @Test
        @DisplayName("[성공] 엔드포인트 권한이 없을 때 빈 목록 반환")
        void getEndpointPermissionSpec_withNoEndpoints_returnsEmptyList() throws Exception {
            // Given
            EndpointPermissionSpecListResponse response =
                    EndpointPermissionSpecListResponse.of(List.of(), Instant.now().toString());

            given(
                            getAllEndpointPermissionSpecUseCase.execute(
                                    any(GetServiceEndpointPermissionSpecQuery.class)))
                    .willReturn(response);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions/spec")
                                    .param("serviceName", "auth-hub")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.endpoints").isArray())
                    .andExpect(jsonPath("$.data.endpoints").isEmpty())
                    .andExpect(jsonPath("$.data.version").exists());

            verify(getAllEndpointPermissionSpecUseCase)
                    .execute(any(GetServiceEndpointPermissionSpecQuery.class));
        }

        @Test
        @DisplayName("[성공] 필수 권한과 역할이 포함된 엔드포인트 스펙 반환")
        void getEndpointPermissionSpec_withRolesAndPermissions_returnsCorrectData()
                throws Exception {
            // Given
            EndpointPermissionSpecItemResponse item =
                    EndpointPermissionSpecItemResponse.of(
                            "DELETE",
                            "/api/v1/users/{userId}",
                            "auth-hub",
                            Set.of("SUPER_ADMIN", "ADMIN"),
                            Set.of("user:delete", "user:write"),
                            false);
            EndpointPermissionSpecListResponse response =
                    EndpointPermissionSpecListResponse.of(List.of(item), "2025-12-11T06:00:00Z");

            given(
                            getAllEndpointPermissionSpecUseCase.execute(
                                    any(GetServiceEndpointPermissionSpecQuery.class)))
                    .willReturn(response);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions/spec")
                                    .param("serviceName", "auth-hub")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.endpoints[0].method").value("DELETE"))
                    .andExpect(jsonPath("$.data.endpoints[0].requiredRoles").isArray())
                    .andExpect(jsonPath("$.data.endpoints[0].requiredPermissions").isArray());

            verify(getAllEndpointPermissionSpecUseCase)
                    .execute(any(GetServiceEndpointPermissionSpecQuery.class));
        }

        @Test
        @DisplayName("[실패] 서비스 이름 누락 시 400 Bad Request 반환")
        void getEndpointPermissionSpec_withoutServiceName_returns400BadRequest() throws Exception {
            // When & Then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions/spec")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/endpoint-permissions/{endpointPermissionId} - 엔드포인트 권한 단건 조회")
    class GetEndpointPermissionTest {

        @Test
        @DisplayName("[성공] 존재하는 엔드포인트 권한 ID로 조회 시 200 OK 반환")
        void getEndpointPermission_withExistingId_returns200Ok() throws Exception {
            // Given
            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            EndpointPermissionResponse useCaseResponse =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users/{userId}",
                            "GET",
                            "사용자 상세 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);
            EndpointPermissionApiResponse apiResponse =
                    new EndpointPermissionApiResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users/{userId}",
                            "GET",
                            "사용자 상세 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);

            given(mapper.toGetQuery(endpointPermissionId))
                    .willReturn(new GetEndpointPermissionQuery(endpointPermissionId));
            given(getEndpointPermissionUseCase.execute(any(GetEndpointPermissionQuery.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(EndpointPermissionResponse.class)))
                    .willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get(
                                            "/api/v1/endpoint-permissions/{endpointPermissionId}",
                                            endpointPermissionId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(endpointPermissionId))
                    .andExpect(jsonPath("$.data.serviceName").value("auth-hub"))
                    .andExpect(jsonPath("$.data.path").value("/api/v1/users/{userId}"))
                    .andExpect(jsonPath("$.data.method").value("GET"))
                    .andExpect(jsonPath("$.data.isPublic").value(false));

            verify(getEndpointPermissionUseCase).execute(any(GetEndpointPermissionQuery.class));
        }

        @Test
        @DisplayName("[성공] 공개 엔드포인트 권한 조회 시 200 OK 반환")
        void getEndpointPermission_withPublicEndpoint_returns200Ok() throws Exception {
            // Given
            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            EndpointPermissionResponse useCaseResponse =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/health",
                            "GET",
                            "Health check endpoint",
                            true,
                            Set.of(),
                            Set.of(),
                            0L,
                            now,
                            now);
            EndpointPermissionApiResponse apiResponse =
                    new EndpointPermissionApiResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/health",
                            "GET",
                            "Health check endpoint",
                            true,
                            Set.of(),
                            Set.of(),
                            0L,
                            now,
                            now);

            given(mapper.toGetQuery(endpointPermissionId))
                    .willReturn(new GetEndpointPermissionQuery(endpointPermissionId));
            given(getEndpointPermissionUseCase.execute(any(GetEndpointPermissionQuery.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(EndpointPermissionResponse.class)))
                    .willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get(
                                            "/api/v1/endpoint-permissions/{endpointPermissionId}",
                                            endpointPermissionId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(endpointPermissionId))
                    .andExpect(jsonPath("$.data.isPublic").value(true))
                    .andExpect(jsonPath("$.data.requiredPermissions").isEmpty())
                    .andExpect(jsonPath("$.data.requiredRoles").isEmpty());

            verify(getEndpointPermissionUseCase).execute(any(GetEndpointPermissionQuery.class));
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 엔드포인트 권한 ID로 조회 시 404 Not Found 반환")
        void getEndpointPermission_withNonExistingId_returns404NotFound() throws Exception {
            // Given
            String endpointPermissionId = UUID.randomUUID().toString();
            given(mapper.toGetQuery(endpointPermissionId))
                    .willReturn(new GetEndpointPermissionQuery(endpointPermissionId));
            given(getEndpointPermissionUseCase.execute(any(GetEndpointPermissionQuery.class)))
                    .willThrow(
                            new EndpointPermissionNotFoundException(
                                    EndpointPermissionId.of(endpointPermissionId)));

            ErrorMapper.MappedError mappedError =
                    new ErrorMapper.MappedError(
                            HttpStatus.NOT_FOUND,
                            "Endpoint Permission Not Found",
                            "Endpoint permission not found",
                            URI.create(
                                    "https://authhub.ryuqq.com/errors/endpoint-permission-not-found"));
            given(errorMapperRegistry.map(any(), any())).willReturn(Optional.of(mappedError));

            // When & Then
            mockMvc.perform(
                            get(
                                            "/api/v1/endpoint-permissions/{endpointPermissionId}",
                                            endpointPermissionId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.title").value("Endpoint Permission Not Found"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/endpoint-permissions - 엔드포인트 권한 목록 검색")
    class SearchEndpointPermissionsTest {

        @Test
        @DisplayName("[성공] 필터 없이 조회 시 기본값으로 200 OK 반환")
        void searchEndpointPermissions_withoutFilters_returns200Ok() throws Exception {
            // Given
            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            EndpointPermissionResponse response =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            "사용자 목록 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);
            EndpointPermissionApiResponse apiResponse =
                    new EndpointPermissionApiResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            "사용자 목록 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);

            given(mapper.toQuery(any()))
                    .willReturn(new SearchEndpointPermissionsQuery(null, null, null, null, 0, 20));
            given(
                            searchEndpointPermissionsUseCase.execute(
                                    any(SearchEndpointPermissionsQuery.class)))
                    .willReturn(List.of(response));
            given(mapper.toApiResponseList(any())).willReturn(List.of(apiResponse));

            // When & Then
            mockMvc.perform(get("/api/v1/endpoint-permissions").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].id").value(endpointPermissionId))
                    .andExpect(jsonPath("$.data[0].serviceName").value("auth-hub"));

            verify(searchEndpointPermissionsUseCase)
                    .execute(any(SearchEndpointPermissionsQuery.class));
        }

        @Test
        @DisplayName("[성공] 서비스 이름 필터로 조회 시 200 OK 반환")
        void searchEndpointPermissions_withServiceNameFilter_returns200Ok() throws Exception {
            // Given
            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            EndpointPermissionResponse response =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            "사용자 목록 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);
            EndpointPermissionApiResponse apiResponse =
                    new EndpointPermissionApiResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            "사용자 목록 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);

            given(mapper.toQuery(any()))
                    .willReturn(
                            new SearchEndpointPermissionsQuery(
                                    "auth-hub", null, null, null, 0, 20));
            given(
                            searchEndpointPermissionsUseCase.execute(
                                    any(SearchEndpointPermissionsQuery.class)))
                    .willReturn(List.of(response));
            given(mapper.toApiResponseList(any())).willReturn(List.of(apiResponse));

            // When & Then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions")
                                    .param("serviceName", "auth-hub")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].serviceName").value("auth-hub"));

            verify(searchEndpointPermissionsUseCase)
                    .execute(any(SearchEndpointPermissionsQuery.class));
        }

        @Test
        @DisplayName("[성공] 경로 패턴 필터로 조회 시 200 OK 반환")
        void searchEndpointPermissions_withPathPatternFilter_returns200Ok() throws Exception {
            // Given
            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            EndpointPermissionResponse response =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users/{userId}",
                            "GET",
                            "사용자 상세 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);
            EndpointPermissionApiResponse apiResponse =
                    new EndpointPermissionApiResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users/{userId}",
                            "GET",
                            "사용자 상세 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);

            given(mapper.toQuery(any()))
                    .willReturn(
                            new SearchEndpointPermissionsQuery(null, "/users", null, null, 0, 20));
            given(
                            searchEndpointPermissionsUseCase.execute(
                                    any(SearchEndpointPermissionsQuery.class)))
                    .willReturn(List.of(response));
            given(mapper.toApiResponseList(any())).willReturn(List.of(apiResponse));

            // When & Then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions")
                                    .param("pathPattern", "/users")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].path").value("/api/v1/users/{userId}"));

            verify(searchEndpointPermissionsUseCase)
                    .execute(any(SearchEndpointPermissionsQuery.class));
        }

        @Test
        @DisplayName("[성공] HTTP 메서드 필터로 조회 시 200 OK 반환")
        void searchEndpointPermissions_withMethodFilter_returns200Ok() throws Exception {
            // Given
            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            EndpointPermissionResponse response =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users",
                            "POST",
                            "사용자 생성",
                            false,
                            Set.of("user:write"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);
            EndpointPermissionApiResponse apiResponse =
                    new EndpointPermissionApiResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users",
                            "POST",
                            "사용자 생성",
                            false,
                            Set.of("user:write"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);

            given(mapper.toQuery(any()))
                    .willReturn(
                            new SearchEndpointPermissionsQuery(null, null, "POST", null, 0, 20));
            given(
                            searchEndpointPermissionsUseCase.execute(
                                    any(SearchEndpointPermissionsQuery.class)))
                    .willReturn(List.of(response));
            given(mapper.toApiResponseList(any())).willReturn(List.of(apiResponse));

            // When & Then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions")
                                    .param("method", "POST")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].method").value("POST"));

            verify(searchEndpointPermissionsUseCase)
                    .execute(any(SearchEndpointPermissionsQuery.class));
        }

        @Test
        @DisplayName("[성공] 공개 여부 필터로 조회 시 200 OK 반환")
        void searchEndpointPermissions_withIsPublicFilter_returns200Ok() throws Exception {
            // Given
            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            EndpointPermissionResponse response =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/health",
                            "GET",
                            "Health check",
                            true,
                            Set.of(),
                            Set.of(),
                            0L,
                            now,
                            now);
            EndpointPermissionApiResponse apiResponse =
                    new EndpointPermissionApiResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/health",
                            "GET",
                            "Health check",
                            true,
                            Set.of(),
                            Set.of(),
                            0L,
                            now,
                            now);

            given(mapper.toQuery(any()))
                    .willReturn(new SearchEndpointPermissionsQuery(null, null, null, true, 0, 20));
            given(
                            searchEndpointPermissionsUseCase.execute(
                                    any(SearchEndpointPermissionsQuery.class)))
                    .willReturn(List.of(response));
            given(mapper.toApiResponseList(any())).willReturn(List.of(apiResponse));

            // When & Then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions")
                                    .param("isPublic", "true")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].isPublic").value(true));

            verify(searchEndpointPermissionsUseCase)
                    .execute(any(SearchEndpointPermissionsQuery.class));
        }

        @Test
        @DisplayName("[성공] 복합 필터로 조회 시 200 OK 반환")
        void searchEndpointPermissions_withMultipleFilters_returns200Ok() throws Exception {
            // Given
            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            EndpointPermissionResponse response =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            "사용자 목록 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);
            EndpointPermissionApiResponse apiResponse =
                    new EndpointPermissionApiResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            "사용자 목록 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            0L,
                            now,
                            now);

            given(mapper.toQuery(any()))
                    .willReturn(
                            new SearchEndpointPermissionsQuery(
                                    "auth-hub", "/users", "GET", false, 0, 10));
            given(
                            searchEndpointPermissionsUseCase.execute(
                                    any(SearchEndpointPermissionsQuery.class)))
                    .willReturn(List.of(response));
            given(mapper.toApiResponseList(any())).willReturn(List.of(apiResponse));

            // When & Then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions")
                                    .param("serviceName", "auth-hub")
                                    .param("pathPattern", "/users")
                                    .param("method", "GET")
                                    .param("isPublic", "false")
                                    .param("page", "0")
                                    .param("size", "10")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());

            verify(searchEndpointPermissionsUseCase)
                    .execute(any(SearchEndpointPermissionsQuery.class));
        }

        @Test
        @DisplayName("[성공] 페이지네이션 적용하여 조회 시 200 OK 반환")
        void searchEndpointPermissions_withPagination_returns200Ok() throws Exception {
            // Given
            given(mapper.toQuery(any()))
                    .willReturn(new SearchEndpointPermissionsQuery(null, null, null, null, 1, 5));
            given(
                            searchEndpointPermissionsUseCase.execute(
                                    any(SearchEndpointPermissionsQuery.class)))
                    .willReturn(List.of());
            given(mapper.toApiResponseList(any())).willReturn(List.of());

            // When & Then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions")
                                    .param("page", "1")
                                    .param("size", "5")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(searchEndpointPermissionsUseCase)
                    .execute(any(SearchEndpointPermissionsQuery.class));
        }
    }
}
