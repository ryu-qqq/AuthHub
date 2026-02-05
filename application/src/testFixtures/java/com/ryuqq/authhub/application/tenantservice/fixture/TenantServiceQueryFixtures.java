package com.ryuqq.authhub.application.tenantservice.fixture;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.tenantservice.dto.query.TenantServiceSearchParams;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServicePageResult;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServiceResult;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import java.time.Instant;
import java.util.List;

/**
 * TenantService Query DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Query/Result DTO를 제공합니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>Domain Fixture와 일관된 기본값 사용
 *   <li>테스트 가독성을 위한 명확한 팩토리 메서드
 *   <li>불변 객체 반환으로 테스트 격리 보장
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantServiceQueryFixtures {

    private static final Long DEFAULT_TENANT_SERVICE_ID = TenantServiceFixture.defaultIdValue();
    private static final String DEFAULT_TENANT_ID = TenantServiceFixture.defaultTenantIdValue();
    private static final Long DEFAULT_SERVICE_ID = TenantServiceFixture.defaultServiceIdValue();
    private static final String DEFAULT_STATUS = "ACTIVE";
    private static final Instant FIXED_TIME = TenantServiceFixture.fixedTime();

    private TenantServiceQueryFixtures() {}

    // ==================== TenantServiceSearchParams ====================

    /** 기본 검색 파라미터 반환 (전체 조회) */
    public static TenantServiceSearchParams searchParams() {
        return new TenantServiceSearchParams(defaultCommonSearchParams(), null, null, List.of());
    }

    /** 페이징 정보를 지정한 검색 파라미터 반환 */
    public static TenantServiceSearchParams searchParams(int page, int size) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
        return new TenantServiceSearchParams(commonParams, null, null, List.of());
    }

    /** 테넌트 필터를 지정한 검색 파라미터 반환 */
    public static TenantServiceSearchParams searchParamsWithTenant(String tenantId) {
        return new TenantServiceSearchParams(
                defaultCommonSearchParams(), tenantId, null, List.of());
    }

    /** 서비스 필터를 지정한 검색 파라미터 반환 */
    public static TenantServiceSearchParams searchParamsWithService(Long serviceId) {
        return new TenantServiceSearchParams(
                defaultCommonSearchParams(), null, serviceId, List.of());
    }

    /** 상태 필터를 지정한 검색 파라미터 반환 */
    public static TenantServiceSearchParams searchParamsWithStatuses(List<String> statuses) {
        return new TenantServiceSearchParams(defaultCommonSearchParams(), null, null, statuses);
    }

    // ==================== CommonSearchParams ====================

    /** 기본 CommonSearchParams */
    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }

    // ==================== TenantServiceResult ====================

    /** 기본 TenantServiceResult 반환 */
    public static TenantServiceResult tenantServiceResult() {
        return new TenantServiceResult(
                DEFAULT_TENANT_SERVICE_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                DEFAULT_STATUS,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 ID로 TenantServiceResult 반환 */
    public static TenantServiceResult tenantServiceResult(Long tenantServiceId) {
        return new TenantServiceResult(
                tenantServiceId,
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                DEFAULT_STATUS,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 테넌트로 TenantServiceResult 반환 */
    public static TenantServiceResult tenantServiceResultWithTenant(String tenantId) {
        return new TenantServiceResult(
                DEFAULT_TENANT_SERVICE_ID,
                tenantId,
                DEFAULT_SERVICE_ID,
                DEFAULT_STATUS,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 상태로 TenantServiceResult 반환 */
    public static TenantServiceResult tenantServiceResultWithStatus(String status) {
        return new TenantServiceResult(
                DEFAULT_TENANT_SERVICE_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                status,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    // ==================== TenantServicePageResult ====================

    /** 기본 PageResult 반환 (빈 목록) */
    public static TenantServicePageResult emptyPageResult() {
        return TenantServicePageResult.of(List.of(), 0, 10, 0L);
    }

    /** 단일 항목을 포함한 PageResult 반환 */
    public static TenantServicePageResult singleItemPageResult() {
        return TenantServicePageResult.of(List.of(tenantServiceResult()), 0, 10, 1L);
    }

    /** 지정된 내용으로 PageResult 반환 */
    public static TenantServicePageResult pageResult(
            List<TenantServiceResult> content, int page, int size, long totalElements) {
        return TenantServicePageResult.of(content, page, size, totalElements);
    }

    // ==================== 기본값 접근자 ====================

    public static Long defaultTenantServiceId() {
        return DEFAULT_TENANT_SERVICE_ID;
    }

    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    public static Long defaultServiceId() {
        return DEFAULT_SERVICE_ID;
    }

    public static String defaultStatus() {
        return DEFAULT_STATUS;
    }

    public static Instant fixedTime() {
        return FIXED_TIME;
    }
}
