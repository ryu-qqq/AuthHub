package com.ryuqq.authhub.adapter.in.rest.role.controller.query;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.RoleApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.role.dto.request.SearchRolesOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.mapper.RoleQueryApiMapper;
import com.ryuqq.authhub.application.role.dto.query.RoleSearchParams;
import com.ryuqq.authhub.application.role.dto.response.RolePageResult;
import com.ryuqq.authhub.application.role.port.in.query.SearchRolesUseCase;
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
 * RoleQueryController - Role 조회 API
 *
 * <p>역할 조회 엔드포인트를 제공합니다.
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
@Tag(name = "Role", description = "역할 관리 API")
@RestController
@RequestMapping(RoleApiEndpoints.ROLES)
public class RoleQueryController {

    private final SearchRolesUseCase searchRolesUseCase;
    private final RoleQueryApiMapper mapper;

    /**
     * RoleQueryController 생성자
     *
     * @param searchRolesUseCase Role 복합 조건 조회 UseCase
     * @param mapper API 매퍼
     */
    public RoleQueryController(SearchRolesUseCase searchRolesUseCase, RoleQueryApiMapper mapper) {
        this.searchRolesUseCase = searchRolesUseCase;
        this.mapper = mapper;
    }

    /**
     * Role 복합 조건 조회 API (Offset 기반)
     *
     * <p>이름, 유형, 생성일시 범위 필터를 지원하여 역할 목록을 Offset 기반으로 조회합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반, 필터 포함)
     * @return Role 페이지 목록
     */
    @Operation(
            summary = "역할 복합 조건 조회",
            description = "이름, 유형, 생성일시 범위 필터를 지원하여 역할 목록을 Offset 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('role', 'read')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<RoleApiResponse>>> searchRolesByOffset(
            @Valid @ModelAttribute SearchRolesOffsetApiRequest request) {

        RoleSearchParams params = mapper.toSearchParams(request);
        RolePageResult pageResult = searchRolesUseCase.execute(params);
        PageApiResponse<RoleApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.ofSuccess(response));
    }
}
