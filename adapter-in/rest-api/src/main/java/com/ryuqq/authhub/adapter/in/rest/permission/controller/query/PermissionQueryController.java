package com.ryuqq.authhub.adapter.in.rest.permission.controller.query;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.PermissionApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.SearchPermissionsOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.mapper.PermissionQueryApiMapper;
import com.ryuqq.authhub.application.permission.dto.query.PermissionSearchParams;
import com.ryuqq.authhub.application.permission.dto.response.PermissionPageResult;
import com.ryuqq.authhub.application.permission.port.in.query.SearchPermissionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PermissionQueryController - Permission 조회 API
 *
 * <p>권한 조회 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: Controller는 @RestController로 정의.
 *
 * <p>CTR-002: Controller는 UseCase만 주입받음.
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 처리.
 *
 * <p>RDTO-009: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag(name = "Permission", description = "권한 관리 API")
@RestController
@RequestMapping(PermissionApiEndpoints.PERMISSIONS)
public class PermissionQueryController {

    private final SearchPermissionsUseCase searchPermissionsUseCase;
    private final PermissionQueryApiMapper mapper;

    /**
     * PermissionQueryController 생성자
     *
     * @param searchPermissionsUseCase Permission 복합 조건 조회 UseCase
     * @param mapper API 매퍼
     */
    public PermissionQueryController(
            SearchPermissionsUseCase searchPermissionsUseCase, PermissionQueryApiMapper mapper) {
        this.searchPermissionsUseCase = searchPermissionsUseCase;
        this.mapper = mapper;
    }

    /**
     * Permission 복합 조건 조회 API (Offset 기반)
     *
     * <p>리소스, 유형, 생성일시 범위 필터를 지원하여 권한 목록을 Offset 기반으로 조회합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반, 필터 포함)
     * @return Permission 페이지 목록
     */
    @Operation(
            summary = "권한 복합 조건 조회",
            description = "리소스, 유형, 생성일시 범위 필터를 지원하여 권한 목록을 Offset 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('permission', 'read')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<PermissionApiResponse>>>
            searchPermissionsByOffset(
                    @Valid @ModelAttribute SearchPermissionsOffsetApiRequest request) {

        PermissionSearchParams params = mapper.toSearchParams(request);
        PermissionPageResult pageResult = searchPermissionsUseCase.execute(params);
        PageApiResponse<PermissionApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.ofSuccess(response));
    }
}
