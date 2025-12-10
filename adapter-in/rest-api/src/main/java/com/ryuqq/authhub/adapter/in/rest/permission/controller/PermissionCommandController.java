package com.ryuqq.authhub.adapter.in.rest.permission.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.CreatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.UpdatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.CreatePermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.mapper.PermissionApiMapper;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PermissionCommandController - 권한 Command API 컨트롤러
 *
 * <p>권한 생성, 수정, 삭제 등 Command 작업을 처리합니다.
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
@Tag(name = "Permission", description = "권한 관리 API")
@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionCommandController {

    private final CreatePermissionUseCase createPermissionUseCase;
    private final UpdatePermissionUseCase updatePermissionUseCase;
    private final DeletePermissionUseCase deletePermissionUseCase;
    private final PermissionApiMapper mapper;

    public PermissionCommandController(
            CreatePermissionUseCase createPermissionUseCase,
            UpdatePermissionUseCase updatePermissionUseCase,
            DeletePermissionUseCase deletePermissionUseCase,
            PermissionApiMapper mapper) {
        this.createPermissionUseCase = createPermissionUseCase;
        this.updatePermissionUseCase = updatePermissionUseCase;
        this.deletePermissionUseCase = deletePermissionUseCase;
        this.mapper = mapper;
    }

    /**
     * 권한 생성
     *
     * <p>POST /api/v1/permissions
     *
     * @param request 권한 생성 요청
     * @return 201 Created + 생성된 권한 ID
     */
    @Operation(summary = "권한 생성", description = "새로운 권한을 생성합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "권한 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 권한")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CreatePermissionApiResponse>> createPermission(
            @Valid @RequestBody CreatePermissionApiRequest request) {
        PermissionResponse response = createPermissionUseCase.execute(mapper.toCommand(request));
        CreatePermissionApiResponse apiResponse = mapper.toCreateResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 권한 수정
     *
     * <p>PUT /api/v1/permissions/{permissionId}
     *
     * @param permissionId 권한 ID
     * @param request 권한 수정 요청
     * @return 200 OK
     */
    @Operation(summary = "권한 수정", description = "권한 정보를 수정합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "권한을 찾을 수 없음")
    })
    @PutMapping("/{permissionId}")
    public ResponseEntity<ApiResponse<Void>> updatePermission(
            @Parameter(description = "권한 ID", required = true) @PathVariable String permissionId,
            @Valid @RequestBody UpdatePermissionApiRequest request) {
        updatePermissionUseCase.execute(mapper.toCommand(permissionId, request));
        return ResponseEntity.ok(ApiResponse.ofSuccess(null));
    }

    /**
     * 권한 삭제
     *
     * <p>DELETE /api/v1/permissions/{permissionId}
     *
     * @param permissionId 권한 ID
     * @return 204 No Content
     */
    @Operation(summary = "권한 삭제", description = "권한을 삭제합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "권한을 찾을 수 없음")
    })
    @PatchMapping("/{permissionId}/delete")
    public ResponseEntity<Void> deletePermission(
            @Parameter(description = "권한 ID", required = true) @PathVariable String permissionId) {
        deletePermissionUseCase.execute(mapper.toDeleteCommand(permissionId));
        return ResponseEntity.noContent().build();
    }
}
