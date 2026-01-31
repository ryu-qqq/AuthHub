package com.ryuqq.authhub.adapter.in.rest.userrole.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.userrole.UserRoleApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.userrole.dto.request.AssignUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.userrole.dto.request.RevokeUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.userrole.mapper.UserRoleCommandApiMapper;
import com.ryuqq.authhub.application.userrole.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.userrole.dto.command.RevokeUserRoleCommand;
import com.ryuqq.authhub.application.userrole.port.in.command.AssignUserRoleUseCase;
import com.ryuqq.authhub.application.userrole.port.in.command.RevokeUserRoleUseCase;
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
 * UserRoleCommandController - 사용자-역할 관계 관리 API
 *
 * <p>사용자에게 역할을 할당/철회하는 엔드포인트를 제공합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/users/{userId}/roles
 *   ├── POST   # 역할 할당 (Assign)
 *   └── DELETE # 역할 철회 (Revoke)
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
@Tag(name = "User Role", description = "사용자-역할 관계 관리 API")
@RestController
@RequestMapping(UserRoleApiEndpoints.BASE)
public class UserRoleCommandController {

    private final AssignUserRoleUseCase assignUserRoleUseCase;
    private final RevokeUserRoleUseCase revokeUserRoleUseCase;
    private final UserRoleCommandApiMapper mapper;

    /**
     * UserRoleCommandController 생성자
     *
     * @param assignUserRoleUseCase 역할 할당 UseCase
     * @param revokeUserRoleUseCase 역할 철회 UseCase
     * @param mapper API 매퍼
     */
    public UserRoleCommandController(
            AssignUserRoleUseCase assignUserRoleUseCase,
            RevokeUserRoleUseCase revokeUserRoleUseCase,
            UserRoleCommandApiMapper mapper) {
        this.assignUserRoleUseCase = assignUserRoleUseCase;
        this.revokeUserRoleUseCase = revokeUserRoleUseCase;
        this.mapper = mapper;
    }

    /**
     * 사용자에게 역할 할당 API
     *
     * <p>지정한 사용자에게 하나 이상의 역할을 할당합니다.
     *
     * @param userId 사용자 ID
     * @param request 역할 할당 요청 DTO
     * @return 성공 응답
     */
    @Operation(summary = "사용자에게 역할 할당", description = "지정한 사용자에게 하나 이상의 역할을 할당합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "역할 할당 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자 또는 역할을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "이미 할당된 역할")
    })
    @PreAuthorize("@access.hasPermission('user', 'update')")
    @PostMapping(UserRoleApiEndpoints.ROLES)
    public ResponseEntity<ApiResponse<Void>> assignRoles(
            @Parameter(description = "사용자 ID", required = true)
                    @PathVariable(UserRoleApiEndpoints.PATH_USER_ID)
                    String userId,
            @Valid @RequestBody AssignUserRoleApiRequest request) {

        AssignUserRoleCommand command = mapper.toAssignCommand(userId, request);
        assignUserRoleUseCase.assign(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(null));
    }

    /**
     * 사용자로부터 역할 철회 API
     *
     * <p>지정한 사용자로부터 하나 이상의 역할을 철회합니다.
     *
     * @param userId 사용자 ID
     * @param request 역할 철회 요청 DTO
     * @return 삭제 성공 응답
     */
    @Operation(summary = "사용자로부터 역할 철회", description = "지정한 사용자로부터 하나 이상의 역할을 철회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "역할 철회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자 또는 사용자-역할 관계를 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('user', 'update')")
    @DeleteMapping(UserRoleApiEndpoints.ROLES)
    public ResponseEntity<Void> revokeRoles(
            @Parameter(description = "사용자 ID", required = true)
                    @PathVariable(UserRoleApiEndpoints.PATH_USER_ID)
                    String userId,
            @Valid @RequestBody RevokeUserRoleApiRequest request) {

        RevokeUserRoleCommand command = mapper.toRevokeCommand(userId, request);
        revokeUserRoleUseCase.revoke(command);

        return ResponseEntity.noContent().build();
    }
}
