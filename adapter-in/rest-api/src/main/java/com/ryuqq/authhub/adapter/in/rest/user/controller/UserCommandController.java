package com.ryuqq.authhub.adapter.in.rest.user.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangePasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangeUserStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.CreateUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserApiMapper;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.ChangeUserStatusCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.in.ChangePasswordUseCase;
import com.ryuqq.authhub.application.user.port.in.ChangeUserStatusUseCase;
import com.ryuqq.authhub.application.user.port.in.CreateUserUseCase;
import com.ryuqq.authhub.application.user.port.in.UpdateUserUseCase;

import jakarta.validation.Valid;

/**
 * User Command Controller - 사용자 상태 변경 API
 *
 * <p>사용자 생성, 수정, 비밀번호 변경, 상태 변경 등 Command 작업을 처리합니다.
 *
 * <p><strong>엔드포인트:</strong>
 * <ul>
 *   <li>POST /api/v1/users - 사용자 생성 (201 Created)</li>
 *   <li>PATCH /api/v1/users/{userId} - 사용자 정보 수정 (200 OK)</li>
 *   <li>PATCH /api/v1/users/{userId}/password - 비밀번호 변경 (200 OK)</li>
 *   <li>PATCH /api/v1/users/{userId}/status - 상태 변경 (200 OK)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping("${api.endpoints.base-v1}${api.endpoints.user.base}")
@Validated
public class UserCommandController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ChangeUserStatusUseCase changeUserStatusUseCase;
    private final UserApiMapper userApiMapper;

    public UserCommandController(
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            ChangeUserStatusUseCase changeUserStatusUseCase,
            UserApiMapper userApiMapper) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.changeUserStatusUseCase = changeUserStatusUseCase;
        this.userApiMapper = userApiMapper;
    }

    /**
     * 사용자 생성 API
     *
     * @param request 사용자 생성 요청 DTO
     * @return 201 Created와 생성된 사용자 정보 (userId, createdAt)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateUserApiResponse>> createUser(
            @Valid @RequestBody CreateUserApiRequest request) {
        CreateUserCommand command = userApiMapper.toCreateUserCommand(request);
        CreateUserResponse useCaseResponse = createUserUseCase.execute(command);
        CreateUserApiResponse apiResponse = CreateUserApiResponse.from(useCaseResponse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 사용자 정보 수정 API
     *
     * @param userId 수정 대상 사용자 ID
     * @param request 사용자 수정 요청 DTO
     * @return 200 OK와 수정된 사용자 정보
     */
    @PatchMapping("${api.endpoints.user.by-id}")
    public ResponseEntity<ApiResponse<UserApiResponse>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserApiRequest request) {
        UpdateUserCommand command = userApiMapper.toUpdateUserCommand(userId, request);
        UserResponse useCaseResponse = updateUserUseCase.execute(command);
        UserApiResponse apiResponse = UserApiResponse.from(useCaseResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 비밀번호 변경 API
     *
     * @param userId 사용자 ID
     * @param request 비밀번호 변경 요청 DTO
     * @return 200 OK
     */
    @PatchMapping("${api.endpoints.user.password}")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangePasswordApiRequest request) {
        ChangePasswordCommand command = userApiMapper.toChangePasswordCommand(userId, request);
        changePasswordUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.ofSuccess());
    }

    /**
     * 사용자 상태 변경 API
     *
     * @param userId 사용자 ID
     * @param request 상태 변경 요청 DTO
     * @return 200 OK
     */
    @PatchMapping("${api.endpoints.user.status}")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangeUserStatusApiRequest request) {
        ChangeUserStatusCommand command = userApiMapper.toChangeUserStatusCommand(userId, request);
        changeUserStatusUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.ofSuccess());
    }
}
