package com.ryuqq.authhub.adapter.out.persistence.userrole.fixture;

import com.ryuqq.authhub.adapter.out.persistence.userrole.entity.UserRoleJpaEntity;
import java.time.Instant;

/**
 * UserRoleJpaEntity 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserRoleJpaEntityFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_USER_ROLE_ID = 1L;
    private static final String DEFAULT_USER_ID = "01941234-5678-7000-8000-123456789001";
    private static final Long DEFAULT_ROLE_ID = 1L;

    private UserRoleJpaEntityFixture() {}

    /** 기본 UserRoleJpaEntity 생성 */
    public static UserRoleJpaEntity create() {
        return UserRoleJpaEntity.of(
                DEFAULT_USER_ROLE_ID, DEFAULT_USER_ID, DEFAULT_ROLE_ID, FIXED_TIME, FIXED_TIME);
    }

    /** 신규 UserRoleJpaEntity 생성 */
    public static UserRoleJpaEntity createNew() {
        return UserRoleJpaEntity.forNew(DEFAULT_USER_ID, DEFAULT_ROLE_ID, FIXED_TIME);
    }

    /** 지정된 userId와 roleId로 생성 */
    public static UserRoleJpaEntity createWith(String userId, Long roleId) {
        return UserRoleJpaEntity.of(DEFAULT_USER_ROLE_ID, userId, roleId, FIXED_TIME, FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 UserRole ID 반환 */
    public static Long defaultUserRoleId() {
        return DEFAULT_USER_ROLE_ID;
    }

    /** 기본 User ID (UUID) 반환 */
    public static String defaultUserId() {
        return DEFAULT_USER_ID;
    }

    /** 기본 Role ID 반환 */
    public static Long defaultRoleId() {
        return DEFAULT_ROLE_ID;
    }
}
