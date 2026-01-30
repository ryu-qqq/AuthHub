package com.ryuqq.authhub.adapter.in.rest.user.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.UserApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserQueryApiMapper;
import com.ryuqq.authhub.application.user.dto.query.UserSearchParams;
import com.ryuqq.authhub.application.user.dto.response.UserPageResult;
import com.ryuqq.authhub.application.user.dto.response.UserResult;
import com.ryuqq.authhub.application.user.port.in.query.GetUserUseCase;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersUseCase;
import com.ryuqq.authhub.domain.common.vo.PageMeta;
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
     * User 목록 조회 API
     *
     * <p>검색 조건에 따라 사용자 목록을 조회합니다.
     *
     * @param organizationId 소속 조직 ID (선택)
     * @param identifier 식별자 검색 (선택)
     * @param status 상태 필터 (선택)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 사용자 목록 (페이징)
     */
    @Operation(summary = "사용자 목록 조회", description = "검색 조건에 따라 사용자 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('user', 'read')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<UserApiResponse>>> search(
            @Parameter(description = "소속 조직 ID") @RequestParam(required = false)
                    String organizationId,
            @Parameter(description = "검색어 (식별자 또는 전화번호)") @RequestParam(required = false)
                    String searchWord,
            @Parameter(description = "상태 필터 (ACTIVE, INACTIVE, SUSPENDED)")
                    @RequestParam(required = false)
                    String status,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {

        UserSearchParams params =
                UserSearchParams.ofOrganization(
                        organizationId,
                        searchWord,
                        status != null ? List.of(status) : null,
                        null,
                        null,
                        page,
                        size);
        UserPageResult result = searchUsersUseCase.execute(params);
        PageMeta pageMeta = result.pageMeta();

        PageApiResponse<UserApiResponse> apiResponse =
                PageApiResponse.of(
                        result.content().stream().map(mapper::toApiResponse).toList(),
                        pageMeta.page(),
                        pageMeta.size(),
                        pageMeta.totalElements());

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
