package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.query.SearchTenantsAdminApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.query.SearchTenantsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.query.GetTenantQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantDetailResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantSummaryResponse;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantDetailUseCase;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantUseCase;
import com.ryuqq.authhub.application.tenant.port.in.query.SearchTenantsAdminUseCase;
import com.ryuqq.authhub.application.tenant.port.in.query.SearchTenantsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TenantQueryController - 테넌트 Query API 컨트롤러 (Admin)
 *
 * <p>테넌트 조회 작업을 처리합니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/admin/tenants (admin.connectly.com)
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @RestController} + {@code @RequestMapping} 필수
 *   <li>UseCase 의존
 *   <li>Thin Controller (비즈니스 로직 금지)
 *   <li>Lombok 금지
 *   <li>{@code @Transactional} 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag(name = "Tenant", description = "테넌트 관리 API (Admin)")
@RestController
@RequestMapping(ApiPaths.Tenants.BASE)
public class TenantQueryController {

    private final GetTenantUseCase getTenantUseCase;
    private final SearchTenantsUseCase searchTenantsUseCase;
    private final SearchTenantsAdminUseCase searchTenantsAdminUseCase;
    private final GetTenantDetailUseCase getTenantDetailUseCase;
    private final TenantApiMapper mapper;

    public TenantQueryController(
            GetTenantUseCase getTenantUseCase,
            SearchTenantsUseCase searchTenantsUseCase,
            SearchTenantsAdminUseCase searchTenantsAdminUseCase,
            GetTenantDetailUseCase getTenantDetailUseCase,
            TenantApiMapper mapper) {
        this.getTenantUseCase = getTenantUseCase;
        this.searchTenantsUseCase = searchTenantsUseCase;
        this.searchTenantsAdminUseCase = searchTenantsAdminUseCase;
        this.getTenantDetailUseCase = getTenantDetailUseCase;
        this.mapper = mapper;
    }

    /**
     * 테넌트 단건 조회
     *
     * <p>GET /api/v1/tenants/{tenantId}
     *
     * @param tenantId 테넌트 ID
     * @return 200 OK + 테넌트 정보
     */
    @Operation(
            summary = "테넌트 단건 조회",
            description =
                    """
                    테넌트 ID로 테넌트 정보를 조회합니다.

                    **필요 권한**: Super Admin 또는 해당 테넌트 소속 사용자
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "테넌트를 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin() or @access.sameTenant(#tenantId.toString())")
    @GetMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantApiResponse>> getTenant(
            @Parameter(description = "테넌트 ID", required = true) @PathVariable UUID tenantId) {
        TenantResponse response = getTenantUseCase.execute(GetTenantQuery.of(tenantId));
        return ResponseEntity.ok(ApiResponse.ofSuccess(mapper.toApiResponse(response)));
    }

    /**
     * 테넌트 목록 조회 (페이징)
     *
     * <p>GET /api/v1/tenants
     *
     * @param request 검색 조건
     * @return 200 OK + 테넌트 목록 (페이징)
     */
    @Operation(
            summary = "테넌트 목록 조회",
            description =
                    """
                    조건에 맞는 테넌트 목록을 페이징하여 조회합니다.

                    **필요 권한**: Super Admin 전용
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.superAdmin()")
    @GetMapping
    public ResponseEntity<PageApiResponse<TenantApiResponse>> getTenants(
            @Valid @ModelAttribute SearchTenantsApiRequest request) {
        PageResponse<TenantResponse> pageResponse =
                searchTenantsUseCase.execute(mapper.toQuery(request));
        return ResponseEntity.ok(PageApiResponse.from(pageResponse, mapper::toApiResponse));
    }

    // ==================== Admin Query 엔드포인트 ====================

    /**
     * Admin 테넌트 목록 검색 (확장 필터)
     *
     * <p>GET /api/v1/tenants/admin/search
     *
     * @param request 검색 조건 (확장 필터 포함)
     * @return 200 OK + 테넌트 목록 (페이징, organizationCount 포함)
     */
    @Operation(
            summary = "Admin 테넌트 목록 검색",
            description =
                    """
                    확장 필터가 적용된 테넌트 목록을 검색합니다.
                    organizationCount가 포함되어 프론트엔드에서 추가 조회 없이 표시 가능합니다.

                    **필요 권한**: Super Admin 전용
                    **확장 필터**: 날짜 범위 (createdFrom, createdTo), 정렬 (sortBy, sortDirection)
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "검색 성공")
    })
    @PreAuthorize("@access.superAdmin()")
    @GetMapping("/admin/search")
    public ResponseEntity<ApiResponse<PageApiResponse<TenantSummaryApiResponse>>>
            searchTenantsAdmin(@Valid @ModelAttribute SearchTenantsAdminApiRequest request) {
        PageResponse<TenantSummaryResponse> pageResponse =
                searchTenantsAdminUseCase.execute(mapper.toAdminQuery(request));
        PageResponse<TenantSummaryApiResponse> apiPageResponse =
                mapper.toSummaryApiPageResponse(pageResponse);
        return ResponseEntity.ok(
                ApiResponse.ofSuccess(PageApiResponse.from(apiPageResponse, r -> r)));
    }

    /**
     * Admin 테넌트 상세 조회 (조직 목록 포함)
     *
     * <p>GET /api/v1/tenants/{tenantId}/admin/detail
     *
     * @param tenantId 테넌트 ID
     * @return 200 OK + 테넌트 상세 정보 (조직 목록 포함)
     */
    @Operation(
            summary = "Admin 테넌트 상세 조회",
            description =
                    """
                    테넌트 상세 정보와 소속 조직 목록을 함께 조회합니다.
                    추가 API 호출 없이 상세 화면을 구성할 수 있습니다.

                    **필요 권한**: Super Admin 전용
                    **포함 정보**: organizations (최근 N개), organizationCount
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "테넌트를 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin()")
    @GetMapping("/{tenantId}/admin/detail")
    public ResponseEntity<ApiResponse<TenantDetailApiResponse>> getTenantAdminDetail(
            @Parameter(description = "테넌트 ID", required = true) @PathVariable UUID tenantId) {
        TenantDetailResponse response = getTenantDetailUseCase.execute(GetTenantQuery.of(tenantId));
        return ResponseEntity.ok(ApiResponse.ofSuccess(mapper.toDetailApiResponse(response)));
    }
}
