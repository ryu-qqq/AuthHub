package com.ryuqq.authhub.adapter.out.persistence.permission.fixture;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.time.Instant;

/**
 * PermissionJpaEntity 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionJpaEntityFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_PERMISSION_ID = 1L;
    private static final String DEFAULT_PERMISSION_KEY = "user:read";
    private static final String DEFAULT_RESOURCE = "user";
    private static final String DEFAULT_ACTION = "read";
    private static final String DEFAULT_DESCRIPTION = "사용자 조회 권한";

    private PermissionJpaEntityFixture() {}

    /** 기본 PermissionJpaEntity 생성 */
    public static PermissionJpaEntity create() {
        return PermissionJpaEntity.of(
                DEFAULT_PERMISSION_ID,
                DEFAULT_PERMISSION_KEY,
                DEFAULT_RESOURCE,
                DEFAULT_ACTION,
                DEFAULT_DESCRIPTION,
                PermissionType.CUSTOM,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 시스템 권한 PermissionJpaEntity 생성 */
    public static PermissionJpaEntity createSystemPermission() {
        return PermissionJpaEntity.of(
                DEFAULT_PERMISSION_ID,
                "system:manage",
                "system",
                "manage",
                "시스템 관리 권한",
                PermissionType.SYSTEM,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 삭제된 PermissionJpaEntity 생성 */
    public static PermissionJpaEntity createDeleted() {
        return PermissionJpaEntity.of(
                DEFAULT_PERMISSION_ID,
                DEFAULT_PERMISSION_KEY,
                DEFAULT_RESOURCE,
                DEFAULT_ACTION,
                DEFAULT_DESCRIPTION,
                PermissionType.CUSTOM,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 resource와 action으로 PermissionJpaEntity 생성 */
    public static PermissionJpaEntity createWithResourceAndAction(String resource, String action) {
        return PermissionJpaEntity.of(
                DEFAULT_PERMISSION_ID,
                resource + ":" + action,
                resource,
                action,
                resource + " " + action + " 권한",
                PermissionType.CUSTOM,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 지정된 ID로 PermissionJpaEntity 생성 */
    public static PermissionJpaEntity createWithId(Long permissionId) {
        return PermissionJpaEntity.of(
                permissionId,
                DEFAULT_PERMISSION_KEY + permissionId,
                DEFAULT_RESOURCE,
                DEFAULT_ACTION,
                DEFAULT_DESCRIPTION,
                PermissionType.CUSTOM,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 Permission ID 반환 */
    public static Long defaultPermissionId() {
        return DEFAULT_PERMISSION_ID;
    }
}
