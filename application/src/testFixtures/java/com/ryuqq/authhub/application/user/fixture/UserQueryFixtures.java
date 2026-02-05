package com.ryuqq.authhub.application.user.fixture;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.user.dto.query.UserSearchParams;
import java.util.List;

/**
 * User Query DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Query DTO를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserQueryFixtures {

    private UserQueryFixtures() {}

    // ==================== UserSearchParams ====================

    /** 기본 검색 파라미터 반환 (전체 조회) */
    public static UserSearchParams searchParams() {
        return new UserSearchParams(defaultCommonSearchParams(), null, null, null, null);
    }

    /** 페이징 정보를 지정한 검색 파라미터 반환 */
    public static UserSearchParams searchParams(int page, int size) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
        return new UserSearchParams(commonParams, null, null, null, null);
    }

    /** 조직 ID를 지정한 검색 파라미터 반환 */
    public static UserSearchParams searchParamsWithOrganization(String organizationId) {
        return new UserSearchParams(defaultCommonSearchParams(), organizationId, null, null, null);
    }

    /** 상태 필터를 지정한 검색 파라미터 반환 */
    public static UserSearchParams searchParamsWithStatuses(List<String> statuses) {
        return new UserSearchParams(defaultCommonSearchParams(), null, null, null, statuses);
    }

    /** 검색어와 검색 필드를 지정한 검색 파라미터 반환 */
    public static UserSearchParams searchParamsWithSearch(String searchWord, String searchField) {
        return new UserSearchParams(
                defaultCommonSearchParams(), null, searchWord, searchField, null);
    }

    // ==================== CommonSearchParams ====================

    /** 기본 CommonSearchParams */
    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }
}
