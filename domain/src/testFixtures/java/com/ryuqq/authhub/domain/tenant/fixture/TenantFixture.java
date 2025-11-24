package com.ryuqq.authhub.domain.tenant.fixture;

import com.ryuqq.authhub.domain.tenant.Tenant;
import com.ryuqq.authhub.domain.tenant.TenantStatus;
import com.ryuqq.authhub.domain.tenant.vo.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.fixture.TenantIdFixture;
import com.ryuqq.authhub.domain.tenant.vo.fixture.TenantNameFixture;

/**
 * Tenant Aggregate Test Fixture
 * Object Mother 패턴을 사용한 테스트 데이터 생성
 */
public class TenantFixture {

    private static final TenantStatus DEFAULT_TENANT_STATUS = TenantStatus.ACTIVE;

    /**
     * 기본 Tenant 생성
     * @return Tenant 인스턴스
     */
    public static Tenant aTenant() {
        return Tenant.create(
                TenantIdFixture.aTenantId(),
                TenantNameFixture.aTenantName(),
                DEFAULT_TENANT_STATUS
        );
    }

    /**
     * 특정 TenantId로 Tenant 생성
     * @param tenantId TenantId
     * @return Tenant 인스턴스
     */
    public static Tenant aTenant(TenantId tenantId) {
        return Tenant.create(
                tenantId,
                TenantNameFixture.aTenantName(),
                DEFAULT_TENANT_STATUS
        );
    }

    /**
     * 특정 TenantName으로 Tenant 생성
     * @param tenantName TenantName
     * @return Tenant 인스턴스
     */
    public static Tenant aTenant(TenantName tenantName) {
        return Tenant.create(
                TenantIdFixture.aTenantId(),
                tenantName,
                DEFAULT_TENANT_STATUS
        );
    }

    /**
     * 특정 TenantStatus로 Tenant 생성
     * @param tenantStatus TenantStatus
     * @return Tenant 인스턴스
     */
    public static Tenant aTenantWithStatus(TenantStatus tenantStatus) {
        return Tenant.create(
                TenantIdFixture.aTenantId(),
                TenantNameFixture.aTenantName(),
                tenantStatus
        );
    }

    /**
     * INACTIVE 상태의 Tenant 생성
     * @return Tenant 인스턴스
     */
    public static Tenant anInactiveTenant() {
        return aTenantWithStatus(TenantStatus.INACTIVE);
    }

    /**
     * DELETED 상태의 Tenant 생성
     * @return Tenant 인스턴스
     */
    public static Tenant aDeletedTenant() {
        return aTenantWithStatus(TenantStatus.DELETED);
    }

    private TenantFixture() {
        // Utility class
    }
}
