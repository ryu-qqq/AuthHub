package com.ryuqq.authhub.adapter.out.persistence.rolepermission.fixture;

import com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.RolePermissionJpaEntity;
import java.time.Instant;

/**
 * RolePermissionJpaEntity 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RolePermissionJpaEntityFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_ROLE_PERMISSION_ID = 1L;
    private static final Long DEFAULT_ROLE_ID = 1L;
    private static final Long DEFAULT_PERMISSION_ID = 1L;

    private RolePermissionJpaEntityFixture() {}

    /** 기본 RolePermissionJpaEntity 생성 */
    public static RolePermissionJpaEntity create() {
        return RolePermissionJpaEntity.of(
                DEFAULT_ROLE_PERMISSION_ID, DEFAULT_ROLE_ID, DEFAULT_PERMISSION_ID, FIXED_TIME);
    }

    /** 신규 RolePermissionJpaEntity 생성 */
    public static RolePermissionJpaEntity createNew() {
        return RolePermissionJpaEntity.create(DEFAULT_ROLE_ID, DEFAULT_PERMISSION_ID, FIXED_TIME);
    }

    /** 지정된 roleId와 permissionId로 생성 */
    public static RolePermissionJpaEntity createWith(Long roleId, Long permissionId) {
        return RolePermissionJpaEntity.of(
                DEFAULT_ROLE_PERMISSION_ID, roleId, permissionId, FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 RolePermission ID 반환 */
    public static Long defaultRolePermissionId() {
        return DEFAULT_ROLE_PERMISSION_ID;
    }

    /** 기본 Role ID 반환 */
    public static Long defaultRoleId() {
        return DEFAULT_ROLE_ID;
    }

    /** 기본 Permission ID 반환 */
    public static Long defaultPermissionId() {
        return DEFAULT_PERMISSION_ID;
    }
}
