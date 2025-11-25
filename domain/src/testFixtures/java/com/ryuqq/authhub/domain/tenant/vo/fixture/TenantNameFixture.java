package com.ryuqq.authhub.domain.tenant.vo.fixture;

import com.ryuqq.authhub.domain.tenant.vo.TenantName;

/** TenantName VO Test Fixture Object Mother 패턴을 사용한 테스트 데이터 생성 */
public class TenantNameFixture {

    private static final String DEFAULT_NAME = "Test Tenant";

    /**
     * 기본 TenantName 생성
     *
     * @return TenantName 인스턴스
     */
    public static TenantName aTenantName() {
        return TenantName.of(DEFAULT_NAME);
    }

    /**
     * 특정 값으로 TenantName 생성
     *
     * @param name 테넌트명
     * @return TenantName 인스턴스
     */
    public static TenantName aTenantName(String name) {
        return TenantName.of(name);
    }

    private TenantNameFixture() {
        // Utility class
    }
}
