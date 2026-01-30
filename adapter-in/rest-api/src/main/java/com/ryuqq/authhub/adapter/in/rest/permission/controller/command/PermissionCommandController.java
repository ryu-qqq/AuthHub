package com.ryuqq.authhub.adapter.in.rest.permission.controller.command;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.PermissionApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.CreatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.UpdatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionIdApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.mapper.PermissionCommandApiMapper;
import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.application.permission.port.in.command.CreatePermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.command.DeletePermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.command.UpdatePermissionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PermissionCommandController - Permission 생성/수정/삭제 API
 *
 * <p>권한 생성/수정/삭제 엔드포인트를 제공합니다.
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
@Tag(name = "Permission", description = "권한 관리 API")
@RestController
@RequestMapping(PermissionApiEndpoints.PERMISSIONS)
public class PermissionCommandController {

    private final CreatePermissionUseCase createPermissionUseCase;
    private final UpdatePermissionUseCase updatePermissionUseCase;
    private final DeletePermissionUseCase deletePermissionUseCase;
    private final PermissionCommandApiMapper mapper;

    /**
     * PermissionCommandController 생성자
     *
     * @param createPermissionUseCase Permission 생성 UseCase
     * @param updatePermissionUseCase Permission 수정 UseCase
     * @param deletePermissionUseCase Permission 삭제 UseCase
     * @param mapper API 매퍼
     */
    public PermissionCommandController(
            CreatePermissionUseCase createPermissionUseCase,
            UpdatePermissionUseCase updatePermissionUseCase,
            DeletePermissionUseCase deletePermissionUseCase,
            PermissionCommandApiMapper mapper) {
        this.createPermissionUseCase = createPermissionUseCase;
        this.updatePermissionUseCase = updatePermissionUseCase;
        this.deletePermissionUseCase = deletePermissionUseCase;
        this.mapper = mapper;
    }

    /**
     * Permission 생성 API
     *
     * <p>새로운 권한을 생성합니다. REST API를 통해 생성되는 권한은 CUSTOM 타입입니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 Permission ID
     */
    @Operation(summary = "권한 생성", description = "새로운 권한을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 권한 키")
    })
    @PreAuthorize("@access.hasPermission('permission', 'create')")
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionIdApiResponse>> create(
            @Valid @RequestBody CreatePermissionApiRequest request) {

        CreatePermissionCommand command = mapper.toCommand(request);
        Long permissionId = createPermissionUseCase.execute(command);

        PermissionIdApiResponse apiResponse = PermissionIdApiResponse.of(permissionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * Permission 수정 API
     *
     * <p>기존 권한의 설명을 수정합니다. 시스템 권한은 수정할 수 없습니다.
     *
     * @param permissionId Permission ID
     * @param request 수정 요청 DTO
     * @return 수정된 Permission ID
     */
    @Operation(summary = "권한 수정", description = "기존 권한의 설명을 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = "시스템 권한 수정 불가"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "권한을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('permission', 'update')")
    @PutMapping(PermissionApiEndpoints.ID)
    public ResponseEntity<ApiResponse<PermissionIdApiResponse>> update(
            @Parameter(description = "Permission ID", required = true)
                    @PathVariable(PermissionApiEndpoints.PATH_PERMISSION_ID)
                    Long permissionId,
            @Valid @RequestBody UpdatePermissionApiRequest request) {

        UpdatePermissionCommand command = mapper.toCommand(permissionId, request);
        updatePermissionUseCase.execute(command);

        PermissionIdApiResponse apiResponse = PermissionIdApiResponse.of(permissionId);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * Permission 삭제 API
     *
     * <p>권한을 삭제합니다. 시스템 권한 및 사용 중인 권한은 삭제할 수 없습니다.
     *
     * @param permissionId Permission ID
     * @return 삭제 성공 응답
     */
    @Operation(summary = "권한 삭제", description = "권한을 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = "시스템 권한 삭제 불가"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "권한을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "사용 중인 권한")
    })
    @PreAuthorize("@access.hasPermission('permission', 'delete')")
    @DeleteMapping(PermissionApiEndpoints.ID)
    public ResponseEntity<Void> delete(
            @Parameter(description = "Permission ID", required = true)
                    @PathVariable(PermissionApiEndpoints.PATH_PERMISSION_ID)
                    Long permissionId) {

        DeletePermissionCommand command = mapper.toDeleteCommand(permissionId);
        deletePermissionUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
