package com.ryuqq.authhub.adapter.in.rest.tenant.controller.command;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.TenantApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.UpdateTenantNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.UpdateTenantStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantIdApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantCommandApiMapper;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.port.in.command.CreateTenantUseCase;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantNameUseCase;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantStatusUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TenantCommandController - Tenant 생성/수정 API
 *
 * <p>테넌트 생성/수정 엔드포인트를 제공합니다.
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
@Tag(name = "Tenant", description = "테넌트 관리 API")
@RestController
@RequestMapping(TenantApiEndpoints.TENANTS)
public class TenantCommandController {

    private final CreateTenantUseCase createTenantUseCase;
    private final UpdateTenantNameUseCase updateTenantNameUseCase;
    private final UpdateTenantStatusUseCase updateTenantStatusUseCase;
    private final TenantCommandApiMapper mapper;

    /**
     * TenantCommandController 생성자
     *
     * @param createTenantUseCase Tenant 생성 UseCase
     * @param updateTenantNameUseCase Tenant 이름 수정 UseCase
     * @param updateTenantStatusUseCase Tenant 상태 수정 UseCase
     * @param mapper API 매퍼
     */
    public TenantCommandController(
            CreateTenantUseCase createTenantUseCase,
            UpdateTenantNameUseCase updateTenantNameUseCase,
            UpdateTenantStatusUseCase updateTenantStatusUseCase,
            TenantCommandApiMapper mapper) {
        this.createTenantUseCase = createTenantUseCase;
        this.updateTenantNameUseCase = updateTenantNameUseCase;
        this.updateTenantStatusUseCase = updateTenantStatusUseCase;
        this.mapper = mapper;
    }

    /**
     * Tenant 생성 API
     *
     * <p>새로운 테넌트를 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 Tenant ID
     */
    @Operation(summary = "테넌트 생성", description = "새로운 테넌트를 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 테넌트 이름")
    })
    @PreAuthorize("@access.superAdmin()")
    @PostMapping
    public ResponseEntity<ApiResponse<TenantIdApiResponse>> create(
            @Valid @RequestBody CreateTenantApiRequest request) {

        CreateTenantCommand command = mapper.toCommand(request);
        String tenantId = createTenantUseCase.execute(command);

        TenantIdApiResponse apiResponse = TenantIdApiResponse.of(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * Tenant 이름 수정 API
     *
     * <p>기존 테넌트의 이름을 수정합니다.
     *
     * @param tenantId Tenant ID
     * @param request 수정 요청 DTO
     * @return 수정된 Tenant ID
     */
    @Operation(summary = "테넌트 이름 수정", description = "기존 테넌트의 이름을 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "테넌트를 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin()")
    @PutMapping(TenantApiEndpoints.NAME)
    public ResponseEntity<ApiResponse<TenantIdApiResponse>> updateName(
            @Parameter(description = "Tenant ID", required = true)
                    @PathVariable(TenantApiEndpoints.PATH_TENANT_ID)
                    UUID tenantId,
            @Valid @RequestBody UpdateTenantNameApiRequest request) {

        UpdateTenantNameCommand command = mapper.toCommand(tenantId, request);
        updateTenantNameUseCase.execute(command);

        TenantIdApiResponse apiResponse = TenantIdApiResponse.of(tenantId.toString());
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * Tenant 상태 수정 API
     *
     * <p>기존 테넌트의 상태를 수정합니다.
     *
     * @param tenantId Tenant ID
     * @param request 수정 요청 DTO
     * @return 수정된 Tenant ID
     */
    @Operation(
            summary = "테넌트 상태 수정",
            description = "기존 테넌트의 상태를 수정합니다 (ACTIVE, SUSPENDED, INACTIVE).")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "테넌트를 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin()")
    @PatchMapping(TenantApiEndpoints.STATUS)
    public ResponseEntity<ApiResponse<TenantIdApiResponse>> updateStatus(
            @Parameter(description = "Tenant ID", required = true)
                    @PathVariable(TenantApiEndpoints.PATH_TENANT_ID)
                    UUID tenantId,
            @Valid @RequestBody UpdateTenantStatusApiRequest request) {

        UpdateTenantStatusCommand command = mapper.toCommand(tenantId, request);
        updateTenantStatusUseCase.execute(command);

        TenantIdApiResponse apiResponse = TenantIdApiResponse.of(tenantId.toString());
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
