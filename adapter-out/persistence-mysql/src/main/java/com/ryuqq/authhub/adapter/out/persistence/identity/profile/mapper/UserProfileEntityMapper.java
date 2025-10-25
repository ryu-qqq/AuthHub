package com.ryuqq.authhub.adapter.out.persistence.identity.profile.mapper;

import com.ryuqq.authhub.adapter.out.persistence.common.UserIdResolver;
import com.ryuqq.authhub.adapter.out.persistence.identity.profile.entity.UserProfileJpaEntity;
import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.identity.profile.UserProfile;
import com.ryuqq.authhub.domain.identity.profile.vo.Bio;
import com.ryuqq.authhub.domain.identity.profile.vo.Nickname;
import com.ryuqq.authhub.domain.identity.profile.vo.ProfileImageUrl;
import com.ryuqq.authhub.domain.identity.profile.vo.UserProfileId;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Domain의 UserProfile과 JPA의 UserProfileJpaEntity 간 변환을 담당하는 Mapper.
 *
 * <p>Persistence Adapter에서 Domain Layer와 Persistence Layer 간의 데이터 변환을 수행합니다.
 * Anti-Corruption Layer 역할을 하며, Domain 모델의 순수성을 보호합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Domain UserProfile → JPA UserProfileJpaEntity 변환 (영속화 시)</li>
 *   <li>JPA UserProfileJpaEntity → Domain UserProfile 변환 (조회 시)</li>
 *   <li>Domain Value Object ↔ JPA 기본 타입 매핑</li>
 *   <li>UserId Long FK 변환 처리</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접 getter 호출, chaining 금지</li>
 *   <li>✅ Null 안전성 - null 체크 철저히 수행</li>
 *   <li>✅ Long FK 전략 - UserId를 Long으로 변환</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class UserProfileEntityMapper {

    private final UserIdResolver userIdResolver;

    /**
     * UserProfileEntityMapper 생성자.
     *
     * @param userIdResolver UserId ↔ Long FK 변환을 담당하는 Resolver (null 불가)
     * @throws NullPointerException userIdResolver가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserProfileEntityMapper(final UserIdResolver userIdResolver) {
        this.userIdResolver = Objects.requireNonNull(
                userIdResolver,
                "userIdResolver cannot be null"
        );
    }

    /**
     * Domain의 UserProfile을 JPA의 UserProfileJpaEntity로 변환합니다.
     *
     * <p>UserProfile Aggregate를 데이터베이스에 영속화하기 위해 JPA Entity로 변환합니다.</p>
     *
     * <p><strong>매핑 규칙:</strong></p>
     * <ul>
     *   <li>UserProfileId.asString() → UserProfileJpaEntity.uid (UUID 문자열)</li>
     *   <li>UserId → Long (Long FK 전략)</li>
     *   <li>Nickname.getValue() → UserProfileJpaEntity.nickname</li>
     *   <li>ProfileImageUrl.value() → UserProfileJpaEntity.profileImageUrl (nullable)</li>
     *   <li>Bio.value() → UserProfileJpaEntity.bio (nullable)</li>
     * </ul>
     *
     * <p><strong>Zero-Tolerance 규칙:</strong> Long FK 전략 적용</p>
     * <ul>
     *   <li>✅ UserId를 Long으로 변환</li>
     *   <li>❌ User Entity 참조 금지</li>
     * </ul>
     *
     * @param profile Domain UserProfile (null 불가)
     * @param userIdAsLong UserId를 Long으로 변환한 값 (null 불가)
     * @return UserProfileJpaEntity
     * @throws NullPointerException profile 또는 userIdAsLong이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserProfileJpaEntity toEntity(final UserProfile profile, final Long userIdAsLong) {
        Objects.requireNonNull(profile, "UserProfile cannot be null");
        Objects.requireNonNull(userIdAsLong, "userIdAsLong cannot be null");

        return UserProfileJpaEntity.create(
                profile.getId().asString(),
                userIdAsLong,
                profile.getNicknameValue(),
                mapProfileImageUrlToString(profile.getProfileImageUrl()),
                mapBioToString(profile.getBio()),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    /**
     * JPA의 UserProfileJpaEntity를 Domain의 UserProfile으로 변환합니다.
     *
     * <p>데이터베이스에서 조회한 JPA Entity를 Domain Aggregate로 재구성합니다.
     * UserProfile.reconstruct() 팩토리 메서드를 사용하여 불변 객체를 생성합니다.</p>
     *
     * <p><strong>매핑 규칙:</strong></p>
     * <ul>
     *   <li>UserProfileJpaEntity.uid → UserProfileId (UUID 파싱)</li>
     *   <li>UserProfileJpaEntity.userId (Long) → UserId (Value Object 변환)</li>
     *   <li>UserProfileJpaEntity.nickname → Nickname</li>
     *   <li>UserProfileJpaEntity.profileImageUrl → ProfileImageUrl (nullable)</li>
     *   <li>UserProfileJpaEntity.bio → Bio (nullable)</li>
     * </ul>
     *
     * @param entity UserProfileJpaEntity (null 불가)
     * @return Domain UserProfile
     * @throws NullPointerException entity가 null인 경우
     * @throws IllegalArgumentException entity.uid가 유효하지 않은 UUID 형식인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserProfile toDomain(final UserProfileJpaEntity entity) {
        Objects.requireNonNull(entity, "UserProfileJpaEntity cannot be null");

        return UserProfile.reconstruct(
                UserProfileId.fromString(entity.getUid()),
                mapLongToUserId(entity.getUserId()),
                new Nickname(entity.getNickname()),
                mapStringToProfileImageUrl(entity.getProfileImageUrl()),
                mapStringToBio(entity.getBio()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Long FK를 Domain의 UserId로 변환합니다.
     *
     * <p><strong>Long FK 전략:</strong> UserProfileJpaEntity는 userId를 Long FK로 저장하지만,
     * Domain의 UserProfile은 UserId (UUID 기반 Value Object)를 사용합니다.
     * UserIdResolver를 통해 User 테이블을 조회하여 Long → UUID 변환을 수행합니다.</p>
     *
     * <p><strong>동작 방식:</strong></p>
     * <pre>
     * 1. UserIdResolver.resolveToUserId(Long) 호출
     * 2. User 테이블에서 id (Long PK)로 조회
     * 3. UserJpaEntity.uid (UUID) 추출
     * 4. UserId.fromString(uuid) 변환
     * </pre>
     *
     * @param userId Long FK (UserJpaEntity.id 참조, null 불가)
     * @return Domain UserId (UUID 기반)
     * @throws NullPointerException userId가 null인 경우
     * @throws IllegalStateException userId에 해당하는 User가 존재하지 않는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private UserId mapLongToUserId(final Long userId) {
        Objects.requireNonNull(userId, "userId cannot be null");
        return userIdResolver.resolveToUserId(userId);
    }

    /**
     * Domain의 ProfileImageUrl을 JPA의 String으로 변환합니다.
     *
     * <p>ProfileImageUrl의 value()가 기본 이미지 URL인 경우 null로 저장할 수도 있고,
     * 그대로 저장할 수도 있습니다. 현재는 그대로 저장하는 방식을 선택했습니다.</p>
     *
     * @param profileImageUrl Domain ProfileImageUrl (null 불가)
     * @return String (nullable)
     * @throws NullPointerException profileImageUrl이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private String mapProfileImageUrlToString(final ProfileImageUrl profileImageUrl) {
        Objects.requireNonNull(profileImageUrl, "ProfileImageUrl cannot be null");
        return profileImageUrl.value(); // nullable
    }

    /**
     * JPA의 String을 Domain의 ProfileImageUrl로 변환합니다.
     *
     * <p>null이거나 빈 문자열인 경우 기본 프로필 이미지를 반환합니다.</p>
     *
     * @param profileImageUrl String (nullable)
     * @return Domain ProfileImageUrl
     * @author AuthHub Team
     * @since 1.0.0
     */
    private ProfileImageUrl mapStringToProfileImageUrl(final String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.trim().isEmpty()) {
            return ProfileImageUrl.defaultImage();
        }
        return new ProfileImageUrl(profileImageUrl);
    }

    /**
     * Domain의 Bio를 JPA의 String으로 변환합니다.
     *
     * @param bio Domain Bio (null 불가)
     * @return String (nullable)
     * @throws NullPointerException bio가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private String mapBioToString(final Bio bio) {
        Objects.requireNonNull(bio, "Bio cannot be null");
        return bio.value(); // nullable
    }

    /**
     * JPA의 String을 Domain의 Bio로 변환합니다.
     *
     * <p>null인 경우 빈 Bio를 반환합니다.</p>
     *
     * @param bio String (nullable)
     * @return Domain Bio
     * @author AuthHub Team
     * @since 1.0.0
     */
    private Bio mapStringToBio(final String bio) {
        if (bio == null) {
            return Bio.empty();
        }
        return new Bio(bio);
    }
}
