package com.ryuqq.authhub.adapter.out.persistence.common;

import com.ryuqq.authhub.adapter.out.persistence.auth.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.auth.user.repository.UserJpaRepository;
import com.ryuqq.authhub.domain.auth.user.UserId;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * UserId 변환 컴포넌트 - Domain UserId (UUID) ↔ Persistence Long FK 변환.
 *
 * <p>Domain Layer의 UserId (UUID 기반 Value Object)와 Persistence Layer의 Long FK 간 변환을 담당합니다.
 * Anti-Corruption Layer의 일부로서, Domain과 Persistence 간의 ID 매핑을 캡슐화합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>UserId (UUID) → Long FK 변환 (저장 시)</li>
 *   <li>Long FK → UserId (UUID) 변환 (조회 시)</li>
 *   <li>존재하지 않는 User에 대한 예외 처리</li>
 *   <li>변환 로직 중앙화 및 재사용성 제공</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Null 안전성 - Objects.requireNonNull() 사용</li>
 *   <li>✅ Long FK 전략 지원 - JPA 관계 어노테이션 없이 ID만 사용</li>
 * </ul>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>UserProfile 저장 시 UserId → Long 변환</li>
 *   <li>UserProfile 조회 시 Long → UserId 변환</li>
 *   <li>다른 Aggregate에서 User 참조 시 ID 변환</li>
 * </ul>
 *
 * <p><strong>성능 고려사항:</strong></p>
 * <ul>
 *   <li>DB 조회가 발생하므로 필요한 경우에만 호출</li>
 *   <li>향후 캐싱 적용 가능 (User ID 매핑은 변경 빈도 낮음)</li>
 *   <li>Batch 조회 메서드 추가 가능 (대량 변환 시)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class UserIdResolver {

    private final UserJpaRepository userJpaRepository;

    /**
     * UserIdResolver 생성자.
     *
     * @param userJpaRepository UserJpaEntity를 위한 JPA Repository (null 불가)
     * @throws NullPointerException userJpaRepository가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserIdResolver(final UserJpaRepository userJpaRepository) {
        this.userJpaRepository = Objects.requireNonNull(
                userJpaRepository,
                "userJpaRepository cannot be null"
        );
    }

    /**
     * Domain UserId (UUID)를 Persistence Long FK로 변환합니다.
     *
     * <p>UserProfile 저장 시 userId를 Long FK로 변환하기 위해 사용됩니다.
     * User 테이블에서 uid로 조회하여 id (Long PK)를 반환합니다.</p>
     *
     * <p><strong>동작 방식:</strong></p>
     * <pre>
     * 1. UserId.asString() → UUID 문자열 추출
     * 2. UserJpaRepository.findByUid(uuid) → User 조회
     * 3. UserJpaEntity.getId() → Long PK 반환
     * </pre>
     *
     * <p><strong>사용 예시:</strong></p>
     * <pre>
     * UserId userId = UserId.generate();
     * Long userIdAsLong = userIdResolver.resolveToLongId(userId);
     * // userIdAsLong을 UserProfileJpaEntity.userId에 저장
     * </pre>
     *
     * @param userId Domain UserId (UUID 기반, null 불가)
     * @return User 테이블의 Long PK
     * @throws NullPointerException userId가 null인 경우
     * @throws IllegalStateException userId에 해당하는 User가 존재하지 않는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Long resolveToLongId(final UserId userId) {
        Objects.requireNonNull(userId, "UserId cannot be null");

        return userJpaRepository.findByUid(userId.asString())
                .map(UserJpaEntity::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "User not found for UserId: " + userId.asString() + ". " +
                        "Cannot resolve to Long FK. Please ensure the User exists before creating UserProfile."
                ));
    }

    /**
     * Persistence Long FK를 Domain UserId (UUID)로 변환합니다.
     *
     * <p>UserProfile 조회 시 Long FK를 UserId로 변환하기 위해 사용됩니다.
     * User 테이블에서 id (Long PK)로 조회하여 uid (UUID)를 UserId로 변환합니다.</p>
     *
     * <p><strong>동작 방식:</strong></p>
     * <pre>
     * 1. UserJpaRepository.findById(userIdAsLong) → User 조회
     * 2. UserJpaEntity.getUid() → UUID 문자열 추출
     * 3. UserId.fromString(uuid) → UserId Value Object 생성
     * </pre>
     *
     * <p><strong>사용 예시:</strong></p>
     * <pre>
     * Long userIdAsLong = entity.getUserId();  // UserProfileJpaEntity에서 추출
     * UserId userId = userIdResolver.resolveToUserId(userIdAsLong);
     * // userId를 UserProfile.reconstruct()에 전달
     * </pre>
     *
     * @param userIdAsLong User 테이블의 Long PK (null 불가)
     * @return Domain UserId (UUID 기반)
     * @throws NullPointerException userIdAsLong이 null인 경우
     * @throws IllegalStateException userIdAsLong에 해당하는 User가 존재하지 않는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserId resolveToUserId(final Long userIdAsLong) {
        Objects.requireNonNull(userIdAsLong, "userIdAsLong cannot be null");

        return userJpaRepository.findById(userIdAsLong)
                .map(entity -> UserId.fromString(entity.getUid()))
                .orElseThrow(() -> new IllegalStateException(
                        "User not found for Long FK: " + userIdAsLong + ". " +
                        "Cannot resolve to UserId. This indicates data inconsistency."
                ));
    }

    /**
     * Long FK가 유효한 User를 참조하는지 검증합니다.
     *
     * <p>UserProfile 저장 전 User 존재 여부를 사전 검증할 때 사용할 수 있습니다.</p>
     *
     * @param userIdAsLong 검증할 Long FK (null 불가)
     * @return User가 존재하면 true, 아니면 false
     * @throws NullPointerException userIdAsLong이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean exists(final Long userIdAsLong) {
        Objects.requireNonNull(userIdAsLong, "userIdAsLong cannot be null");
        return userJpaRepository.existsById(userIdAsLong);
    }

    /**
     * UserId (UUID)가 유효한 User를 참조하는지 검증합니다.
     *
     * <p>UserProfile 생성 전 User 존재 여부를 사전 검증할 때 사용할 수 있습니다.</p>
     *
     * @param userId 검증할 UserId (null 불가)
     * @return User가 존재하면 true, 아니면 false
     * @throws NullPointerException userId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean exists(final UserId userId) {
        Objects.requireNonNull(userId, "UserId cannot be null");
        return userJpaRepository.existsByUid(userId.asString());
    }
}
