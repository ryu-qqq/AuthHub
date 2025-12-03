package com.ryuqq.authhub.adapter.in.rest.role.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.UserRolesApiResponse;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.role.port.in.GetUserRolesUseCase;

/**
 * Role Query Controller - 권한 조회 API
 *
 * <p>사용자 역할 및 권한 정보 조회 Query 작업을 처리합니다.
 *
 * <p><strong>엔드포인트:</strong>
 * <ul>
 *   <li>GET /api/v1/users/{userId}/roles - 사용자 권한 조회 (200 OK)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping("${api.endpoints.base-v1}${api.endpoints.user.base}")
public class RoleQueryController {

    private final GetUserRolesUseCase getUserRolesUseCase;

    public RoleQueryController(GetUserRolesUseCase getUserRolesUseCase) {
        this.getUserRolesUseCase = getUserRolesUseCase;
    }

    /**
     * 사용자 권한 조회 API
     *
     * @param userId 사용자 ID
     * @return 200 OK와 사용자의 역할 및 권한 정보
     */
    @GetMapping("${api.endpoints.user.roles}")
    public ResponseEntity<ApiResponse<UserRolesApiResponse>> getUserRoles(@PathVariable UUID userId) {
        UserRolesResponse useCaseResponse = getUserRolesUseCase.execute(userId);
        UserRolesApiResponse apiResponse = UserRolesApiResponse.from(useCaseResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
