package com.ryuqq.authhub.domain.organization.fixture;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.identifier.fixture.OrganizationIdFixture;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.organization.vo.fixture.OrganizationNameFixture;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Instant;

/** Organization Aggregate Test Fixture Object Mother 패턴을 사용한 테스트 데이터 생성 */
public class OrganizationFixture {

    private static final TenantId DEFAULT_TENANT_ID = TenantId.of(1L);
    private static final OrganizationStatus DEFAULT_ORGANIZATION_STATUS = OrganizationStatus.ACTIVE;
    private static final Clock DEFAULT_CLOCK = () -> Instant.parse("2025-11-24T00:00:00Z");

    /**
     * 기본 Organization 생성
     *
     * @return Organization 인스턴스
     */
    public static Organization anOrganization() {
        return Organization.of(
                OrganizationIdFixture.anOrganizationId(),
                OrganizationNameFixture.anOrganizationName(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * 특정 OrganizationId로 Organization 생성
     *
     * @param organizationId OrganizationId
     * @return Organization 인스턴스
     */
    public static Organization anOrganization(OrganizationId organizationId) {
        return Organization.of(
                organizationId,
                OrganizationNameFixture.anOrganizationName(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * 특정 OrganizationName으로 Organization 생성
     *
     * @param organizationName OrganizationName
     * @return Organization 인스턴스
     */
    public static Organization anOrganization(OrganizationName organizationName) {
        return Organization.of(
                OrganizationIdFixture.anOrganizationId(),
                organizationName,
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * 특정 TenantId로 Organization 생성
     *
     * @param tenantId Tenant ID
     * @return Organization 인스턴스
     */
    public static Organization anOrganizationWithTenantId(TenantId tenantId) {
        return Organization.of(
                OrganizationIdFixture.anOrganizationId(),
                OrganizationNameFixture.anOrganizationName(),
                tenantId,
                DEFAULT_ORGANIZATION_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * 특정 OrganizationStatus로 Organization 생성
     *
     * @param organizationStatus OrganizationStatus
     * @return Organization 인스턴스
     */
    public static Organization anOrganizationWithStatus(OrganizationStatus organizationStatus) {
        return Organization.of(
                OrganizationIdFixture.anOrganizationId(),
                OrganizationNameFixture.anOrganizationName(),
                DEFAULT_TENANT_ID,
                organizationStatus,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * INACTIVE 상태의 Organization 생성
     *
     * @return Organization 인스턴스
     */
    public static Organization anInactiveOrganization() {
        return anOrganizationWithStatus(OrganizationStatus.INACTIVE);
    }

    /**
     * DELETED 상태의 Organization 생성
     *
     * @return Organization 인스턴스
     */
    public static Organization aDeletedOrganization() {
        return anOrganizationWithStatus(OrganizationStatus.DELETED);
    }

    private OrganizationFixture() {
        // Utility class
    }
}
