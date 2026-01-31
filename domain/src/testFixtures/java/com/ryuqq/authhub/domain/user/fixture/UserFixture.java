package com.ryuqq.authhub.domain.user.fixture;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.HashedPassword;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * User 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String DEFAULT_USER_ID = "01941234-5678-7000-8000-123456789001";
    private static final String DEFAULT_ORG_ID = "01941234-5678-7000-8000-123456789def";
    private static final String DEFAULT_IDENTIFIER = "test@example.com";
    private static final String DEFAULT_PHONE = "010-1234-5678";
    private static final String DEFAULT_HASHED_PASSWORD = "$2a$10$hashedpasswordvalue";

    private UserFixture() {}

    /** 기본 User 생성 (ID 할당됨, ACTIVE) */
    public static User create() {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_ID),
                OrganizationId.of(DEFAULT_ORG_ID),
                Identifier.of(DEFAULT_IDENTIFIER),
                PhoneNumber.of(DEFAULT_PHONE),
                HashedPassword.of(DEFAULT_HASHED_PASSWORD),
                UserStatus.ACTIVE,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 식별자로 User 생성 */
    public static User createWithIdentifier(String identifier) {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_ID),
                OrganizationId.of(DEFAULT_ORG_ID),
                Identifier.of(identifier),
                PhoneNumber.of(DEFAULT_PHONE),
                HashedPassword.of(DEFAULT_HASHED_PASSWORD),
                UserStatus.ACTIVE,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 조직으로 User 생성 */
    public static User createWithOrganization(String organizationId) {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_ID),
                OrganizationId.of(organizationId),
                Identifier.of(DEFAULT_IDENTIFIER),
                PhoneNumber.of(DEFAULT_PHONE),
                HashedPassword.of(DEFAULT_HASHED_PASSWORD),
                UserStatus.ACTIVE,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 새로운 User 생성 (ID 할당됨) */
    public static User createNew() {
        UserId userId = UserId.forNew(UUID.randomUUID().toString());
        return User.create(
                userId,
                OrganizationId.of(DEFAULT_ORG_ID),
                Identifier.of(DEFAULT_IDENTIFIER),
                PhoneNumber.of(DEFAULT_PHONE),
                HashedPassword.of(DEFAULT_HASHED_PASSWORD),
                FIXED_TIME);
    }

    /** 전화번호 없이 User 생성 */
    public static User createWithoutPhone() {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_ID),
                OrganizationId.of(DEFAULT_ORG_ID),
                Identifier.of(DEFAULT_IDENTIFIER),
                null,
                HashedPassword.of(DEFAULT_HASHED_PASSWORD),
                UserStatus.ACTIVE,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 상태로 User 생성 */
    public static User createWithStatus(UserStatus status) {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_ID),
                OrganizationId.of(DEFAULT_ORG_ID),
                Identifier.of(DEFAULT_IDENTIFIER),
                PhoneNumber.of(DEFAULT_PHONE),
                HashedPassword.of(DEFAULT_HASHED_PASSWORD),
                status,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** INACTIVE 상태의 User 생성 */
    public static User createInactive() {
        return createWithStatus(UserStatus.INACTIVE);
    }

    /** SUSPENDED 상태의 User 생성 */
    public static User createSuspended() {
        return createWithStatus(UserStatus.SUSPENDED);
    }

    /** 삭제된 User 생성 */
    public static User createDeleted() {
        return User.reconstitute(
                UserId.of(DEFAULT_USER_ID),
                OrganizationId.of(DEFAULT_ORG_ID),
                Identifier.of(DEFAULT_IDENTIFIER),
                PhoneNumber.of(DEFAULT_PHONE),
                HashedPassword.of(DEFAULT_HASHED_PASSWORD),
                UserStatus.INACTIVE,
                DeletionStatus.deletedAt(FIXED_TIME),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 UserId 반환 */
    public static UserId defaultId() {
        return UserId.of(DEFAULT_USER_ID);
    }

    /** 기본 User ID 문자열 반환 */
    public static String defaultIdString() {
        return DEFAULT_USER_ID;
    }

    /** 기본 OrganizationId 반환 */
    public static OrganizationId defaultOrganizationId() {
        return OrganizationId.of(DEFAULT_ORG_ID);
    }

    /** 기본 Organization ID 문자열 반환 */
    public static String defaultOrganizationIdString() {
        return DEFAULT_ORG_ID;
    }

    /** 기본 Identifier 반환 */
    public static Identifier defaultIdentifier() {
        return Identifier.of(DEFAULT_IDENTIFIER);
    }

    /** 기본 Identifier 문자열 반환 */
    public static String defaultIdentifierString() {
        return DEFAULT_IDENTIFIER;
    }

    /** 기본 HashedPassword 반환 */
    public static HashedPassword defaultHashedPassword() {
        return HashedPassword.of(DEFAULT_HASHED_PASSWORD);
    }
}
