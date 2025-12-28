package com.ryuqq.authhub.adapter.in.rest.organization.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.query.SearchOrganizationsAdminApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.query.SearchOrganizationsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationDetailResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationSummaryResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationUserResponse;
import com.ryuqq.authhub.application.organization.port.in.query.GetOrganizationDetailUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.GetOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationUsersUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsAdminUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OrganizationQueryController - 조직 Query API 컨트롤러 (Admin)
 *
 * <p>조직 조회 등 Query 작업을 처리합니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/admin/organizations (admin.connectly.com)
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
@Tag(name = "Organization", description = "조직 관리 API (Admin)")
@RestController
@RequestMapping(ApiPaths.Organizations.BASE)
public class OrganizationQueryController {

    private final GetOrganizationUseCase getOrganizationUseCase;
    private final SearchOrganizationsUseCase searchOrganizationsUseCase;
    private final SearchOrganizationUsersUseCase searchOrganizationUsersUseCase;
    private final SearchOrganizationsAdminUseCase searchOrganizationsAdminUseCase;
    private final GetOrganizationDetailUseCase getOrganizationDetailUseCase;
    private final OrganizationApiMapper mapper;

    public OrganizationQueryController(
            GetOrganizationUseCase getOrganizationUseCase,
            SearchOrganizationsUseCase searchOrganizationsUseCase,
            SearchOrganizationUsersUseCase searchOrganizationUsersUseCase,
            SearchOrganizationsAdminUseCase searchOrganizationsAdminUseCase,
            GetOrganizationDetailUseCase getOrganizationDetailUseCase,
            OrganizationApiMapper mapper) {
        this.getOrganizationUseCase = getOrganizationUseCase;
        this.searchOrganizationsUseCase = searchOrganizationsUseCase;
        this.searchOrganizationUsersUseCase = searchOrganizationUsersUseCase;
        this.searchOrganizationsAdminUseCase = searchOrganizationsAdminUseCase;
        this.getOrganizationDetailUseCase = getOrganizationDetailUseCase;
        this.mapper = mapper;
    }

