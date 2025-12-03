package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantApiMapper;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.response.CreateTenantResponse;
import com.ryuqq.authhub.application.tenant.port.in.CreateTenantUseCase;

import jakarta.validation.Valid;

/**
 * Tenant Command Controller - 테넌트 상태 변경 API
 *
 * <p>테넌트 생성 등 Command 작업을 처리합니다.
 *
 * <p><strong>엔드포인트:</strong>
 * <ul>
 *   <li>POST /api/v1/tenants - 테넌트 생성 (201 Created)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping("${api.endpoints.base-v1}${api.endpoints.tenant.base}")
@Validated
public class TenantCommandController {

    private final CreateTenantUseCase createTenantUseCase;
    private final TenantApiMapper tenantApiMapper;

    public TenantCommandController(
            CreateTenantUseCase createTenantUseCase,
            TenantApiMapper tenantApiMapper) {
        this.createTenantUseCase = createTenantUseCase;
        this.tenantApiMapper = tenantApiMapper;
    }

    /**
     * 테넌트 생성 API
     *
     * @param request 테넌트 생성 요청 DTO
     * @return 201 Created와 생성된 테넌트 정보 (tenantId)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateTenantApiResponse>> createTenant(
            @Valid @RequestBody CreateTenantApiRequest request) {
        CreateTenantCommand command = tenantApiMapper.toCreateTenantCommand(request);
        CreateTenantResponse useCaseResponse = createTenantUseCase.execute(command);
        CreateTenantApiResponse apiResponse = CreateTenantApiResponse.from(useCaseResponse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ofSuccess(apiResponse));
    }
}
