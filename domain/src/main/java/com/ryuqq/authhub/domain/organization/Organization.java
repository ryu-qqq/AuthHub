package com.ryuqq.authhub.domain.organization;

import com.ryuqq.authhub.domain.common.model.AggregateRoot;
import com.ryuqq.authhub.domain.organization.vo.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;

import java.util.Objects;

/**
 * Organization Aggregate Root
 * 조직 도메인 객체 (Tenant에 속함)
 */
public class Organization implements AggregateRoot {

    private final OrganizationId organizationId;
    private final OrganizationName organizationName;
    private final Long tenantId;
    private final OrganizationStatus organizationStatus;

    private Organization(OrganizationId organizationId, OrganizationName organizationName, Long tenantId, OrganizationStatus organizationStatus) {
        validateOrganizationId(organizationId);
        validateOrganizationName(organizationName);
        validateTenantId(tenantId);
        validateOrganizationStatus(organizationStatus);

        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.tenantId = tenantId;
        this.organizationStatus = organizationStatus;
    }

    public static Organization create(OrganizationId organizationId, OrganizationName organizationName, Long tenantId, OrganizationStatus organizationStatus) {
        return new Organization(organizationId, organizationName, tenantId, organizationStatus);
    }

    private void validateOrganizationId(OrganizationId organizationId) {
        if (organizationId == null) {
            throw new IllegalArgumentException("OrganizationId는 null일 수 없습니다");
        }
    }

    private void validateOrganizationName(OrganizationName organizationName) {
        if (organizationName == null) {
            throw new IllegalArgumentException("OrganizationName은 null일 수 없습니다");
        }
    }

    private void validateTenantId(Long tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
        }
    }

    private void validateOrganizationStatus(OrganizationStatus organizationStatus) {
        if (organizationStatus == null) {
            throw new IllegalArgumentException("OrganizationStatus는 null일 수 없습니다");
        }
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public OrganizationName getOrganizationName() {
        return organizationName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public OrganizationStatus getOrganizationStatus() {
        return organizationStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Organization that = (Organization) o;
        return Objects.equals(organizationId, that.organizationId) &&
                Objects.equals(organizationName, that.organizationName) &&
                Objects.equals(tenantId, that.tenantId) &&
                organizationStatus == that.organizationStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizationId, organizationName, tenantId, organizationStatus);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "organizationId=" + organizationId +
                ", organizationName=" + organizationName +
                ", tenantId=" + tenantId +
                ", organizationStatus=" + organizationStatus +
                '}';
    }
}
