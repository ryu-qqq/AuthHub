package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.query.SearchTenantsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.query.GetTenantQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantUseCase;
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
    private final TenantApiMapper mapper;

    public TenantQueryController(
            GetTenantUseCase getTenantUseCase,
            SearchTenantsUseCase searchTenantsUseCase,
            TenantApiMapper mapper) {
        this.getTenantUseCase = getTenantUseCase;
        this.searchTenantsUseCase = searchTenantsUseCase;
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
    @Operation(summary = "테넌트 단건 조회", description = "테넌트 ID로 테넌트 정보를 조회합니다")
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
    @Operation(summary = "테넌트 목록 조회", description = "조건에 맞는 테넌트 목록을 페이징하여 조회합니다")
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
}
