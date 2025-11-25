package com.ryuqq.authhub.domain.organization.identifier.fixture;

import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;

/** OrganizationId Test Fixture Object Mother 패턴을 사용한 테스트 데이터 생성 */
public class OrganizationIdFixture {

    private static final Long DEFAULT_ORGANIZATION_ID = 100L;

    /**
     * 기본 OrganizationId 생성
     *
     * @return OrganizationId 인스턴스
     */
    public static OrganizationId anOrganizationId() {
        return OrganizationId.of(DEFAULT_ORGANIZATION_ID);
    }

    /**
     * 특정 값으로 OrganizationId 생성
     *
     * @param value Organization ID 값
     * @return OrganizationId 인스턴스
     */
    public static OrganizationId anOrganizationId(Long value) {
        return OrganizationId.of(value);
    }

    private OrganizationIdFixture() {
        // Utility class
    }
}
