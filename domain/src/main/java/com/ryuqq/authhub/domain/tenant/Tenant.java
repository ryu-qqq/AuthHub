package com.ryuqq.authhub.domain.tenant;

import com.ryuqq.authhub.domain.common.model.AggregateRoot;
import com.ryuqq.authhub.domain.tenant.vo.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;

import java.util.Objects;

/**
 * Tenant Aggregate Root
 * 멀티 테넌시 도메인 객체
 */
public class Tenant implements AggregateRoot {

    private final TenantId tenantId;
    private final TenantName tenantName;
    private final TenantStatus tenantStatus;

    private Tenant(TenantId tenantId, TenantName tenantName, TenantStatus tenantStatus) {
        validateTenantId(tenantId);
        validateTenantName(tenantName);
        validateTenantStatus(tenantStatus);

        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.tenantStatus = tenantStatus;
    }

    public static Tenant create(TenantId tenantId, TenantName tenantName, TenantStatus tenantStatus) {
        return new Tenant(tenantId, tenantName, tenantStatus);
    }

    private void validateTenantId(TenantId tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
        }
    }

    private void validateTenantName(TenantName tenantName) {
        if (tenantName == null) {
            throw new IllegalArgumentException("TenantName은 null일 수 없습니다");
        }
    }

    private void validateTenantStatus(TenantStatus tenantStatus) {
        if (tenantStatus == null) {
            throw new IllegalArgumentException("TenantStatus는 null일 수 없습니다");
        }
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public TenantName getTenantName() {
        return tenantName;
    }

    public TenantStatus getTenantStatus() {
        return tenantStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tenant tenant = (Tenant) o;
        return Objects.equals(tenantId, tenant.tenantId) &&
                Objects.equals(tenantName, tenant.tenantName) &&
                tenantStatus == tenant.tenantStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, tenantName, tenantStatus);
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "tenantId=" + tenantId +
                ", tenantName=" + tenantName +
                ", tenantStatus=" + tenantStatus +
                '}';
    }
}
