package com.ryuqq.authhub.application.tenant.fixture;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.tenant.dto.query.TenantSearchParams;
import java.util.List;

/**
 * Tenant Query DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Query DTO를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantQueryFixtures {

    private TenantQueryFixtures() {}

    // ==================== TenantSearchParams ====================

    /** 기본 검색 파라미터 반환 (전체 조회) */
    public static TenantSearchParams searchParams() {
        return new TenantSearchParams(defaultCommonSearchParams(), null, null, null);
    }

    /** 페이징 정보를 지정한 검색 파라미터 반환 */
    public static TenantSearchParams searchParams(int page, int size) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
        return new TenantSearchParams(commonParams, null, null, null);
    }

    /** 상태 필터를 지정한 검색 파라미터 반환 */
    public static TenantSearchParams searchParamsWithStatuses(List<String> statuses) {
        return new TenantSearchParams(defaultCommonSearchParams(), null, null, statuses);
    }

    /** 검색어와 검색 필드를 지정한 검색 파라미터 반환 */
    public static TenantSearchParams searchParamsWithSearch(String searchWord, String searchField) {
        return new TenantSearchParams(defaultCommonSearchParams(), searchWord, searchField, null);
    }

    // ==================== CommonSearchParams ====================

    /** 기본 CommonSearchParams */
    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }
}
