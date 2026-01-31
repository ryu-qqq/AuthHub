package com.ryuqq.authhub.adapter.out.persistence.token.fixture;

import com.ryuqq.authhub.adapter.out.persistence.token.entity.RefreshTokenJpaEntity;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import java.time.Instant;
import java.util.UUID;

/**
 * RefreshTokenJpaEntity 테스트 픽스처
 *
 * <p>Persistence Layer 테스트에서 재사용 가능한 JPA Entity를 제공합니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>Domain Fixture와 일관된 기본값 사용
 *   <li>Entity 생성은 of() / forNew() 팩토리 메서드 사용
 *   <li>테스트 격리를 위한 불변 객체 반환
 *   <li>UUID PK 전략 (UUIDv7)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RefreshTokenJpaEntityFixture {

    private static final UUID DEFAULT_REFRESH_TOKEN_ID =
            UUID.fromString("01941234-5678-7000-8000-123456789abc");
    private static final UUID DEFAULT_USER_ID = UUID.fromString(UserFixture.defaultIdString());
    private static final String DEFAULT_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
                    + ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0"
                    + ".Gfx6VO9tcxwk6xqx9yYzSfebfeakZp5JYIgP_edcw_A";
    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    private RefreshTokenJpaEntityFixture() {}

    // ==================== 기본 Entity 생성 ====================

    /**
     * 기본 RefreshToken Entity 생성
     *
     * @return RefreshTokenJpaEntity
     */
    public static RefreshTokenJpaEntity create() {
        return RefreshTokenJpaEntity.of(
                DEFAULT_REFRESH_TOKEN_ID, DEFAULT_USER_ID, DEFAULT_TOKEN, FIXED_TIME, FIXED_TIME);
    }

    /**
     * 신규 RefreshToken Entity 생성 (forNew 팩토리 사용)
     *
     * @return RefreshTokenJpaEntity (createdAt == updatedAt)
     */
    public static RefreshTokenJpaEntity createNew() {
        return RefreshTokenJpaEntity.forNew(
                DEFAULT_REFRESH_TOKEN_ID, DEFAULT_USER_ID, DEFAULT_TOKEN, FIXED_TIME);
    }

    /**
     * 지정된 UserId로 Entity 생성
     *
     * @param userId 사용자 UUID
     * @return RefreshTokenJpaEntity
     */
    public static RefreshTokenJpaEntity createWithUserId(UUID userId) {
        return RefreshTokenJpaEntity.of(
                DEFAULT_REFRESH_TOKEN_ID, userId, DEFAULT_TOKEN, FIXED_TIME, FIXED_TIME);
    }

    /**
     * 지정된 UserId 문자열로 Entity 생성
     *
     * @param userId 사용자 ID 문자열
     * @return RefreshTokenJpaEntity
     */
    public static RefreshTokenJpaEntity createWithUserId(String userId) {
        return createWithUserId(UUID.fromString(userId));
    }

    /**
     * 지정된 토큰으로 Entity 생성
     *
     * @param token 토큰 문자열
     * @return RefreshTokenJpaEntity
     */
    public static RefreshTokenJpaEntity createWithToken(String token) {
        return RefreshTokenJpaEntity.of(
                DEFAULT_REFRESH_TOKEN_ID, DEFAULT_USER_ID, token, FIXED_TIME, FIXED_TIME);
    }

    /**
     * 지정된 RefreshToken ID로 Entity 생성
     *
     * @param refreshTokenId RefreshToken UUID
     * @return RefreshTokenJpaEntity
     */
    public static RefreshTokenJpaEntity createWithId(UUID refreshTokenId) {
        return RefreshTokenJpaEntity.of(
                refreshTokenId, DEFAULT_USER_ID, DEFAULT_TOKEN, FIXED_TIME, FIXED_TIME);
    }

    /**
     * 갱신된 토큰 Entity 생성
     *
     * @param newToken 새로운 토큰
     * @param updatedAt 갱신 시각
     * @return RefreshTokenJpaEntity
     */
    public static RefreshTokenJpaEntity createUpdated(String newToken, Instant updatedAt) {
        return RefreshTokenJpaEntity.of(
                DEFAULT_REFRESH_TOKEN_ID, DEFAULT_USER_ID, newToken, FIXED_TIME, updatedAt);
    }

    // ==================== 전체 필드 지정 ====================

    /**
     * 모든 필드를 지정하여 Entity 생성
     *
     * @param refreshTokenId RefreshToken UUID
     * @param userId 사용자 UUID
     * @param token 토큰 문자열
     * @param createdAt 생성 시각
     * @param updatedAt 갱신 시각
     * @return RefreshTokenJpaEntity
     */
    public static RefreshTokenJpaEntity create(
            UUID refreshTokenId, UUID userId, String token, Instant createdAt, Instant updatedAt) {
        return RefreshTokenJpaEntity.of(refreshTokenId, userId, token, createdAt, updatedAt);
    }

    // ==================== 기본값 접근자 ====================

    /**
     * 기본 RefreshToken ID 반환
     *
     * @return RefreshToken UUID
     */
    public static UUID defaultRefreshTokenId() {
        return DEFAULT_REFRESH_TOKEN_ID;
    }

    /**
     * 기본 User ID 반환
     *
     * @return User UUID
     */
    public static UUID defaultUserId() {
        return DEFAULT_USER_ID;
    }

    /**
     * 기본 토큰 반환
     *
     * @return 토큰 문자열
     */
    public static String defaultToken() {
        return DEFAULT_TOKEN;
    }

    /**
     * 고정 시간 반환
     *
     * @return 테스트용 고정 시간
     */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }
}
