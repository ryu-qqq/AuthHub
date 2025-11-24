package com.ryuqq.authhub.domain.organization.fixture;

import com.ryuqq.authhub.domain.organization.Organization;
import com.ryuqq.authhub.domain.organization.OrganizationStatus;
import com.ryuqq.authhub.domain.organization.vo.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.fixture.OrganizationIdFixture;
import com.ryuqq.authhub.domain.organization.vo.fixture.OrganizationNameFixture;

/**
 * Organization Aggregate Test Fixture
 * Object Mother 패턴을 사용한 테스트 데이터 생성
 */
public class OrganizationFixture {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final OrganizationStatus DEFAULT_ORGANIZATION_STATUS = OrganizationStatus.ACTIVE;

    /**
     * 기본 Organization 생성
     * @return Organization 인스턴스
     */
    public static Organization anOrganization() {
        return Organization.create(
                OrganizationIdFixture.anOrganizationId(),
                OrganizationNameFixture.anOrganizationName(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_STATUS
        );
    }

    /**
     * 특정 OrganizationId로 Organization 생성
     * @param organizationId OrganizationId
     * @return Organization 인스턴스
     */
    public static Organization anOrganization(OrganizationId organizationId) {
        return Organization.create(
                organizationId,
                OrganizationNameFixture.anOrganizationName(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_STATUS
        );
    }

    /**
     * 특정 OrganizationName으로 Organization 생성
     * @param organizationName OrganizationName
     * @return Organization 인스턴스
     */
    public static Organization anOrganization(OrganizationName organizationName) {
        return Organization.create(
                OrganizationIdFixture.anOrganizationId(),
                organizationName,
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_STATUS
        );
    }

    /**
     * 특정 TenantId로 Organization 생성
     * @param tenantId Tenant ID
     * @return Organization 인스턴스
     */
    public static Organization anOrganizationWithTenantId(Long tenantId) {
        return Organization.create(
                OrganizationIdFixture.anOrganizationId(),
                OrganizationNameFixture.anOrganizationName(),
                tenantId,
                DEFAULT_ORGANIZATION_STATUS
        );
    }

    /**
     * 특정 OrganizationStatus로 Organization 생성
     * @param organizationStatus OrganizationStatus
     * @return Organization 인스턴스
     */
    public static Organization anOrganizationWithStatus(OrganizationStatus organizationStatus) {
        return Organization.create(
                OrganizationIdFixture.anOrganizationId(),
                OrganizationNameFixture.anOrganizationName(),
                DEFAULT_TENANT_ID,
                organizationStatus
        );
    }

    /**
     * INACTIVE 상태의 Organization 생성
     * @return Organization 인스턴스
     */
    public static Organization anInactiveOrganization() {
        return anOrganizationWithStatus(OrganizationStatus.INACTIVE);
    }

    /**
     * DELETED 상태의 Organization 생성
     * @return Organization 인스턴스
     */
    public static Organization aDeletedOrganization() {
        return anOrganizationWithStatus(OrganizationStatus.DELETED);
    }

    private OrganizationFixture() {
        // Utility class
    }
}
