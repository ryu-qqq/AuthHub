package com.ryuqq.authhub.domain.auth.user;

import java.time.Instant;
import java.util.Objects;

/**
 * User Aggregate Root.
 *
 * <p>사용자 도메인의 Aggregate Root로서, 사용자의 핵심 정보와 행위를 캡슐화합니다.
 * DDD(Domain-Driven Design) 원칙에 따라 설계되었으며, 불변성과 도메인 규칙을 엄격히 준수합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>사용자 식별자(UserId) 관리</li>
 *   <li>사용자 상태(UserStatus) 관리 및 전환</li>
 *   <li>마지막 로그인 시각(LastLoginAt) 추적</li>
 *   <li>생성/수정 시각 관리</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/메서드 직접 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공, getter chaining 금지</li>
 *   <li>✅ 불변성 보장 - 모든 필드 final, 상태 변경 시 새 인스턴스 반환</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 @author, @since 포함</li>
 * </ul>
 *
 * <p><strong>도메인 규칙:</strong></p>
 * <ul>
 *   <li>사용자는 반드시 고유한 UserId를 가져야 함</li>
 *   <li>생성 시 기본 상태는 ACTIVE</li>
 *   <li>SUSPENDED 상태에서는 시스템 사용 불가</li>
 *   <li>로그인 시각은 현재 시각으로만 갱신 가능</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public final class User {

    private final UserId id;
    private final UserStatus status;
    private final LastLoginAt lastLoginAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * User 생성자 (private).
     * 외부에서는 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param id 사용자 식별자 (null 불가)
     * @param status 사용자 상태 (null 불가)
     * @param lastLoginAt 마지막 로그인 시각 (null 허용)
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     */
    private User(
            final UserId id,
            final UserStatus status,
            final LastLoginAt lastLoginAt,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "UserId cannot be null");
        this.status = Objects.requireNonNull(status, "UserStatus cannot be null");
        this.lastLoginAt = Objects.requireNonNull(lastLoginAt, "LastLoginAt cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
    }

    /**
     * 새로운 User를 생성합니다.
     * 기본 상태는 ACTIVE이며, 로그인 기록은 없는 상태로 생성됩니다.
     *
     * @param id 사용자 식별자 (null 불가)
     * @return 새로 생성된 User 인스턴스
     * @throws NullPointerException id가 null인 경우
     */
    public static User create(final UserId id) {
        Instant now = Instant.now();
        return new User(
                id,
                UserStatus.ACTIVE,
                LastLoginAt.neverLoggedIn(),
                now,
                now
        );
    }

    /**
     * 기존 데이터로부터 User를 재구성합니다.
     * 주로 영속성 계층에서 데이터를 로드할 때 사용됩니다.
     *
     * @param id 사용자 식별자 (null 불가)
     * @param status 사용자 상태 (null 불가)
     * @param lastLoginAt 마지막 로그인 시각
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     * @return 재구성된 User 인스턴스
     */
    public static User reconstruct(
            final UserId id,
            final UserStatus status,
            final LastLoginAt lastLoginAt,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new User(id, status, lastLoginAt, createdAt, updatedAt);
    }

    /**
     * 사용자 식별자를 반환합니다.
     *
     * @return UserId 인스턴스
     */
    public UserId getId() {
        return this.id;
    }

    /**
     * 사용자 상태를 반환합니다.
     *
     * @return UserStatus enum
     */
    public UserStatus getStatus() {
        return this.status;
    }

    /**
     * 마지막 로그인 시각을 반환합니다.
     *
     * @return LastLoginAt 인스턴스
     */
    public LastLoginAt getLastLoginAt() {
        return this.lastLoginAt;
    }

    /**
     * 생성 시각을 반환합니다.
     *
     * @return 생성 시각 Instant
     */
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    /**
     * 수정 시각을 반환합니다.
     *
     * @return 수정 시각 Instant
     */
    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * 사용자가 시스템을 사용할 수 있는지 확인합니다.
     * Law of Demeter 준수 - status.canUseSystem()을 직접 호출하지 않고 User에서 제공
     *
     * @return ACTIVE 상태이면 true, 아니면 false
     */
    public boolean canUseSystem() {
        return this.status.canUseSystem();
    }

    /**
     * 사용자가 활성 상태인지 확인합니다.
     *
     * @return ACTIVE 상태이면 true, 아니면 false
     */
    public boolean isActive() {
        return this.status.isActive();
    }

    /**
     * 사용자가 정지 상태인지 확인합니다.
     *
     * @return SUSPENDED 상태이면 true, 아니면 false
     */
    public boolean isSuspended() {
        return this.status.isSuspended();
    }

    /**
     * 사용자가 한 번도 로그인하지 않았는지 확인합니다.
     *
     * @return 로그인 기록이 없으면 true, 있으면 false
     */
    public boolean hasNeverLoggedIn() {
        return this.lastLoginAt.hasNeverLoggedIn();
    }

    /**
     * 로그인을 수행하고 로그인 시각을 현재 시각으로 갱신합니다.
     * 불변성 원칙에 따라 새로운 User 인스턴스를 반환합니다.
     *
     * @return 로그인 시각이 갱신된 새로운 User 인스턴스
     * @throws IllegalStateException 사용자가 시스템을 사용할 수 없는 상태인 경우
     */
    public User login() {
        if (!this.canUseSystem()) {
            throw new IllegalStateException(
                    "Cannot login: User is not in usable state (status=" + this.status + ")"
            );
        }
        return new User(
                this.id,
                this.status,
                LastLoginAt.now(),
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 사용자를 활성화합니다.
     * INACTIVE 또는 SUSPENDED 상태에서 ACTIVE로 전환합니다.
     *
     * @return 활성화된 새로운 User 인스턴스
     */
    public User activate() {
        if (this.status == UserStatus.ACTIVE) {
            return this; // 이미 활성 상태이면 현재 인스턴스 반환
        }
        return new User(
                this.id,
                UserStatus.ACTIVE,
                this.lastLoginAt,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 사용자를 비활성화합니다.
     * 장기간 미사용 등의 사유로 계정을 휴면 처리합니다.
     *
     * @return 비활성화된 새로운 User 인스턴스
     */
    public User deactivate() {
        if (this.status == UserStatus.INACTIVE) {
            return this; // 이미 비활성 상태이면 현재 인스턴스 반환
        }
        return new User(
                this.id,
                UserStatus.INACTIVE,
                this.lastLoginAt,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 사용자를 정지합니다.
     * 관리자에 의해 계정 사용이 일시적 또는 영구적으로 차단됩니다.
     *
     * @return 정지된 새로운 User 인스턴스
     */
    public User suspend() {
        if (this.status == UserStatus.SUSPENDED) {
            return this; // 이미 정지 상태이면 현재 인스턴스 반환
        }
        return new User(
                this.id,
                UserStatus.SUSPENDED,
                this.lastLoginAt,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 두 User 객체의 동등성을 비교합니다.
     * UserId가 같으면 같은 사용자로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return UserId가 같으면 true, 아니면 false
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        return Objects.equals(this.id, other.id);
    }

    /**
     * 해시 코드를 반환합니다.
     * UserId를 기준으로 계산됩니다.
     *
     * @return 해시 코드
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * User의 문자열 표현을 반환합니다.
     *
     * @return "User{id=..., status=..., ...}" 형식의 문자열
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + this.id +
                ", status=" + this.status +
                ", lastLoginAt=" + this.lastLoginAt +
                ", createdAt=" + this.createdAt +
                ", updatedAt=" + this.updatedAt +
                '}';
    }
}
