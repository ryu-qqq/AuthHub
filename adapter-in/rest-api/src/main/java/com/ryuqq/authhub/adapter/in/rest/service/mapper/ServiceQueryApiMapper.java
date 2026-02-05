package com.ryuqq.authhub.adapter.in.rest.service.mapper;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.authhub.adapter.in.rest.service.dto.request.SearchServicesOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.service.dto.response.ServiceApiResponse;
import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.service.dto.query.ServiceSearchParams;
import com.ryuqq.authhub.application.service.dto.response.ServicePageResult;
import com.ryuqq.authhub.application.service.dto.response.ServiceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ServiceQueryApiMapper - Service Query API 변환 매퍼
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
public class ServiceQueryApiMapper {

    /**
     * SearchServicesOffsetApiRequest -> ServiceSearchParams 변환
     *
     * <p>기본값 처리는 Application Layer(CommonSearchParams)에서 수행합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반)
     * @return ServiceSearchParams 객체
     */
    public ServiceSearchParams toSearchParams(SearchServicesOffsetApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        request.startDate(),
                        request.endDate(),
                        "createdAt",
                        "DESC",
                        request.page(),
                        request.size());

        return ServiceSearchParams.of(
                searchParams, request.searchWord(), request.searchField(), request.statuses());
    }

    /**
     * ServiceResult -> ServiceApiResponse 변환
     *
     * <p>CFG-002: DateTimeFormatUtils를 사용하여 String으로 변환.
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public ServiceApiResponse toResponse(ServiceResult result) {
        return new ServiceApiResponse(
                result.serviceId(),
                result.serviceCode(),
                result.name(),
                result.description(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * ServiceResult 목록 -> ServiceApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<ServiceApiResponse> toResponses(List<ServiceResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * ServicePageResult -> PageApiResponse<ServiceApiResponse> 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<ServiceApiResponse> toPageResponse(ServicePageResult pageResult) {
        List<ServiceApiResponse> content = toResponses(pageResult.content());
        return PageApiResponse.of(
                content,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