    /**
     * 조직 단건 조회
     *
     * <p>GET /api/v1/organizations/{organizationId}
     *
     * @param organizationId 조직 ID
     * @return 200 OK + 조직 상세 정보
     */
    @Operation(
            summary = "조직 단건 조회",
            description =
                    """
                    조직 ID로 조직 정보를 조회합니다.

                    **필요 권한**: 해당 조직에 대한 `organization:read` 권한
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "조직을 찾을 수 없음")
    })
    @PreAuthorize("@access.organization(#organizationId, 'read')")
    @GetMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<OrganizationApiResponse>> getOrganization(
            @Parameter(description = "조직 ID", required = true) @PathVariable @NotBlank
                    String organizationId) {
        OrganizationResponse response =
                getOrganizationUseCase.execute(mapper.toGetQuery(organizationId));
        OrganizationApiResponse apiResponse = mapper.toApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 조직 목록 검색
     *
     * <p>GET /api/v1/organizations
     *
     * @param tenantId 테넌트 ID (필수)
     * @param name 조직 이름 필터 (선택)
     * @param status 조직 상태 필터 (선택)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 200 OK + 조직 목록 (페이징)
     */
    @Operation(
            summary = "조직 목록 검색",
            description =
                    """
                    조건에 맞는 조직 목록을 페이징하여 조회합니다.

                    **필요 권한**: Super Admin 또는 해당 테넌트 소속 사용자
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.superAdmin() or @access.sameTenant(#tenantId)")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<OrganizationApiResponse>>>
            searchOrganizations(
                    @Parameter(description = "테넌트 ID", required = true) @RequestParam
                            String tenantId,
                    @Parameter(description = "조직 이름 필터") @RequestParam(required = false)
                            String name,
                    @Parameter(description = "조직 상태 필터") @RequestParam(required = false)
                            String status,
                    @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") @Min(0)
                            int page,
                    @Parameter(description = "페이지 크기")
                            @RequestParam(defaultValue = "20")
                            @Min(1)
                            @Max(100)
                            int size) {
        SearchOrganizationsApiRequest request =
                new SearchOrganizationsApiRequest(tenantId, name, status, page, size);
        PageResponse<OrganizationResponse> response =
                searchOrganizationsUseCase.execute(mapper.toQuery(request));
        PageApiResponse<OrganizationApiResponse> pagedResponse =
                PageApiResponse.from(response, mapper::toApiResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(pagedResponse));
    }

    /**
     * 조직별 사용자 목록 조회
     *
     * <p>GET /api/v1/auth/organizations/{organizationId}/users
     *
     * @param organizationId 조직 ID
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 200 OK + 사용자 목록
     */
    @Operation(
            summary = "조직별 사용자 목록 조회",
            description =
                    """
                    특정 조직에 소속된 사용자 목록을 조회합니다.

                    **필요 권한**: `ROLE_SUPER_ADMIN` 또는 `ROLE_TENANT_ADMIN`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "조직을 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin() or @access.tenantAdmin()")
    @GetMapping(ApiPaths.Organizations.USERS)
    public ResponseEntity<ApiResponse<PageResponse<OrganizationUserApiResponse>>>
            getOrganizationUsers(
                    @Parameter(description = "조직 ID", required = true) @PathVariable @NotBlank
                            String organizationId,
                    @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") @Min(0)
                            Integer page,
                    @Parameter(description = "페이지 크기")
                            @RequestParam(defaultValue = "20")
                            @Min(1)
                            @Max(100)
                            Integer size) {
        PageResponse<OrganizationUserResponse> response =
                searchOrganizationUsersUseCase.execute(
                        mapper.toOrganizationUsersQuery(organizationId, page, size));
        PageResponse<OrganizationUserApiResponse> apiResponse =
                mapper.toOrganizationUserApiPageResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    // ===== Admin Query 엔드포인트 (확장 필터 + 연관 데이터) =====

    /**
     * Admin 조직 목록 검색 (확장 필터 + 연관 데이터)
     *
     * <p>GET /api/v1/organizations/admin/search
     *
     * <p>기존 searchOrganizations와 다른 점:
     *
     * <ul>
     *   <li>tenantId 선택적 (전체 조회 가능)
     *   <li>날짜 범위 필터 지원 (createdFrom, createdTo)
     *   <li>정렬 옵션 지원 (sortBy, sortDirection)
     *   <li>tenantName, userCount 포함한 Summary 응답
     * </ul>
     *
     * @param tenantId 테넌트 ID (선택)
     * @param name 조직 이름 필터 (선택)
     * @param status 조직 상태 필터 (선택)
     * @param createdFrom 생성일시 시작 (선택)
     * @param createdTo 생성일시 종료 (선택)
     * @param sortBy 정렬 기준 (기본: createdAt)
     * @param sortDirection 정렬 방향 (기본: DESC)
     * @param page 페이지 번호 (기본: 0)
     * @param size 페이지 크기 (기본: 20)
     * @return 200 OK + 조직 Summary 목록 (페이징)
     */
    @Operation(
            summary = "Admin 조직 목록 검색",
            description =
                    """
                    어드민 친화적 조직 목록 검색 API입니다.

                    **확장 기능:**
                    - tenantId 선택적 (전체 테넌트 조회 가능)
                    - 날짜 범위 필터 (createdFrom, createdTo)
                    - 정렬 옵션 (sortBy, sortDirection)
                    - 응답에 tenantName, userCount 포함

                    **필요 권한**: `ROLE_SUPER_ADMIN`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.superAdmin()")
    @GetMapping("/admin/search")
    public ResponseEntity<ApiResponse<PageApiResponse<OrganizationSummaryApiResponse>>>
            searchOrganizationsAdmin(
                    @Valid @ModelAttribute SearchOrganizationsAdminApiRequest request) {
        PageResponse<OrganizationSummaryResponse> response =
                searchOrganizationsAdminUseCase.execute(mapper.toAdminQuery(request));
        PageApiResponse<OrganizationSummaryApiResponse> pagedResponse =
                PageApiResponse.from(response, mapper::toSummaryApiResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(pagedResponse));
    }

    /**
     * Admin 조직 상세 조회 (연관 데이터 포함)
     *
     * <p>GET /api/v1/organizations/{organizationId}/admin/detail
     *
     * <p>기존 getOrganization과 다른 점:
     *
     * <ul>
     *   <li>tenantName 포함
     *   <li>소속 사용자 목록 포함 (최근 N명)
     *   <li>userCount 포함
     * </ul>
     *
     * @param organizationId 조직 ID
     * @return 200 OK + 조직 상세 정보 (연관 데이터 포함)
     */
    @Operation(
            summary = "Admin 조직 상세 조회",
            description =
                    """
                    어드민 친화적 조직 상세 조회 API입니다.

                    **확장 기능:**
                    - tenantName 포함
                    - 소속 사용자 목록 포함 (최근 N명)
                    - userCount 포함

                    **필요 권한**: `ROLE_SUPER_ADMIN` 또는 `ROLE_TENANT_ADMIN`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "조직을 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin() or @access.tenantAdmin()")
    @GetMapping("/{organizationId}/admin/detail")
    public ResponseEntity<ApiResponse<OrganizationDetailApiResponse>> getOrganizationAdminDetail(
            @Parameter(description = "조직 ID", required = true) @PathVariable @NotBlank
                    String organizationId) {
        OrganizationDetailResponse response =
                getOrganizationDetailUseCase.execute(mapper.toGetQuery(organizationId));
        OrganizationDetailApiResponse apiResponse = mapper.toDetailApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
