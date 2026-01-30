package com.ryuqq.authhub.adapter.in.rest.tenant.controller.query;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.TenantApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.SearchTenantsOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantQueryApiMapper;
import com.ryuqq.authhub.application.tenant.dto.query.TenantSearchParams;
import com.ryuqq.authhub.application.tenant.dto.response.TenantPageResult;
import com.ryuqq.authhub.application.tenant.port.in.query.SearchTenantsByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TenantQueryController - Tenant 조회 API
 *
 * <p>테넌트 조회 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: Controller는 @RestController로 정의.
 *
 * <p>CTR-002: Controller는 UseCase만 주입받음.
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 처리.
 *
 * <p>RDTO-009: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag(name = "Tenant", description = "테넌트 관리 API")
@RestController
@RequestMapping(TenantApiEndpoints.TENANTS)
public class TenantQueryController {

    private final SearchTenantsByOffsetUseCase searchTenantsByOffsetUseCase;
    private final TenantQueryApiMapper mapper;

    /**
     * TenantQueryController 생성자
     *
     * @param searchTenantsByOffsetUseCase Tenant 복합 조건 조회 UseCase (Offset 기반)
     * @param mapper API 매퍼
     */
    public TenantQueryController(
            SearchTenantsByOffsetUseCase searchTenantsByOffsetUseCase,
            TenantQueryApiMapper mapper) {
        this.searchTenantsByOffsetUseCase = searchTenantsByOffsetUseCase;
        this.mapper = mapper;
    }

    /**
     * Tenant 복합 조건 조회 API (Offset 기반)
     *
     * <p>이름, 상태, 생성일시 범위 필터를 지원하여 테넌트 목록을 Offset 기반으로 조회합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반, 필터 포함)
     * @return Tenant 페이지 목록
     */
    @Operation(
            summary = "테넌트 복합 조건 조회",
            description = "이름, 상태, 생성일시 범위 필터를 지원하여 테넌트 목록을 Offset 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.superAdmin()")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<TenantApiResponse>>> searchTenantsByOffset(
            @Valid @ModelAttribute SearchTenantsOffsetApiRequest request) {

        TenantSearchParams params = mapper.toSearchParams(request);
        TenantPageResult pageResult = searchTenantsByOffsetUseCase.execute(params);
        PageApiResponse<TenantApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.ofSuccess(response));
    }
}
