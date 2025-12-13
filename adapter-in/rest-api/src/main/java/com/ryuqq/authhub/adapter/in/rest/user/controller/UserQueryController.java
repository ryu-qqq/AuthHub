package com.ryuqq.authhub.adapter.in.rest.user.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContextHolder;
import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.query.SearchUsersApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserApiMapper;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.in.query.GetUserUseCase;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserQueryController - 사용자 Query API 컨트롤러 (Admin)
 *
 * <p>사용자 조회 등 Query 작업을 처리합니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/admin/users (admin.connectly.com)
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @RestController} + {@code @RequestMapping} 필수
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
public class UserQueryController {

    private final GetUserUseCase getUserUseCase;
    private final SearchUsersUseCase searchUsersUseCase;
    private final UserApiMapper mapper;

    public UserQueryController(
            GetUserUseCase getUserUseCase,
            SearchUsersUseCase searchUsersUseCase,
            UserApiMapper mapper) {
        this.getUserUseCase = getUserUseCase;
        this.searchUsersUseCase = searchUsersUseCase;
        this.mapper = mapper;
    }

    /**
     * 내 정보 조회
     *
     * <p>GET /api/v1/auth/users/me
     *
     * <p>현재 인증된 사용자의 정보를 조회합니다. 인증된 모든 사용자가 접근할 수 있습니다.
     *
     * @return 200 OK + 현재 사용자 정보
     */
    @Operation(summary = "내 정보 조회", description = "현재 인증된 사용자의 정보를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "인증 필요")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping(ApiPaths.Users.ME)
    public ResponseEntity<ApiResponse<UserApiResponse>> getMyInfo() {
        String currentUserId = SecurityContextHolder.getCurrentUserId();
        UserResponse response = getUserUseCase.execute(mapper.toGetQuery(currentUserId));
        UserApiResponse apiResponse = mapper.toApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 사용자 단건 조회
     *
     * <p>GET /api/v1/users/{userId}
     *
     * @param userId 사용자 ID
     * @return 200 OK + 사용자 상세 정보
     */
    @Operation(summary = "사용자 단건 조회", description = "사용자 ID로 사용자 정보를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("@access.user(#userId, 'read')")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserApiResponse>> getUser(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId) {
        UserResponse response = getUserUseCase.execute(mapper.toGetQuery(userId));
        UserApiResponse apiResponse = mapper.toApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 사용자 목록 검색
     *
     * <p>GET /api/v1/users
     *
     * @param tenantId 테넌트 ID 필터 (선택)
     * @param organizationId 조직 ID 필터 (선택)
     * @param identifier 식별자 필터 (선택)
     * @param status 상태 필터 (선택, ACTIVE/INACTIVE/SUSPENDED)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 200 OK + 사용자 목록
     */
    @Operation(summary = "사용자 목록 검색", description = "조건에 맞는 사용자 목록을 검색합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('user:read')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserApiResponse>>> searchUsers(
            @Parameter(description = "테넌트 ID 필터") @RequestParam(required = false) String tenantId,
            @Parameter(description = "조직 ID 필터") @RequestParam(required = false)
                    String organizationId,
            @Parameter(description = "식별자 필터") @RequestParam(required = false) String identifier,
            @Parameter(description = "상태 필터 (ACTIVE/INACTIVE/SUSPENDED)")
                    @RequestParam(required = false)
                    String status,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") Integer size) {
        SearchUsersApiRequest request =
                new SearchUsersApiRequest(tenantId, organizationId, identifier, status, page, size);
        List<UserResponse> responses = searchUsersUseCase.execute(mapper.toQuery(request));
        List<UserApiResponse> apiResponses = mapper.toApiResponseList(responses);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
    }
}
