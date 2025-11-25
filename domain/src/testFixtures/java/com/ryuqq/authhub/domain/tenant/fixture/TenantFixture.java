package com.ryuqq.authhub.domain.tenant.fixture;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.identifier.fixture.TenantIdFixture;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import com.ryuqq.authhub.domain.tenant.vo.fixture.TenantNameFixture;
import java.time.Instant;

/** Tenant Aggregate Test Fixture Object Mother + Builder 패턴을 조합한 테스트 데이터 생성 */
public class TenantFixture {

    private static final TenantStatus DEFAULT_TENANT_STATUS = TenantStatus.ACTIVE;
    private static final Clock DEFAULT_CLOCK = () -> Instant.parse("2025-11-24T00:00:00Z");

    /**
     * 기본 새 Tenant 생성 (forNew 사용)
     *
     * @return Tenant 인스턴스
     */
    public static Tenant aNewTenant() {
        return Tenant.forNew(TenantNameFixture.aTenantName(), DEFAULT_CLOCK);
    }

    /**
     * 기본 기존 Tenant 생성 (reconstitute 사용)
     *
     * @return Tenant 인스턴스
     */
    public static Tenant aTenant() {
        return Tenant.reconstitute(
                TenantIdFixture.aTenantId(),
                TenantNameFixture.aTenantName(),
                DEFAULT_TENANT_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * 특정 TenantId로 Tenant 생성
     *
     * @param tenantId TenantId
     * @return Tenant 인스턴스
     */
    public static Tenant aTenant(TenantId tenantId) {
        return Tenant.reconstitute(
                tenantId,
                TenantNameFixture.aTenantName(),
                DEFAULT_TENANT_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * 특정 TenantName으로 Tenant 생성
     *
     * @param tenantName TenantName
     * @return Tenant 인스턴스
     */
    public static Tenant aTenant(TenantName tenantName) {
        return Tenant.reconstitute(
                TenantIdFixture.aTenantId(),
                tenantName,
                DEFAULT_TENANT_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * 특정 TenantStatus로 Tenant 생성
     *
     * @param tenantStatus TenantStatus
     * @return Tenant 인스턴스
     */
    public static Tenant aTenantWithStatus(TenantStatus tenantStatus) {
        return Tenant.reconstitute(
                TenantIdFixture.aTenantId(),
                TenantNameFixture.aTenantName(),
                tenantStatus,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * INACTIVE 상태의 Tenant 생성
     *
     * @return Tenant 인스턴스
     */
    public static Tenant anInactiveTenant() {
        return aTenantWithStatus(TenantStatus.INACTIVE);
    }

    /**
     * DELETED 상태의 Tenant 생성
     *
     * @return Tenant 인스턴스
     */
    public static Tenant aDeletedTenant() {
        return aTenantWithStatus(TenantStatus.DELETED);
    }

    /**
     * Builder 패턴을 사용한 유연한 Tenant 생성
     *
     * @return TenantBuilder 인스턴스
     */
    public static TenantBuilder builder() {
        return new TenantBuilder();
    }

    /** Tenant Builder 클래스 테스트에서 필요한 필드만 선택적으로 설정 가능 */
    public static class TenantBuilder {
        private TenantId tenantId;
        private TenantName tenantName = TenantNameFixture.aTenantName();
        private TenantStatus tenantStatus = DEFAULT_TENANT_STATUS;
        private Clock clock = DEFAULT_CLOCK;
        private Instant createdAt;
        private Instant updatedAt;
        private boolean isNew;

        public TenantBuilder tenantId(TenantId tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public TenantBuilder tenantName(TenantName tenantName) {
            this.tenantName = tenantName;
            return this;
        }

        public TenantBuilder tenantStatus(TenantStatus tenantStatus) {
            this.tenantStatus = tenantStatus;
            return this;
        }

        public TenantBuilder clock(Clock clock) {
            this.clock = clock;
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

        public TenantBuilder asNew() {
            this.isNew = true;
            this.tenantId = null;
            return this;
        }

        public TenantBuilder asExisting() {
            this.isNew = false;
            if (this.tenantId == null) {
                this.tenantId = TenantIdFixture.aTenantId();
            }
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

        public Tenant build() {
            Instant now = clock.now();
            Instant finalCreatedAt = (createdAt != null) ? createdAt : now;
            Instant finalUpdatedAt = (updatedAt != null) ? updatedAt : now;

            if (isNew) {
                // forNew는 항상 ACTIVE 상태로 생성하므로, 다른 상태가 필요한 경우 Tenant.of() 사용
                if (tenantStatus == TenantStatus.ACTIVE) {
                    return Tenant.forNew(tenantName, clock);
                } else {
                    return Tenant.of(
                            null, tenantName, tenantStatus, finalCreatedAt, finalUpdatedAt);
                }
            } else {
                TenantId finalTenantId =
                        (tenantId != null) ? tenantId : TenantIdFixture.aTenantId();
                return Tenant.reconstitute(
                        finalTenantId, tenantName, tenantStatus, finalCreatedAt, finalUpdatedAt);
            }
        }
    }

    private TenantFixture() {
        // Utility class
    }
}
