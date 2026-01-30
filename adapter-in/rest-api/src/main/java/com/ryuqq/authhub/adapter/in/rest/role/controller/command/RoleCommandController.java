package com.ryuqq.authhub.adapter.in.rest.role.controller.command;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.RoleApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.role.dto.request.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.request.UpdateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleIdApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.mapper.RoleCommandApiMapper;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RoleCommandController - Role 생성/수정/삭제 API
 *
 * <p>역할 생성/수정/삭제 엔드포인트를 제공합니다.
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
@Tag(name = "Role", description = "역할 관리 API")
@RestController
@RequestMapping(RoleApiEndpoints.ROLES)
public class RoleCommandController {

    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final RoleCommandApiMapper mapper;

    /**
     * RoleCommandController 생성자
     *
     * @param createRoleUseCase Role 생성 UseCase
     * @param updateRoleUseCase Role 수정 UseCase
     * @param deleteRoleUseCase Role 삭제 UseCase
     * @param mapper API 매퍼
     */
    public RoleCommandController(
            CreateRoleUseCase createRoleUseCase,
            UpdateRoleUseCase updateRoleUseCase,
            DeleteRoleUseCase deleteRoleUseCase,
            RoleCommandApiMapper mapper) {
        this.createRoleUseCase = createRoleUseCase;
        this.updateRoleUseCase = updateRoleUseCase;
        this.deleteRoleUseCase = deleteRoleUseCase;
        this.mapper = mapper;
    }

    /**
     * Role 생성 API
     *
     * <p>새로운 역할을 생성합니다. REST API를 통해 생성되는 역할은 CUSTOM 타입입니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 Role ID
     */
    @Operation(summary = "역할 생성", description = "새로운 역할을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 역할 이름")
    })
    @PreAuthorize("@access.hasPermission('role', 'create')")
    @PostMapping
    public ResponseEntity<ApiResponse<RoleIdApiResponse>> create(
            @Valid @RequestBody CreateRoleApiRequest request) {

        CreateRoleCommand command = mapper.toCommand(request);
        Long roleId = createRoleUseCase.execute(command);

        RoleIdApiResponse apiResponse = RoleIdApiResponse.of(roleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * Role 수정 API
     *
     * <p>기존 역할의 표시 이름과 설명을 수정합니다. 시스템 역할은 수정할 수 없습니다.
     *
     * @param roleId Role ID
     * @param request 수정 요청 DTO
     * @return 수정된 Role ID
     */
    @Operation(summary = "역할 수정", description = "기존 역할의 표시 이름과 설명을 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = "시스템 역할 수정 불가"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "역할을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('role', 'update')")
    @PutMapping(RoleApiEndpoints.ID)
    public ResponseEntity<ApiResponse<RoleIdApiResponse>> update(
            @Parameter(description = "Role ID", required = true)
                    @PathVariable(RoleApiEndpoints.PATH_ROLE_ID)
                    Long roleId,
            @Valid @RequestBody UpdateRoleApiRequest request) {

        UpdateRoleCommand command = mapper.toCommand(roleId, request);
        updateRoleUseCase.execute(command);

        RoleIdApiResponse apiResponse = RoleIdApiResponse.of(roleId);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * Role 삭제 API
     *
     * <p>역할을 삭제합니다. 시스템 역할 및 사용 중인 역할은 삭제할 수 없습니다.
     *
     * @param roleId Role ID
     * @return 삭제 성공 응답
     */
    @Operation(summary = "역할 삭제", description = "역할을 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = "시스템 역할 삭제 불가"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "역할을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "사용 중인 역할")
    })
    @PreAuthorize("@access.hasPermission('role', 'delete')")
    @DeleteMapping(RoleApiEndpoints.ID)
    public ResponseEntity<Void> delete(
            @Parameter(description = "Role ID", required = true)
                    @PathVariable(RoleApiEndpoints.PATH_ROLE_ID)
                    Long roleId) {

        DeleteRoleCommand command = mapper.toDeleteCommand(roleId);
        deleteRoleUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
