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

/** Organization Aggregate Test Fixture Object Mother + Builder 패턴을 조합한 테스트 데이터 생성 */
public class OrganizationFixture {

    private static final TenantId DEFAULT_TENANT_ID = TenantId.of(1L);
    private static final OrganizationStatus DEFAULT_ORGANIZATION_STATUS = OrganizationStatus.ACTIVE;
    private static final Clock DEFAULT_CLOCK = () -> Instant.parse("2025-11-24T00:00:00Z");

    /**
     * 기본 새 Organization 생성 (forNew 사용)
     *
     * @return Organization 인스턴스
     */
    public static Organization aNewOrganization() {
        return Organization.forNew(
                OrganizationNameFixture.anOrganizationName(), DEFAULT_TENANT_ID, DEFAULT_CLOCK);
    }

    /**
     * 기본 기존 Organization 생성 (reconstitute 사용)
     *
     * @return Organization 인스턴스
     */
    public static Organization anOrganization() {
        return Organization.reconstitute(
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

    /**
     * Builder 패턴을 사용한 유연한 Organization 생성
     *
     * @return OrganizationBuilder 인스턴스
     */
    public static OrganizationBuilder builder() {
        return new OrganizationBuilder();
    }

    /** Organization Builder 클래스 테스트에서 필요한 필드만 선택적으로 설정 가능 */
    public static class OrganizationBuilder {
        private OrganizationId organizationId;
        private OrganizationName organizationName = OrganizationNameFixture.anOrganizationName();
        private TenantId tenantId = DEFAULT_TENANT_ID;
        private OrganizationStatus organizationStatus = DEFAULT_ORGANIZATION_STATUS;
        private Clock clock = DEFAULT_CLOCK;
        private Instant createdAt;
        private Instant updatedAt;
        private boolean isNew;

        public OrganizationBuilder organizationId(OrganizationId organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public OrganizationBuilder organizationName(OrganizationName organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public OrganizationBuilder tenantId(TenantId tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public OrganizationBuilder organizationStatus(OrganizationStatus organizationStatus) {
            this.organizationStatus = organizationStatus;
            return this;
        }

        public OrganizationBuilder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public OrganizationBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OrganizationBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public OrganizationBuilder asNew() {
            this.isNew = true;
            this.organizationId = null;
            return this;
        }

        public OrganizationBuilder asExisting() {
            this.isNew = false;
            if (this.organizationId == null) {
                this.organizationId = OrganizationIdFixture.anOrganizationId();
            }
            return this;
        }

        public OrganizationBuilder asActive() {
            this.organizationStatus = OrganizationStatus.ACTIVE;
            return this;
        }

        public OrganizationBuilder asInactive() {
            this.organizationStatus = OrganizationStatus.INACTIVE;
            return this;
        }

        public OrganizationBuilder asDeleted() {
            this.organizationStatus = OrganizationStatus.DELETED;
            return this;
        }

        public Organization build() {
            Instant now = clock.now();
            Instant finalCreatedAt = (createdAt != null) ? createdAt : now;
            Instant finalUpdatedAt = (updatedAt != null) ? updatedAt : now;

            if (isNew) {
                // forNew는 항상 ACTIVE 상태로 생성하므로, 다른 상태가 필요한 경우 Organization.of() 사용
                if (organizationStatus == OrganizationStatus.ACTIVE) {
                    return Organization.forNew(organizationName, tenantId, clock);
                } else {
                    return Organization.of(
                            null,
                            organizationName,
                            tenantId,
                            organizationStatus,
                            finalCreatedAt,
                            finalUpdatedAt);
                }
            } else {
                OrganizationId finalOrganizationId =
                        (organizationId != null)
                                ? organizationId
                                : OrganizationIdFixture.anOrganizationId();
                return Organization.reconstitute(
                        finalOrganizationId,
                        organizationName,
                        tenantId,
                        organizationStatus,
                        finalCreatedAt,
                        finalUpdatedAt);
            }
        }
    }

    private OrganizationFixture() {
        // Utility class
    }
}
