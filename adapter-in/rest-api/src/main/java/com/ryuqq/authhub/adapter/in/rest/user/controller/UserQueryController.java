package com.ryuqq.authhub.adapter.in.rest.user.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.UserApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.user.dto.request.SearchUsersOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserQueryApiMapper;
import com.ryuqq.authhub.application.user.dto.query.UserSearchParams;
import com.ryuqq.authhub.application.user.dto.response.UserPageResult;
import com.ryuqq.authhub.application.user.dto.response.UserResult;
import com.ryuqq.authhub.application.user.port.in.query.GetUserUseCase;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserQueryController - User 조회 API
 *
 * <p>사용자 조회 엔드포인트를 제공합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/users
 *   ├── GET    /          # 목록 조회 (복합 조건)
 *   └── GET    /{userId}  # 단건 조회
 * }</pre>
 *
 * <p>CTR-001: Controller는 @RestController로 정의.
 *
 * <p>CTR-002: Controller는 UseCase만 주입받음.
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping(UserApiEndpoints.USERS)
public class UserQueryController {

    private final GetUserUseCase getUserUseCase;
    private final SearchUsersUseCase searchUsersUseCase;
    private final UserQueryApiMapper mapper;

    /**
     * UserQueryController 생성자
     *
     * @param getUserUseCase User 단건 조회 UseCase
     * @param searchUsersUseCase User 목록 조회 UseCase
     * @param mapper API 매퍼
     */
    public UserQueryController(
            GetUserUseCase getUserUseCase,
            SearchUsersUseCase searchUsersUseCase,
            UserQueryApiMapper mapper) {
        this.getUserUseCase = getUserUseCase;
        this.searchUsersUseCase = searchUsersUseCase;
        this.mapper = mapper;
    }

    /**
     * User 단건 조회 API
     *
     * <p>ID로 사용자 정보를 조회합니다.
     *
     * @param userId User ID
     * @return 사용자 정보
     */
    @Operation(summary = "사용자 단건 조회", description = "ID로 사용자 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('user', 'read') or @access.self(#userId)")
    @GetMapping(UserApiEndpoints.ID)
    public ResponseEntity<ApiResponse<UserApiResponse>> getById(
            @Parameter(description = "User ID", required = true)
                    @PathVariable(UserApiEndpoints.PATH_USER_ID)
                    String userId) {

        UserResult result = getUserUseCase.execute(userId);
        UserApiResponse apiResponse = mapper.toApiResponse(result);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * User 복합 조건 조회 API (Offset 기반)
     *
     * <p>조직, 검색어, 상태, 생성일시 범위 필터를 지원하여 사용자 목록을 Offset 기반으로 조회합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반, 필터 포함)
     * @return 사용자 목록 (페이징)
     */
    @Operation(
            summary = "사용자 복합 조건 조회",
            description = "조직, 검색어, 상태, 생성일시 범위 필터를 지원하여 사용자 목록을 Offset 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('user', 'read')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<UserApiResponse>>> searchUsersByOffset(
            @Valid @ModelAttribute SearchUsersOffsetApiRequest request) {

        UserSearchParams params = mapper.toSearchParams(request);
        UserPageResult pageResult = searchUsersUseCase.execute(params);
        PageApiResponse<UserApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.ofSuccess(response));
    }
}
