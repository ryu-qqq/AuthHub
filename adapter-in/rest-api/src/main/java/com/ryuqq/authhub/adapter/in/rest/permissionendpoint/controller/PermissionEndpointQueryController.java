package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.controller;

import static com.ryuqq.authhub.adapter.in.rest.permissionendpoint.PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.SearchPermissionEndpointsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.response.PermissionEndpointApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.mapper.PermissionEndpointQueryApiMapper;
import com.ryuqq.authhub.application.permissionendpoint.dto.query.PermissionEndpointSearchParams;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointPageResult;
import com.ryuqq.authhub.application.permissionendpoint.port.in.query.SearchPermissionEndpointsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PermissionEndpointQueryController - PermissionEndpoint 조회 API Controller
 *
 * <p>PermissionEndpoint 검색/조회 REST API를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping(PERMISSION_ENDPOINTS)
@Tag(name = "PermissionEndpoint Query", description = "PermissionEndpoint 조회 API")
public class PermissionEndpointQueryController {

    private final SearchPermissionEndpointsUseCase searchPermissionEndpointsUseCase;
    private final PermissionEndpointQueryApiMapper mapper;

    public PermissionEndpointQueryController(
            SearchPermissionEndpointsUseCase searchPermissionEndpointsUseCase,
            PermissionEndpointQueryApiMapper mapper) {
        this.searchPermissionEndpointsUseCase = searchPermissionEndpointsUseCase;
        this.mapper = mapper;
    }

    /**
     * PermissionEndpoint 목록 검색
     *
     * @param request 검색 요청 DTO
     * @return 페이지 응답
     */
    @GetMapping
    @Operation(summary = "PermissionEndpoint 목록 검색", description = "권한 엔드포인트 매핑 목록을 검색합니다.")
    public ApiResponse<PageApiResponse<PermissionEndpointApiResponse>> search(
            @Valid @ParameterObject SearchPermissionEndpointsApiRequest request) {
        PermissionEndpointSearchParams params = mapper.toSearchParams(request);
        PermissionEndpointPageResult result = searchPermissionEndpointsUseCase.search(params);
        PageApiResponse<PermissionEndpointApiResponse> response = mapper.toPageResponse(result);
        return ApiResponse.ofSuccess(response);
    }
}
