package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints.TENANTS;
import static com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints.TENANT_CONFIG;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.TenantConfigApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalTenantConfigApiMapper;
import com.ryuqq.authhub.application.tenant.dto.response.TenantConfigResult;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantConfigUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InternalTenantConfigController - Gateway용 테넌트 설정 Internal API Controller
 *
 * <p>Gateway가 테넌트 유효성 검증을 위해 설정 정보를 조회합니다.
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
@RequestMapping(TENANTS)
@Tag(name = "Internal - Tenant Config", description = "Gateway용 테넌트 설정 Internal API")
public class InternalTenantConfigController {

    private final GetTenantConfigUseCase getTenantConfigUseCase;
    private final InternalTenantConfigApiMapper mapper;

    public InternalTenantConfigController(
            GetTenantConfigUseCase getTenantConfigUseCase, InternalTenantConfigApiMapper mapper) {
        this.getTenantConfigUseCase = getTenantConfigUseCase;
        this.mapper = mapper;
    }

    /**
     * 테넌트 설정 조회
     *
     * <p>Gateway가 요청 처리 시 테넌트 활성 여부를 검증하기 위해 호출합니다.
     *
     * @param tenantId 테넌트 ID
     * @return 테넌트 설정
     */
    @GetMapping(TENANT_CONFIG)
    @Operation(summary = "테넌트 설정 조회", description = "Gateway가 테넌트 유효성 검증을 위해 설정 정보를 조회합니다.")
    public ApiResponse<TenantConfigApiResponse> getConfig(
            @Parameter(description = "테넌트 ID", required = true) @PathVariable String tenantId) {
        TenantConfigResult result = getTenantConfigUseCase.getByTenantId(tenantId);
        TenantConfigApiResponse response = mapper.toApiResponse(result);
        return ApiResponse.ofSuccess(response);
    }
}
