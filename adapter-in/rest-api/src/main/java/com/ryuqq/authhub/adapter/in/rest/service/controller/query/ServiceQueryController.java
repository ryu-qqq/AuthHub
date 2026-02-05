package com.ryuqq.authhub.adapter.in.rest.service.controller.query;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.service.ServiceApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.service.dto.request.SearchServicesOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.service.dto.response.ServiceApiResponse;
import com.ryuqq.authhub.adapter.in.rest.service.mapper.ServiceQueryApiMapper;
import com.ryuqq.authhub.application.service.dto.query.ServiceSearchParams;
import com.ryuqq.authhub.application.service.dto.response.ServicePageResult;
import com.ryuqq.authhub.application.service.port.in.query.SearchServicesUseCase;
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
 * ServiceQueryController - Service 조회 API
 *
 * <p>서비스 조회 엔드포인트를 제공합니다.
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
@Tag(name = "Service", description = "서비스 관리 API")
@RestController
@RequestMapping(ServiceApiEndpoints.SERVICES)
public class ServiceQueryController {

    private final SearchServicesUseCase searchServicesUseCase;
    private final ServiceQueryApiMapper mapper;

    /**
     * ServiceQueryController 생성자
     *
     * @param searchServicesUseCase Service 복합 조건 조회 UseCase
     * @param mapper API 매퍼
     */
    public ServiceQueryController(
            SearchServicesUseCase searchServicesUseCase, ServiceQueryApiMapper mapper) {
        this.searchServicesUseCase = searchServicesUseCase;
        this.mapper = mapper;
    }

    /**
     * Service 복합 조건 조회 API (Offset 기반)
     *
     * <p>이름, 서비스 코드, 상태, 생성일시 범위 필터를 지원하여 서비스 목록을 Offset 기반으로 조회합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반, 필터 포함)
     * @return Service 페이지 목록
     */
    @Operation(
            summary = "서비스 복합 조건 조회",
            description = "이름, 서비스 코드, 상태, 생성일시 범위 필터를 지원하여 서비스 목록을 Offset 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('service', 'read')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<ServiceApiResponse>>> searchServicesByOffset(
            @Valid @ModelAttribute SearchServicesOffsetApiRequest request) {

        ServiceSearchParams params = mapper.toSearchParams(request);
        ServicePageResult pageResult = searchServicesUseCase.execute(params);
        PageApiResponse<ServiceApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.ofSuccess(response));
    }
}
