package com.ryuqq.authhub.adapter.in.rest.user.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContextHolder;
import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.query.SearchUsersAdminApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.query.SearchUsersApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.user.dto.response.UserDetailResponse;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.dto.response.UserSummaryResponse;
import com.ryuqq.authhub.application.user.port.in.query.GetUserDetailUseCase;
import com.ryuqq.authhub.application.user.port.in.query.GetUserUseCase;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersAdminUseCase;
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
@SuppressWarnings("PMD.ExcessiveImports")
public class UserQueryController {

    private final GetUserUseCase getUserUseCase;
    private final GetUserDetailUseCase getUserDetailUseCase;
    private final SearchUsersUseCase searchUsersUseCase;
    private final SearchUsersAdminUseCase searchUsersAdminUseCase;
    private final UserApiMapper mapper;

    public UserQueryController(
            GetUserUseCase getUserUseCase,
            GetUserDetailUseCase getUserDetailUseCase,
            SearchUsersUseCase searchUsersUseCase,
            SearchUsersAdminUseCase searchUsersAdminUseCase,
            UserApiMapper mapper) {
        this.getUserUseCase = getUserUseCase;
        this.getUserDetailUseCase = getUserDetailUseCase;
        this.searchUsersUseCase = searchUsersUseCase;
        this.searchUsersAdminUseCase = searchUsersAdminUseCase;
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
    @Operation(
            summary = "내 정보 조회",
            description =
                    """
                    현재 인증된 사용자의 정보를 조회합니다.

                    **필요 권한**: 인증된 사용자 (로그인 필수)
                    """)
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
    @Operation(
            summary = "사용자 단건 조회",
            description =
                    """
                    사용자 ID로 사용자 정보를 조회합니다.

                    **필요 권한**: 본인 또는 `user:read` 권한 보유자
                    """)
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
     * @param request 검색 조건 (필수: createdFrom, createdTo)
     * @return 200 OK + 페이징된 사용자 목록
     */
    @Operation(
            summary = "사용자 목록 검색",
            description =
                    """
                    조건에 맞는 사용자 목록을 검색합니다.

                    **필수 필터:**
                    - createdFrom: 생성일 시작 (ISO-8601)
                    - createdTo: 생성일 종료 (ISO-8601)

                    **필요 권한**: `user:read`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('user:read')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<UserApiResponse>>> searchUsers(
            @Valid @ModelAttribute SearchUsersApiRequest request) {
        PageResponse<UserResponse> pageResponse =
                searchUsersUseCase.execute(mapper.toQuery(request));
        PageApiResponse<UserApiResponse> pagedResponse =
                PageApiResponse.from(pageResponse, mapper::toApiResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(pagedResponse));
    }

    // ===== Admin 전용 API =====

    /**
     * 사용자 목록 검색 (Admin용 - 확장 필터, 페이징)
     *
     * <p>GET /api/v1/users/admin/search
     *
     * <p>확장된 필터와 페이징 정보를 포함한 응답을 반환합니다.
     *
     * @param request 검색 조건 (확장 필터 포함)
     * @return 200 OK + 페이징된 사용자 Summary 목록
     */
    @Operation(
            summary = "사용자 목록 검색 (Admin)",
            description =
                    """
                    확장된 필터로 사용자 목록을 검색합니다.

                    **확장 필터:**
                    - 날짜 범위 (createdFrom, createdTo)
                    - 역할 필터 (roleId)
                    - 정렬 옵션 (sortBy, sortDirection)

                    **응답 데이터:**
                    - 테넌트명, 조직명 포함
                    - 할당된 역할 수 포함
                    - 페이징 정보 포함

                    **필요 권한**: `user:read`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('user:read')")
    @GetMapping("/admin/search")
    public ResponseEntity<ApiResponse<PageApiResponse<UserSummaryApiResponse>>> searchUsersAdmin(
            @Valid @ModelAttribute SearchUsersAdminApiRequest request) {
        PageResponse<UserSummaryResponse> pageResponse =
                searchUsersAdminUseCase.execute(mapper.toAdminQuery(request));
        PageApiResponse<UserSummaryApiResponse> pagedResponse =
                PageApiResponse.from(pageResponse, mapper::toSummaryApiResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(pagedResponse));
    }

    /**
     * 사용자 상세 조회 (Admin용 - 역할 목록 포함)
     *
     * <p>GET /api/v1/users/{userId}/admin/detail
     *
     * <p>할당된 역할 목록을 포함한 상세 정보를 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 200 OK + 사용자 상세 정보 (역할 포함)
     */
    @Operation(
            summary = "사용자 상세 조회 (Admin)",
            description =
                    """
                    사용자의 상세 정보를 조회합니다.

                    **응답 데이터:**
                    - 테넌트명, 조직명 포함
                    - 할당된 역할 목록 포함 (이름, 설명, 범위, 유형)

                    **필요 권한**: 본인 또는 `user:read` 권한 보유자
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("@access.user(#userId, 'read')")
    @GetMapping("/{userId}/admin/detail")
    public ResponseEntity<ApiResponse<UserDetailApiResponse>> getUserDetail(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId) {
        UserDetailResponse response = getUserDetailUseCase.execute(mapper.toGetQuery(userId));
        UserDetailApiResponse apiResponse = mapper.toDetailApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
