package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantApiMapper;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.port.in.command.CreateTenantUseCase;
import com.ryuqq.authhub.application.tenant.port.in.command.DeleteTenantUseCase;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@Tag(name = "Tenant", description = "테넌트 관리 API")
@RestController
@RequestMapping("/api/v1/tenants")
public class TenantCommandController {

    private final CreateTenantUseCase createTenantUseCase;
    private final UpdateTenantNameUseCase updateTenantNameUseCase;
    private final UpdateTenantStatusUseCase updateTenantStatusUseCase;
    private final DeleteTenantUseCase deleteTenantUseCase;
    private final TenantApiMapper mapper;

    public TenantCommandController(
            CreateTenantUseCase createTenantUseCase,
            UpdateTenantNameUseCase updateTenantNameUseCase,
            UpdateTenantStatusUseCase updateTenantStatusUseCase,
            DeleteTenantUseCase deleteTenantUseCase,
            TenantApiMapper mapper) {
        this.createTenantUseCase = createTenantUseCase;
        this.updateTenantNameUseCase = updateTenantNameUseCase;
        this.updateTenantStatusUseCase = updateTenantStatusUseCase;
        this.deleteTenantUseCase = deleteTenantUseCase;
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
    @Operation(summary = "테넌트 생성", description = "새로운 테넌트를 생성합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "테넌트 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 테넌트 이름")
    })
    @PostMapping
    public ResponseEntity<
                    com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse<
                            CreateTenantApiResponse>>
            createTenant(@Valid @RequestBody CreateTenantApiRequest request) {
        TenantResponse response = createTenantUseCase.execute(mapper.toCommand(request));
        CreateTenantApiResponse apiResponse = mapper.toCreateResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 테넌트 이름 변경
     *
     * <p>PUT /api/v1/tenants/{id}/name
     *
     * @param id 테넌트 ID
     * @param request 이름 변경 요청
     * @return 200 OK + 변경된 테넌트 정보
     */
    @Operation(summary = "테넌트 이름 변경", description = "테넌트의 이름을 변경합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "이름 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "테넌트를 찾을 수 없음")
    })
    @PutMapping("/{id}/name")
    public ResponseEntity<
                    com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse<TenantApiResponse>>
            updateTenantName(
                    @Parameter(description = "테넌트 ID", required = true) @PathVariable UUID id,
                    @Valid @RequestBody UpdateTenantNameApiRequest request) {
        TenantResponse response = updateTenantNameUseCase.execute(mapper.toCommand(id, request));
        TenantApiResponse apiResponse = mapper.toApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 테넌트 상태 변경
     *
     * <p>PATCH /api/v1/tenants/{id}/status
     *
     * @param id 테넌트 ID
     * @param request 상태 변경 요청
     * @return 200 OK + 변경된 테넌트 정보
     */
    @Operation(summary = "테넌트 상태 변경", description = "테넌트의 상태를 변경합니다 (ACTIVE, SUSPENDED, INACTIVE)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "상태 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "테넌트를 찾을 수 없음")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<
                    com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse<TenantApiResponse>>
            updateTenantStatus(
                    @Parameter(description = "테넌트 ID", required = true) @PathVariable UUID id,
                    @Valid @RequestBody UpdateTenantStatusApiRequest request) {
        TenantResponse response = updateTenantStatusUseCase.execute(mapper.toCommand(id, request));
        TenantApiResponse apiResponse = mapper.toApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 테넌트 삭제 (Soft Delete)
     *
     * <p>DELETE /api/v1/tenants/{id}
     *
     * @param id 테넌트 ID
     * @return 204 No Content
     */
    @Operation(summary = "테넌트 삭제", description = "테넌트를 삭제합니다 (Soft Delete)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "테넌트를 찾을 수 없음")
    })
    @PatchMapping("/{id}/delete")
    public ResponseEntity<Void> deleteTenant(
            @Parameter(description = "테넌트 ID", required = true) @PathVariable UUID id) {
        deleteTenantUseCase.execute(mapper.toDeleteCommand(id));
        return ResponseEntity.noContent().build();
    }
}
