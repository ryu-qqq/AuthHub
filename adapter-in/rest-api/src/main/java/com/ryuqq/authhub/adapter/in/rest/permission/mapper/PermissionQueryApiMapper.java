package com.ryuqq.authhub.adapter.in.rest.permission.mapper;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.SearchPermissionsOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionApiResponse;
import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.permission.dto.query.PermissionSearchParams;
import com.ryuqq.authhub.application.permission.dto.response.PermissionPageResult;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PermissionQueryApiMapper - Permission Query API 변환 매퍼 (Global Only)
 *
 * <p>API Request/Response와 Application SearchParams/Result 간 변환을 담당합니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 변환 로직이 제거되었습니다
 * </ul>
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
public class PermissionQueryApiMapper {

    /**
     * SearchPermissionsOffsetApiRequest -> PermissionSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * <p>APP-DTO-003: SearchParams는 CommonSearchParams 포함 필수.
     *
     * <p>기본값 처리는 Application Layer(CommonSearchParams)에서 수행합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반)
     * @return PermissionSearchParams 객체
     */
    public PermissionSearchParams toSearchParams(SearchPermissionsOffsetApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        request.startDate(),
                        request.endDate(),
                        "createdAt",
                        "DESC",
                        request.page(),
                        request.size());

        return PermissionSearchParams.of(
                request.serviceId(),
                searchParams,
                request.searchWord(),
                request.searchField(),
                request.types(),
                request.resources());
    }

    /**
     * PermissionResult -> PermissionApiResponse 변환
     *
     * <p>CFG-002: DateTimeFormatUtils를 사용하여 String으로 변환.
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public PermissionApiResponse toResponse(PermissionResult result) {
        return new PermissionApiResponse(
                result.permissionId(),
                result.serviceId(),
                result.permissionKey(),
                result.resource(),
                result.action(),
                result.description(),
                result.type(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * PermissionResult 목록 -> PermissionApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<PermissionApiResponse> toResponses(List<PermissionResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * PermissionPageResult -> PageApiResponse<PermissionApiResponse> 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<PermissionApiResponse> toPageResponse(PermissionPageResult pageResult) {
        List<PermissionApiResponse> content = toResponses(pageResult.content());
        return PageApiResponse.of(
                content,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
