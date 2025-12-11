package com.ryuqq.authhub.adapter.in.rest.endpointpermission.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.query.SearchEndpointPermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response.EndpointPermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.mapper.EndpointPermissionApiMapper;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.GetEndpointPermissionUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.SearchEndpointPermissionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * EndpointPermissionQueryController - 엔드포인트 권한 Query API 컨트롤러
 *
 * <p>엔드포인트 권한 조회 등 Query 작업을 처리합니다.
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
@Tag(name = "EndpointPermission", description = "엔드포인트 권한 관리 API")
@RestController
@RequestMapping("/api/v1/endpoint-permissions")
public class EndpointPermissionQueryController {

    private final GetEndpointPermissionUseCase getEndpointPermissionUseCase;
    private final SearchEndpointPermissionsUseCase searchEndpointPermissionsUseCase;
    private final EndpointPermissionApiMapper mapper;

    public EndpointPermissionQueryController(
            GetEndpointPermissionUseCase getEndpointPermissionUseCase,
            SearchEndpointPermissionsUseCase searchEndpointPermissionsUseCase,
            EndpointPermissionApiMapper mapper) {
        this.getEndpointPermissionUseCase = getEndpointPermissionUseCase;
        this.searchEndpointPermissionsUseCase = searchEndpointPermissionsUseCase;
        this.mapper = mapper;
    }

    /**
     * 엔드포인트 권한 단건 조회
     *
     * <p>GET /api/v1/endpoint-permissions/{endpointPermissionId}
     *
     * @param endpointPermissionId 엔드포인트 권한 ID
     * @return 200 OK + 엔드포인트 권한 상세 정보
     */
    @Operation(summary = "엔드포인트 권한 단건 조회", description = "엔드포인트 권한 ID로 권한 정보를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "엔드포인트 권한을 찾을 수 없음")
    })
    @GetMapping("/{endpointPermissionId}")
    public ResponseEntity<ApiResponse<EndpointPermissionApiResponse>> getEndpointPermission(
            @Parameter(description = "엔드포인트 권한 ID", required = true) @PathVariable
                    String endpointPermissionId) {
        EndpointPermissionResponse response =
                getEndpointPermissionUseCase.execute(mapper.toGetQuery(endpointPermissionId));
        EndpointPermissionApiResponse apiResponse = mapper.toApiResponse(response);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 엔드포인트 권한 목록 검색
     *
     * <p>GET /api/v1/endpoint-permissions
     *
     * @param serviceName 서비스 이름 필터 (선택, 정확한 매칭)
     * @param pathPattern 경로 패턴 필터 (선택, 부분 일치)
     * @param method HTTP 메서드 필터 (선택)
     * @param isPublic 공개 여부 필터 (선택)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 200 OK + 엔드포인트 권한 목록
     */
    @Operation(summary = "엔드포인트 권한 목록 검색", description = "조건에 맞는 엔드포인트 권한 목록을 검색합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<EndpointPermissionApiResponse>>>
            searchEndpointPermissions(
                    @Parameter(description = "서비스 이름 필터") @RequestParam(required = false)
                            String serviceName,
                    @Parameter(description = "경로 패턴 필터 (부분 일치)") @RequestParam(required = false)
                            String pathPattern,
                    @Parameter(description = "HTTP 메서드 필터") @RequestParam(required = false)
                            String method,
                    @Parameter(description = "공개 여부 필터") @RequestParam(required = false)
                            Boolean isPublic,
                    @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
                    @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20")
                            int size) {
        SearchEndpointPermissionsApiRequest request =
                new SearchEndpointPermissionsApiRequest(
                        serviceName, pathPattern, method, isPublic, page, size);
        List<EndpointPermissionResponse> responses =
                searchEndpointPermissionsUseCase.execute(mapper.toQuery(request));
        List<EndpointPermissionApiResponse> apiResponses = mapper.toApiResponseList(responses);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
    }
}
