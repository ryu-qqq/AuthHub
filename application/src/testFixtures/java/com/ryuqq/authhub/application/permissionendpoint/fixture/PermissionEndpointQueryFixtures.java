package com.ryuqq.authhub.application.permissionendpoint.fixture;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.permissionendpoint.dto.query.PermissionEndpointSearchParams;
import java.time.LocalDate;
import java.util.List;

/**
 * PermissionEndpoint Query DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Query DTO를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionEndpointQueryFixtures {

    private PermissionEndpointQueryFixtures() {}

    // ==================== PermissionEndpointSearchParams ====================

    /** 기본 검색 파라미터 반환 (전체 조회) */
    public static PermissionEndpointSearchParams searchParams() {
        return new PermissionEndpointSearchParams(
                defaultCommonSearchParams(), null, null, null, null);
    }

    /** 페이징 정보를 지정한 검색 파라미터 반환 */
    public static PermissionEndpointSearchParams searchParams(int page, int size) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
        return new PermissionEndpointSearchParams(commonParams, null, null, null, null);
    }

    /** 특정 권한의 엔드포인트 조회용 검색 파라미터 반환 */
    public static PermissionEndpointSearchParams searchParamsForPermission(
            Long permissionId, Integer page, Integer size) {
        return PermissionEndpointSearchParams.forPermission(permissionId, page, size);
    }

    /** 검색어·HTTP 메서드·날짜 범위를 지정한 기본 조회용 검색 파라미터 반환 */
    public static PermissionEndpointSearchParams ofDefault(
            String searchWord,
            List<String> httpMethods,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size) {
        return PermissionEndpointSearchParams.ofDefault(
                searchWord, httpMethods, startDate, endDate, page, size);
    }

    /** sortKey/sortDirection을 null로 둔 검색 파라미터 (Factory 기본값 검증용) */
    public static PermissionEndpointSearchParams searchParamsWithNullSort() {
        CommonSearchParams commonParams =
                CommonSearchParams.of(false, null, null, null, null, 0, 20);
        return new PermissionEndpointSearchParams(commonParams, null, null, null, null);
    }

    /** httpMethods null 검색 파라미터 */
    public static PermissionEndpointSearchParams searchParamsWithNullHttpMethods() {
        return new PermissionEndpointSearchParams(
                defaultCommonSearchParams(), null, null, null, null);
    }

    // ==================== CommonSearchParams ====================

    /** 기본 CommonSearchParams */
    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }
}
