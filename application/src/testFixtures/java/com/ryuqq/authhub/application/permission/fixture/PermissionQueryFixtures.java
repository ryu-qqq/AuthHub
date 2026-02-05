package com.ryuqq.authhub.application.permission.fixture;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.permission.dto.query.PermissionSearchParams;
import java.time.LocalDate;
import java.util.List;

/**
 * Permission Query DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Query DTO를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionQueryFixtures {

    private PermissionQueryFixtures() {}

    // ==================== PermissionSearchParams ====================

    /** 기본 검색 파라미터 반환 (전체 조회) */
    public static PermissionSearchParams searchParams() {
        return PermissionSearchParams.of(null, defaultCommonSearchParams(), null, null, null, null);
    }

    /** 페이징 정보를 지정한 검색 파라미터 반환 */
    public static PermissionSearchParams searchParams(int page, int size) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
        return PermissionSearchParams.of(null, commonParams, null, null, null, null);
    }

    /** types 필터를 지정한 검색 파라미터 반환 */
    public static PermissionSearchParams searchParamsWithTypes(List<String> types) {
        return PermissionSearchParams.of(
                null, defaultCommonSearchParams(), null, null, types, null);
    }

    /** resources 필터를 지정한 검색 파라미터 반환 */
    public static PermissionSearchParams searchParamsWithResources(List<String> resources) {
        return PermissionSearchParams.of(
                null, defaultCommonSearchParams(), null, null, null, resources);
    }

    /** 검색어와 검색 필드를 지정한 검색 파라미터 반환 */
    public static PermissionSearchParams searchParamsWithSearch(
            String searchWord, String searchField) {
        return PermissionSearchParams.of(
                null, defaultCommonSearchParams(), searchWord, searchField, null, null);
    }

    /** ofDefault 래퍼 (간편 생성) */
    public static PermissionSearchParams ofDefault(
            Long serviceId,
            String searchWord,
            List<String> types,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size) {
        return PermissionSearchParams.ofDefault(
                serviceId, searchWord, types, startDate, endDate, page, size);
    }

    // ==================== CommonSearchParams ====================

    /** 기본 CommonSearchParams */
    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }
}
