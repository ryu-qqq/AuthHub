package com.ryuqq.authhub.domain.userrole.fixture;

import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.id.UserRoleId;
import java.time.Instant;

/**
 * UserRole 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserRoleFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_USER_ROLE_ID = 1L;
    private static final String DEFAULT_USER_ID = "01941234-5678-7000-8000-123456789001";
    private static final Long DEFAULT_ROLE_ID = 1L;

    private UserRoleFixture() {}

    /** 기본 UserRole 생성 (ID 할당됨) */
    public static UserRole create() {
        return UserRole.reconstitute(
                UserRoleId.of(DEFAULT_USER_ROLE_ID),
                UserId.of(DEFAULT_USER_ID),
                RoleId.of(DEFAULT_ROLE_ID),
                FIXED_TIME);
    }

    /** 지정된 사용자 ID로 UserRole 생성 */
    public static UserRole createWithUser(String userId) {
        return UserRole.reconstitute(
                UserRoleId.of(DEFAULT_USER_ROLE_ID),
                UserId.of(userId),
                RoleId.of(DEFAULT_ROLE_ID),
                FIXED_TIME);
    }

    /** 지정된 역할 ID로 UserRole 생성 */
    public static UserRole createWithRole(Long roleId) {
        return UserRole.reconstitute(
                UserRoleId.of(DEFAULT_USER_ROLE_ID),
                UserId.of(DEFAULT_USER_ID),
                RoleId.of(roleId),
                FIXED_TIME);
    }

    /** 지정된 사용자 ID와 역할 ID로 UserRole 생성 */
    public static UserRole createWithUserAndRole(String userId, Long roleId) {
        return UserRole.reconstitute(
                UserRoleId.of(DEFAULT_USER_ROLE_ID),
                UserId.of(userId),
                RoleId.of(roleId),
                FIXED_TIME);
    }

    /** 새로운 UserRole 생성 (ID 없음) */
    public static UserRole createNew() {
        return UserRole.create(UserId.of(DEFAULT_USER_ID), RoleId.of(DEFAULT_ROLE_ID), FIXED_TIME);
    }

    /** 새로운 UserRole 생성 (지정된 사용자 ID와 역할 ID, ID 없음) */
    public static UserRole createNewWithUserAndRole(String userId, Long roleId) {
        return UserRole.create(UserId.of(userId), RoleId.of(roleId), FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 UserRoleId 반환 */
    public static UserRoleId defaultId() {
        return UserRoleId.of(DEFAULT_USER_ROLE_ID);
    }

    /** 기본 UserRole ID 값 반환 */
    public static Long defaultIdValue() {
        return DEFAULT_USER_ROLE_ID;
    }

    /** 기본 UserId 반환 */
    public static UserId defaultUserId() {
        return UserId.of(DEFAULT_USER_ID);
    }

    /** 기본 User ID 문자열 반환 */
    public static String defaultUserIdString() {
        return DEFAULT_USER_ID;
    }

    /** 기본 RoleId 반환 */
    public static RoleId defaultRoleId() {
        return RoleId.of(DEFAULT_ROLE_ID);
    }

    /** 기본 Role ID 값 반환 */
    public static Long defaultRoleIdValue() {
        return DEFAULT_ROLE_ID;
    }
}
