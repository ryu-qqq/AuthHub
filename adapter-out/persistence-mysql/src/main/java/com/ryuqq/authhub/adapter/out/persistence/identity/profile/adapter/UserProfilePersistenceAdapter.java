package com.ryuqq.authhub.adapter.out.persistence.identity.profile.adapter;

import com.ryuqq.authhub.adapter.out.persistence.common.UserIdResolver;
import com.ryuqq.authhub.adapter.out.persistence.identity.profile.entity.UserProfileJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.identity.profile.mapper.UserProfileEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.identity.profile.repository.UserProfileJpaRepository;
import com.ryuqq.authhub.application.identity.port.out.CheckDuplicateNicknamePort;
import com.ryuqq.authhub.application.identity.port.out.SaveUserProfilePort;
import com.ryuqq.authhub.domain.identity.profile.UserProfile;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * UserProfile Persistence Adapter - Hexagonal Architecture의 Adapter-Out 구현체.
 *
 * <p>Application Layer의 Port를 구현하여 Domain과 Persistence Layer 간의 Anti-Corruption Layer 역할을 수행합니다.
 * JPA Repository와 Mapper를 조합하여 Domain Aggregate를 영속화하고 조회합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Domain UserProfile → JPA UserProfileJpaEntity 변환 및 저장</li>
 *   <li>JPA UserProfileJpaEntity → Domain UserProfile 변환</li>
 *   <li>Nickname 중복 확인</li>
 *   <li>JPA Dirty Checking을 활용한 업데이트 최적화</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Long FK 전략 - UserId를 Long으로 변환하여 사용</li>
 *   <li>✅ JPA Dirty Checking 활용 - UPDATE 쿼리 최적화</li>
 *   <li>✅ Null 안전성 - Objects.requireNonNull() 사용</li>
 * </ul>
 *
 * <p><strong>구현 패턴:</strong></p>
 * <ul>
 *   <li>신규 Profile 저장: JpaRepository.save() → INSERT</li>
 *   <li>기존 Profile 업데이트: findByUid() → entity.updateFrom() → Dirty Checking → UPDATE</li>
 *   <li>Nickname 중복 확인: JpaRepository.existsByNickname()</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class UserProfilePersistenceAdapter implements SaveUserProfilePort, CheckDuplicateNicknamePort {

    private final UserProfileJpaRepository userProfileJpaRepository;
    private final UserProfileEntityMapper userProfileEntityMapper;
    private final UserIdResolver userIdResolver;

    /**
     * UserProfilePersistenceAdapter 생성자.
     *
     * @param userProfileJpaRepository UserProfileJpaEntity를 위한 JPA Repository (null 불가)
     * @param userProfileEntityMapper Domain-Persistence 변환 Mapper (null 불가)
     * @param userIdResolver UserId ↔ Long FK 변환을 담당하는 Resolver (null 불가)
     * @throws NullPointerException repository, mapper 또는 resolver가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserProfilePersistenceAdapter(
            final UserProfileJpaRepository userProfileJpaRepository,
            final UserProfileEntityMapper userProfileEntityMapper,
            final UserIdResolver userIdResolver
    ) {
        this.userProfileJpaRepository = Objects.requireNonNull(
                userProfileJpaRepository,
                "userProfileJpaRepository cannot be null"
        );
        this.userProfileEntityMapper = Objects.requireNonNull(
                userProfileEntityMapper,
                "userProfileEntityMapper cannot be null"
        );
        this.userIdResolver = Objects.requireNonNull(
                userIdResolver,
                "userIdResolver cannot be null"
        );
    }

    /**
     * UserProfile Aggregate를 영속화합니다.
     *
     * <p>신규 Profile 생성 시 INSERT, 기존 Profile 수정 시 JPA Dirty Checking을 활용한 UPDATE를 수행합니다.</p>
     *
     * <p><strong>동작 방식:</strong></p>
     * <ul>
     *   <li>1. UserIdResolver를 통해 Domain UserId (UUID) → Long FK 변환</li>
     *   <li>2. uid로 기존 Entity 존재 여부 확인</li>
     *   <li>3-a. 기존 Entity 존재: updateFrom() → JPA Dirty Checking → UPDATE</li>
     *   <li>3-b. 기존 Entity 없음: toEntity() → save() → INSERT</li>
     *   <li>4. 저장된 Entity를 Domain으로 변환하여 반환</li>
     * </ul>
     *
     * <p><strong>Long FK 변환:</strong></p>
     * <ul>
     *   <li>UserIdResolver.resolveToLongId(UserId) → User 테이블 조회 → Long PK 반환</li>
     *   <li>User가 존재하지 않으면 IllegalStateException 발생</li>
     * </ul>
     *
     * @param profile 저장할 Domain UserProfile (null 불가)
     * @return 영속화된 UserProfile (JPA ID 포함)
     * @throws NullPointerException profile이 null인 경우
     * @throws IllegalStateException profile의 UserId에 해당하는 User가 존재하지 않는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public UserProfile save(final UserProfile profile) {
        Objects.requireNonNull(profile, "UserProfile cannot be null");

        final String uid = profile.getId().asString();
        final Long userIdAsLong = userIdResolver.resolveToLongId(profile.getUserId());

        // toEntity() 중복 호출 방지를 위해 한 번만 호출
        final UserProfileJpaEntity newOrUpdatedEntity = userProfileEntityMapper.toEntity(profile, userIdAsLong);
        final UserProfileJpaEntity entity = userProfileJpaRepository.findByUid(uid)
                .map(existingEntity -> {
                    // 기존 Entity 업데이트 (JPA Dirty Checking 활용)
                    existingEntity.updateFrom(newOrUpdatedEntity);
                    return existingEntity;
                })
                .orElse(newOrUpdatedEntity);

        final UserProfileJpaEntity savedEntity = userProfileJpaRepository.save(entity);
        return userProfileEntityMapper.toDomain(savedEntity);
    }

    /**
     * Nickname 중복 여부를 확인합니다.
     *
     * <p>UserProfile 생성 또는 Nickname 변경 시 중복 확인을 위해 사용됩니다.</p>
     *
     * @param nickname 중복 확인할 닉네임 (null 불가)
     * @return 중복이면 true, 아니면 false
     * @throws IllegalArgumentException nickname이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean existsByNickname(final String nickname) {
        Objects.requireNonNull(nickname, "nickname cannot be null");
        return userProfileJpaRepository.existsByNickname(nickname);
    }
}
