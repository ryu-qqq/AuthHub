package com.ryuqq.authhub.domain.organization.fixture;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.Instant;
import java.util.UUID;

/**
 * Organization 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OrganizationFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String DEFAULT_ORG_ID = "01941234-5678-7000-8000-123456789def";
    private static final String DEFAULT_TENANT_ID = "01941234-5678-7000-8000-123456789abc";

    private OrganizationFixture() {}

    /** 기본 Organization 생성 (ID 할당됨, ACTIVE) */
    public static Organization create() {
        return Organization.reconstitute(
                OrganizationId.of(DEFAULT_ORG_ID),
                TenantId.of(DEFAULT_TENANT_ID),
                OrganizationName.of("Test Organization"),
                OrganizationStatus.ACTIVE,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 이름으로 Organization 생성 */
    public static Organization createWithName(String name) {
        return Organization.reconstitute(
                OrganizationId.of(DEFAULT_ORG_ID),
                TenantId.of(DEFAULT_TENANT_ID),
                OrganizationName.of(name),
                OrganizationStatus.ACTIVE,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 테넌트로 Organization 생성 */
    public static Organization createWithTenant(String tenantId) {
        return Organization.reconstitute(
                OrganizationId.of(DEFAULT_ORG_ID),
                TenantId.of(tenantId),
                OrganizationName.of("Test Organization"),
                OrganizationStatus.ACTIVE,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 새로운 Organization 생성 (ID 할당됨) */
    public static Organization createNew() {
        OrganizationId organizationId = OrganizationId.forNew(UUID.randomUUID().toString());
        return Organization.create(
                organizationId,
                TenantId.of(DEFAULT_TENANT_ID),
                OrganizationName.of("New Organization"),
                FIXED_TIME);
    }

    /** 지정된 상태로 Organization 생성 */
    public static Organization createWithStatus(OrganizationStatus status) {
        return Organization.reconstitute(
                OrganizationId.of(DEFAULT_ORG_ID),
                TenantId.of(DEFAULT_TENANT_ID),
                OrganizationName.of("Test Organization"),
                status,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 Organization 생성 */
    public static Organization createInactive() {
        return createWithStatus(OrganizationStatus.INACTIVE);
    }

    /** 삭제된 Organization 생성 */
    public static Organization createDeleted() {
        return Organization.reconstitute(
                OrganizationId.of(DEFAULT_ORG_ID),
                TenantId.of(DEFAULT_TENANT_ID),
                OrganizationName.of("Test Organization"),
                OrganizationStatus.INACTIVE,
                DeletionStatus.deletedAt(FIXED_TIME),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 OrganizationId 반환 */
    public static OrganizationId defaultId() {
        return OrganizationId.of(DEFAULT_ORG_ID);
    }

    /** 기본 Organization ID 문자열 반환 */
    public static String defaultIdString() {
        return DEFAULT_ORG_ID;
    }

    /** 기본 TenantId 반환 */
    public static TenantId defaultTenantId() {
        return TenantId.of(DEFAULT_TENANT_ID);
    }

    /** 기본 Tenant ID 문자열 반환 */
    public static String defaultTenantIdString() {
        return DEFAULT_TENANT_ID;
    }
}
