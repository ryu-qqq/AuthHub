package com.ryuqq.authhub.domain.tenant.aggregate;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;

import java.time.Instant;
import java.util.Objects;

/**
 * Tenant - Tenant Aggregate Root
 *
 * <p>멀티테넌시 지원을 위한 테넌트 정보를 관리합니다.
 * 불변 객체로 상태 변경 시 새로운 객체를 반환합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Tenant {

    private final TenantId tenantId;
    private final TenantName tenantName;
    private final TenantStatus tenantStatus;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Tenant(
            TenantId tenantId,
            TenantName tenantName,
            TenantStatus tenantStatus,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.tenantStatus = tenantStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Factory Methods ==========

    public static Tenant forNew(TenantName tenantName, Clock clock) {
        validateTenantName(tenantName);
        Instant now = clock.now();
        return new Tenant(null, tenantName, TenantStatus.ACTIVE, now, now);
    }

    public static Tenant of(
            TenantId tenantId,
            TenantName tenantName,
            TenantStatus tenantStatus,
            Instant createdAt,
            Instant updatedAt
    ) {
        validateTenantName(tenantName);
        validateTenantStatus(tenantStatus);
        validateCreatedAt(createdAt);
        validateUpdatedAt(updatedAt);
        return new Tenant(tenantId, tenantName, tenantStatus, createdAt, updatedAt);
    }

    public static Tenant reconstitute(
            TenantId tenantId,
            TenantName tenantName,
            TenantStatus tenantStatus,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (tenantId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null tenantId");
        }
        validateTenantName(tenantName);
        validateTenantStatus(tenantStatus);
        validateCreatedAt(createdAt);
        validateUpdatedAt(updatedAt);
        return new Tenant(tenantId, tenantName, tenantStatus, createdAt, updatedAt);
    }

    // ========== Validation Methods ==========

    private static void validateTenantName(TenantName tenantName) {
        if (tenantName == null) {
            throw new IllegalArgumentException("TenantName은 null일 수 없습니다");
        }
    }

    private static void validateTenantStatus(TenantStatus tenantStatus) {
        if (tenantStatus == null) {
            throw new IllegalArgumentException("TenantStatus는 null일 수 없습니다");
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

    public Tenant activate(Clock clock) {
        if (!tenantStatus.canActivate()) {
            throw new InvalidTenantStateException(tenantStatus, TenantStatus.ACTIVE);
        }
        return new Tenant(tenantId, tenantName, TenantStatus.ACTIVE, createdAt, clock.now());
    }

    public Tenant deactivate(Clock clock) {
        if (!tenantStatus.canDeactivate()) {
            throw new InvalidTenantStateException(tenantStatus, TenantStatus.INACTIVE);
        }
        return new Tenant(tenantId, tenantName, TenantStatus.INACTIVE, createdAt, clock.now());
    }

    public Tenant delete(Clock clock) {
        if (!tenantStatus.canDelete()) {
            throw new InvalidTenantStateException(tenantStatus, TenantStatus.DELETED);
        }
        return new Tenant(tenantId, tenantName, TenantStatus.DELETED, createdAt, clock.now());
    }

    // ========== Helper Methods ==========

    public Long tenantIdValue() {
        return tenantId == null ? null : tenantId.value();
    }

    public String tenantNameValue() {
        return tenantName.value();
    }

    public String statusValue() {
        return tenantStatus.name();
    }

    public boolean isNew() {
        return tenantId == null;
    }

    public boolean isActive() {
        return tenantStatus == TenantStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return tenantStatus == TenantStatus.DELETED;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    // ========== Legacy Getters (for compatibility) ==========

    public TenantId getTenantId() {
        return tenantId;
    }

    public TenantName getTenantName() {
        return tenantName;
    }

    public TenantStatus getTenantStatus() {
        return tenantStatus;
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
        Tenant tenant = (Tenant) o;
        return Objects.equals(tenantId, tenant.tenantId)
                && Objects.equals(tenantName, tenant.tenantName)
                && tenantStatus == tenant.tenantStatus
                && Objects.equals(createdAt, tenant.createdAt)
                && Objects.equals(updatedAt, tenant.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, tenantName, tenantStatus, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Tenant{"
                + "tenantId=" + tenantId
                + ", tenantName=" + tenantName
                + ", tenantStatus=" + tenantStatus
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
