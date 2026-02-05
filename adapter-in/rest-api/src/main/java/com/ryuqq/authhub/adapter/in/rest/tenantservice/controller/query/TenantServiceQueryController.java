package com.ryuqq.authhub.adapter.in.rest.tenantservice.controller.query;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.TenantServiceApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.SearchTenantServicesOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.response.TenantServiceApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.mapper.TenantServiceQueryApiMapper;
import com.ryuqq.authhub.application.tenantservice.dto.query.TenantServiceSearchParams;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServicePageResult;
import com.ryuqq.authhub.application.tenantservice.port.in.query.SearchTenantServicesUseCase;
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
 * TenantServiceQueryController - 테넌트-서비스 구독 조회 API
 *
 * <p>테넌트-서비스 구독 목록 조회 엔드포인트를 제공합니다.
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
@Tag(name = "TenantService", description = "테넌트-서비스 구독 관리 API")
@RestController
@RequestMapping(TenantServiceApiEndpoints.TENANT_SERVICES)
public class TenantServiceQueryController {

    private final SearchTenantServicesUseCase searchTenantServicesUseCase;
    private final TenantServiceQueryApiMapper mapper;

    /**
     * TenantServiceQueryController 생성자
     *
     * @param searchTenantServicesUseCase 테넌트-서비스 복합 조건 조회 UseCase
     * @param mapper API 매퍼
     */
    public TenantServiceQueryController(
            SearchTenantServicesUseCase searchTenantServicesUseCase,
            TenantServiceQueryApiMapper mapper) {
        this.searchTenantServicesUseCase = searchTenantServicesUseCase;
        this.mapper = mapper;
    }

    /**
     * 테넌트-서비스 구독 복합 조건 조회 API (Offset 기반)
     *
     * <p>테넌트 ID, 서비스 ID, 상태, 날짜 범위 필터를 지원하여 구독 목록을 Offset 기반으로 조회합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반, 필터 포함)
     * @return TenantService 페이지 목록
     */
    @Operation(
            summary = "테넌트-서비스 구독 복합 조건 조회",
            description = "테넌트 ID, 서비스 ID, 상태, 날짜 범위 필터를 지원하여 구독 목록을 Offset 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('tenant-service', 'read')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<TenantServiceApiResponse>>>
            searchTenantServicesByOffset(
                    @Valid @ModelAttribute SearchTenantServicesOffsetApiRequest request) {

        TenantServiceSearchParams params = mapper.toSearchParams(request);
        TenantServicePageResult pageResult = searchTenantServicesUseCase.execute(params);
        PageApiResponse<TenantServiceApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.ofSuccess(response));
    }
}
