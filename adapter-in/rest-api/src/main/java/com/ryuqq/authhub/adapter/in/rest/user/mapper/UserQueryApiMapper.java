package com.ryuqq.authhub.adapter.in.rest.user.mapper;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.request.SearchUsersOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.user.dto.query.UserSearchParams;
import com.ryuqq.authhub.application.user.dto.response.UserPageResult;
import com.ryuqq.authhub.application.user.dto.response.UserResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * UserQueryApiMapper - User Query API 변환 매퍼
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
public class UserQueryApiMapper {

    /**
     * SearchUsersOffsetApiRequest -> UserSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * <p>APP-DTO-003: SearchParams는 CommonSearchParams 포함 필수.
     *
     * <p>기본값 처리는 Application Layer(CommonSearchParams)에서 수행합니다.
     *
     * @param request 조회 요청 DTO (Offset 기반)
     * @return UserSearchParams 객체
     */
    public UserSearchParams toSearchParams(SearchUsersOffsetApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        request.startDate(),
                        request.endDate(),
                        "createdAt",
                        "DESC",
                        request.page(),
                        request.size());

        return UserSearchParams.of(
                searchParams,
                request.organizationId(),
                request.searchWord(),
                request.searchField(),
                request.statuses());
    }

    /**
     * UserResult -> UserApiResponse 변환
     *
     * @param result Application 응답 DTO
     * @return API 응답 DTO
     */
    public UserApiResponse toApiResponse(UserResult result) {
        return new UserApiResponse(
                result.userId(),
                result.organizationId(),
                result.identifier(),
                result.phoneNumber(),
                result.status(),
                result.createdAt(),
                result.updatedAt());
    }

    /**
     * UserResult 목록 -> UserApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<UserApiResponse> toResponses(List<UserResult> results) {
        return results.stream().map(this::toApiResponse).toList();
    }

    /**
     * UserPageResult -> PageApiResponse<UserApiResponse> 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<UserApiResponse> toPageResponse(UserPageResult pageResult) {
        List<UserApiResponse> content = toResponses(pageResult.content());
        return PageApiResponse.of(
                content,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
