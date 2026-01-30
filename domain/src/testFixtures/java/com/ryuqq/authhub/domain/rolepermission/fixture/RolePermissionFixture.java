package com.ryuqq.authhub.domain.rolepermission.fixture;

import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.id.RolePermissionId;
import java.time.Instant;

/**
 * RolePermission 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RolePermissionFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_ROLE_PERMISSION_ID = 1L;
    private static final Long DEFAULT_ROLE_ID = 1L;
    private static final Long DEFAULT_PERMISSION_ID = 1L;

    private RolePermissionFixture() {}

    /** 기본 RolePermission 생성 (ID 할당됨) */
    public static RolePermission create() {
        return RolePermission.reconstitute(
                RolePermissionId.of(DEFAULT_ROLE_PERMISSION_ID),
                RoleId.of(DEFAULT_ROLE_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                FIXED_TIME);
    }

    /** 지정된 역할 ID로 RolePermission 생성 */
    public static RolePermission createWithRole(Long roleId) {
        return RolePermission.reconstitute(
                RolePermissionId.of(DEFAULT_ROLE_PERMISSION_ID),
                RoleId.of(roleId),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                FIXED_TIME);
    }

    /** 지정된 권한 ID로 RolePermission 생성 */
    public static RolePermission createWithPermission(Long permissionId) {
        return RolePermission.reconstitute(
                RolePermissionId.of(DEFAULT_ROLE_PERMISSION_ID),
                RoleId.of(DEFAULT_ROLE_ID),
                PermissionId.of(permissionId),
                FIXED_TIME);
    }

    /** 지정된 역할 ID와 권한 ID로 RolePermission 생성 */
    public static RolePermission createWithRoleAndPermission(Long roleId, Long permissionId) {
        return RolePermission.reconstitute(
                RolePermissionId.of(DEFAULT_ROLE_PERMISSION_ID),
                RoleId.of(roleId),
                PermissionId.of(permissionId),
                FIXED_TIME);
    }

    /** 새로운 RolePermission 생성 (ID 없음) */
    public static RolePermission createNew() {
        return RolePermission.create(
                RoleId.of(DEFAULT_ROLE_ID), PermissionId.of(DEFAULT_PERMISSION_ID), FIXED_TIME);
    }

    /** 새로운 RolePermission 생성 (지정된 역할 ID와 권한 ID, ID 없음) */
    public static RolePermission createNewWithRoleAndPermission(Long roleId, Long permissionId) {
        return RolePermission.create(RoleId.of(roleId), PermissionId.of(permissionId), FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 RolePermissionId 반환 */
    public static RolePermissionId defaultId() {
        return RolePermissionId.of(DEFAULT_ROLE_PERMISSION_ID);
    }

    /** 기본 RolePermission ID 값 반환 */
    public static Long defaultIdValue() {
        return DEFAULT_ROLE_PERMISSION_ID;
    }

    /** 기본 RoleId 반환 */
    public static RoleId defaultRoleId() {
        return RoleId.of(DEFAULT_ROLE_ID);
    }

    /** 기본 Role ID 값 반환 */
    public static Long defaultRoleIdValue() {
        return DEFAULT_ROLE_ID;
    }

    /** 기본 PermissionId 반환 */
    public static PermissionId defaultPermissionId() {
        return PermissionId.of(DEFAULT_PERMISSION_ID);
    }

    /** 기본 Permission ID 값 반환 */
    public static Long defaultPermissionIdValue() {
        return DEFAULT_PERMISSION_ID;
    }
}
