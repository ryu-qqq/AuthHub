package com.ryuqq.authhub.adapter.in.rest.organization.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.query.GetOrganizationQuery;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationUserResponse;
import com.ryuqq.authhub.application.organization.port.in.query.GetOrganizationDetailUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.GetOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationUsersUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsAdminUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsUseCase;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
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
 * OrganizationQueryController 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>HTTP 요청/응답 매핑
 *   <li>Request 파라미터 검증
 *   <li>Response DTO 직렬화
 *   <li>HTTP Status Code
 *   <li>UseCase 호출 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(OrganizationQueryController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("OrganizationQueryController 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
class OrganizationQueryControllerTest {

    private static final Instant NOW = Instant.now();
    private static final Instant ONE_MONTH_AGO = NOW.minus(30, ChronoUnit.DAYS);

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private GetOrganizationUseCase getOrganizationUseCase;

    @MockBean private GetOrganizationDetailUseCase getOrganizationDetailUseCase;

    @MockBean private SearchOrganizationsUseCase searchOrganizationsUseCase;

    @MockBean private SearchOrganizationsAdminUseCase searchOrganizationsAdminUseCase;

    @MockBean private SearchOrganizationUsersUseCase searchOrganizationUsersUseCase;

    @MockBean private OrganizationApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/organizations/{organizationId} - 조직 단건 조회")
    class GetOrganizationTest {

        @Test
        @DisplayName("[성공] 유효한 ID로 조직 조회 시 200 OK 반환")
        void getOrganization_withValidId_returns200Ok() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();
            OrganizationResponse useCaseResponse =
                    new OrganizationResponse(
                            organizationId, tenantId, "TestOrg", "ACTIVE", now, now);
            OrganizationApiResponse apiResponse =
                    new OrganizationApiResponse(
                            organizationId.toString(),
                            tenantId.toString(),
                            "TestOrg",
                            "ACTIVE",
                            now,
                            now);

            given(mapper.toGetQuery(organizationId.toString()))
                    .willReturn(new GetOrganizationQuery(organizationId));
            given(getOrganizationUseCase.execute(any(GetOrganizationQuery.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toApiResponse(useCaseResponse)).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/organizations/{organizationId}", organizationId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.organizationId").value(organizationId.toString()))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId.toString()))
                    .andExpect(jsonPath("$.data.name").value("TestOrg"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));

            verify(getOrganizationUseCase).execute(any(GetOrganizationQuery.class));
        }

        @Test
        @DisplayName("[실패] 잘못된 UUID 형식이면 400 Bad Request 반환")
        void getOrganization_withInvalidUuid_returns400BadRequest() throws Exception {
            // Given - mapper가 잘못된 UUID로 IllegalArgumentException 던지도록 설정
            given(mapper.toGetQuery("invalid-uuid"))
                    .willThrow(new IllegalArgumentException("Invalid UUID string: invalid-uuid"));

            // When & Then
            mockMvc.perform(get("/api/v1/auth/organizations/{organizationId}", "invalid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/organizations - 조직 목록 검색")
    class SearchOrganizationsTest {

        @Test
        @DisplayName("[성공] 테넌트 ID로 조직 목록 조회 시 200 OK 반환")
        void searchOrganizations_withTenantId_returns200Ok() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();

            OrganizationResponse orgResponse =
                    new OrganizationResponse(
                            organizationId, tenantId, "TestOrg", "ACTIVE", now, now);
            PageResponse<OrganizationResponse> pageResponse =
                    PageResponse.of(List.of(orgResponse), 0, 20, 1L, 1, true, true);

            OrganizationApiResponse apiOrgResponse =
                    new OrganizationApiResponse(
                            organizationId.toString(),
                            tenantId.toString(),
                            "TestOrg",
                            "ACTIVE",
                            now,
                            now);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchOrganizationsQuery.of(
                                    tenantId, null, null, ONE_MONTH_AGO, NOW, 0, 20));
            given(searchOrganizationsUseCase.execute(any(SearchOrganizationsQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toApiResponse(any(OrganizationResponse.class))).willReturn(apiOrgResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/organizations")
                                    .param("tenantId", tenantId.toString())
                                    .param("createdFrom", ONE_MONTH_AGO.toString())
                                    .param("createdTo", NOW.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(
                            jsonPath("$.data.content[0].organizationId")
                                    .value(organizationId.toString()))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(1));

            verify(searchOrganizationsUseCase).execute(any(SearchOrganizationsQuery.class));
        }

        @Test
        @DisplayName("[성공] 이름 필터로 조직 검색 시 200 OK 반환")
        void searchOrganizations_withNameFilter_returns200Ok() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();

            OrganizationResponse orgResponse =
                    new OrganizationResponse(
                            organizationId, tenantId, "TestOrg", "ACTIVE", now, now);
            PageResponse<OrganizationResponse> pageResponse =
                    PageResponse.of(List.of(orgResponse), 0, 20, 1L, 1, true, true);

            OrganizationApiResponse apiOrgResponse =
                    new OrganizationApiResponse(
                            organizationId.toString(),
                            tenantId.toString(),
                            "TestOrg",
                            "ACTIVE",
                            now,
                            now);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchOrganizationsQuery.of(
                                    tenantId, "Test", null, ONE_MONTH_AGO, NOW, 0, 20));
            given(searchOrganizationsUseCase.execute(any(SearchOrganizationsQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toApiResponse(any(OrganizationResponse.class))).willReturn(apiOrgResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/organizations")
                                    .param("tenantId", tenantId.toString())
                                    .param("name", "Test")
                                    .param("createdFrom", ONE_MONTH_AGO.toString())
                                    .param("createdTo", NOW.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray());

            verify(searchOrganizationsUseCase).execute(any(SearchOrganizationsQuery.class));
        }

        @Test
        @DisplayName("[성공] 상태 필터로 조직 검색 시 200 OK 반환")
        void searchOrganizations_withStatusFilter_returns200Ok() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();

            OrganizationResponse orgResponse =
                    new OrganizationResponse(
                            organizationId, tenantId, "TestOrg", "INACTIVE", now, now);
            PageResponse<OrganizationResponse> pageResponse =
                    PageResponse.of(List.of(orgResponse), 0, 20, 1L, 1, true, true);

            OrganizationApiResponse apiOrgResponse =
                    new OrganizationApiResponse(
                            organizationId.toString(),
                            tenantId.toString(),
                            "TestOrg",
                            "INACTIVE",
                            now,
                            now);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchOrganizationsQuery.of(
                                    tenantId,
                                    null,
                                    List.of("INACTIVE"),
                                    ONE_MONTH_AGO,
                                    NOW,
                                    0,
                                    20));
            given(searchOrganizationsUseCase.execute(any(SearchOrganizationsQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toApiResponse(any(OrganizationResponse.class))).willReturn(apiOrgResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/organizations")
                                    .param("tenantId", tenantId.toString())
                                    .param("statuses", "INACTIVE")
                                    .param("createdFrom", ONE_MONTH_AGO.toString())
                                    .param("createdTo", NOW.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content[0].status").value("INACTIVE"));

            verify(searchOrganizationsUseCase).execute(any(SearchOrganizationsQuery.class));
        }

        @Test
        @DisplayName("[성공] 빈 결과 조회 시 빈 배열 반환")
        void searchOrganizations_withNoResults_returnsEmptyArray() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();

            PageResponse<OrganizationResponse> emptyPageResponse =
                    PageResponse.of(Collections.emptyList(), 0, 20, 0L, 0, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchOrganizationsQuery.of(
                                    tenantId, null, null, ONE_MONTH_AGO, NOW, 0, 20));
            given(searchOrganizationsUseCase.execute(any(SearchOrganizationsQuery.class)))
                    .willReturn(emptyPageResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/organizations")
                                    .param("tenantId", tenantId.toString())
                                    .param("createdFrom", ONE_MONTH_AGO.toString())
                                    .param("createdTo", NOW.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));

            verify(searchOrganizationsUseCase).execute(any(SearchOrganizationsQuery.class));
        }

        @Test
        @DisplayName("[성공] 페이지네이션 파라미터로 조회 시 200 OK 반환")
        void searchOrganizations_withPagination_returns200Ok() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();

            OrganizationResponse orgResponse =
                    new OrganizationResponse(
                            organizationId, tenantId, "TestOrg", "ACTIVE", now, now);
            PageResponse<OrganizationResponse> pageResponse =
                    PageResponse.of(List.of(orgResponse), 1, 10, 25L, 3, false, false);

            OrganizationApiResponse apiOrgResponse =
                    new OrganizationApiResponse(
                            organizationId.toString(),
                            tenantId.toString(),
                            "TestOrg",
                            "ACTIVE",
                            now,
                            now);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchOrganizationsQuery.of(
                                    tenantId, null, null, ONE_MONTH_AGO, NOW, 1, 10));
            given(searchOrganizationsUseCase.execute(any(SearchOrganizationsQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toApiResponse(any(OrganizationResponse.class))).willReturn(apiOrgResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/organizations")
                                    .param("tenantId", tenantId.toString())
                                    .param("createdFrom", ONE_MONTH_AGO.toString())
                                    .param("createdTo", NOW.toString())
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

            verify(searchOrganizationsUseCase).execute(any(SearchOrganizationsQuery.class));
        }

        @Test
        @DisplayName("[실패] 테넌트 ID 없이 조회 시 400 Bad Request 반환")
        void searchOrganizations_withoutTenantId_returns400BadRequest() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/auth/organizations")).andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/organizations/{organizationId}/users - 조직별 사용자 목록 조회")
    class GetOrganizationUsersTest {

        @Test
        @DisplayName("[성공] 유효한 조직 ID로 사용자 목록 조회 시 200 OK 반환")
        void getOrganizationUsers_withValidOrganizationId_returns200Ok() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();

            OrganizationUserResponse useCaseResponse =
                    new OrganizationUserResponse(
                            userId.toString(), "user@example.com", tenantId.toString(), now);
            PageResponse<OrganizationUserResponse> pageResponse =
                    PageResponse.of(List.of(useCaseResponse), 0, 20, 1L, 1, true, true);

            OrganizationUserApiResponse apiResponse =
                    new OrganizationUserApiResponse(
                            userId.toString(), "user@example.com", tenantId.toString(), now);
            PageResponse<OrganizationUserApiResponse> apiPageResponse =
                    PageResponse.of(List.of(apiResponse), 0, 20, 1L, 1, true, true);

            given(searchOrganizationUsersUseCase.execute(any())).willReturn(pageResponse);
            given(mapper.toOrganizationUserApiPageResponse(any())).willReturn(apiPageResponse);

            // When & Then
            mockMvc.perform(
                            get(
                                    "/api/v1/auth/organizations/{organizationId}/users",
                                    organizationId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.content[0].email").value("user@example.com"))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(1));

            verify(searchOrganizationUsersUseCase).execute(any());
        }

        @Test
        @DisplayName("[성공] 빈 사용자 목록 조회 시 빈 배열 반환")
        void getOrganizationUsers_withNoUsers_returnsEmptyArray() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();

            PageResponse<OrganizationUserResponse> emptyPageResponse =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);
            PageResponse<OrganizationUserApiResponse> emptyApiPageResponse =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(searchOrganizationUsersUseCase.execute(any())).willReturn(emptyPageResponse);
            given(mapper.toOrganizationUserApiPageResponse(any())).willReturn(emptyApiPageResponse);

            // When & Then
            mockMvc.perform(
                            get(
                                    "/api/v1/auth/organizations/{organizationId}/users",
                                    organizationId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));

            verify(searchOrganizationUsersUseCase).execute(any());
        }

        @Test
        @DisplayName("[성공] 페이지네이션 파라미터로 조회 시 200 OK 반환")
        void getOrganizationUsers_withPagination_returns200Ok() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            Instant now = Instant.now();

            OrganizationUserResponse useCaseResponse =
                    new OrganizationUserResponse(
                            userId.toString(), "user@example.com", tenantId.toString(), now);
            PageResponse<OrganizationUserResponse> pageResponse =
                    PageResponse.of(List.of(useCaseResponse), 1, 10, 25L, 3, false, false);

            OrganizationUserApiResponse apiResponse =
                    new OrganizationUserApiResponse(
                            userId.toString(), "user@example.com", tenantId.toString(), now);
            PageResponse<OrganizationUserApiResponse> apiPageResponse =
                    PageResponse.of(List.of(apiResponse), 1, 10, 25L, 3, false, false);

            given(searchOrganizationUsersUseCase.execute(any())).willReturn(pageResponse);
            given(mapper.toOrganizationUserApiPageResponse(any())).willReturn(apiPageResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/organizations/{organizationId}/users", organizationId)
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

            verify(searchOrganizationUsersUseCase).execute(any());
        }
    }
}
