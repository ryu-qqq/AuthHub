package com.ryuqq.authhub.domain.user.fixture;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.Credential;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import com.ryuqq.authhub.domain.user.vo.fixture.CredentialFixture;
import com.ryuqq.authhub.domain.user.vo.fixture.UserProfileFixture;

import java.time.Instant;
import java.util.UUID;

/**
 * UserFixture - User Aggregate 테스트 픽스처
 *
 * <p>테스트에서 User 객체를 쉽게 생성할 수 있도록 도와주는 빌더 패턴 기반 픽스처입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserFixture {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Long DEFAULT_ORGANIZATION_ID = 100L;
    private static final Clock DEFAULT_CLOCK = () -> Instant.parse("2025-11-24T00:00:00Z");

    private UserFixture() {
    }

    // ========== Simple Factory Methods ==========

    public static User aUser() {
        return builder().asExisting().build();
    }

    public static User aUser(UserId userId) {
        return builder().asExisting().userId(userId).build();
    }

    public static User aNewUser() {
        return builder().asNew().build();
    }

    public static User aUserWithStatus(UserStatus status) {
        return builder().asExisting().status(status).build();
    }

    public static User aUserWithTenantId(TenantId tenantId) {
        return builder().asExisting().tenantId(tenantId).build();
    }

    public static User anInternalUser() {
        return builder().asExisting().asInternal().build();
    }

    public static User aPublicUserWithoutOrganization() {
        return builder().asExisting().withoutOrganization().build();
    }

    // ========== Builder ==========

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static final class UserBuilder {

        private UserId userId;
        private TenantId tenantId = TenantId.of(DEFAULT_TENANT_ID);
        private OrganizationId organizationId = OrganizationId.of(DEFAULT_ORGANIZATION_ID);
        private UserType userType = UserType.PUBLIC;
        private UserStatus userStatus = UserStatus.ACTIVE;
        private Credential credential = CredentialFixture.aPhoneCredential();
        private UserProfile profile = UserProfileFixture.aUserProfile();
        private Instant createdAt = DEFAULT_CLOCK.now();
        private Instant updatedAt = DEFAULT_CLOCK.now();
        private boolean isNew = false;

        private UserBuilder() {
        }

        public UserBuilder asNew() {
            this.isNew = true;
            this.userId = null;
            return this;
        }

        public UserBuilder asExisting() {
            this.isNew = false;
            this.userId = UserId.of(UUID.randomUUID());
            return this;
        }

        public UserBuilder userId(UserId userId) {
            this.userId = userId;
            this.isNew = (userId == null);
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

        public UserBuilder withoutOrganization() {
            this.organizationId = null;
            return this;
        }

        public UserBuilder userType(UserType userType) {
            this.userType = userType;
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

        public UserBuilder status(UserStatus status) {
            this.userStatus = status;
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

        public UserBuilder credential(Credential credential) {
            this.credential = credential;
            return this;
        }

        public UserBuilder profile(UserProfile profile) {
            this.profile = profile;
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

        public UserBuilder clock(Clock clock) {
            this.createdAt = clock.now();
            this.updatedAt = clock.now();
            return this;
        }

        public User build() {
            return User.of(
                    userId,
                    tenantId,
                    organizationId,
                    userType,
                    userStatus,
                    credential,
                    profile,
                    createdAt,
                    updatedAt
            );
        }
    }
}
