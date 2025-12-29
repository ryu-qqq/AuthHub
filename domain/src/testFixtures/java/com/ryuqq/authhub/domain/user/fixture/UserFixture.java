package com.ryuqq.authhub.domain.user.fixture;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * User 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final UUID DEFAULT_USER_UUID =
            UUID.fromString("01941234-5678-7000-8000-000000000001");
    private static final UUID DEFAULT_TENANT_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789abc");
    private static final UUID DEFAULT_ORG_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789def");
    private static final String DEFAULT_IDENTIFIER = "user@example.com";
    private static final String DEFAULT_PHONE_NUMBER = "01012345678";
    private static final String DEFAULT_HASHED_PASSWORD = "hashed_password_123";

    private UserFixture() {}

    /** 기본 User 생성 (ID 할당됨, ACTIVE) */
    public static User create() {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                OrganizationId.of(DEFAULT_ORG_UUID),
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_HASHED_PASSWORD,
                UserStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 식별자로 User 생성 */
    public static User createWithIdentifier(String identifier) {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                OrganizationId.of(DEFAULT_ORG_UUID),
                identifier,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_HASHED_PASSWORD,
                UserStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 핸드폰 번호로 User 생성 */
    public static User createWithPhoneNumber(String phoneNumber) {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                OrganizationId.of(DEFAULT_ORG_UUID),
                DEFAULT_IDENTIFIER,
                phoneNumber,
                DEFAULT_HASHED_PASSWORD,
                UserStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 Organization으로 User 생성 */
    public static User createWithOrganization(UUID organizationUUID) {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                OrganizationId.of(organizationUUID),
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_HASHED_PASSWORD,
                UserStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 Tenant로 User 생성 */
    public static User createWithTenant(UUID tenantUUID) {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_UUID),
                TenantId.of(tenantUUID),
                OrganizationId.of(DEFAULT_ORG_UUID),
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_HASHED_PASSWORD,
                UserStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 새로운 User 생성 (create 팩토리 사용) */
    public static User createNew() {
        return User.create(
                UserId.forNew(DEFAULT_USER_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                OrganizationId.of(DEFAULT_ORG_UUID),
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_HASHED_PASSWORD,
                fixedClock());
    }

    /** 지정된 상태로 User 생성 */
    public static User createWithStatus(UserStatus status) {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_UUID),
                TenantId.of(DEFAULT_TENANT_UUID),
                OrganizationId.of(DEFAULT_ORG_UUID),
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_HASHED_PASSWORD,
                status,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 User 생성 */
    public static User createInactive() {
        return createWithStatus(UserStatus.INACTIVE);
    }

    /** 테스트용 고정 ClockHolder 반환 */
    public static ClockHolder fixedClockHolder() {
        return () -> Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
    }

    /** 테스트용 고정 Clock 반환 */
    public static Clock fixedClock() {
        return fixedClockHolder().clock();
    }

    /** 기본 UserId 반환 */
    public static UserId defaultId() {
        return UserId.of(DEFAULT_USER_UUID);
    }

    /** 기본 User UUID 반환 */
    public static UUID defaultUUID() {
        return DEFAULT_USER_UUID;
    }

    /** 기본 TenantId 반환 */
    public static TenantId defaultTenantId() {
        return TenantId.of(DEFAULT_TENANT_UUID);
    }

    /** 기본 Tenant UUID 반환 */
    public static UUID defaultTenantUUID() {
        return DEFAULT_TENANT_UUID;
    }

    /** 기본 OrganizationId 반환 */
    public static OrganizationId defaultOrganizationId() {
        return OrganizationId.of(DEFAULT_ORG_UUID);
    }

    /** 기본 Organization UUID 반환 */
    public static UUID defaultOrganizationUUID() {
        return DEFAULT_ORG_UUID;
    }

    /** 기본 식별자 반환 */
    public static String defaultIdentifier() {
        return DEFAULT_IDENTIFIER;
    }

    /** 기본 해시된 비밀번호 반환 */
    public static String defaultHashedPassword() {
        return DEFAULT_HASHED_PASSWORD;
    }

    /** 기본 핸드폰 번호 반환 */
    public static String defaultPhoneNumber() {
        return DEFAULT_PHONE_NUMBER;
    }
}
