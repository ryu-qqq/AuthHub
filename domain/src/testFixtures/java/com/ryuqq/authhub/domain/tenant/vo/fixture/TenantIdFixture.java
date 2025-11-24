package com.ryuqq.authhub.domain.tenant.vo.fixture;

import com.ryuqq.authhub.domain.tenant.vo.TenantId;

/**
 * TenantId VO Test Fixture
 * Object Mother 패턴을 사용한 테스트 데이터 생성
 */
public class TenantIdFixture {

    private static final Long DEFAULT_TENANT_ID = 1L;

    /**
     * 기본 TenantId 생성
     * @return TenantId 인스턴스
     */
    public static TenantId aTenantId() {
        return TenantId.of(DEFAULT_TENANT_ID);
    }

    /**
     * 특정 값으로 TenantId 생성
     * @param value Tenant ID 값
     * @return TenantId 인스턴스
     */
    public static TenantId aTenantId(Long value) {
        return TenantId.of(value);
    }

    private TenantIdFixture() {
        // Utility class
    }
}
