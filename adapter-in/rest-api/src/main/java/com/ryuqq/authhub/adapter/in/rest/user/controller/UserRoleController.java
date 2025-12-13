package com.ryuqq.authhub.adapter.in.rest.user.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.AssignUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserApiMapper;
import com.ryuqq.authhub.application.user.dto.response.UserRoleResponse;
import com.ryuqq.authhub.application.user.port.in.command.AssignUserRoleUseCase;
import com.ryuqq.authhub.application.user.port.in.command.RevokeUserRoleUseCase;
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
 * UserRoleController - 사용자 역할 할당/해제 API 컨트롤러 (Admin)
 *
 * <p>사용자에게 역할을 할당하거나 해제하는 API를 제공합니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/admin/users/{userId}/roles (admin.connectly.com)
 *
 * <p><strong>RESTful 설계:</strong>
 *
 * <ul>
 *   <li>POST /api/v1/auth/admin/users/{userId}/roles - 역할 할당
 *   <li>DELETE /api/v1/auth/admin/users/{userId}/roles/{roleId} - 역할 해제
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
@Tag(name = "UserRole", description = "사용자 역할 관리 API (Admin)")
@RestController
@RequestMapping(ApiPaths.Users.BASE + ApiPaths.Users.ROLES)
public class UserRoleController {

    private final AssignUserRoleUseCase assignUserRoleUseCase;
    private final RevokeUserRoleUseCase revokeUserRoleUseCase;
    private final UserApiMapper mapper;

    public UserRoleController(
            AssignUserRoleUseCase assignUserRoleUseCase,
            RevokeUserRoleUseCase revokeUserRoleUseCase,
            UserApiMapper mapper) {
        this.assignUserRoleUseCase = assignUserRoleUseCase;
        this.revokeUserRoleUseCase = revokeUserRoleUseCase;
        this.mapper = mapper;
    }

    /**
     * 사용자에게 역할 할당
     *
     * <p>POST /api/v1/users/{userId}/roles
     *
     * @param userId 사용자 ID
     * @param request 역할 할당 요청 (roleId)
     * @return 201 Created + 할당된 역할 정보
     */
    @Operation(summary = "사용자에게 역할 할당", description = "사용자에게 역할을 할당합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "역할 할당 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자 또는 역할을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('role:assign')")
    @PostMapping
    public ResponseEntity<ApiResponse<UserRoleApiResponse>> assignRole(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId,
            @Valid @RequestBody AssignUserRoleApiRequest request) {
        UserRoleResponse response =
                assignUserRoleUseCase.execute(mapper.toAssignRoleCommand(userId, request));
        UserRoleApiResponse apiResponse = mapper.toUserRoleApiResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 사용자에서 역할 해제
     *
     * <p>DELETE /api/v1/users/{userId}/roles/{roleId}
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 204 No Content
     */
    @Operation(summary = "사용자에서 역할 해제", description = "사용자에서 역할을 해제합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "역할 해제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자 또는 역할을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('role:assign')")
    @PatchMapping("/{roleId}/revoke")
    public ResponseEntity<Void> revokeRole(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId,
            @Parameter(description = "역할 ID", required = true) @PathVariable String roleId) {
        revokeUserRoleUseCase.execute(mapper.toRevokeRoleCommand(userId, roleId));
        return ResponseEntity.noContent().build();
    }
}
