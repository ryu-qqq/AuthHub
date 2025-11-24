package com.ryuqq.authhub.domain.user.fixture;

import com.ryuqq.authhub.domain.user.User;
import com.ryuqq.authhub.domain.user.UserStatus;
import com.ryuqq.authhub.domain.user.UserType;
import com.ryuqq.authhub.domain.user.vo.UserId;
import com.ryuqq.authhub.domain.user.vo.fixture.UserIdFixture;

/**
 * User Aggregate Test Fixture
 * Object Mother 패턴을 사용한 테스트 데이터 생성
 */
public class UserFixture {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Long DEFAULT_ORGANIZATION_ID = 100L;
    private static final UserType DEFAULT_USER_TYPE = UserType.PUBLIC;
    private static final UserStatus DEFAULT_USER_STATUS = UserStatus.ACTIVE;

    /**
     * 기본 User 생성
     * @return User 인스턴스
     */
    public static User aUser() {
        return User.create(
                UserIdFixture.aUserId(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                DEFAULT_USER_STATUS
        );
    }

    /**
     * 특정 UserId로 User 생성
     * @param userId UserId
     * @return User 인스턴스
     */
    public static User aUser(UserId userId) {
        return User.create(
                userId,
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                DEFAULT_USER_STATUS
        );
    }

    /**
     * 특정 TenantId로 User 생성
     * @param tenantId Tenant ID
     * @return User 인스턴스
     */
    public static User aUserWithTenantId(Long tenantId) {
        return User.create(
                UserIdFixture.aUserId(),
                tenantId,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                DEFAULT_USER_STATUS
        );
    }

    /**
     * 특정 UserStatus로 User 생성
     * @param userStatus UserStatus
     * @return User 인스턴스
     */
    public static User aUserWithStatus(UserStatus userStatus) {
        return User.create(
                UserIdFixture.aUserId(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                userStatus
        );
    }

    /**
     * INTERNAL 유형의 User 생성
     * @return User 인스턴스
     */
    public static User anInternalUser() {
        return User.create(
                UserIdFixture.aUserId(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                UserType.INTERNAL,
                DEFAULT_USER_STATUS
        );
    }

    /**
     * INACTIVE 상태의 User 생성
     * @return User 인스턴스
     */
    public static User anInactiveUser() {
        return aUserWithStatus(UserStatus.INACTIVE);
    }

    /**
     * SUSPENDED 상태의 User 생성
     * @return User 인스턴스
     */
    public static User aSuspendedUser() {
        return aUserWithStatus(UserStatus.SUSPENDED);
    }

    /**
     * DELETED 상태의 User 생성
     * @return User 인스턴스
     */
    public static User aDeletedUser() {
        return aUserWithStatus(UserStatus.DELETED);
    }

    private UserFixture() {
        // Utility class
    }
}
