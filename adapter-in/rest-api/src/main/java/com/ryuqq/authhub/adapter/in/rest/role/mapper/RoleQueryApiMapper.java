package com.ryuqq.authhub.adapter.in.rest.role.mapper;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.authhub.adapter.in.rest.role.dto.request.SearchRolesOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleApiResponse;
import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.role.dto.query.RoleSearchParams;
import com.ryuqq.authhub.application.role.dto.response.RolePageResult;
import com.ryuqq.authhub.application.role.dto.response.RoleResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RoleQueryApiMapper - Role Query API 변환 매퍼
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
public class RoleQueryApiMapper {

    /**
     * SearchRolesOffsetApiRequest -> RoleSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * <p>APP-DTO-003: SearchParams는 CommonSearchParams 포함 필수.
     *
     * <p>기본값 처리는 Application Layer(CommonSearchParams)에서 수행합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반)
     * @return RoleSearchParams 객체
     */
    public RoleSearchParams toSearchParams(SearchRolesOffsetApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        request.startDate(),
                        request.endDate(),
                        "createdAt",
                        "DESC",
                        request.page(),
                        request.size());

        return RoleSearchParams.of(
                searchParams,
                request.tenantId(),
                request.serviceId(),
                request.searchWord(),
                request.searchField(),
                request.types());
    }

    /**
     * RoleResult -> RoleApiResponse 변환
     *
     * <p>CFG-002: DateTimeFormatUtils를 사용하여 String으로 변환.
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public RoleApiResponse toResponse(RoleResult result) {
        return new RoleApiResponse(
                result.roleId(),
                result.tenantId(),
                result.serviceId(),
                result.name(),
                result.displayName(),
                result.description(),
                result.type(),
                result.scope(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * RoleResult 목록 -> RoleApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<RoleApiResponse> toResponses(List<RoleResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * RolePageResult -> PageApiResponse<RoleApiResponse> 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<RoleApiResponse> toPageResponse(RolePageResult pageResult) {
        List<RoleApiResponse> content = toResponses(pageResult.content());
        return PageApiResponse.of(
                content,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
