package com.ryuqq.authhub.adapter.out.persistence.identity.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * UserProfile JPA Entity.
 *
 * <p>UserProfile Aggregate를 관계형 데이터베이스에 영속화하기 위한 JPA Entity입니다.
 * Hexagonal Architecture의 Persistence Adapter 계층에 위치하며, Domain Layer와 분리됩니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/setter 직접 구현</li>
 *   <li>✅ Long FK 전략 - JPA 관계 어노테이션 절대 금지</li>
 *   <li>✅ Hibernate 전용 protected 생성자 제공</li>
 *   <li>✅ 불변성 지향 - setter는 package-private으로 제한</li>
 * </ul>
 *
 * <p><strong>Long FK 전략 예시:</strong></p>
 * <pre>
 * ❌ 잘못된 방식 (관계 어노테이션 사용):
 *    JPA 관계 어노테이션 사용
 *    private UserJpaEntity user;
 *
 * ✅ 올바른 방식 (Long FK 사용):
 *    private Long userId;
 * </pre>
 *
 * <p><strong>테이블 구조:</strong></p>
 * <ul>
 *   <li>테이블명: user_profiles</li>
 *   <li>인덱스: uid (unique), user_id, nickname (unique)</li>
 *   <li>제약조건: uid UNIQUE, nickname UNIQUE, user_id FK (application level)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(
        name = "user_profiles",
        indexes = {
                @Index(name = "idx_user_profile_uid", columnList = "uid"),
                @Index(name = "idx_user_profile_user_id", columnList = "user_id"),
                @Index(name = "idx_user_profile_nickname", columnList = "nickname")
        }
)
public class UserProfileJpaEntity {

    /**
     * 데이터베이스 기본 키 (Auto Increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * UserProfile의 고유 식별자 (UUID 기반, 도메인 ID).
     * Domain의 UserProfileId.value()와 매핑됩니다.
     */
    @Column(name = "uid", nullable = false, unique = true, length = 36, updatable = false)
    private String uid;

    /**
     * User 참조 (Long FK).
     *
     * <p><strong>Zero-Tolerance 규칙:</strong> JPA 관계 어노테이션 절대 금지!</p>
     * <ul>
     *   <li>✅ private Long userId (올바른 방식)</li>
     *   <li>❌ 관계 어노테이션 사용 (금지된 방식)</li>
     * </ul>
     *
     * <p>Domain의 UserId.value()와 매핑됩니다.</p>
     */
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    /**
     * 닉네임 (사용자 표시 이름, 중복 불가).
     * Domain의 Nickname.getValue()와 매핑됩니다.
     */
    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    /**
     * 프로필 이미지 URL (nullable - 기본 이미지 사용 가능).
     * Domain의 ProfileImageUrl.getValue()와 매핑됩니다.
     */
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    /**
     * 자기소개 (nullable).
     * Domain의 Bio.getValue()와 매핑됩니다.
     */
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    /**
     * 생성 시각.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * 수정 시각.
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Hibernate 전용 기본 생성자 (protected).
     * 외부에서 직접 호출 금지, Hibernate만 사용합니다.
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    protected UserProfileJpaEntity() {
        // Hibernate용 기본 생성자
    }

    /**
     * UserProfileJpaEntity 생성자 (private).
     * 외부에서는 정적 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param uid 프로필 고유 식별자 (UUID 문자열, null 불가)
     * @param userId 사용자 ID (Long FK, null 불가)
     * @param nickname 닉네임 (null 불가)
     * @param profileImageUrl 프로필 이미지 URL (null 허용)
     * @param bio 자기소개 (null 허용)
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     */
    private UserProfileJpaEntity(
            final String uid,
            final Long userId,
            final String nickname,
            final String profileImageUrl,
            final String bio,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.uid = Objects.requireNonNull(uid, "uid cannot be null");
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.nickname = Objects.requireNonNull(nickname, "nickname cannot be null");
        this.profileImageUrl = profileImageUrl; // nullable
        this.bio = bio; // nullable
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
    }

