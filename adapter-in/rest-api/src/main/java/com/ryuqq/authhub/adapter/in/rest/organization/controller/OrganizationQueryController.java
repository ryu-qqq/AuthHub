package com.ryuqq.authhub.adapter.in.rest.organization.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.OrganizationApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.query.SearchOrganizationsOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationQueryApiMapper;
import com.ryuqq.authhub.application.organization.dto.query.OrganizationSearchParams;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationPageResult;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsByOffsetUseCase;
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
@RequestMapping(OrganizationApiEndpoints.ORGANIZATIONS)
public class OrganizationQueryController {

    private final SearchOrganizationsByOffsetUseCase searchOrganizationsByOffsetUseCase;
    private final OrganizationQueryApiMapper mapper;

    public OrganizationQueryController(
            SearchOrganizationsByOffsetUseCase searchOrganizationsByOffsetUseCase,
            OrganizationQueryApiMapper mapper) {
        this.searchOrganizationsByOffsetUseCase = searchOrganizationsByOffsetUseCase;
        this.mapper = mapper;
    }

    /**
     * 조직 복합 조건 조회 (Offset 기반)
     *
     * <p>GET /api/v1/organizations
     *
     * @param request 검색 조건 (tenantIds, searchWord, searchField, statuses, startDate, endDate, page,
     *     size)
     * @return 200 OK + 조직 목록 (페이징)
     */
    @Operation(
            summary = "조직 복합 조건 조회",
            description =
                    """
                    복합 조건에 맞는 조직 목록을 Offset 기반으로 조회합니다.

                    **선택 파라미터:**
                    - tenantIds: 테넌트 ID 목록 (null 시 전체 조회)
                    - searchWord: 검색어
                    - searchField: 검색 필드 (NAME, 기본: NAME)
                    - statuses: 상태 필터 (다중 선택 가능)
                    - startDate: 조회 시작일
                    - endDate: 조회 종료일
                    - page: 페이지 번호 (기본값: 0)
                    - size: 페이지 크기 (기본값: 20)

                    **필요 권한**: Super Admin
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.superAdmin()")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<OrganizationApiResponse>>>
            searchOrganizationsByOffset(
                    @Valid @ModelAttribute SearchOrganizationsOffsetApiRequest request) {

        OrganizationSearchParams params = mapper.toSearchParams(request);
        OrganizationPageResult pageResult = searchOrganizationsByOffsetUseCase.execute(params);
        PageApiResponse<OrganizationApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.ofSuccess(response));
    }
}
