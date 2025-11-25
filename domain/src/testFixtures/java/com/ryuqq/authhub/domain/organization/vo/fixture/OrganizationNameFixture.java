package com.ryuqq.authhub.domain.organization.vo.fixture;

import com.ryuqq.authhub.domain.organization.vo.OrganizationName;

/** OrganizationName VO Test Fixture Object Mother 패턴을 사용한 테스트 데이터 생성 */
public class OrganizationNameFixture {

    private static final String DEFAULT_NAME = "Test Organization";

    /**
     * 기본 OrganizationName 생성
     *
     * @return OrganizationName 인스턴스
     */
    public static OrganizationName anOrganizationName() {
        return OrganizationName.of(DEFAULT_NAME);
    }

    /**
     * 특정 값으로 OrganizationName 생성
     *
     * @param name 조직명
     * @return OrganizationName 인스턴스
     */
    public static OrganizationName anOrganizationName(String name) {
        return OrganizationName.of(name);
    }

    private OrganizationNameFixture() {
        // Utility class
    }
}
