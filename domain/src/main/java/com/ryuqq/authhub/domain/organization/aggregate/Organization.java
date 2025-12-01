package com.ryuqq.authhub.domain.organization.aggregate;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.exception.InvalidOrganizationStateException;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;

import java.time.Instant;
import java.util.Objects;

/**
 * Organization - Organization Aggregate Root
 *
 * <p>테넌트 내의 조직 정보를 관리합니다.
 * 불변 객체로 상태 변경 시 새로운 객체를 반환합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Organization {

    private final OrganizationId organizationId;
    private final OrganizationName organizationName;
    private final TenantId tenantId;
    private final OrganizationStatus organizationStatus;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Organization(
            OrganizationId organizationId,
            OrganizationName organizationName,
            TenantId tenantId,
            OrganizationStatus organizationStatus,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.tenantId = tenantId;
        this.organizationStatus = organizationStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Factory Methods ==========

    public static Organization forNew(OrganizationName organizationName, TenantId tenantId, Clock clock) {
        validateOrganizationName(organizationName);
        validateTenantId(tenantId);
        Instant now = clock.now();
        return new Organization(null, organizationName, tenantId, OrganizationStatus.ACTIVE, now, now);
    }

    public static Organization of(
            OrganizationId organizationId,
            OrganizationName organizationName,
            TenantId tenantId,
            OrganizationStatus organizationStatus,
            Instant createdAt,
            Instant updatedAt
    ) {
        validateOrganizationName(organizationName);
        validateTenantId(tenantId);
        validateOrganizationStatus(organizationStatus);
        validateCreatedAt(createdAt);
        validateUpdatedAt(updatedAt);
        return new Organization(organizationId, organizationName, tenantId, organizationStatus, createdAt, updatedAt);
    }

    public static Organization reconstitute(
            OrganizationId organizationId,
            OrganizationName organizationName,
            TenantId tenantId,
            OrganizationStatus organizationStatus,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (organizationId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null organizationId");
        }
        validateOrganizationName(organizationName);
        validateTenantId(tenantId);
        validateOrganizationStatus(organizationStatus);
        validateCreatedAt(createdAt);
        validateUpdatedAt(updatedAt);
        return new Organization(organizationId, organizationName, tenantId, organizationStatus, createdAt, updatedAt);
    }

    // ========== Validation Methods ==========

    private static void validateOrganizationName(OrganizationName organizationName) {
        if (organizationName == null) {
            throw new IllegalArgumentException("OrganizationName은 null일 수 없습니다");
        }
    }

    private static void validateTenantId(TenantId tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
        }
        if (tenantId.isNew()) {
            throw new IllegalArgumentException("Organization은 기존 Tenant를 참조해야 합니다");
        }
    }

    private static void validateOrganizationStatus(OrganizationStatus organizationStatus) {
        if (organizationStatus == null) {
            throw new IllegalArgumentException("OrganizationStatus는 null일 수 없습니다");
        }
    }

    private static void validateCreatedAt(Instant createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt는 null일 수 없습니다");
        }
    }

    private static void validateUpdatedAt(Instant updatedAt) {
        if (updatedAt == null) {
            throw new IllegalArgumentException("updatedAt는 null일 수 없습니다");
        }
    }

    // ========== Business Methods ==========

    public Organization activate(Clock clock) {
        if (!organizationStatus.canActivate()) {
            throw new InvalidOrganizationStateException(organizationStatus, OrganizationStatus.ACTIVE);
        }
        return new Organization(organizationId, organizationName, tenantId, OrganizationStatus.ACTIVE, createdAt, clock.now());
    }

    public Organization deactivate(Clock clock) {
        if (!organizationStatus.canDeactivate()) {
            throw new InvalidOrganizationStateException(organizationStatus, OrganizationStatus.INACTIVE);
        }
        return new Organization(organizationId, organizationName, tenantId, OrganizationStatus.INACTIVE, createdAt, clock.now());
    }

    public Organization delete(Clock clock) {
        if (!organizationStatus.canDelete()) {
            throw new InvalidOrganizationStateException(organizationStatus, OrganizationStatus.DELETED);
        }
        return new Organization(organizationId, organizationName, tenantId, OrganizationStatus.DELETED, createdAt, clock.now());
    }

    // ========== Helper Methods ==========

    public Long organizationIdValue() {
        return organizationId == null ? null : organizationId.value();
    }

    public String organizationNameValue() {
        return organizationName.value();
    }

    public Long tenantIdValue() {
        return tenantId.value();
    }

    public String statusValue() {
        return organizationStatus.name();
    }

    public boolean isNew() {
        return organizationId == null;
    }

    public boolean isActive() {
        return organizationStatus == OrganizationStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return organizationStatus == OrganizationStatus.DELETED;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    // ========== Legacy Getters (for compatibility) ==========

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public OrganizationName getOrganizationName() {
        return organizationName;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public OrganizationStatus getOrganizationStatus() {
        return organizationStatus;
    }

    // ========== Object Methods ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Organization that = (Organization) o;
        return Objects.equals(organizationId, that.organizationId)
                && Objects.equals(organizationName, that.organizationName)
                && Objects.equals(tenantId, that.tenantId)
                && organizationStatus == that.organizationStatus
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizationId, organizationName, tenantId, organizationStatus, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Organization{"
                + "organizationId=" + organizationId
                + ", organizationName=" + organizationName
                + ", tenantId=" + tenantId
                + ", organizationStatus=" + organizationStatus
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
