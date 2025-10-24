package com.ryuqq.authhub.adapter.out.persistence.auth.user.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * User JPA Entity.
 *
 * <p>User Aggregate를 관계형 데이터베이스에 영속화하기 위한 JPA Entity입니다.
 * Hexagonal Architecture의 Persistence Adapter 계층에 위치하며, Domain Layer와 분리됩니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/setter 직접 구현</li>
 *   <li>✅ Long FK 전략 - JPA 관계 어노테이션 절대 금지 ({@code @ManyToOne}, {@code @OneToMany} 등)</li>
 *   <li>✅ Hibernate 전용 protected 생성자 제공</li>
 *   <li>✅ 불변성 지향 - setter는 package-private으로 제한</li>
 * </ul>
 *
 * <p><strong>Long FK 전략 예시:</strong></p>
 * <pre>
 * ❌ 잘못된 방식 (관계 어노테이션 사용):
 *    {@code @ManyToOne}
 *    {@code private UserCredentialJpaEntity credential;}
 *
 * ✅ 올바른 방식 (Long FK 사용):
 *    {@code private Long credentialId;}
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_uid", columnList = "uid"),
                @Index(name = "idx_user_status", columnList = "status"),
                @Index(name = "idx_user_created_at", columnList = "created_at")
        }
)
public class UserJpaEntity {

    /**
     * 데이터베이스 기본 키 (Auto Increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * User의 고유 식별자 (UUID 기반, 도메인 ID).
     * Domain의 UserId.value()와 매핑됩니다.
     */
    @Column(name = "uid", nullable = false, unique = true, length = 36, updatable = false)
    private String uid;

    /**
     * 사용자 계정 상태 (ACTIVE, INACTIVE, SUSPENDED).
     * Domain의 UserStatus enum과 매핑됩니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatusEnum status;

    /**
     * 마지막 로그인 시각 (nullable - 한 번도 로그인하지 않은 경우 null).
     * Domain의 LastLoginAt.value()와 매핑됩니다.
     */
    @Column(name = "last_login_at")
    private Instant lastLoginAt;

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
    protected UserJpaEntity() {
        // Hibernate용 기본 생성자
    }

    /**
     * UserJpaEntity 생성자 (private).
     * 외부에서는 정적 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param uid 사용자 고유 식별자 (UUID 문자열, null 불가)
     * @param status 사용자 상태 (null 불가)
     * @param lastLoginAt 마지막 로그인 시각 (null 허용)
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     */
    private UserJpaEntity(
            final String uid,
            final UserStatusEnum status,
            final Instant lastLoginAt,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.uid = Objects.requireNonNull(uid, "uid cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.lastLoginAt = lastLoginAt; // nullable
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
    }

    /**
     * 새로운 UserJpaEntity를 생성합니다.
     * Domain의 User.create() 결과를 영속화할 때 사용됩니다.
     *
     * @param uid 사용자 고유 식별자 (UUID 문자열, null 불가)
     * @param status 사용자 상태 (null 불가)
     * @param lastLoginAt 마지막 로그인 시각 (null 허용)
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     * @return 새로 생성된 UserJpaEntity 인스턴스
     * @throws NullPointerException uid, status, createdAt, updatedAt 중 하나라도 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static UserJpaEntity create(
            final String uid,
            final UserStatusEnum status,
            final Instant lastLoginAt,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new UserJpaEntity(uid, status, lastLoginAt, createdAt, updatedAt);
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
     * 사용자 고유 식별자 (UUID)를 반환합니다.
     *
     * @return UUID 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getUid() {
        return this.uid;
    }

    /**
     * 사용자 상태를 반환합니다.
     *
     * @return UserStatusEnum
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserStatusEnum getStatus() {
        return this.status;
    }

    /**
     * 마지막 로그인 시각을 반환합니다.
     *
     * @return 마지막 로그인 시각 Instant (로그인 기록 없으면 null)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getLastLoginAt() {
        return this.lastLoginAt;
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
     * 사용자 상태를 변경합니다 (package-private).
     * Domain의 User.activate(), User.suspend() 등의 결과를 반영할 때 사용됩니다.
     *
     * @param status 새로운 상태 (null 불가)
     * @throws NullPointerException status가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    void setStatus(final UserStatusEnum status) {
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.updatedAt = Instant.now();
    }

    /**
     * 마지막 로그인 시각을 변경합니다 (package-private).
     * Domain의 User.login() 결과를 반영할 때 사용됩니다.
     *
     * @param lastLoginAt 새로운 로그인 시각 (null 허용)
     * @author AuthHub Team
     * @since 1.0.0
     */
    void setLastLoginAt(final Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        this.updatedAt = Instant.now();
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
     * 다른 UserJpaEntity의 값으로 현재 엔티티를 업데이트합니다.
     * JPA Dirty Checking을 활용하여 변경된 필드만 UPDATE 쿼리로 반영됩니다.
     *
     * <p>이 메서드는 Persistence Adapter의 save() 메서드에서 사용되며,
     * 기존 엔티티의 상태를 유지하면서 변경된 값만 업데이트합니다.</p>
     *
     * @param source 업데이트할 값을 가진 UserJpaEntity (null 불가)
     * @throws NullPointerException source가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void updateFrom(final UserJpaEntity source) {
        Objects.requireNonNull(source, "source cannot be null");

        // uid는 불변이므로 업데이트하지 않음
        this.setStatus(source.getStatus());
        this.setLastLoginAt(source.getLastLoginAt());
        this.setUpdatedAt(source.getUpdatedAt());
    }

    /**
     * 두 UserJpaEntity 객체의 동등성을 비교합니다.
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
        UserJpaEntity other = (UserJpaEntity) obj;
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
     * UserJpaEntity의 문자열 표현을 반환합니다.
     *
     * @return "UserJpaEntity{id=..., uid=..., status=..., ...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "UserJpaEntity{" +
                "id=" + this.id +
                ", uid='" + this.uid + '\'' +
                ", status=" + this.status +
                ", lastLoginAt=" + this.lastLoginAt +
                ", createdAt=" + this.createdAt +
                ", updatedAt=" + this.updatedAt +
                '}';
    }

    /**
     * User 상태를 나타내는 JPA용 Enum.
     *
     * <p>Domain의 UserStatus와 매핑되며, DB에는 문자열로 저장됩니다.</p>
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    public enum UserStatusEnum {
        /**
         * 정상 활성 상태.
         */
        ACTIVE,

        /**
         * 비활성 상태 (휴면 계정 등).
         */
        INACTIVE,

        /**
         * 정지 상태 (관리자에 의한 계정 정지).
         */
        SUSPENDED
    }
}
