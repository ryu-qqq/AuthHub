package com.ryuqq.authhub.domain.identity.profile;

import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.identity.profile.vo.Bio;
import com.ryuqq.authhub.domain.identity.profile.vo.Nickname;
import com.ryuqq.authhub.domain.identity.profile.vo.ProfileImageUrl;
import com.ryuqq.authhub.domain.identity.profile.vo.UserProfileId;

import java.time.Instant;
import java.util.Objects;

/**
 * UserProfile Aggregate Root.
 *
 * <p>사용자 프로필 도메인의 Aggregate Root로서, 사용자의 공개 프로필 정보를 캡슐화합니다.
 * DDD(Domain-Driven Design) 원칙에 따라 설계되었으며, 불변성과 도메인 규칙을 엄격히 준수합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>프로필 식별자(UserProfileId) 관리</li>
 *   <li>사용자 참조(UserId) 관리 - Long FK 패턴 대신 Value Object 사용</li>
 *   <li>닉네임(Nickname) 관리 및 중복 검증</li>
 *   <li>프로필 이미지(ProfileImageUrl) 관리</li>
 *   <li>자기소개(Bio) 관리</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/메서드 직접 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공, getter chaining 금지</li>
 *   <li>✅ UserId는 Value Object로 참조 - JPA 관계 어노테이션 금지</li>
 *   <li>✅ 불변성 보장 - 상태 변경 시 새 인스턴스 반환</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 @author, @since 포함</li>
 * </ul>
 *
 * <p><strong>도메인 규칙:</strong></p>
 * <ul>
 *   <li>프로필은 반드시 고유한 UserProfileId를 가져야 함</li>
 *   <li>프로필은 반드시 UserId를 참조해야 함 (1:1 관계)</li>
 *   <li>닉네임은 중복 불가 (Application Layer에서 검증)</li>
 *   <li>프로필 이미지가 없는 경우 기본 이미지 사용</li>
 *   <li>자기소개는 선택 사항</li>
 * </ul>
 *
 * <p><strong>도메인 이벤트:</strong></p>
 * <ul>
 *   <li>프로필 변경 시 Application Layer에서 이벤트 발행</li>
 *   <li>Domain Layer는 Framework 의존성 없이 Pure Java로 구현</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public final class UserProfile {

    private final UserProfileId id;
    private final UserId userId;
    private final Nickname nickname;
    private final ProfileImageUrl profileImageUrl;
    private final Bio bio;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * UserProfile 생성자 (private).
     * 외부에서는 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param id 프로필 식별자 (null 불가)
     * @param userId 사용자 식별자 (null 불가)
     * @param nickname 닉네임 (null 불가)
     * @param profileImageUrl 프로필 이미지 URL (null 허용, 기본 이미지 사용)
     * @param bio 자기소개 (null 허용)
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     */
    private UserProfile(
            final UserProfileId id,
            final UserId userId,
            final Nickname nickname,
            final ProfileImageUrl profileImageUrl,
            final Bio bio,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "UserProfileId cannot be null");
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null");
        this.nickname = Objects.requireNonNull(nickname, "Nickname cannot be null");
        this.profileImageUrl = profileImageUrl != null ? profileImageUrl : ProfileImageUrl.defaultImage();
        this.bio = bio != null ? bio : Bio.empty();
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
    }

    /**
     * 새로운 UserProfile을 생성합니다.
     *
     * @param userId 사용자 식별자 (null 불가)
     * @param nickname 닉네임 (null 불가)
     * @return 새로 생성된 UserProfile 인스턴스
     * @throws NullPointerException userId 또는 nickname이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static UserProfile create(final UserId userId, final Nickname nickname) {
        final Instant now = Instant.now();
        return new UserProfile(
                UserProfileId.newId(),
                userId,
                nickname,
                ProfileImageUrl.defaultImage(),
                Bio.empty(),
                now,
                now
        );
    }

    /**
     * 기존 데이터로부터 UserProfile을 재구성합니다.
     * 주로 영속성 계층에서 데이터를 로드할 때 사용됩니다.
     *
     * @param id 프로필 식별자 (null 불가)
     * @param userId 사용자 식별자 (null 불가)
     * @param nickname 닉네임 (null 불가)
     * @param profileImageUrl 프로필 이미지 URL (null 허용)
     * @param bio 자기소개 (null 허용)
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     * @return 재구성된 UserProfile 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static UserProfile reconstruct(
            final UserProfileId id,
            final UserId userId,
            final Nickname nickname,
            final ProfileImageUrl profileImageUrl,
            final Bio bio,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new UserProfile(id, userId, nickname, profileImageUrl, bio, createdAt, updatedAt);
    }

    /**
     * 프로필 식별자를 반환합니다.
     *
     * @return UserProfileId 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserProfileId getId() {
        return this.id;
    }

    /**
     * 사용자 식별자를 반환합니다.
     *
     * @return UserId 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserId getUserId() {
        return this.userId;
    }

    /**
     * 닉네임을 반환합니다.
     *
     * @return Nickname 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Nickname getNickname() {
        return this.nickname;
    }

    /**
     * 프로필 이미지 URL을 반환합니다.
     *
     * @return ProfileImageUrl 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public ProfileImageUrl getProfileImageUrl() {
        return this.profileImageUrl;
    }

    /**
     * 자기소개를 반환합니다.
     *
     * @return Bio 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Bio getBio() {
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
     * 닉네임 문자열을 반환합니다 (Law of Demeter 준수).
     * getter chaining 방지 - nickname.getValue() 대신 사용.
     *
     * @return 닉네임 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getNicknameValue() {
        return this.nickname.getValue();
    }

    /**
     * 프로필 이미지 URL 문자열을 반환합니다 (Law of Demeter 준수).
     * getter chaining 방지 - profileImageUrl.getValue() 대신 사용.
     *
     * @return 프로필 이미지 URL 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getProfileImageUrlValue() {
        return this.profileImageUrl.getValue();
    }

    /**
     * 자기소개 문자열을 반환합니다 (Law of Demeter 준수).
     * getter chaining 방지 - bio.getValue() 대신 사용.
     *
     * @return 자기소개 문자열 (없으면 빈 문자열)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getBioValue() {
        return this.bio.getValue();
    }

    /**
     * 프로필 이미지가 기본 이미지인지 확인합니다.
     *
     * @return 기본 이미지이면 true, 사용자 지정 이미지이면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean hasDefaultImage() {
        return this.profileImageUrl.isDefault();
    }

    /**
     * 자기소개가 설정되어 있는지 확인합니다.
     *
     * @return 자기소개가 있으면 true, 없으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean hasBio() {
        return this.bio.hasValue();
    }

    /**
     * 닉네임을 변경합니다.
     * 불변성 원칙에 따라 새로운 UserProfile 인스턴스를 반환합니다.
     *
     * @param newNickname 새로운 닉네임 (null 불가)
     * @return 닉네임이 변경된 새로운 UserProfile 인스턴스
     * @throws NullPointerException newNickname이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserProfile updateNickname(final Nickname newNickname) {
        Objects.requireNonNull(newNickname, "New nickname cannot be null");

        if (this.nickname.equals(newNickname)) {
            return this; // 동일한 닉네임이면 현재 인스턴스 반환
        }

        return new UserProfile(
                this.id,
                this.userId,
                newNickname,
                this.profileImageUrl,
                this.bio,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 프로필 이미지를 변경합니다.
     * 불변성 원칙에 따라 새로운 UserProfile 인스턴스를 반환합니다.
     *
     * @param newProfileImageUrl 새로운 프로필 이미지 URL (null 불가)
     * @return 프로필 이미지가 변경된 새로운 UserProfile 인스턴스
     * @throws NullPointerException newProfileImageUrl이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserProfile updateProfileImage(final ProfileImageUrl newProfileImageUrl) {
        Objects.requireNonNull(newProfileImageUrl, "New profile image URL cannot be null");

        if (this.profileImageUrl.equals(newProfileImageUrl)) {
            return this; // 동일한 이미지 URL이면 현재 인스턴스 반환
        }

        return new UserProfile(
                this.id,
                this.userId,
                this.nickname,
                newProfileImageUrl,
                this.bio,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 자기소개를 변경합니다.
     * 불변성 원칙에 따라 새로운 UserProfile 인스턴스를 반환합니다.
     *
     * @param newBio 새로운 자기소개 (null 불가)
     * @return 자기소개가 변경된 새로운 UserProfile 인스턴스
     * @throws NullPointerException newBio가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserProfile updateBio(final Bio newBio) {
        Objects.requireNonNull(newBio, "New bio cannot be null");

        if (this.bio.equals(newBio)) {
            return this; // 동일한 자기소개이면 현재 인스턴스 반환
        }

        return new UserProfile(
                this.id,
                this.userId,
                this.nickname,
                this.profileImageUrl,
                newBio,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 프로필 전체를 업데이트합니다 (닉네임, 이미지, 자기소개).
     * 불변성 원칙에 따라 새로운 UserProfile 인스턴스를 반환합니다.
     *
     * @param newNickname 새로운 닉네임 (null 불가)
     * @param newProfileImageUrl 새로운 프로필 이미지 URL (null 불가)
     * @param newBio 새로운 자기소개 (null 불가)
     * @return 프로필이 업데이트된 새로운 UserProfile 인스턴스
     * @throws NullPointerException 파라미터 중 하나라도 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserProfile updateProfile(
            final Nickname newNickname,
            final ProfileImageUrl newProfileImageUrl,
            final Bio newBio
    ) {
        Objects.requireNonNull(newNickname, "New nickname cannot be null");
        Objects.requireNonNull(newProfileImageUrl, "New profile image URL cannot be null");
        Objects.requireNonNull(newBio, "New bio cannot be null");

        // 모든 값이 동일하면 현재 인스턴스 반환
        if (this.nickname.equals(newNickname)
                && this.profileImageUrl.equals(newProfileImageUrl)
                && this.bio.equals(newBio)) {
            return this;
        }

        return new UserProfile(
                this.id,
                this.userId,
                newNickname,
                newProfileImageUrl,
                newBio,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 두 UserProfile 객체의 동등성을 비교합니다.
     * UserProfileId가 같으면 같은 프로필로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return UserProfileId가 같으면 true, 아니면 false
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
        UserProfile other = (UserProfile) obj;
        return Objects.equals(this.id, other.id);
    }

    /**
     * 해시 코드를 반환합니다.
     * UserProfileId를 기준으로 계산됩니다.
     *
     * @return 해시 코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * UserProfile의 문자열 표현을 반환합니다.
     *
     * @return "UserProfile{id=..., userId=..., ...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + this.id +
                ", userId=" + this.userId +
                ", nickname=" + this.nickname +
                ", profileImageUrl=" + this.profileImageUrl +
                ", bio=" + this.bio +
                ", createdAt=" + this.createdAt +
                ", updatedAt=" + this.updatedAt +
                '}';
    }
}
