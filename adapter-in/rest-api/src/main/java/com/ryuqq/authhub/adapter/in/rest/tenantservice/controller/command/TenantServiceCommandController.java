package com.ryuqq.authhub.adapter.in.rest.tenantservice.controller.command;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.TenantServiceApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.SubscribeTenantServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.UpdateTenantServiceStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.response.TenantServiceIdApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.mapper.TenantServiceCommandApiMapper;
import com.ryuqq.authhub.application.tenantservice.dto.command.SubscribeTenantServiceCommand;
import com.ryuqq.authhub.application.tenantservice.dto.command.UpdateTenantServiceStatusCommand;
import com.ryuqq.authhub.application.tenantservice.port.in.command.SubscribeTenantServiceUseCase;
import com.ryuqq.authhub.application.tenantservice.port.in.command.UpdateTenantServiceStatusUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TenantServiceCommandController - 테넌트-서비스 구독/상태변경 API
 *
 * <p>테넌트-서비스 구독 생성 및 상태 변경 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: Controller는 @RestController로 정의.
 *
 * <p>CTR-002: Controller는 UseCase만 주입받음.
 *
 * <p>CTR-003: @Valid 필수 적용.
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag(name = "TenantService", description = "테넌트-서비스 구독 관리 API")
@RestController
@RequestMapping(TenantServiceApiEndpoints.TENANT_SERVICES)
public class TenantServiceCommandController {

    private final SubscribeTenantServiceUseCase subscribeTenantServiceUseCase;
    private final UpdateTenantServiceStatusUseCase updateTenantServiceStatusUseCase;
    private final TenantServiceCommandApiMapper mapper;

    /**
     * TenantServiceCommandController 생성자
     *
     * @param subscribeTenantServiceUseCase 테넌트-서비스 구독 UseCase
     * @param updateTenantServiceStatusUseCase 테넌트-서비스 상태 변경 UseCase
     * @param mapper API 매퍼
     */
    public TenantServiceCommandController(
            SubscribeTenantServiceUseCase subscribeTenantServiceUseCase,
            UpdateTenantServiceStatusUseCase updateTenantServiceStatusUseCase,
            TenantServiceCommandApiMapper mapper) {
        this.subscribeTenantServiceUseCase = subscribeTenantServiceUseCase;
        this.updateTenantServiceStatusUseCase = updateTenantServiceStatusUseCase;
        this.mapper = mapper;
    }

    /**
     * 테넌트-서비스 구독 API
     *
     * <p>테넌트가 서비스에 구독합니다.
     *
     * @param request 구독 요청 DTO
     * @return 생성된 TenantService ID
     */
    @Operation(summary = "테넌트-서비스 구독", description = "테넌트가 서비스에 구독합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "구독 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "이미 구독된 테넌트-서비스")
    })
    @PreAuthorize("@access.hasPermission('tenant-service', 'create')")
    @PostMapping
    public ResponseEntity<ApiResponse<TenantServiceIdApiResponse>> subscribe(
            @Valid @RequestBody SubscribeTenantServiceApiRequest request) {

        SubscribeTenantServiceCommand command = mapper.toCommand(request);
        Long tenantServiceId = subscribeTenantServiceUseCase.execute(command);

        TenantServiceIdApiResponse apiResponse = TenantServiceIdApiResponse.of(tenantServiceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 테넌트-서비스 상태 변경 API
     *
     * <p>테넌트-서비스 구독의 상태를 변경합니다.
     *
     * @param tenantServiceId TenantService ID
     * @param request 상태 변경 요청 DTO
     * @return 변경된 TenantService ID
     */
    @Operation(summary = "테넌트-서비스 상태 변경", description = "테넌트-서비스 구독의 상태를 변경합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "상태 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "테넌트-서비스를 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('tenant-service', 'update')")
    @PutMapping(TenantServiceApiEndpoints.STATUS)
    public ResponseEntity<ApiResponse<TenantServiceIdApiResponse>> updateStatus(
            @Parameter(description = "TenantService ID", required = true)
                    @PathVariable(TenantServiceApiEndpoints.PATH_TENANT_SERVICE_ID)
                    Long tenantServiceId,
            @Valid @RequestBody UpdateTenantServiceStatusApiRequest request) {

        UpdateTenantServiceStatusCommand command = mapper.toCommand(tenantServiceId, request);
        updateTenantServiceStatusUseCase.execute(command);

        TenantServiceIdApiResponse apiResponse = TenantServiceIdApiResponse.of(tenantServiceId);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
