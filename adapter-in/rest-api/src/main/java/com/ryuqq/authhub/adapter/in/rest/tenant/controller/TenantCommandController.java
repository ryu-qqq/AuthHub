package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantApiMapper;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.port.in.command.CreateTenantUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TenantCommandController - 테넌트 Command API 컨트롤러
 *
 * <p>테넌트 생성, 수정, 삭제 등 Command 작업을 처리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @RestController} + {@code @RequestMapping} 필수
 *   <li>{@code @Valid} 필수
 *   <li>UseCase 단일 의존
 *   <li>Thin Controller (비즈니스 로직 금지)
 *   <li>Lombok 금지
 *   <li>{@code @Transactional} 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/tenants")
public class TenantCommandController {

    private final CreateTenantUseCase createTenantUseCase;
    private final TenantApiMapper mapper;

    public TenantCommandController(
            CreateTenantUseCase createTenantUseCase, TenantApiMapper mapper) {
        this.createTenantUseCase = createTenantUseCase;
        this.mapper = mapper;
    }

    /**
     * 테넌트 생성
     *
     * <p>POST /api/v1/tenants
     *
     * @param request 테넌트 생성 요청
     * @return 201 Created + 생성된 테넌트 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateTenantApiResponse>> createTenant(
            @Valid @RequestBody CreateTenantApiRequest request) {
        TenantResponse response = createTenantUseCase.execute(mapper.toCommand(request));
        CreateTenantApiResponse apiResponse = mapper.toCreateResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }
}
