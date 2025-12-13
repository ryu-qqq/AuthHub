package com.ryuqq.authhub.adapter.in.rest.user.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserPasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.CreateUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserApiMapper;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.in.command.CreateUserUseCase;
import com.ryuqq.authhub.application.user.port.in.command.DeleteUserUseCase;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserPasswordUseCase;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserStatusUseCase;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserUseCase;
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
 * UserCommandController - 사용자 Command API 컨트롤러 (Admin)
 *
 * <p>사용자 생성, 수정, 삭제 등 Command 작업을 처리합니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/admin/users (admin.connectly.com)
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
@Tag(name = "User", description = "사용자 관리 API (Admin)")
@RestController
@RequestMapping(ApiPaths.Users.BASE)
public class UserCommandController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UpdateUserStatusUseCase updateUserStatusUseCase;
    private final UpdateUserPasswordUseCase updateUserPasswordUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserApiMapper mapper;

    public UserCommandController(
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            UpdateUserStatusUseCase updateUserStatusUseCase,
            UpdateUserPasswordUseCase updateUserPasswordUseCase,
            DeleteUserUseCase deleteUserUseCase,
            UserApiMapper mapper) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.updateUserStatusUseCase = updateUserStatusUseCase;
        this.updateUserPasswordUseCase = updateUserPasswordUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.mapper = mapper;
    }

    /**
     * 사용자 생성
     *
     * <p>POST /api/v1/users
     *
     * @param request 사용자 생성 요청
     * @return 201 Created + 생성된 사용자 ID
     */
    @Operation(
            summary = "사용자 생성",
            description =
                    """
                    새로운 사용자를 생성합니다.

                    **필요 권한**: `user:create`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "사용자 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 사용자")
    })
    @PreAuthorize("@access.hasPermission('user:create')")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateUserApiResponse>> createUser(
            @Valid @RequestBody CreateUserApiRequest request) {
        UserResponse response = createUserUseCase.execute(mapper.toCommand(request));
        CreateUserApiResponse apiResponse = mapper.toCreateResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 사용자 정보 수정
     *
     * <p>PUT /api/v1/users/{userId}
     *
     * @param userId 사용자 ID
     * @param request 사용자 수정 요청
     * @return 200 OK
     */
    @Operation(
            summary = "사용자 정보 수정",
            description =
                    """
                    사용자 정보를 수정합니다.

                    **필요 권한**: 본인 또는 `user:update` 권한 보유자
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
                description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("@access.user(#userId, 'update')")
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId,
            @Valid @RequestBody UpdateUserApiRequest request) {
        updateUserUseCase.execute(mapper.toCommand(userId, request));
        return ResponseEntity.ok(ApiResponse.ofSuccess(null));
    }

    /**
     * 사용자 상태 변경
     *
     * <p>PATCH /api/v1/users/{userId}/status
     *
     * @param userId 사용자 ID
     * @param request 상태 변경 요청
     * @return 200 OK
     */
    @Operation(
            summary = "사용자 상태 변경",
            description =
                    """
                    사용자의 상태를 변경합니다.

                    **필요 권한**: `user:update`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "상태 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('user:update')")
    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId,
            @Valid @RequestBody UpdateUserStatusApiRequest request) {
        updateUserStatusUseCase.execute(mapper.toStatusCommand(userId, request));
        return ResponseEntity.ok(ApiResponse.ofSuccess(null));
    }

    /**
     * 사용자 비밀번호 변경
     *
     * <p>PATCH /api/v1/users/{userId}/password
     *
     * @param userId 사용자 ID
     * @param request 비밀번호 변경 요청
     * @return 200 OK
     */
    @Operation(
            summary = "사용자 비밀번호 변경",
            description =
                    """
                    사용자의 비밀번호를 변경합니다.

                    **필요 권한**: 본인 또는 `user:update` 권한 보유자
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "비밀번호 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("@access.user(#userId, 'update')")
    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> updateUserPassword(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId,
            @Valid @RequestBody UpdateUserPasswordApiRequest request) {
        updateUserPasswordUseCase.execute(mapper.toPasswordCommand(userId, request));
        return ResponseEntity.ok(ApiResponse.ofSuccess(null));
    }

    /**
     * 사용자 삭제
     *
     * <p>DELETE /api/v1/users/{userId}
     *
     * @param userId 사용자 ID
     * @return 204 No Content
     */
    @Operation(
            summary = "사용자 삭제",
            description =
                    """
                    사용자를 삭제합니다.

                    **필요 권한**: `user:delete`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('user:delete')")
    @PatchMapping("/{userId}/delete")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId) {
        deleteUserUseCase.execute(mapper.toDeleteCommand(userId));
        return ResponseEntity.noContent().build();
    }
}
