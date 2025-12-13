package com.ryuqq.authhub.adapter.in.rest.role.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RolePermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.mapper.RoleApiMapper;
import com.ryuqq.authhub.application.role.dto.response.RolePermissionResponse;
import com.ryuqq.authhub.application.role.port.in.command.GrantRolePermissionUseCase;
import com.ryuqq.authhub.application.role.port.in.command.RevokeRolePermissionUseCase;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RolePermissionController - 역할 권한 부여/해제 API 컨트롤러 (Admin)
 *
 * <p>역할에 권한을 부여하거나 해제하는 API를 제공합니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/admin/roles/{roleId}/permissions (admin.connectly.com)
 *
 * <p><strong>RESTful 설계:</strong>
 *
 * <ul>
 *   <li>POST /api/v1/auth/admin/roles/{roleId}/permissions - 권한 부여
 *   <li>DELETE /api/v1/auth/admin/roles/{roleId}/permissions/{permissionId} - 권한 해제
 * </ul>
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
@Tag(name = "RolePermission", description = "역할 권한 관리 API (Admin)")
@RestController
@RequestMapping(ApiPaths.Roles.BASE + ApiPaths.Roles.PERMISSIONS)
public class RolePermissionController {

    private final GrantRolePermissionUseCase grantRolePermissionUseCase;
    private final RevokeRolePermissionUseCase revokeRolePermissionUseCase;
    private final RoleApiMapper mapper;

    public RolePermissionController(
            GrantRolePermissionUseCase grantRolePermissionUseCase,
            RevokeRolePermissionUseCase revokeRolePermissionUseCase,
            RoleApiMapper mapper) {
        this.grantRolePermissionUseCase = grantRolePermissionUseCase;
        this.revokeRolePermissionUseCase = revokeRolePermissionUseCase;
        this.mapper = mapper;
    }

    /**
     * 역할에 권한 부여
     *
     * <p>POST /api/v1/roles/{roleId}/permissions
     *
     * @param roleId 역할 ID
     * @param request 권한 부여 요청 (permissionId)
     * @return 201 Created + 부여된 권한 정보
     */
    @Operation(summary = "역할에 권한 부여", description = "역할에 권한을 부여합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "권한 부여 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "역할 또는 권한을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('permission:assign')")
    @PostMapping
    public ResponseEntity<ApiResponse<RolePermissionApiResponse>> grantPermission(
            @Parameter(description = "역할 ID", required = true) @PathVariable String roleId,
            @Valid @RequestBody GrantRolePermissionApiRequest request) {
        RolePermissionResponse response =
                grantRolePermissionUseCase.execute(
                        mapper.toGrantPermissionCommand(roleId, request));
        RolePermissionApiResponse apiResponse = mapper.toRolePermissionApiResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 역할에서 권한 해제
     *
     * <p>DELETE /api/v1/roles/{roleId}/permissions/{permissionId}
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 204 No Content
     */
    @Operation(summary = "역할에서 권한 해제", description = "역할에서 권한을 해제합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "권한 해제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "역할 또는 권한을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('permission:assign')")
    @PatchMapping("/{permissionId}/revoke")
    public ResponseEntity<Void> revokePermission(
            @Parameter(description = "역할 ID", required = true) @PathVariable String roleId,
            @Parameter(description = "권한 ID", required = true) @PathVariable String permissionId) {
        revokeRolePermissionUseCase.execute(mapper.toRevokePermissionCommand(roleId, permissionId));
        return ResponseEntity.noContent().build();
    }
}
