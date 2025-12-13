package com.ryuqq.authhub.adapter.in.rest.role.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.UpdateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.CreateRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.mapper.RoleApiMapper;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.port.in.command.CreateRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.command.DeleteRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.command.UpdateRoleUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
 * RoleCommandController - 역할 Command API 컨트롤러 (Admin)
 *
 * <p>역할 생성, 수정, 삭제 등 Command 작업을 처리합니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/admin/roles (admin.connectly.com)
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
@Tag(name = "Role", description = "역할 관리 API (Admin)")
@RestController
@RequestMapping(ApiPaths.Roles.BASE)
public class RoleCommandController {

    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final RoleApiMapper mapper;

    public RoleCommandController(
            CreateRoleUseCase createRoleUseCase,
            UpdateRoleUseCase updateRoleUseCase,
            DeleteRoleUseCase deleteRoleUseCase,
            RoleApiMapper mapper) {
        this.createRoleUseCase = createRoleUseCase;
        this.updateRoleUseCase = updateRoleUseCase;
        this.deleteRoleUseCase = deleteRoleUseCase;
        this.mapper = mapper;
    }

    /**
     * 역할 생성
     *
     * <p>POST /api/v1/roles
     *
     * @param request 역할 생성 요청
     * @return 201 Created + 생성된 역할 ID
     */
    @Operation(
            summary = "역할 생성",
            description =
                    """
                    새로운 역할을 생성합니다.

                    **필요 권한**: `role:create`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "역할 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 역할")
    })
    @PreAuthorize("@access.hasPermission('role:create')")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateRoleApiResponse>> createRole(
            @Valid @RequestBody CreateRoleApiRequest request) {
        RoleResponse response = createRoleUseCase.execute(mapper.toCommand(request));
        CreateRoleApiResponse apiResponse = mapper.toCreateResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 역할 수정
     *
     * <p>PUT /api/v1/roles/{roleId}
     *
     * @param roleId 역할 ID
     * @param request 역할 수정 요청
     * @return 200 OK
     */
    @Operation(
            summary = "역할 수정",
            description =
                    """
                    역할 정보를 수정합니다.

                    **필요 권한**: `role:update`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "역할을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('role:update')")
    @PutMapping("/{roleId}")
    public ResponseEntity<ApiResponse<Void>> updateRole(
            @Parameter(description = "역할 ID", required = true) @PathVariable String roleId,
            @Valid @RequestBody UpdateRoleApiRequest request) {
        updateRoleUseCase.execute(mapper.toCommand(roleId, request));
        return ResponseEntity.ok(ApiResponse.ofSuccess(null));
    }

    /**
     * 역할 삭제
     *
     * <p>DELETE /api/v1/roles/{roleId}
     *
     * @param roleId 역할 ID
     * @return 204 No Content
     */
    @Operation(
            summary = "역할 삭제",
            description =
                    """
                    역할을 삭제합니다.

                    **필요 권한**: `role:delete`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "역할을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('role:delete')")
    @PatchMapping("/{roleId}/delete")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "역할 ID", required = true) @PathVariable String roleId) {
        deleteRoleUseCase.execute(mapper.toDeleteCommand(roleId));
        return ResponseEntity.noContent().build();
    }
}
