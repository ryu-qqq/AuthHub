package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.query.GetTenantQuery;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantDetailUseCase;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantUseCase;
import com.ryuqq.authhub.application.tenant.port.in.query.SearchTenantsAdminUseCase;
import com.ryuqq.authhub.application.tenant.port.in.query.SearchTenantsUseCase;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
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
 * TenantQueryController 단위 테스트
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
@WebMvcTest(TenantQueryController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TenantQueryController 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
class TenantQueryControllerTest {

    private static final Instant NOW = Instant.now();
    private static final Instant ONE_MONTH_AGO = NOW.minus(30, ChronoUnit.DAYS);

    @Autowired private MockMvc mockMvc;

    @MockBean private GetTenantUseCase getTenantUseCase;

    @MockBean private GetTenantDetailUseCase getTenantDetailUseCase;

    @MockBean private SearchTenantsUseCase searchTenantsUseCase;

    @MockBean private SearchTenantsAdminUseCase searchTenantsAdminUseCase;

    @MockBean private TenantApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/tenants/{tenantId} - 테넌트 단건 조회")
    class GetTenantTest {

        @Test
        @DisplayName("[성공] 존재하는 테넌트 ID로 조회 시 200 OK 반환")
        void getTenant_withExistingId_returns200Ok() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            TenantResponse useCaseResponse =
                    new TenantResponse(
                            tenantId, "TestTenant", "ACTIVE", Instant.now(), Instant.now());
            TenantApiResponse apiResponse =
                    new TenantApiResponse(
                            tenantId.toString(),
                            "TestTenant",
                            "ACTIVE",
                            Instant.now(),
                            Instant.now());

            given(getTenantUseCase.execute(any(GetTenantQuery.class))).willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(TenantResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/tenants/{tenantId}", tenantId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId.toString()))
                    .andExpect(jsonPath("$.data.name").value("TestTenant"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));

            verify(getTenantUseCase).execute(any(GetTenantQuery.class));
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 테넌트 ID로 조회 시 404 Not Found 반환")
        void getTenant_withNonExistingId_returns404NotFound() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            given(getTenantUseCase.execute(any(GetTenantQuery.class)))
                    .willThrow(new TenantNotFoundException(TenantId.of(tenantId)));

            // ErrorMapperRegistry mock 설정 - 404 응답 매핑
            ErrorMapper.MappedError mappedError =
                    new ErrorMapper.MappedError(
                            HttpStatus.NOT_FOUND,
                            "Tenant Not Found",
                            "Tenant not found",
                            URI.create("https://authhub.ryuqq.com/errors/tenant-not-found"));
            given(errorMapperRegistry.map(any(), any())).willReturn(Optional.of(mappedError));

            // When & Then
            // GlobalExceptionHandler가 ProblemDetail (RFC 7807) 형식으로 반환
            mockMvc.perform(
                            get("/api/v1/auth/tenants/{tenantId}", tenantId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.title").value("Tenant Not Found"));
        }

        @Test
        @DisplayName("[실패] 잘못된 UUID 형식으로 조회 시 400 Bad Request 반환")
        void getTenant_withInvalidUuid_returns400BadRequest() throws Exception {
            // Given
            String invalidUuid = "not-a-valid-uuid";

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/tenants/{tenantId}", invalidUuid)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/tenants - 테넌트 목록 조회")
    class SearchTenantsTest {

        @Test
        @DisplayName("[성공] 페이징 파라미터 없이 조회 시 기본값으로 200 OK 반환")
        void searchTenants_withoutParams_returns200Ok() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            TenantResponse response =
                    new TenantResponse(
                            tenantId, "TestTenant", "ACTIVE", Instant.now(), Instant.now());
            PageResponse<TenantResponse> pageResponse =
                    PageResponse.of(List.of(response), 0, 20, 1, 1, true, true);
            TenantApiResponse apiResponse =
                    new TenantApiResponse(
                            tenantId.toString(),
                            "TestTenant",
                            "ACTIVE",
                            Instant.now(),
                            Instant.now());

            given(mapper.toQuery(any()))
                    .willReturn(SearchTenantsQuery.of(null, null, ONE_MONTH_AGO, NOW, 0, 20));
            given(searchTenantsUseCase.execute(any(SearchTenantsQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toApiResponse(any(TenantResponse.class))).willReturn(apiResponse);

            // When & Then
            // ApiResponse<PageApiResponse<T>> 형식으로 래핑
            mockMvc.perform(
                            get("/api/v1/auth/tenants")
                                    .param("createdFrom", ONE_MONTH_AGO.toString())
                                    .param("createdTo", NOW.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.totalElements").value(1));

            verify(searchTenantsUseCase).execute(any(SearchTenantsQuery.class));
        }

        @Test
        @DisplayName("[성공] 이름 필터와 페이징 파라미터로 조회 시 200 OK 반환")
        void searchTenants_withNameFilterAndPaging_returns200Ok() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            TenantResponse response =
                    new TenantResponse(
                            tenantId, "TestTenant", "ACTIVE", Instant.now(), Instant.now());
            PageResponse<TenantResponse> pageResponse =
                    PageResponse.of(List.of(response), 0, 10, 1, 1, true, true);
            TenantApiResponse apiResponse =
                    new TenantApiResponse(
                            tenantId.toString(),
                            "TestTenant",
                            "ACTIVE",
                            Instant.now(),
                            Instant.now());

            given(mapper.toQuery(any()))
                    .willReturn(SearchTenantsQuery.of("Test", null, ONE_MONTH_AGO, NOW, 0, 10));
            given(searchTenantsUseCase.execute(any(SearchTenantsQuery.class)))
                    .willReturn(pageResponse);
            given(mapper.toApiResponse(any(TenantResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/tenants")
                                    .param("name", "Test")
                                    .param("createdFrom", ONE_MONTH_AGO.toString())
                                    .param("createdTo", NOW.toString())
                                    .param("page", "0")
                                    .param("size", "10")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(10));

            verify(searchTenantsUseCase).execute(any(SearchTenantsQuery.class));
        }

        @Test
        @DisplayName("[성공] 상태 필터로 조회 시 200 OK 반환")
        void searchTenants_withStatusFilter_returns200Ok() throws Exception {
            // Given
            PageResponse<TenantResponse> pageResponse =
                    PageResponse.of(List.of(), 0, 20, 0, 0, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchTenantsQuery.of(
                                    null, List.of("ACTIVE"), ONE_MONTH_AGO, NOW, 0, 20));
            given(searchTenantsUseCase.execute(any(SearchTenantsQuery.class)))
                    .willReturn(pageResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/tenants")
                                    .param("statuses", "ACTIVE")
                                    .param("createdFrom", ONE_MONTH_AGO.toString())
                                    .param("createdTo", NOW.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());

            verify(searchTenantsUseCase).execute(any(SearchTenantsQuery.class));
        }

        @Test
        @DisplayName("[실패] 페이지 번호가 음수면 400 Bad Request 반환")
        void searchTenants_withNegativePage_returns400BadRequest() throws Exception {
            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/tenants")
                                    .param("page", "-1")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("[실패] 페이지 크기가 0이면 400 Bad Request 반환")
        void searchTenants_withZeroSize_returns400BadRequest() throws Exception {
            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/tenants")
                                    .param("size", "0")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("[실패] 페이지 크기가 100 초과면 400 Bad Request 반환")
        void searchTenants_withTooLargeSize_returns400BadRequest() throws Exception {
            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/tenants")
                                    .param("size", "101")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }
}
