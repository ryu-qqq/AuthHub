package com.ryuqq.authhub.domain.organization.fixture;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;

import java.time.Instant;

/**
 * OrganizationFixture - Organization Aggregate 테스트 픽스처
 *
 * <p>테스트에서 Organization 객체를 쉽게 생성할 수 있도록 도와주는 빌더 패턴 기반 픽스처입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OrganizationFixture {

    private static final Long DEFAULT_ORGANIZATION_ID = 1L;
    private static final String DEFAULT_ORGANIZATION_NAME = "Default Organization";
    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Clock DEFAULT_CLOCK = () -> Instant.parse("2025-11-24T00:00:00Z");

    private OrganizationFixture() {
    }

    // ========== Simple Factory Methods ==========

    public static Organization anOrganization() {
        return builder().asExisting().build();
    }

    public static Organization anOrganization(OrganizationId organizationId) {
        return builder().asExisting().organizationId(organizationId).build();
    }

    public static Organization anOrganization(OrganizationName organizationName) {
        return builder().asExisting().organizationName(organizationName).build();
    }

    public static Organization aNewOrganization() {
        return builder().asNew().build();
    }

    public static Organization anInactiveOrganization() {
        return builder().asExisting().asInactive().build();
    }

    public static Organization aDeletedOrganization() {
        return builder().asExisting().asDeleted().build();
    }

    // ========== Builder ==========

    public static OrganizationBuilder builder() {
        return new OrganizationBuilder();
    }

    public static final class OrganizationBuilder {

        private OrganizationId organizationId;
        private OrganizationName organizationName = OrganizationName.of(DEFAULT_ORGANIZATION_NAME);
        private TenantId tenantId = TenantId.of(DEFAULT_TENANT_ID);
        private OrganizationStatus organizationStatus = OrganizationStatus.ACTIVE;
        private Instant createdAt = DEFAULT_CLOCK.now();
        private Instant updatedAt = DEFAULT_CLOCK.now();
        private boolean isNew = false;

        private OrganizationBuilder() {
        }

        public OrganizationBuilder asNew() {
            this.isNew = true;
            this.organizationId = null;
            return this;
        }

        public OrganizationBuilder asExisting() {
            this.isNew = false;
            this.organizationId = OrganizationId.of(DEFAULT_ORGANIZATION_ID);
            return this;
        }

        public OrganizationBuilder organizationId(OrganizationId organizationId) {
            this.organizationId = organizationId;
            this.isNew = (organizationId == null);
            return this;
        }

        public OrganizationBuilder organizationName(OrganizationName organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public OrganizationBuilder organizationName(String organizationName) {
            this.organizationName = OrganizationName.of(organizationName);
            return this;
        }

        public OrganizationBuilder tenantId(TenantId tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public OrganizationBuilder tenantId(Long tenantId) {
            this.tenantId = TenantId.of(tenantId);
            return this;
        }

        public OrganizationBuilder organizationStatus(OrganizationStatus organizationStatus) {
            this.organizationStatus = organizationStatus;
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

        public OrganizationBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OrganizationBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public OrganizationBuilder clock(Clock clock) {
            this.createdAt = clock.now();
            this.updatedAt = clock.now();
            return this;
        }

        public Organization build() {
            if (isNew) {
                return Organization.forNew(organizationName, tenantId, DEFAULT_CLOCK);
            }
            return Organization.of(
                    organizationId,
                    organizationName,
                    tenantId,
                    organizationStatus,
                    createdAt,
                    updatedAt
            );
        }
    }
}
