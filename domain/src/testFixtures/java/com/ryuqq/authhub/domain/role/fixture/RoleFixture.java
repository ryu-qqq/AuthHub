package com.ryuqq.authhub.domain.role.fixture;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.Instant;

/**
 * Role 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RoleFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_ROLE_ID = 1L;
    private static final String DEFAULT_TENANT_ID = "01941234-5678-7000-8000-123456789abc";
    private static final String DEFAULT_ROLE_NAME = "TEST_ROLE";
    private static final String DEFAULT_DISPLAY_NAME = "테스트 역할";
    private static final String DEFAULT_DESCRIPTION = "테스트용 역할입니다";

    private RoleFixture() {}

    /** 기본 커스텀 역할 생성 (ID 할당됨, Global) */
    public static Role create() {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_ID),
                null,
                null,
                RoleName.of(DEFAULT_ROLE_NAME),
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DESCRIPTION,
                RoleType.CUSTOM,
                RoleScope.GLOBAL,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 시스템 역할 생성 (Global) */
    public static Role createSystemRole() {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_ID),
                null,
                null,
                RoleName.of("SUPER_ADMIN"),
                "슈퍼 관리자",
                "시스템 전체 관리 권한",
                RoleType.SYSTEM,
                RoleScope.GLOBAL,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 이름으로 시스템 역할 생성 */
    public static Role createSystemRoleWithName(String name) {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_ID),
                null,
                null,
                RoleName.of(name),
                name + " 표시명",
                name + " 설명",
                RoleType.SYSTEM,
                RoleScope.GLOBAL,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 역할 생성 (Global) */
    public static Role createCustomRole() {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_ID),
                null,
                null,
                RoleName.of("CUSTOM_ROLE"),
                "커스텀 역할",
                "사용자 정의 역할",
                RoleType.CUSTOM,
                RoleScope.GLOBAL,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 이름으로 커스텀 역할 생성 */
    public static Role createCustomRoleWithName(String name) {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_ID),
                null,
                null,
                RoleName.of(name),
                name + " 표시명",
                name + " 설명",
                RoleType.CUSTOM,
                RoleScope.GLOBAL,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 테넌트 전용 커스텀 역할 생성 */
    public static Role createTenantRole() {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_ID),
                TenantId.of(DEFAULT_TENANT_ID),
                null,
                RoleName.of("TENANT_CUSTOM_ROLE"),
                "테넌트 커스텀 역할",
                "특정 테넌트 전용 역할",
                RoleType.CUSTOM,
                RoleScope.TENANT,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 테넌트로 커스텀 역할 생성 */
    public static Role createTenantRoleWithTenant(String tenantId) {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_ID),
                TenantId.of(tenantId),
                null,
                RoleName.of("TENANT_CUSTOM_ROLE"),
                "테넌트 커스텀 역할",
                "특정 테넌트 전용 역할",
                RoleType.CUSTOM,
                RoleScope.TENANT,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 새로운 시스템 역할 생성 (ID 없음) */
    public static Role createNewSystemRole() {
        return Role.createSystem(
                RoleName.of("NEW_SYSTEM_ROLE"), "새 시스템 역할", "새로 생성된 시스템 역할", FIXED_TIME);
    }

    /** 새로운 커스텀 역할 생성 (ID 없음) */
    public static Role createNewCustomRole() {
        return Role.createCustom(
                RoleName.of("NEW_CUSTOM_ROLE"), "새 커스텀 역할", "새로 생성된 커스텀 역할", FIXED_TIME);
    }

    /** 새로운 테넌트 커스텀 역할 생성 (ID 없음) */
    public static Role createNewTenantCustomRole() {
        return Role.createTenantCustom(
                TenantId.of(DEFAULT_TENANT_ID),
                RoleName.of("NEW_TENANT_ROLE"),
                "새 테넌트 역할",
                "새로 생성된 테넌트 전용 역할",
                FIXED_TIME);
    }

    /** 삭제된 역할 생성 */
    public static Role createDeleted() {
        return Role.reconstitute(
                RoleId.of(DEFAULT_ROLE_ID),
                null,
                null,
                RoleName.of(DEFAULT_ROLE_NAME),
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DESCRIPTION,
                RoleType.CUSTOM,
                RoleScope.GLOBAL,
                DeletionStatus.deletedAt(FIXED_TIME),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 RoleId 반환 */
    public static RoleId defaultId() {
        return RoleId.of(DEFAULT_ROLE_ID);
    }

    /** 기본 Role ID 값 반환 */
    public static Long defaultIdValue() {
        return DEFAULT_ROLE_ID;
    }

    /** 기본 TenantId 반환 */
    public static TenantId defaultTenantId() {
        return TenantId.of(DEFAULT_TENANT_ID);
    }

    /** 기본 Tenant ID 문자열 반환 */
    public static String defaultTenantIdString() {
        return DEFAULT_TENANT_ID;
    }

    /** 기본 RoleName 반환 */
    public static RoleName defaultRoleName() {
        return RoleName.of(DEFAULT_ROLE_NAME);
    }
}
