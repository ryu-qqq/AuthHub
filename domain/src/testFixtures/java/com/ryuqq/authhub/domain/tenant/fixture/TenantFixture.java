package com.ryuqq.authhub.domain.tenant.fixture;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;

import java.time.Instant;

/**
 * TenantFixture - Tenant Aggregate 테스트 픽스처
 *
 * <p>테스트에서 Tenant 객체를 쉽게 생성할 수 있도록 도와주는 빌더 패턴 기반 픽스처입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantFixture {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final String DEFAULT_TENANT_NAME = "Default Tenant";
    private static final Clock DEFAULT_CLOCK = () -> Instant.parse("2025-11-24T00:00:00Z");

    private TenantFixture() {
    }

    // ========== Simple Factory Methods ==========

    public static Tenant aTenant() {
        return builder().asExisting().build();
    }

    public static Tenant aTenant(TenantId tenantId) {
        return builder().asExisting().tenantId(tenantId).build();
    }

    public static Tenant aTenant(TenantName tenantName) {
        return builder().asExisting().tenantName(tenantName).build();
    }

    public static Tenant aNewTenant() {
        return builder().asNew().build();
    }

    public static Tenant anInactiveTenant() {
        return builder().asExisting().asInactive().build();
    }

    public static Tenant aDeletedTenant() {
        return builder().asExisting().asDeleted().build();
    }

    // ========== Builder ==========

    public static TenantBuilder builder() {
        return new TenantBuilder();
    }

    public static final class TenantBuilder {

        private TenantId tenantId;
        private TenantName tenantName = TenantName.of(DEFAULT_TENANT_NAME);
        private TenantStatus tenantStatus = TenantStatus.ACTIVE;
        private Instant createdAt = DEFAULT_CLOCK.now();
        private Instant updatedAt = DEFAULT_CLOCK.now();
        private boolean isNew = false;

        private TenantBuilder() {
        }

        public TenantBuilder asNew() {
            this.isNew = true;
            this.tenantId = null;
            return this;
        }

        public TenantBuilder asExisting() {
            this.isNew = false;
            this.tenantId = TenantId.of(DEFAULT_TENANT_ID);
            return this;
        }

        public TenantBuilder tenantId(TenantId tenantId) {
            this.tenantId = tenantId;
            this.isNew = (tenantId == null);
            return this;
        }

        public TenantBuilder tenantName(TenantName tenantName) {
            this.tenantName = tenantName;
            return this;
        }

        public TenantBuilder tenantName(String tenantName) {
            this.tenantName = TenantName.of(tenantName);
            return this;
        }

        public TenantBuilder tenantStatus(TenantStatus tenantStatus) {
            this.tenantStatus = tenantStatus;
            return this;
        }

        public TenantBuilder asActive() {
            this.tenantStatus = TenantStatus.ACTIVE;
            return this;
        }

        public TenantBuilder asInactive() {
            this.tenantStatus = TenantStatus.INACTIVE;
            return this;
        }

        public TenantBuilder asDeleted() {
            this.tenantStatus = TenantStatus.DELETED;
            return this;
        }

        public TenantBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TenantBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public TenantBuilder clock(Clock clock) {
            this.createdAt = clock.now();
            this.updatedAt = clock.now();
            return this;
        }

        public Tenant build() {
            return Tenant.of(
                    tenantId,
                    tenantName,
                    tenantStatus,
                    createdAt,
                    updatedAt
            );
        }
    }
}
