package com.ryuqq.authhub.adapter.out.persistence.user.fixture;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Instant;

/**
 * UserJpaEntity 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserJpaEntityFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String DEFAULT_USER_ID = "01941234-5678-7000-8000-123456789001";
    private static final String DEFAULT_ORG_ID = "01941234-5678-7000-8000-123456789def";
    private static final String DEFAULT_IDENTIFIER = "test@example.com";
    private static final String DEFAULT_PHONE = "010-1234-5678";
    private static final String DEFAULT_HASHED_PASSWORD = "$2a$10$hashedpassword";

    private UserJpaEntityFixture() {}

    /** 기본 UserJpaEntity 생성 */
    public static UserJpaEntity create() {
        return UserJpaEntity.of(
                DEFAULT_USER_ID,
                DEFAULT_ORG_ID,
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE,
                DEFAULT_HASHED_PASSWORD,
                UserStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 전체 필드로 UserJpaEntity 생성 */
    public static UserJpaEntity create(
            String userId,
            String organizationId,
            String identifier,
            String phoneNumber,
            String hashedPassword,
            UserStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return UserJpaEntity.of(
                userId,
                organizationId,
                identifier,
                phoneNumber,
                hashedPassword,
                status,
                createdAt,
                updatedAt,
                deletedAt);
    }

    /** 지정된 식별자로 UserJpaEntity 생성 */
    public static UserJpaEntity createWithIdentifier(String identifier) {
        return UserJpaEntity.of(
                DEFAULT_USER_ID,
                DEFAULT_ORG_ID,
                identifier,
                DEFAULT_PHONE,
                DEFAULT_HASHED_PASSWORD,
                UserStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 전화번호 없이 UserJpaEntity 생성 */
    public static UserJpaEntity createWithoutPhone() {
        return UserJpaEntity.of(
                DEFAULT_USER_ID,
                DEFAULT_ORG_ID,
                DEFAULT_IDENTIFIER,
                null,
                DEFAULT_HASHED_PASSWORD,
                UserStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 비활성화된 UserJpaEntity 생성 */
    public static UserJpaEntity createInactive() {
        return UserJpaEntity.of(
                DEFAULT_USER_ID,
                DEFAULT_ORG_ID,
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE,
                DEFAULT_HASHED_PASSWORD,
                UserStatus.INACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 삭제된 UserJpaEntity 생성 */
    public static UserJpaEntity createDeleted() {
        return UserJpaEntity.of(
                DEFAULT_USER_ID,
                DEFAULT_ORG_ID,
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE,
                DEFAULT_HASHED_PASSWORD,
                UserStatus.INACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 User ID 반환 */
    public static String defaultUserId() {
        return DEFAULT_USER_ID;
    }
}
