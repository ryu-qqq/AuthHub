package com.ryuqq.authhub.adapter.in.rest.permission.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.query.SearchPermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionSpecApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.UserPermissionsApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.mapper.PermissionApiMapper;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.dto.response.PermissionSpecResponse;
import com.ryuqq.authhub.application.permission.port.in.query.GetPermissionSpecUseCase;
import com.ryuqq.authhub.application.permission.port.in.query.GetPermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.query.SearchPermissionsUseCase;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.role.port.in.GetUserRolesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PermissionQueryController - 권한 Query API 컨트롤러 (Admin)
 *
 * <p>권한 조회 등 Query 작업을 처리합니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/admin/permissions (admin.connectly.com)
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
@Tag(name = "Permission", description = "권한 관리 API (Admin)")
@RestController
@RequestMapping(ApiPaths.Permissions.BASE)
public class PermissionQueryController {

    private final GetPermissionUseCase getPermissionUseCase;
    private final SearchPermissionsUseCase searchPermissionsUseCase;
    private final GetUserRolesUseCase getUserRolesUseCase;
    private final GetPermissionSpecUseCase getPermissionSpecUseCase;
    private final PermissionApiMapper mapper;

    public PermissionQueryController(
            GetPermissionUseCase getPermissionUseCase,
            SearchPermissionsUseCase searchPermissionsUseCase,
            GetUserRolesUseCase getUserRolesUseCase,
            GetPermissionSpecUseCase getPermissionSpecUseCase,
            PermissionApiMapper mapper) {
        this.getPermissionUseCase = getPermissionUseCase;
        this.searchPermissionsUseCase = searchPermissionsUseCase;
        this.getUserRolesUseCase = getUserRolesUseCase;
        this.getPermissionSpecUseCase = getPermissionSpecUseCase;
        this.mapper = mapper;
    }

    /**
     * 권한 단건 조회
     *
     * <p>GET /api/v1/permissions/{permissionId}
     *
     * @param permissionId 권한 ID
     * @return 200 OK + 권한 상세 정보
     */
    @Operation(summary = "권한 단건 조회", description = "권한 ID로 권한 정보를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "권한을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('permission:read')")
    @GetMapping("/{permissionId}")
    public ResponseEntity<ApiResponse<PermissionApiResponse>> getPermission(
            @Parameter(description = "권한 ID", required = true) @PathVariable String permissionId) {
        PermissionResponse response = getPermissionUseCase.execute(mapper.toGetQuery(permissionId));
        PermissionApiResponse apiResponse = mapper.toApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 권한 목록 검색
     *
     * <p>GET /api/v1/permissions
     *
     * @param resource 리소스 필터 (선택)
     * @param action 액션 필터 (선택)
     * @param type 권한 유형 필터 (선택, SYSTEM/CUSTOM)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 200 OK + 권한 목록
     */
    @Operation(summary = "권한 목록 검색", description = "조건에 맞는 권한 목록을 검색합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('permission:read')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionApiResponse>>> searchPermissions(
            @Parameter(description = "리소스 필터") @RequestParam(required = false) String resource,
            @Parameter(description = "액션 필터") @RequestParam(required = false) String action,
            @Parameter(description = "권한 유형 필터 (SYSTEM/CUSTOM)") @RequestParam(required = false)
                    String type,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        SearchPermissionsApiRequest request =
                new SearchPermissionsApiRequest(resource, action, type, page, size);
        List<PermissionResponse> responses =
                searchPermissionsUseCase.execute(mapper.toQuery(request));
        List<PermissionApiResponse> apiResponses = mapper.toApiResponseList(responses);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
    }

    /**
     * 사용자 권한 조회 (Gateway 전용)
     *
     * <p>GET /api/v1/permissions/users/{userId}
     *
     * <p>Gateway에서 사용자의 역할 및 권한 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 200 OK + 사용자 역할/권한 정보
     */
    @Operation(summary = "사용자 권한 조회", description = "Gateway에서 사용자의 역할 및 권한 목록을 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("@access.superAdmin()")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserPermissionsApiResponse>> getUserPermissions(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId) {
        UserRolesResponse response = getUserRolesUseCase.execute(UUID.fromString(userId));
        UserPermissionsApiResponse apiResponse = mapper.toUserPermissionsApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 권한 명세 조회 (Gateway 전용)
     *
     * <p>GET /api/v1/permissions/spec
     *
     * <p>Gateway에서 전체 엔드포인트별 권한 명세를 조회합니다. Gateway 시작 시 및 주기적으로 호출하여 권한 정책을 캐싱합니다.
     *
     * @return 200 OK + 권한 명세 정보
     */
    @Operation(summary = "권한 명세 조회", description = "Gateway에서 전체 엔드포인트별 권한 명세를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.superAdmin()")
    @GetMapping("/spec")
    public ResponseEntity<ApiResponse<PermissionSpecApiResponse>> getPermissionSpec() {
        PermissionSpecResponse response = getPermissionSpecUseCase.execute();
        PermissionSpecApiResponse apiResponse = mapper.toPermissionSpecApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
