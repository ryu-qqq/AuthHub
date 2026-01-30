package com.ryuqq.authhub.adapter.in.rest.rolepermission.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.RolePermissionApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.dto.request.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.dto.request.RevokeRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.mapper.RolePermissionCommandApiMapper;
import com.ryuqq.authhub.application.rolepermission.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.rolepermission.dto.command.RevokeRolePermissionCommand;
import com.ryuqq.authhub.application.rolepermission.port.in.command.GrantRolePermissionUseCase;
import com.ryuqq.authhub.application.rolepermission.port.in.command.RevokeRolePermissionUseCase;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RolePermissionCommandController - 역할-권한 관계 관리 API
 *
 * <p>역할에 권한을 부여/제거하는 엔드포인트를 제공합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/roles/{roleId}/permissions
 *   ├── POST   # 권한 부여 (Grant)
 *   └── DELETE # 권한 제거 (Revoke)
 * }</pre>
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
@Tag(name = "Role Permission", description = "역할-권한 관계 관리 API")
@RestController
@RequestMapping(RolePermissionApiEndpoints.BASE)
public class RolePermissionCommandController {

    private final GrantRolePermissionUseCase grantRolePermissionUseCase;
    private final RevokeRolePermissionUseCase revokeRolePermissionUseCase;
    private final RolePermissionCommandApiMapper mapper;

    /**
     * RolePermissionCommandController 생성자
     *
     * @param grantRolePermissionUseCase 권한 부여 UseCase
     * @param revokeRolePermissionUseCase 권한 제거 UseCase
     * @param mapper API 매퍼
     */
    public RolePermissionCommandController(
            GrantRolePermissionUseCase grantRolePermissionUseCase,
            RevokeRolePermissionUseCase revokeRolePermissionUseCase,
            RolePermissionCommandApiMapper mapper) {
        this.grantRolePermissionUseCase = grantRolePermissionUseCase;
        this.revokeRolePermissionUseCase = revokeRolePermissionUseCase;
        this.mapper = mapper;
    }

    /**
     * 역할에 권한 부여 API
     *
     * <p>지정한 역할에 하나 이상의 권한을 부여합니다.
     *
     * @param roleId 역할 ID
     * @param request 권한 부여 요청 DTO
     * @return 성공 응답
     */
    @Operation(summary = "역할에 권한 부여", description = "지정한 역할에 하나 이상의 권한을 부여합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "권한 부여 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "역할 또는 권한을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "이미 부여된 권한")
    })
    @PreAuthorize("@access.hasPermission('role', 'update')")
    @PostMapping(RolePermissionApiEndpoints.PERMISSIONS)
    public ResponseEntity<ApiResponse<Void>> grantPermissions(
            @Parameter(description = "역할 ID", required = true)
                    @PathVariable(RolePermissionApiEndpoints.PATH_ROLE_ID)
                    Long roleId,
            @Valid @RequestBody GrantRolePermissionApiRequest request) {

        GrantRolePermissionCommand command = mapper.toGrantCommand(roleId, request);
        grantRolePermissionUseCase.grant(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(null));
    }

    /**
     * 역할에서 권한 제거 API
     *
     * <p>지정한 역할에서 하나 이상의 권한을 제거합니다.
     *
     * @param roleId 역할 ID
     * @param request 권한 제거 요청 DTO
     * @return 삭제 성공 응답
     */
    @Operation(summary = "역할에서 권한 제거", description = "지정한 역할에서 하나 이상의 권한을 제거합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "권한 제거 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "역할 또는 역할-권한 관계를 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('role', 'update')")
    @DeleteMapping(RolePermissionApiEndpoints.PERMISSIONS)
    public ResponseEntity<Void> revokePermissions(
            @Parameter(description = "역할 ID", required = true)
                    @PathVariable(RolePermissionApiEndpoints.PATH_ROLE_ID)
                    Long roleId,
            @Valid @RequestBody RevokeRolePermissionApiRequest request) {

        RevokeRolePermissionCommand command = mapper.toRevokeCommand(roleId, request);
        revokeRolePermissionUseCase.revoke(command);

        return ResponseEntity.noContent().build();
    }
}
