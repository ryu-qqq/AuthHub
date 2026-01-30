package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.controller;

import static com.ryuqq.authhub.adapter.in.rest.permissionendpoint.PermissionEndpointApiEndpoints.ID;
import static com.ryuqq.authhub.adapter.in.rest.permissionendpoint.PermissionEndpointApiEndpoints.PATH_PERMISSION_ENDPOINT_ID;
import static com.ryuqq.authhub.adapter.in.rest.permissionendpoint.PermissionEndpointApiEndpoints.PERMISSION_ENDPOINTS;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.CreatePermissionEndpointApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.UpdatePermissionEndpointApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.mapper.PermissionEndpointCommandApiMapper;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.CreatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.DeletePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.UpdatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.CreatePermissionEndpointUseCase;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.DeletePermissionEndpointUseCase;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.UpdatePermissionEndpointUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * PermissionEndpointCommandController - PermissionEndpoint 명령 API Controller
 *
 * <p>PermissionEndpoint 생성/수정/삭제 REST API를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping(PERMISSION_ENDPOINTS)
@Tag(name = "PermissionEndpoint Command", description = "PermissionEndpoint 생성/수정/삭제 API")
public class PermissionEndpointCommandController {

    private final CreatePermissionEndpointUseCase createPermissionEndpointUseCase;
    private final UpdatePermissionEndpointUseCase updatePermissionEndpointUseCase;
    private final DeletePermissionEndpointUseCase deletePermissionEndpointUseCase;
    private final PermissionEndpointCommandApiMapper mapper;

    public PermissionEndpointCommandController(
            CreatePermissionEndpointUseCase createPermissionEndpointUseCase,
            UpdatePermissionEndpointUseCase updatePermissionEndpointUseCase,
            DeletePermissionEndpointUseCase deletePermissionEndpointUseCase,
            PermissionEndpointCommandApiMapper mapper) {
        this.createPermissionEndpointUseCase = createPermissionEndpointUseCase;
        this.updatePermissionEndpointUseCase = updatePermissionEndpointUseCase;
        this.deletePermissionEndpointUseCase = deletePermissionEndpointUseCase;
        this.mapper = mapper;
    }

    /**
     * PermissionEndpoint 생성
     *
     * @param request 생성 요청 DTO
     * @return 생성된 PermissionEndpoint ID
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "PermissionEndpoint 생성", description = "새로운 권한 엔드포인트 매핑을 생성합니다.")
    public ApiResponse<Long> create(
            @Valid @RequestBody CreatePermissionEndpointApiRequest request) {
        CreatePermissionEndpointCommand command = mapper.toCommand(request);
        Long id = createPermissionEndpointUseCase.create(command);
        return ApiResponse.ofSuccess(id);
    }

    /**
     * PermissionEndpoint 수정
     *
     * @param permissionEndpointId 수정할 엔드포인트 ID
     * @param request 수정 요청 DTO
     * @return 성공 응답
     */
    @PutMapping(ID)
    @Operation(summary = "PermissionEndpoint 수정", description = "기존 권한 엔드포인트 매핑을 수정합니다.")
    public ApiResponse<Void> update(
            @PathVariable(PATH_PERMISSION_ENDPOINT_ID) Long permissionEndpointId,
            @Valid @RequestBody UpdatePermissionEndpointApiRequest request) {
        UpdatePermissionEndpointCommand command = mapper.toCommand(permissionEndpointId, request);
        updatePermissionEndpointUseCase.update(command);
        return ApiResponse.ofSuccess();
    }

    /**
     * PermissionEndpoint 삭제
     *
     * @param permissionEndpointId 삭제할 엔드포인트 ID
     * @return 성공 응답
     */
    @DeleteMapping(ID)
    @Operation(summary = "PermissionEndpoint 삭제", description = "권한 엔드포인트 매핑을 삭제합니다.")
    public ApiResponse<Void> delete(
            @PathVariable(PATH_PERMISSION_ENDPOINT_ID) Long permissionEndpointId) {
        DeletePermissionEndpointCommand command = mapper.toDeleteCommand(permissionEndpointId);
        deletePermissionEndpointUseCase.delete(command);
        return ApiResponse.ofSuccess();
    }
}
