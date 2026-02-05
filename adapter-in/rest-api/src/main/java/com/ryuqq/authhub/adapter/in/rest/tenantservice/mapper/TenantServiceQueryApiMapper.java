package com.ryuqq.authhub.adapter.in.rest.tenantservice.mapper;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.SearchTenantServicesOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.response.TenantServiceApiResponse;
import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.tenantservice.dto.query.TenantServiceSearchParams;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServicePageResult;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServiceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TenantServiceQueryApiMapper - TenantService Query API 변환 매퍼
 *
 * <p>API Request/Response와 Application SearchParams/Result 간 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-003: Application Result -> API Response 변환.
 *
 * <p>MAPPER-004: Domain 타입 직접 의존 금지.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantServiceQueryApiMapper {

    /**
     * SearchTenantServicesOffsetApiRequest -> TenantServiceSearchParams 변환
     *
     * <p>기본값 처리는 Application Layer(CommonSearchParams)에서 수행합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반)
     * @return TenantServiceSearchParams 객체
     */
    public TenantServiceSearchParams toSearchParams(SearchTenantServicesOffsetApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        request.startDate(),
                        request.endDate(),
                        "subscribedAt",
                        "DESC",
                        request.page(),
                        request.size());

        return TenantServiceSearchParams.of(
                searchParams, request.tenantId(), request.serviceId(), request.statuses());
    }

    /**
     * TenantServiceResult -> TenantServiceApiResponse 변환
     *
     * <p>CFG-002: DateTimeFormatUtils를 사용하여 String으로 변환.
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public TenantServiceApiResponse toResponse(TenantServiceResult result) {
        return new TenantServiceApiResponse(
                result.tenantServiceId(),
                result.tenantId(),
                result.serviceId(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.subscribedAt()),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * TenantServiceResult 목록 -> TenantServiceApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<TenantServiceApiResponse> toResponses(List<TenantServiceResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * TenantServicePageResult -> PageApiResponse<TenantServiceApiResponse> 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<TenantServiceApiResponse> toPageResponse(
            TenantServicePageResult pageResult) {
        List<TenantServiceApiResponse> content = toResponses(pageResult.content());
        return PageApiResponse.of(
                content,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
