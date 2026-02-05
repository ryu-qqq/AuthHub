package com.ryuqq.authhub.adapter.in.rest.service.controller.command;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.service.ServiceApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.service.dto.request.CreateServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.service.dto.request.UpdateServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.service.dto.response.ServiceIdApiResponse;
import com.ryuqq.authhub.adapter.in.rest.service.mapper.ServiceCommandApiMapper;
import com.ryuqq.authhub.application.service.dto.command.CreateServiceCommand;
import com.ryuqq.authhub.application.service.dto.command.UpdateServiceCommand;
import com.ryuqq.authhub.application.service.port.in.command.CreateServiceUseCase;
import com.ryuqq.authhub.application.service.port.in.command.UpdateServiceUseCase;
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
 * ServiceCommandController - Service 생성/수정 API
 *
 * <p>서비스 생성/수정 엔드포인트를 제공합니다.
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
@Tag(name = "Service", description = "서비스 관리 API")
@RestController
@RequestMapping(ServiceApiEndpoints.SERVICES)
public class ServiceCommandController {

    private final CreateServiceUseCase createServiceUseCase;
    private final UpdateServiceUseCase updateServiceUseCase;
    private final ServiceCommandApiMapper mapper;

    /**
     * ServiceCommandController 생성자
     *
     * @param createServiceUseCase Service 생성 UseCase
     * @param updateServiceUseCase Service 수정 UseCase
     * @param mapper API 매퍼
     */
    public ServiceCommandController(
            CreateServiceUseCase createServiceUseCase,
            UpdateServiceUseCase updateServiceUseCase,
            ServiceCommandApiMapper mapper) {
        this.createServiceUseCase = createServiceUseCase;
        this.updateServiceUseCase = updateServiceUseCase;
        this.mapper = mapper;
    }

    /**
     * Service 생성 API
     *
     * <p>새로운 서비스를 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 Service ID
     */
    @Operation(summary = "서비스 생성", description = "새로운 서비스를 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 서비스 코드")
    })
    @PreAuthorize("@access.hasPermission('service', 'create')")
    @PostMapping
    public ResponseEntity<ApiResponse<ServiceIdApiResponse>> create(
            @Valid @RequestBody CreateServiceApiRequest request) {

        CreateServiceCommand command = mapper.toCommand(request);
        Long serviceId = createServiceUseCase.execute(command);

        ServiceIdApiResponse apiResponse = ServiceIdApiResponse.of(serviceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * Service 수정 API
     *
     * <p>기존 서비스의 이름, 설명, 상태를 수정합니다.
     *
     * @param serviceId Service ID
     * @param request 수정 요청 DTO
     * @return 수정된 Service ID
     */
    @Operation(summary = "서비스 수정", description = "기존 서비스의 이름, 설명, 상태를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "서비스를 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('service', 'update')")
    @PutMapping(ServiceApiEndpoints.ID)
    public ResponseEntity<ApiResponse<ServiceIdApiResponse>> update(
            @Parameter(description = "Service ID", required = true)
                    @PathVariable(ServiceApiEndpoints.PATH_SERVICE_ID)
                    Long serviceId,
            @Valid @RequestBody UpdateServiceApiRequest request) {

        UpdateServiceCommand command = mapper.toCommand(serviceId, request);
        updateServiceUseCase.execute(command);

        ServiceIdApiResponse apiResponse = ServiceIdApiResponse.of(serviceId);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
