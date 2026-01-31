package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints.ENDPOINT_PERMISSIONS;
import static com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints.ENDPOINT_PERMISSIONS_SPEC;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.EndpointPermissionSpecListApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalPermissionSpecApiMapper;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecListResult;
import com.ryuqq.authhub.application.permissionendpoint.port.in.query.GetEndpointPermissionSpecUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InternalPermissionSpecController - Gateway용 엔드포인트-권한 스펙 Internal API Controller
 *
 * <p>Gateway가 URL 기반 권한 검사를 위해 전체 엔드포인트-권한 매핑 정보를 조회합니다.
 *
 * <p><strong>보안 참고:</strong>
 *
 * <ul>
 *   <li>이 API는 서비스 토큰 인증으로 보호됩니다
 *   <li>외부 접근이 차단된 내부 네트워크에서만 접근 가능해야 합니다
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping(ENDPOINT_PERMISSIONS)
@Tag(name = "Internal - Permission Spec", description = "Gateway용 엔드포인트-권한 스펙 Internal API")
public class InternalPermissionSpecController {

    private final GetEndpointPermissionSpecUseCase getEndpointPermissionSpecUseCase;
    private final InternalPermissionSpecApiMapper mapper;

    public InternalPermissionSpecController(
            GetEndpointPermissionSpecUseCase getEndpointPermissionSpecUseCase,
            InternalPermissionSpecApiMapper mapper) {
        this.getEndpointPermissionSpecUseCase = getEndpointPermissionSpecUseCase;
        this.mapper = mapper;
    }

    /**
     * 엔드포인트-권한 스펙 전체 조회
     *
     * <p>Gateway가 시작 시 또는 갱신 시 호출하여 전체 스펙을 캐싱합니다.
     *
     * @return 엔드포인트-권한 스펙 목록
     */
    @GetMapping(ENDPOINT_PERMISSIONS_SPEC)
    @Operation(
            summary = "엔드포인트-권한 스펙 전체 조회",
            description = "Gateway가 URL 기반 권한 검사를 위해 전체 엔드포인트-권한 매핑 정보를 조회합니다.")
    public ApiResponse<EndpointPermissionSpecListApiResponse> getSpec() {
        EndpointPermissionSpecListResult result = getEndpointPermissionSpecUseCase.getAll();
        EndpointPermissionSpecListApiResponse response = mapper.toApiResponse(result);
        return ApiResponse.ofSuccess(response);
    }
}
