package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.CreateUserWithRolesApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.CreateUserWithRolesResultApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalUserApiMapper;
import com.ryuqq.authhub.application.user.dto.command.CreateUserWithRolesCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserWithRolesResult;
import com.ryuqq.authhub.application.user.port.in.command.CreateUserWithRolesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InternalUserCommandController - Internal 사용자 등록 Controller
 *
 * <p>사용자 생성 + SERVICE scope Role 자동 할당을 하나의 트랜잭션으로 처리합니다.
 *
 * <p><strong>보안 참고:</strong>
 *
 * <ul>
 *   <li>서비스 토큰 인증으로 보호됩니다
 *   <li>내부 네트워크에서만 접근 가능해야 합니다
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping(InternalApiEndpoints.USERS)
@Tag(name = "Internal - User", description = "Internal User API")
public class InternalUserCommandController {

    private final CreateUserWithRolesUseCase createUserWithRolesUseCase;
    private final InternalUserApiMapper mapper;

    public InternalUserCommandController(
            CreateUserWithRolesUseCase createUserWithRolesUseCase, InternalUserApiMapper mapper) {
        this.createUserWithRolesUseCase = createUserWithRolesUseCase;
        this.mapper = mapper;
    }

    /**
     * 사용자 등록 (생성 + 역할 할당)
     *
     * <p>사용자를 생성하고 선택적으로 SERVICE scope Role을 할당합니다.
     *
     * @param request 사용자 등록 요청
     * @return 생성된 userId, assignedRoleCount
     */
    @PostMapping(InternalApiEndpoints.USERS_REGISTER)
    @Operation(summary = "사용자 등록", description = "사용자 생성 + 역할 할당을 한 번에 처리합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "사용자 등록 성공")
    })
    public ResponseEntity<ApiResponse<CreateUserWithRolesResultApiResponse>> register(
            @Valid @RequestBody CreateUserWithRolesApiRequest request) {
        CreateUserWithRolesCommand command = mapper.toCommand(request);
        CreateUserWithRolesResult result = createUserWithRolesUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ofSuccess(mapper.toApiResponse(result)));
    }
}
