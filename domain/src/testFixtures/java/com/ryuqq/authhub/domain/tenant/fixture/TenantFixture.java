package com.ryuqq.authhub.domain.tenant.fixture;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Tenant 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final UUID DEFAULT_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789abc");

    private TenantFixture() {}

    /** 기본 Tenant 생성 (ID 할당됨) */
    public static Tenant create() {
        return Tenant.reconstitute(
                TenantId.of(DEFAULT_UUID),
                TenantName.of("Test Tenant"),
                TenantStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 이름으로 Tenant 생성 */
    public static Tenant createWithName(String name) {
        return Tenant.reconstitute(
                TenantId.of(DEFAULT_UUID),
                TenantName.of(name),
                TenantStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 새로운 Tenant 생성 (ID 할당됨) */
    public static Tenant createNew() {
        ClockHolder clockHolder = () -> Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
        TenantId tenantId = TenantId.forNew(UUID.randomUUID());
        return Tenant.create(tenantId, TenantName.of("New Tenant"), clockHolder.clock());
    }

    /** 지정된 상태로 Tenant 생성 */
    public static Tenant createWithStatus(TenantStatus status) {
        return Tenant.reconstitute(
                TenantId.of(DEFAULT_UUID),
                TenantName.of("Test Tenant"),
                status,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 Tenant 생성 */
    public static Tenant createInactive() {
        return createWithStatus(TenantStatus.INACTIVE);
    }

    /** 삭제된 Tenant 생성 */
    public static Tenant createDeleted() {
        return createWithStatus(TenantStatus.DELETED);
    }

    /** 테스트용 고정 ClockHolder 반환 */
    public static ClockHolder fixedClockHolder() {
        return () -> Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
    }

    /** 테스트용 고정 Clock 반환 */
    public static Clock fixedClock() {
        return fixedClockHolder().clock();
    }

    /** 기본 TenantId 반환 */
    public static TenantId defaultId() {
        return TenantId.of(DEFAULT_UUID);
    }

    /** 기본 UUID 반환 */
    public static UUID defaultUUID() {
        return DEFAULT_UUID;
    }
}
