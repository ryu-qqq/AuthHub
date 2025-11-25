package com.ryuqq.authhub.domain.user.fixture;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.identifier.fixture.UserIdFixture;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import java.time.Instant;

/** User Aggregate Test Fixture Object Mother + Builder 패턴을 조합한 테스트 데이터 생성 */
public class UserFixture {

    private static final TenantId DEFAULT_TENANT_ID = TenantId.of(1L);
    private static final OrganizationId DEFAULT_ORGANIZATION_ID = OrganizationId.of(100L);
    private static final UserType DEFAULT_USER_TYPE = UserType.PUBLIC;
    private static final UserStatus DEFAULT_USER_STATUS = UserStatus.ACTIVE;
    private static final Clock DEFAULT_CLOCK = () -> Instant.parse("2025-11-24T00:00:00Z");

    /**
     * 기본 새 User 생성 (forNew 사용)
     *
     * @return User 인스턴스
     */
    public static User aNewUser() {
        return User.forNew(
                DEFAULT_TENANT_ID, DEFAULT_ORGANIZATION_ID, DEFAULT_USER_TYPE, DEFAULT_CLOCK);
    }

    /**
     * 기본 기존 User 생성 (reconstitute 사용)
     *
     * @return User 인스턴스
     */
    public static User aUser() {
        return User.reconstitute(
                UserIdFixture.aUserId(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                DEFAULT_USER_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * 특정 UserId로 User 생성
     *
     * @param userId UserId
     * @return User 인스턴스
     */
    public static User aUser(UserId userId) {
        return User.reconstitute(
                userId,
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                DEFAULT_USER_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * 특정 TenantId로 User 생성
     *
     * @param tenantId Tenant ID
     * @return User 인스턴스
     */
    public static User aUserWithTenantId(TenantId tenantId) {
        return User.reconstitute(
                UserIdFixture.aUserId(),
                tenantId,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                DEFAULT_USER_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * 특정 UserStatus로 User 생성
     *
     * @param userStatus UserStatus
     * @return User 인스턴스
     */
    public static User aUserWithStatus(UserStatus userStatus) {
        return User.reconstitute(
                UserIdFixture.aUserId(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_USER_TYPE,
                userStatus,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * INTERNAL 유형의 User 생성
     *
     * @return User 인스턴스
     */
    public static User anInternalUser() {
        return User.reconstitute(
                UserIdFixture.aUserId(),
                DEFAULT_TENANT_ID,
                DEFAULT_ORGANIZATION_ID,
                UserType.INTERNAL,
                DEFAULT_USER_STATUS,
                DEFAULT_CLOCK.now(),
                DEFAULT_CLOCK.now());
    }

    /**
     * INACTIVE 상태의 User 생성
     *
     * @return User 인스턴스
     */
    public static User anInactiveUser() {
        return aUserWithStatus(UserStatus.INACTIVE);
    }

    /**
     * SUSPENDED 상태의 User 생성
     *
     * @return User 인스턴스
     */
    public static User aSuspendedUser() {
        return aUserWithStatus(UserStatus.SUSPENDED);
    }

    /**
     * DELETED 상태의 User 생성
     *
     * @return User 인스턴스
     */
    public static User aDeletedUser() {
        return aUserWithStatus(UserStatus.DELETED);
    }

    /**
     * Builder 패턴을 사용한 유연한 User 생성
     *
     * @return UserBuilder 인스턴스
     */
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    /** User Builder 클래스 테스트에서 필요한 필드만 선택적으로 설정 가능 */
    public static class UserBuilder {
        private UserId userId;
        private TenantId tenantId = DEFAULT_TENANT_ID;
        private OrganizationId organizationId = DEFAULT_ORGANIZATION_ID;
        private UserType userType = DEFAULT_USER_TYPE;
        private UserStatus userStatus = DEFAULT_USER_STATUS;
        private Clock clock = DEFAULT_CLOCK;
        private Instant createdAt;
        private Instant updatedAt;
        private boolean isNew;

        public UserBuilder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public UserBuilder tenantId(TenantId tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public UserBuilder organizationId(OrganizationId organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public UserBuilder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public UserBuilder userStatus(UserStatus userStatus) {
            this.userStatus = userStatus;
            return this;
        }

        public UserBuilder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public UserBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserBuilder asNew() {
            this.isNew = true;
            this.userId = null;
            return this;
        }

        public UserBuilder asExisting() {
            this.isNew = false;
            if (this.userId == null) {
                this.userId = UserIdFixture.aUserId();
            }
            return this;
        }

        public UserBuilder withoutOrganization() {
            this.organizationId = null;
            return this;
        }

        public UserBuilder asActive() {
            this.userStatus = UserStatus.ACTIVE;
            return this;
        }

        public UserBuilder asInactive() {
            this.userStatus = UserStatus.INACTIVE;
            return this;
        }

        public UserBuilder asSuspended() {
            this.userStatus = UserStatus.SUSPENDED;
            return this;
        }

        public UserBuilder asDeleted() {
            this.userStatus = UserStatus.DELETED;
            return this;
        }

        public UserBuilder asInternal() {
            this.userType = UserType.INTERNAL;
            return this;
        }

        public UserBuilder asPublic() {
            this.userType = UserType.PUBLIC;
            return this;
        }

        public User build() {
            Instant now = clock.now();
            Instant finalCreatedAt = (createdAt != null) ? createdAt : now;
            Instant finalUpdatedAt = (updatedAt != null) ? updatedAt : now;

            if (isNew) {
                // forNew는 항상 ACTIVE 상태로 생성하므로, 다른 상태가 필요한 경우 User.of() 사용
                if (userStatus == UserStatus.ACTIVE) {
                    return User.forNew(tenantId, organizationId, userType, clock);
                } else {
                    return User.of(
                            null,
                            tenantId,
                            organizationId,
                            userType,
                            userStatus,
                            finalCreatedAt,
                            finalUpdatedAt);
                }
            } else {
                UserId finalUserId = (userId != null) ? userId : UserIdFixture.aUserId();
                return User.reconstitute(
                        finalUserId,
                        tenantId,
                        organizationId,
                        userType,
                        userStatus,
                        finalCreatedAt,
                        finalUpdatedAt);
            }
        }
    }

    private UserFixture() {
        // Utility class
    }
}
