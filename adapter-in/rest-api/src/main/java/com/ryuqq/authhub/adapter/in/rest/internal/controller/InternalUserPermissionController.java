package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints.USERS;
import static com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints.USER_PERMISSIONS;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.UserPermissionsApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalUserPermissionApiMapper;
import com.ryuqq.authhub.application.userrole.dto.response.UserPermissionsResult;
import com.ryuqq.authhub.application.userrole.port.in.query.GetUserPermissionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InternalUserPermissionController - Gateway용 사용자 권한 Internal API Controller
 *
 * <p>Gateway가 사용자 인가 검증을 위해 역할/권한 정보를 조회합니다.
 *
 * <p><strong>보안 참고:</strong>
 *
 * <ul>
 *   <li>이 API는 서비스 토큰 인증으로 보호됩니다
 *   <li>외부 접근이 차단된 내부 네트워크에서만 접근 가능해야 합니다
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping(USERS)
@Tag(name = "Internal - User Permissions", description = "Gateway용 사용자 권한 Internal API")
public class InternalUserPermissionController {

    private final GetUserPermissionsUseCase getUserPermissionsUseCase;
    private final InternalUserPermissionApiMapper mapper;

    public InternalUserPermissionController(
            GetUserPermissionsUseCase getUserPermissionsUseCase,
            InternalUserPermissionApiMapper mapper) {
        this.getUserPermissionsUseCase = getUserPermissionsUseCase;
        this.mapper = mapper;
    }

    /**
     * 사용자 권한 조회
     *
     * <p>Gateway가 요청 처리 시 사용자 인가 검증을 위해 호출합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 역할/권한 정보
     */
    @GetMapping(USER_PERMISSIONS)
    @Operation(summary = "사용자 권한 조회", description = "Gateway가 사용자 인가 검증을 위해 역할/권한 정보를 조회합니다.")
    public ApiResponse<UserPermissionsApiResponse> getPermissions(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId) {
        UserPermissionsResult result = getUserPermissionsUseCase.getByUserId(userId);
        UserPermissionsApiResponse response = mapper.toApiResponse(result);
        return ApiResponse.ofSuccess(response);
    }
}