    /**
     * 새로운 UserProfileJpaEntity를 생성합니다.
     * Domain의 UserProfile.create() 결과를 영속화할 때 사용됩니다.
     *
     * @param uid 프로필 고유 식별자 (UUID 문자열, null 불가)
     * @param userId 사용자 ID (Long FK, null 불가)
     * @param nickname 닉네임 (null 불가)
     * @param profileImageUrl 프로필 이미지 URL (null 허용)
     * @param bio 자기소개 (null 허용)
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     * @return 새로 생성된 UserProfileJpaEntity 인스턴스
     * @throws NullPointerException uid, userId, nickname, createdAt, updatedAt 중 하나라도 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static UserProfileJpaEntity create(
            final String uid,
            final Long userId,
            final String nickname,
            final String profileImageUrl,
            final String bio,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new UserProfileJpaEntity(uid, userId, nickname, profileImageUrl, bio, createdAt, updatedAt);
    }

    /**
     * 데이터베이스 기본 키를 반환합니다.
     *
     * @return 기본 키 (Long, 영속화 전에는 null)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Long getId() {
        return this.id;
    }

    /**
     * 프로필 고유 식별자 (UUID)를 반환합니다.
     *
     * @return UUID 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getUid() {
        return this.uid;
    }

    /**
     * 사용자 ID (Long FK)를 반환합니다.
     *
     * @return 사용자 ID (Long)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Long getUserId() {
        return this.userId;
    }

    /**
     * 닉네임을 반환합니다.
     *
     * @return 닉네임 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * 프로필 이미지 URL을 반환합니다.
     *
     * @return 프로필 이미지 URL 문자열 (기본 이미지인 경우 null일 수 있음)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getProfileImageUrl() {
        return this.profileImageUrl;
    }

    /**
     * 자기소개를 반환합니다.
     *
     * @return 자기소개 문자열 (없으면 null)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getBio() {
        return this.bio;
    }

    /**
     * 생성 시각을 반환합니다.
     *
     * @return 생성 시각 Instant
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    /**
     * 수정 시각을 반환합니다.
     *
     * @return 수정 시각 Instant
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * 닉네임을 변경합니다 (package-private).
     * Domain의 UserProfile.updateNickname() 결과를 반영할 때 사용됩니다.
     *
     * @param nickname 새로운 닉네임 (null 불가)
     * @throws NullPointerException nickname이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    void setNickname(final String nickname) {
        this.nickname = Objects.requireNonNull(nickname, "nickname cannot be null");
    }

    /**
     * 프로필 이미지 URL을 변경합니다 (package-private).
     * Domain의 UserProfile.updateProfileImage() 결과를 반영할 때 사용됩니다.
     *
     * @param profileImageUrl 새로운 프로필 이미지 URL (null 허용)
     * @author AuthHub Team
     * @since 1.0.0
     */
    void setProfileImageUrl(final String profileImageUrl) {
        this.profileImageUrl = profileImageUrl; // nullable
    }

    /**
     * 자기소개를 변경합니다 (package-private).
     * Domain의 UserProfile.updateBio() 결과를 반영할 때 사용됩니다.
     *
     * @param bio 새로운 자기소개 (null 허용)
     * @author AuthHub Team
     * @since 1.0.0
     */
    void setBio(final String bio) {
        this.bio = bio; // nullable
    }

    /**
     * 수정 시각을 변경합니다 (package-private).
     *
     * @param updatedAt 새로운 수정 시각 (null 불가)
     * @throws NullPointerException updatedAt이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
    }

    /**
     * UUID 문자열을 UUID 객체로 변환하여 반환합니다.
     *
     * @return UUID 객체
     * @throws IllegalArgumentException uid가 유효하지 않은 UUID 형식인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UUID getUidAsUuid() {
        return UUID.fromString(this.uid);
    }

    /**
     * 다른 UserProfileJpaEntity의 값으로 현재 엔티티를 업데이트합니다.
     * JPA Dirty Checking을 활용하여 변경된 필드만 UPDATE 쿼리로 반영됩니다.
     *
     * <p>이 메서드는 Persistence Adapter의 save() 메서드에서 사용되며,
     * 기존 엔티티의 상태를 유지하면서 변경된 값만 업데이트합니다.</p>
     *
     * @param source 업데이트할 값을 가진 UserProfileJpaEntity (null 불가)
     * @throws NullPointerException source가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void updateFrom(final UserProfileJpaEntity source) {
        Objects.requireNonNull(source, "source cannot be null");

        // uid와 userId는 불변이므로 업데이트하지 않음
        this.setNickname(source.getNickname());
        this.setProfileImageUrl(source.getProfileImageUrl());
        this.setBio(source.getBio());
        this.setUpdatedAt(source.getUpdatedAt());
    }

    /**
     * 두 UserProfileJpaEntity 객체의 동등성을 비교합니다.
     * uid가 같으면 같은 엔티티로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return uid가 같으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserProfileJpaEntity other = (UserProfileJpaEntity) obj;
        return Objects.equals(this.uid, other.uid);
    }

    /**
     * 해시 코드를 반환합니다.
     * uid를 기준으로 계산됩니다.
     *
     * @return 해시 코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.uid);
    }

    /**
     * UserProfileJpaEntity의 문자열 표현을 반환합니다.
     *
     * @return "UserProfileJpaEntity{id=..., uid=..., ...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "UserProfileJpaEntity{" +
                "id=" + this.id +
                ", uid='" + this.uid + '\'' +
                ", userId=" + this.userId +
                ", nickname='" + this.nickname + '\'' +
                ", profileImageUrl='" + this.profileImageUrl + '\'' +
                ", bio='" + this.bio + '\'' +
                ", createdAt=" + this.createdAt +
                ", updatedAt=" + this.updatedAt +
                '}';
    }
}
