package com.ryuqq.authhub.domain.organization.fixture;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Organization 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OrganizationFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final UUID DEFAULT_ORG_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789def");
    private static final UUID DEFAULT_TENANT_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789abc");

    private OrganizationFixture() {}

    /** 기본 Organization 생성 (ID 할당됨, ACTIVE) */
    public static Organization create() {
        return Organization.reconstitute(
                OrganizationId.of(DEFAULT_ORG_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                OrganizationName.of("Test Organization"),
                OrganizationStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 이름으로 Organization 생성 */
    public static Organization createWithName(String name) {
        return Organization.reconstitute(
                OrganizationId.of(DEFAULT_ORG_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                OrganizationName.of(name),
                OrganizationStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 테넌트로 Organization 생성 */
    public static Organization createWithTenant(UUID tenantUUID) {
        return Organization.reconstitute(
                OrganizationId.of(DEFAULT_ORG_UUID),
                TenantId.of(tenantUUID),
                OrganizationName.of("Test Organization"),
                OrganizationStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 새로운 Organization 생성 (ID 미할당) */
    public static Organization createNew() {
        ClockHolder clockHolder = () -> Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
        return Organization.create(
                TenantId.of(DEFAULT_TENANT_UUID),
                OrganizationName.of("New Organization"),
                clockHolder.clock());
    }

    /** 지정된 상태로 Organization 생성 */
    public static Organization createWithStatus(OrganizationStatus status) {
        return Organization.reconstitute(
                OrganizationId.of(DEFAULT_ORG_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                OrganizationName.of("Test Organization"),
                status,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 Organization 생성 */
    public static Organization createInactive() {
        return createWithStatus(OrganizationStatus.INACTIVE);
    }

    /** 삭제된 Organization 생성 */
    public static Organization createDeleted() {
        return createWithStatus(OrganizationStatus.DELETED);
    }

    /** 테스트용 고정 ClockHolder 반환 */
    public static ClockHolder fixedClockHolder() {
        return () -> Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
    }

    /** 테스트용 고정 Clock 반환 */
    public static Clock fixedClock() {
        return fixedClockHolder().clock();
    }

    /** 기본 OrganizationId 반환 */
    public static OrganizationId defaultId() {
        return OrganizationId.of(DEFAULT_ORG_UUID);
    }

    /** 기본 Organization UUID 반환 */
    public static UUID defaultUUID() {
        return DEFAULT_ORG_UUID;
    }

    /** 기본 TenantId 반환 */
    public static TenantId defaultTenantId() {
        return TenantId.of(DEFAULT_TENANT_UUID);
    }

    /** 기본 Tenant UUID 반환 */
    public static UUID defaultTenantUUID() {
        return DEFAULT_TENANT_UUID;
    }
}
