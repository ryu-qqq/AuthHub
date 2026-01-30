package com.ryuqq.authhub.adapter.in.rest.user.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.UserApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangePasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserIdApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserCommandApiMapper;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.port.in.command.ChangePasswordUseCase;
import com.ryuqq.authhub.application.user.port.in.command.CreateUserUseCase;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserCommandController - User 생성/수정 API
 *
 * <p>사용자 생성/수정 엔드포인트를 제공합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/users
 *   ├── POST   /                  # 생성
 *   ├── PUT    /{userId}          # 정보 수정
 *   └── PUT    /{userId}/password # 비밀번호 변경
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
@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping(UserApiEndpoints.USERS)
public class UserCommandController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final UserCommandApiMapper mapper;

    /**
     * UserCommandController 생성자
     *
     * @param createUserUseCase User 생성 UseCase
     * @param updateUserUseCase User 정보 수정 UseCase
     * @param changePasswordUseCase 비밀번호 변경 UseCase
     * @param mapper API 매퍼
     */
    public UserCommandController(
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            UserCommandApiMapper mapper) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.mapper = mapper;
    }

    /**
     * User 생성 API
     *
     * <p>새로운 사용자를 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 User ID
     */
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "조직을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 식별자 또는 전화번호")
    })
    @PreAuthorize("@access.hasPermission('user', 'create')")
    @PostMapping
    public ResponseEntity<ApiResponse<UserIdApiResponse>> create(
            @Valid @RequestBody CreateUserApiRequest request) {

        CreateUserCommand command = mapper.toCommand(request);
        String userId = createUserUseCase.execute(command);

        UserIdApiResponse apiResponse = UserIdApiResponse.of(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * User 정보 수정 API
     *
     * <p>기존 사용자의 정보를 수정합니다.
     *
     * @param userId User ID
     * @param request 수정 요청 DTO
     * @return 수정된 User ID
     */
    @Operation(summary = "사용자 정보 수정", description = "기존 사용자의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 전화번호")
    })
    @PreAuthorize("@access.hasPermission('user', 'update') or @access.self(#userId)")
    @PutMapping(UserApiEndpoints.ID)
    public ResponseEntity<ApiResponse<UserIdApiResponse>> update(
            @Parameter(description = "User ID", required = true)
                    @PathVariable(UserApiEndpoints.PATH_USER_ID)
                    String userId,
            @Valid @RequestBody UpdateUserApiRequest request) {

        UpdateUserCommand command = mapper.toCommand(userId, request);
        updateUserUseCase.execute(command);

        UserIdApiResponse apiResponse = UserIdApiResponse.of(userId);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 비밀번호 변경 API
     *
     * <p>사용자의 비밀번호를 변경합니다.
     *
     * @param userId User ID
     * @param request 비밀번호 변경 요청 DTO
     * @return 성공 응답
     */
    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "현재 비밀번호가 일치하지 않음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("@access.self(#userId)")
    @PutMapping(UserApiEndpoints.PASSWORD)
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "User ID", required = true)
                    @PathVariable(UserApiEndpoints.PATH_USER_ID)
                    String userId,
            @Valid @RequestBody ChangePasswordApiRequest request) {

        ChangePasswordCommand command = mapper.toCommand(userId, request);
        changePasswordUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.ofSuccess(null));
    }
}
