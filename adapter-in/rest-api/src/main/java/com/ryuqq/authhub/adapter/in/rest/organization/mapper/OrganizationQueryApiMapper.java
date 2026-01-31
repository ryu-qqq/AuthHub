package com.ryuqq.authhub.adapter.in.rest.organization.mapper;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.query.SearchOrganizationsOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.organization.dto.query.OrganizationSearchParams;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationPageResult;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * OrganizationQueryApiMapper - Organization Query API 변환 매퍼
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
public class OrganizationQueryApiMapper {

    /**
     * SearchOrganizationsOffsetApiRequest -> OrganizationSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * <p>APP-DTO-003: SearchParams는 CommonSearchParams 포함 필수.
     *
     * <p>기본값 처리는 Application Layer(CommonSearchParams)에서 수행합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반)
     * @return OrganizationSearchParams 객체
     */
    public OrganizationSearchParams toSearchParams(SearchOrganizationsOffsetApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        request.startDate(),
                        request.endDate(),
                        "createdAt",
                        "DESC",
                        request.page(),
                        request.size());

        return OrganizationSearchParams.of(
                searchParams,
                request.tenantIds(),
                request.searchWord(),
                request.searchField(),
                request.statuses());
    }

    /**
     * OrganizationResult -> OrganizationApiResponse 변환
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public OrganizationApiResponse toResponse(OrganizationResult result) {
        return new OrganizationApiResponse(
                result.organizationId(),
                result.tenantId(),
                result.name(),
                result.status(),
                result.createdAt(),
                result.updatedAt());
    }

    /**
     * OrganizationResult 목록 -> OrganizationApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<OrganizationApiResponse> toResponses(List<OrganizationResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * OrganizationPageResult -> PageApiResponse<OrganizationApiResponse> 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<OrganizationApiResponse> toPageResponse(
            OrganizationPageResult pageResult) {
        List<OrganizationApiResponse> content = toResponses(pageResult.content());
        return PageApiResponse.of(
                content,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
