package com.ryuqq.authhub.adapter.out.persistence.auth.user.mapper;

import com.ryuqq.authhub.adapter.out.persistence.auth.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.auth.user.LastLoginAt;
import com.ryuqq.authhub.domain.auth.user.User;
import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.auth.user.UserStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain의 User와 JPA의 UserJpaEntity 간 변환을 담당하는 Mapper.
 *
 * <p>Persistence Adapter에서 Domain Layer와 Persistence Layer 간의 데이터 변환을 수행합니다.
 * Anti-Corruption Layer 역할을 하며, Domain 모델의 순수성을 보호합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Domain User → JPA UserJpaEntity 변환 (영속화 시)</li>
 *   <li>JPA UserJpaEntity → Domain User 변환 (조회 시)</li>
 *   <li>Domain Value Object ↔ JPA 기본 타입 매핑</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접 getter 호출, chaining 금지</li>
 *   <li>✅ Null 안전성 - null 체크 철저히 수행</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class UserEntityMapper {

    /**
     * Domain의 User를 JPA의 UserJpaEntity로 변환합니다.
     *
     * <p>User Aggregate를 데이터베이스에 영속화하기 위해 JPA Entity로 변환합니다.
     * User.getId()는 영속화 후 자동 생성되므로 변환 시 null입니다.</p>
     *
     * <p><strong>매핑 규칙:</strong></p>
     * <ul>
     *   <li>UserId.value() → UserJpaEntity.uid (UUID 문자열)</li>
     *   <li>UserStatus → UserStatusEnum (Enum 매핑)</li>
     *   <li>LastLoginAt.value() → UserJpaEntity.lastLoginAt (nullable Instant)</li>
     * </ul>
     *
     * @param user Domain User (null 불가)
     * @return UserJpaEntity
     * @throws NullPointerException user가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserJpaEntity toEntity(final User user) {
        Objects.requireNonNull(user, "User cannot be null");

        return UserJpaEntity.create(
                user.getId().value().toString(),
                mapToStatusEnum(user.getStatus()),
                mapToInstant(user.getLastLoginAt()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    /**
     * JPA의 UserJpaEntity를 Domain의 User로 변환합니다.
     *
     * <p>데이터베이스에서 조회한 JPA Entity를 Domain Aggregate로 재구성합니다.
     * User.reconstruct() 팩토리 메서드를 사용하여 불변 객체를 생성합니다.</p>
     *
     * <p><strong>매핑 규칙:</strong></p>
     * <ul>
     *   <li>UserJpaEntity.uid → UserId (UUID 파싱)</li>
     *   <li>UserStatusEnum → UserStatus (Enum 역매핑)</li>
     *   <li>UserJpaEntity.lastLoginAt → LastLoginAt (nullable)</li>
     * </ul>
     *
     * @param entity UserJpaEntity (null 불가)
     * @return Domain User
     * @throws NullPointerException entity가 null인 경우
     * @throws IllegalArgumentException entity.uid가 유효하지 않은 UUID 형식인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public User toDomain(final UserJpaEntity entity) {
        Objects.requireNonNull(entity, "UserJpaEntity cannot be null");

        return User.reconstruct(
                UserId.fromString(entity.getUid()),
                mapToStatus(entity.getStatus()),
                mapToLastLoginAt(entity.getLastLoginAt()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Domain의 UserStatus를 JPA의 UserStatusEnum으로 변환합니다.
     *
     * @param status Domain UserStatus (null 불가)
     * @return UserStatusEnum
     * @throws NullPointerException status가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private UserJpaEntity.UserStatusEnum mapToStatusEnum(final UserStatus status) {
        Objects.requireNonNull(status, "UserStatus cannot be null");

        return switch (status) {
            case ACTIVE -> UserJpaEntity.UserStatusEnum.ACTIVE;
            case INACTIVE -> UserJpaEntity.UserStatusEnum.INACTIVE;
            case SUSPENDED -> UserJpaEntity.UserStatusEnum.SUSPENDED;
        };
    }

    /**
     * JPA의 UserStatusEnum을 Domain의 UserStatus로 변환합니다.
     *
     * @param statusEnum JPA UserStatusEnum (null 불가)
     * @return Domain UserStatus
     * @throws NullPointerException statusEnum이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private UserStatus mapToStatus(final UserJpaEntity.UserStatusEnum statusEnum) {
        Objects.requireNonNull(statusEnum, "UserStatusEnum cannot be null");

        return switch (statusEnum) {
            case ACTIVE -> UserStatus.ACTIVE;
            case INACTIVE -> UserStatus.INACTIVE;
            case SUSPENDED -> UserStatus.SUSPENDED;
        };
    }

    /**
     * Domain의 LastLoginAt을 JPA의 Instant로 변환합니다.
     *
     * <p>LastLoginAt.value()가 null이면 null을 반환합니다 (한 번도 로그인하지 않은 경우).</p>
     *
     * @param lastLoginAt Domain LastLoginAt (null 불가)
     * @return Instant (nullable)
     * @throws NullPointerException lastLoginAt이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private Instant mapToInstant(final LastLoginAt lastLoginAt) {
        Objects.requireNonNull(lastLoginAt, "LastLoginAt cannot be null");
        return lastLoginAt.value(); // nullable
    }

    /**
     * JPA의 Instant를 Domain의 LastLoginAt으로 변환합니다.
     *
     * <p>instant가 null이면 LastLoginAt.neverLoggedIn()을 반환합니다.</p>
     *
     * @param instant Instant (nullable)
     * @return Domain LastLoginAt
     * @author AuthHub Team
     * @since 1.0.0
     */
    private LastLoginAt mapToLastLoginAt(final Instant instant) {
        if (instant == null) {
            return LastLoginAt.neverLoggedIn();
        }
        return LastLoginAt.of(instant);
    }
}
