package com.ryuqq.authhub.domain.role.fixture;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleDescription;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Role 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RoleFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final UUID DEFAULT_ROLE_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789111");
    private static final UUID DEFAULT_TENANT_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789abc");

    private RoleFixture() {}

    /** 기본 커스텀 Role 생성 (ID 할당됨, ORGANIZATION 범위) */
    public static Role create() {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                RoleName.of("TEST_ROLE"),
                RoleDescription.of("Test role description"),
                RoleScope.ORGANIZATION,
                RoleType.CUSTOM,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 시스템 GLOBAL 역할 생성 */
    public static Role createSystemGlobal() {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_UUID),
                null,
                RoleName.of("SUPER_ADMIN"),
                RoleDescription.of("System super admin role"),
                RoleScope.GLOBAL,
                RoleType.SYSTEM,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 시스템 TENANT 역할 생성 */
    public static Role createSystemTenant() {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                RoleName.of("TENANT_ADMIN"),
                RoleDescription.of("System tenant admin role"),
                RoleScope.TENANT,
                RoleType.SYSTEM,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 TENANT 범위 역할 생성 */
    public static Role createCustomTenant() {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                RoleName.of("CUSTOM_TENANT_ROLE"),
                RoleDescription.of("Custom tenant role"),
                RoleScope.TENANT,
                RoleType.CUSTOM,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 ORGANIZATION 범위 역할 생성 */
    public static Role createCustomOrganization() {
        return create();
    }

    /** 지정된 이름으로 Role 생성 */
    public static Role createWithName(String name) {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                RoleName.of(name),
                RoleDescription.of("Role with custom name"),
                RoleScope.ORGANIZATION,
                RoleType.CUSTOM,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 테넌트로 Role 생성 */
    public static Role createWithTenant(UUID tenantUUID) {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_UUID),
                TenantId.of(tenantUUID),
                RoleName.of("TEST_ROLE"),
                RoleDescription.of("Test role description"),
                RoleScope.ORGANIZATION,
                RoleType.CUSTOM,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 새로운 커스텀 ORGANIZATION Role 생성 (ID 미할당) */
    public static Role createNew() {
        ClockHolder clockHolder = () -> Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
        return Role.createCustomOrganization(
                TenantId.of(DEFAULT_TENANT_UUID),
                RoleName.of("NEW_ROLE"),
                RoleDescription.of("New role description"),
                clockHolder.clock());
    }

    /** 새로운 커스텀 TENANT Role 생성 (ID 미할당) */
    public static Role createNewTenant() {
        ClockHolder clockHolder = () -> Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
        return Role.createCustomTenant(
                TenantId.of(DEFAULT_TENANT_UUID),
                RoleName.of("NEW_TENANT_ROLE"),
                RoleDescription.of("New tenant role description"),
                clockHolder.clock());
    }

    /** 삭제된 Role 생성 */
    public static Role createDeleted() {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                RoleName.of("DELETED_ROLE"),
                RoleDescription.of("Deleted role"),
                RoleScope.ORGANIZATION,
                RoleType.CUSTOM,
                true,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 테스트용 고정 ClockHolder 반환 */
    public static ClockHolder fixedClockHolder() {
        return () -> Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
    }

    /** 테스트용 고정 Clock 반환 */
    public static Clock fixedClock() {
        return fixedClockHolder().clock();
    }

    /** 기본 RoleId 반환 */
    public static RoleId defaultId() {
        return RoleId.of(DEFAULT_ROLE_UUID);
    }

    /** 기본 Role UUID 반환 */
    public static UUID defaultUUID() {
        return DEFAULT_ROLE_UUID;
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
