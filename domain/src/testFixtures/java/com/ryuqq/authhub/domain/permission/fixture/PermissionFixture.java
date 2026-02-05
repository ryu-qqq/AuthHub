package com.ryuqq.authhub.domain.permission.fixture;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.time.Instant;

// Note: PermissionId import kept for defaultId() method

/**
 * Permission 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_PERMISSION_ID = 1L;
    private static final String DEFAULT_RESOURCE = "user";
    private static final String DEFAULT_ACTION = "read";
    private static final String DEFAULT_PERMISSION_KEY = "user:read";
    private static final String DEFAULT_DESCRIPTION = "사용자 조회 권한";

    private PermissionFixture() {}

    /** 기본 커스텀 권한 생성 (ID 할당됨) */
    public static Permission create() {
        return Permission.reconstitute(
                DEFAULT_PERMISSION_ID,
                null,
                DEFAULT_PERMISSION_KEY,
                DEFAULT_RESOURCE,
                DEFAULT_ACTION,
                DEFAULT_DESCRIPTION,
                PermissionType.CUSTOM,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 시스템 권한 생성 */
    public static Permission createSystemPermission() {
        return Permission.reconstitute(
                DEFAULT_PERMISSION_ID,
                null,
                DEFAULT_PERMISSION_KEY,
                DEFAULT_RESOURCE,
                DEFAULT_ACTION,
                DEFAULT_DESCRIPTION,
                PermissionType.SYSTEM,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 권한 생성 */
    public static Permission createCustomPermission() {
        return Permission.reconstitute(
                DEFAULT_PERMISSION_ID,
                null,
                DEFAULT_PERMISSION_KEY,
                DEFAULT_RESOURCE,
                DEFAULT_ACTION,
                DEFAULT_DESCRIPTION,
                PermissionType.CUSTOM,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 리소스와 액션으로 권한 생성 */
    public static Permission createWithResourceAndAction(String resource, String action) {
        String permissionKey = resource + ":" + action;
        return Permission.reconstitute(
                DEFAULT_PERMISSION_ID,
                null,
                permissionKey,
                resource,
                action,
                resource + " " + action + " 권한",
                PermissionType.CUSTOM,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 권한 키로 시스템 권한 생성 */
    public static Permission createSystemWithKey(String resource, String action) {
        String permissionKey = resource + ":" + action;
        return Permission.reconstitute(
                DEFAULT_PERMISSION_ID,
                null,
                permissionKey,
                resource,
                action,
                resource + " " + action + " 권한",
                PermissionType.SYSTEM,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 새로운 시스템 권한 생성 (ID 없음) */
    public static Permission createNewSystemPermission() {
        return Permission.createSystem(null, "resource", "action", "새 시스템 권한", FIXED_TIME);
    }

    /** 새로운 커스텀 권한 생성 (ID 없음) */
    public static Permission createNewCustomPermission() {
        return Permission.createCustom(null, "resource", "action", "새 커스텀 권한", FIXED_TIME);
    }

    /** 새로운 커스텀 권한 생성 (지정된 리소스/액션, ID 없음) */
    public static Permission createNewCustomWithResourceAndAction(String resource, String action) {
        return Permission.createCustom(
                null, resource, action, resource + " " + action + " 권한", FIXED_TIME);
    }

    /** 삭제된 권한 생성 */
    public static Permission createDeleted() {
        return Permission.reconstitute(
                DEFAULT_PERMISSION_ID,
                null,
                DEFAULT_PERMISSION_KEY,
                DEFAULT_RESOURCE,
                DEFAULT_ACTION,
                DEFAULT_DESCRIPTION,
                PermissionType.CUSTOM,
                DeletionStatus.deletedAt(FIXED_TIME),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 PermissionId 반환 */
    public static PermissionId defaultId() {
        return PermissionId.of(DEFAULT_PERMISSION_ID);
    }

    /** 기본 Permission ID 값 반환 */
    public static Long defaultIdValue() {
        return DEFAULT_PERMISSION_ID;
    }

    /** 기본 권한 키 반환 */
    public static String defaultPermissionKey() {
        return DEFAULT_PERMISSION_KEY;
    }

    /** 기본 리소스 반환 */
    public static String defaultResource() {
        return DEFAULT_RESOURCE;
    }

    /** 기본 액션 반환 */
    public static String defaultAction() {
        return DEFAULT_ACTION;
    }
}
