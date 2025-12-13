package com.ryuqq.authhub.adapter.in.rest.role.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.query.SearchRolesApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.mapper.RoleApiMapper;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.port.in.query.GetRoleUseCase;
import com.ryuqq.authhub.application.role.port.in.query.SearchRolesUseCase;
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
 * RoleQueryController - 역할 Query API 컨트롤러 (Admin)
 *
 * <p>역할 조회 등 Query 작업을 처리합니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/admin/roles (admin.connectly.com)
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
@Tag(name = "Role", description = "역할 관리 API (Admin)")
@RestController
@RequestMapping(ApiPaths.Roles.BASE)
public class RoleQueryController {

    private final GetRoleUseCase getRoleUseCase;
    private final SearchRolesUseCase searchRolesUseCase;
    private final RoleApiMapper mapper;

    public RoleQueryController(
            GetRoleUseCase getRoleUseCase,
            SearchRolesUseCase searchRolesUseCase,
            RoleApiMapper mapper) {
        this.getRoleUseCase = getRoleUseCase;
        this.searchRolesUseCase = searchRolesUseCase;
        this.mapper = mapper;
    }

    /**
     * 역할 단건 조회
     *
     * <p>GET /api/v1/roles/{roleId}
     *
     * @param roleId 역할 ID
     * @return 200 OK + 역할 상세 정보
     */
    @Operation(
            summary = "역할 단건 조회",
            description =
                    """
                    역할 ID로 역할 정보를 조회합니다.

                    **필요 권한**: `role:read`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "역할을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('role:read')")
    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse<RoleApiResponse>> getRole(
            @Parameter(description = "역할 ID", required = true) @PathVariable String roleId) {
        RoleResponse response = getRoleUseCase.execute(mapper.toGetQuery(roleId));
        RoleApiResponse apiResponse = mapper.toApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 역할 목록 검색
     *
     * <p>GET /api/v1/roles
     *
     * @param tenantId 테넌트 ID 필터 (선택)
     * @param name 역할 이름 필터 (선택)
     * @param scope 범위 필터 (선택, GLOBAL/TENANT/ORGANIZATION)
     * @param type 역할 유형 필터 (선택, SYSTEM/CUSTOM)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 200 OK + 역할 목록
     */
    @Operation(
            summary = "역할 목록 검색",
            description =
                    """
                    조건에 맞는 역할 목록을 검색합니다.

                    **필요 권한**: `role:read`
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('role:read')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleApiResponse>>> searchRoles(
            @Parameter(description = "테넌트 ID 필터") @RequestParam(required = false) String tenantId,
            @Parameter(description = "역할 이름 필터") @RequestParam(required = false) String name,
            @Parameter(description = "범위 필터 (GLOBAL/TENANT/ORGANIZATION)")
                    @RequestParam(required = false)
                    String scope,
            @Parameter(description = "역할 유형 필터 (SYSTEM/CUSTOM)") @RequestParam(required = false)
                    String type,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") Integer size) {
        SearchRolesApiRequest request =
                new SearchRolesApiRequest(tenantId, name, scope, type, page, size);
        List<RoleResponse> responses = searchRolesUseCase.execute(mapper.toQuery(request));
        List<RoleApiResponse> apiResponses = mapper.toApiResponseList(responses);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
    }
}
