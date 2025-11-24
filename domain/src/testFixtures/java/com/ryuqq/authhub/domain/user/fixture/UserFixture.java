package com.ryuqq.authhub.domain.user.fixture;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.vo.OrganizationId;
import com.ryuqq.authhub.domain.tenant.vo.TenantId;
import com.ryuqq.authhub.domain.user.UserStatus;
import com.ryuqq.authhub.domain.user.UserType;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.vo.UserId;
import com.ryuqq.authhub.domain.user.vo.fixture.UserIdFixture;

import java.time.Instant;

/**
 * User Aggregate Test Fixture
 * Object Mother 패턴을 사용한 테스트 데이터 생성
 */
public class UserFixture {

    private static final TenantId DEFAULT_TENANT_ID = TenantId.of(1L);
    private static final OrganizationId DEFAULT_ORGANIZATION_ID = OrganizationId.of(100L);
    private static final UserType DEFAULT_USER_TYPE = UserType.PUBLIC;
    private static final UserStatus DEFAULT_USER_STATUS = UserStatus.ACTIVE;
    private static final Clock DEFAULT_CLOCK = () -> Instant.parse("2025-11-24T00:00:00Z");

    /**
     * 기본 User 생성
     * @return User 인스턴스
     */
    public static User aUser() {
        return User.of(
                UserIdFixture.aUserId(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                DEFAULT_USER_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now()
        );
    }

    /**
     * 특정 UserId로 User 생성
     * @param userId UserId
     * @return User 인스턴스
     */
    public static User aUser(UserId userId) {
        return User.of(
                userId,
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                DEFAULT_USER_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now()
        );
    }

    /**
     * 특정 TenantId로 User 생성
     * @param tenantId Tenant ID
     * @return User 인스턴스
     */
    public static User aUserWithTenantId(TenantId tenantId) {
        return User.of(
                UserIdFixture.aUserId(),
                tenantId,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                DEFAULT_USER_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now()
        );
    }

    /**
     * 특정 UserStatus로 User 생성
     * @param userStatus UserStatus
     * @return User 인스턴스
     */
    public static User aUserWithStatus(UserStatus userStatus) {
        return User.of(
                UserIdFixture.aUserId(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                userStatus,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now()
        );
    }

    /**
     * INTERNAL 유형의 User 생성
     * @return User 인스턴스
     */
    public static User anInternalUser() {
        return User.of(
                UserIdFixture.aUserId(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                UserType.INTERNAL,
                DEFAULT_USER_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now()
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
