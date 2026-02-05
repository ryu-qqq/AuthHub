package com.ryuqq.authhub.application.organization.fixture;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.organization.dto.query.OrganizationSearchParams;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationPageResult;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResult;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import java.time.Instant;
import java.util.List;

/**
 * Organization Query DTO 테스트 픽스처
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
public final class OrganizationQueryFixtures {

    private static final String DEFAULT_ORG_ID = OrganizationFixture.defaultIdString();
    private static final String DEFAULT_TENANT_ID = OrganizationFixture.defaultTenantIdString();
    private static final String DEFAULT_NAME = "Test Organization";
    private static final String DEFAULT_STATUS = "ACTIVE";
    private static final Instant FIXED_TIME = OrganizationFixture.fixedTime();

    private OrganizationQueryFixtures() {}

    // ==================== OrganizationSearchParams ====================

    /** 기본 검색 파라미터 반환 (전체 조회) */
    public static OrganizationSearchParams searchParams() {
        return new OrganizationSearchParams(
                defaultCommonSearchParams(), null, null, null, List.of());
    }

    /** 페이징 정보를 지정한 검색 파라미터 반환 */
    public static OrganizationSearchParams searchParams(int page, int size) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
        return new OrganizationSearchParams(commonParams, null, null, null, List.of());
    }

    /** 테넌트 ID 목록을 지정한 검색 파라미터 반환 */
    public static OrganizationSearchParams searchParamsWithTenantIds(List<String> tenantIds) {
        return new OrganizationSearchParams(
                defaultCommonSearchParams(), tenantIds, null, null, List.of());
    }

    /** 검색어를 지정한 검색 파라미터 반환 */
    public static OrganizationSearchParams searchParamsWithSearchWord(String searchWord) {
        return new OrganizationSearchParams(
                defaultCommonSearchParams(), null, searchWord, null, List.of());
    }

    /** 상태 필터를 지정한 검색 파라미터 반환 */
    public static OrganizationSearchParams searchParamsWithStatuses(List<String> statuses) {
        return new OrganizationSearchParams(
                defaultCommonSearchParams(), null, null, null, statuses);
    }

    /** tenantIds와 statuses가 null인 검색 파라미터 반환 (엣지 케이스용) */
    public static OrganizationSearchParams searchParamsWithNullFilters() {
        return OrganizationSearchParams.of(defaultCommonSearchParams(), null, null, null, null);
    }

    // ==================== CommonSearchParams ====================

    /** 기본 CommonSearchParams */
    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }

    // ==================== OrganizationResult ====================

    /** 기본 OrganizationResult 반환 */
    public static OrganizationResult organizationResult() {
        return new OrganizationResult(
                DEFAULT_ORG_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_NAME,
                DEFAULT_STATUS,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 상태로 OrganizationResult 반환 */
    public static OrganizationResult organizationResultWithStatus(String status) {
        return new OrganizationResult(
                DEFAULT_ORG_ID, DEFAULT_TENANT_ID, DEFAULT_NAME, status, FIXED_TIME, FIXED_TIME);
    }

    /** 지정된 이름으로 OrganizationResult 반환 */
    public static OrganizationResult organizationResultWithName(String name) {
        return new OrganizationResult(
                DEFAULT_ORG_ID, DEFAULT_TENANT_ID, name, DEFAULT_STATUS, FIXED_TIME, FIXED_TIME);
    }

    // ==================== OrganizationPageResult ====================

    /** 기본 PageResult 반환 (빈 목록) */
    public static OrganizationPageResult emptyPageResult() {
        return OrganizationPageResult.of(List.of(), 0, 10, 0L);
    }

    /** 단일 항목을 포함한 PageResult 반환 */
    public static OrganizationPageResult singleItemPageResult() {
        return OrganizationPageResult.of(List.of(organizationResult()), 0, 10, 1L);
    }

    /** 지정된 내용으로 PageResult 반환 */
    public static OrganizationPageResult pageResult(
            List<OrganizationResult> content, int page, int size, long totalElements) {
        return OrganizationPageResult.of(content, page, size, totalElements);
    }

    // ==================== 기본값 접근자 ====================

    public static String defaultOrganizationId() {
        return DEFAULT_ORG_ID;
    }

    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    public static String defaultName() {
        return DEFAULT_NAME;
    }

    public static String defaultStatus() {
        return DEFAULT_STATUS;
    }

    public static Instant fixedTime() {
        return FIXED_TIME;
    }
}
