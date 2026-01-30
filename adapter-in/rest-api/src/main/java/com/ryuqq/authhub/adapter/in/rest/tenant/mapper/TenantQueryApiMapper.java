package com.ryuqq.authhub.adapter.in.rest.tenant.mapper;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.SearchTenantsOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.tenant.dto.query.TenantSearchParams;
import com.ryuqq.authhub.application.tenant.dto.response.TenantPageResult;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TenantQueryApiMapper - Tenant Query API 변환 매퍼
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
public class TenantQueryApiMapper {

    /**
     * SearchTenantsOffsetApiRequest -> TenantSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * <p>APP-DTO-003: SearchParams는 CommonSearchParams 포함 필수.
     *
     * <p>기본값 처리는 Application Layer(CommonSearchParams)에서 수행합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반)
     * @return TenantSearchParams 객체
     */
    public TenantSearchParams toSearchParams(SearchTenantsOffsetApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        request.startDate(),
                        request.endDate(),
                        "createdAt",
                        "DESC",
                        request.page(),
                        request.size());

        return TenantSearchParams.of(
                searchParams, request.searchWord(), request.searchField(), request.statuses());
    }

    /**
     * TenantResult -> TenantApiResponse 변환
     *
     * <p>CFG-002: DateTimeFormatUtils를 사용하여 String으로 변환.
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public TenantApiResponse toResponse(TenantResult result) {
        return new TenantApiResponse(
                result.tenantId().toString(),
                result.name(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * TenantResult 목록 -> TenantApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<TenantApiResponse> toResponses(List<TenantResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * TenantPageResult -> PageApiResponse<TenantApiResponse> 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<TenantApiResponse> toPageResponse(TenantPageResult pageResult) {
        List<TenantApiResponse> content = toResponses(pageResult.content());
        return PageApiResponse.of(
                content,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
