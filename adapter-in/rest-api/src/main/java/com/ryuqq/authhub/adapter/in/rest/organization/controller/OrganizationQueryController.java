package com.ryuqq.authhub.adapter.in.rest.organization.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.query.SearchOrganizationsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.port.in.query.GetOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OrganizationQueryController - 조직 Query API 컨트롤러
 *
 * <p>조직 조회 등 Query 작업을 처리합니다.
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
@Tag(name = "Organization", description = "조직 관리 API")
@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationQueryController {

    private final GetOrganizationUseCase getOrganizationUseCase;
    private final SearchOrganizationsUseCase searchOrganizationsUseCase;
    private final OrganizationApiMapper mapper;

    public OrganizationQueryController(
            GetOrganizationUseCase getOrganizationUseCase,
            SearchOrganizationsUseCase searchOrganizationsUseCase,
            OrganizationApiMapper mapper) {
        this.getOrganizationUseCase = getOrganizationUseCase;
        this.searchOrganizationsUseCase = searchOrganizationsUseCase;
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
    @Operation(summary = "조직 단건 조회", description = "조직 ID로 조직 정보를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "조직을 찾을 수 없음")
    })
    @GetMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<OrganizationApiResponse>> getOrganization(
            @Parameter(description = "조직 ID", required = true) @PathVariable
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
    @Operation(summary = "조직 목록 검색", description = "조건에 맞는 조직 목록을 페이징하여 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<OrganizationApiResponse>>>
            searchOrganizations(
                    @Parameter(description = "테넌트 ID", required = true) @RequestParam
                            String tenantId,
                    @Parameter(description = "조직 이름 필터") @RequestParam(required = false)
                            String name,
                    @Parameter(description = "조직 상태 필터") @RequestParam(required = false)
                            String status,
                    @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
                    @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20")
                            int size) {
        SearchOrganizationsApiRequest request =
                new SearchOrganizationsApiRequest(tenantId, name, status, page, size);
        PageResponse<OrganizationResponse> response =
                searchOrganizationsUseCase.execute(mapper.toQuery(request));
        PageApiResponse<OrganizationApiResponse> pagedResponse =
                PageApiResponse.from(response, mapper::toApiResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(pagedResponse));
    }
}
