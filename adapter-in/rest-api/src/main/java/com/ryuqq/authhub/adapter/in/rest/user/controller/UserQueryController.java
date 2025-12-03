package com.ryuqq.authhub.adapter.in.rest.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.in.GetUserUseCase;

/**
 * User Query Controller - 사용자 조회 API
 *
 * <p>사용자 정보 조회 Query 작업을 처리합니다.
 *
 * <p><strong>엔드포인트:</strong>
 * <ul>
 *   <li>GET /api/v1/users/{userId} - 사용자 조회 (200 OK)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping("${api.endpoints.base-v1}${api.endpoints.user.base}")
public class UserQueryController {

    private final GetUserUseCase getUserUseCase;

    public UserQueryController(GetUserUseCase getUserUseCase) {
        this.getUserUseCase = getUserUseCase;
    }

    /**
     * 사용자 조회 API
     *
     * @param userId 조회할 사용자 ID
     * @return 200 OK와 사용자 정보
     */
    @GetMapping("${api.endpoints.user.by-id}")
    public ResponseEntity<ApiResponse<UserApiResponse>> getUser(@PathVariable UUID userId) {
        UserResponse useCaseResponse = getUserUseCase.execute(userId);
        UserApiResponse apiResponse = UserApiResponse.from(useCaseResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
