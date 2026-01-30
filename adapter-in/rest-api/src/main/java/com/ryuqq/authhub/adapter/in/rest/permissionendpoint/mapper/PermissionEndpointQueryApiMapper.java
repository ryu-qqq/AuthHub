package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.mapper;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.SearchPermissionEndpointsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.response.PermissionEndpointApiResponse;
import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.permissionendpoint.dto.query.PermissionEndpointSearchParams;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointPageResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointQueryApiMapper - PermissionEndpoint Query API 변환 매퍼
 *
 * <p>API Request/Response와 Application SearchParams/Result 간 변환을 담당합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointQueryApiMapper {

    /**
     * SearchPermissionEndpointsApiRequest -> PermissionEndpointSearchParams 변환
     *
     * @param request 조회 요청 DTO
     * @return PermissionEndpointSearchParams 객체
     */
    public PermissionEndpointSearchParams toSearchParams(
            SearchPermissionEndpointsApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        request.startDate(),
                        request.endDate(),
                        "createdAt",
                        "DESC",
                        request.page(),
                        request.size());

        return PermissionEndpointSearchParams.of(
                searchParams,
                request.permissionIds(),
                request.searchWord(),
                request.searchField(),
                request.httpMethods());
    }

    /**
     * PermissionEndpointResult -> PermissionEndpointApiResponse 변환
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public PermissionEndpointApiResponse toResponse(PermissionEndpointResult result) {
        return new PermissionEndpointApiResponse(
                result.permissionEndpointId(),
                result.permissionId(),
                result.urlPattern(),
                result.httpMethod(),
                result.description(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * PermissionEndpointResult 목록 -> PermissionEndpointApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<PermissionEndpointApiResponse> toResponses(List<PermissionEndpointResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * PermissionEndpointPageResult -> PageApiResponse<PermissionEndpointApiResponse> 변환
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<PermissionEndpointApiResponse> toPageResponse(
            PermissionEndpointPageResult pageResult) {
        List<PermissionEndpointApiResponse> content = toResponses(pageResult.content());
        return PageApiResponse.of(
                content,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
