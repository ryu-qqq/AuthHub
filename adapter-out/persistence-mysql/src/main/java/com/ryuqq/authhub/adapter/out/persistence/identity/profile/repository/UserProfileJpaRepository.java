package com.ryuqq.authhub.adapter.out.persistence.identity.profile.repository;

import com.ryuqq.authhub.adapter.out.persistence.identity.profile.entity.UserProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserProfileJpaEntity를 위한 Spring Data JPA Repository.
 *
 * <p>UserProfile Aggregate를 위한 CRUD 및 조회 메서드를 제공합니다.
 * Spring Data JPA가 자동으로 구현체를 생성하며, QueryDSL과 함께 사용할 수 있습니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>UserProfileJpaEntity 저장 및 조회</li>
 *   <li>uid (UUID 문자열) 기반 프로필 검색</li>
 *   <li>userId (Long FK) 기반 프로필 검색</li>
 *   <li>nickname 기반 중복 확인 및 검색</li>
 *   <li>JPA 기반 영속성 관리</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Long FK 전략 준수 - 관계 어노테이션 사용 금지</li>
 *   <li>✅ 메서드 명명 규칙 - Spring Data JPA 쿼리 메서드 규칙 준수</li>
 *   <li>✅ Optional 반환 - 단건 조회 시 Optional 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 * </ul>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>신규 사용자 등록 시 Profile 생성 및 저장</li>
 *   <li>UserId(Long)로 Profile 조회</li>
 *   <li>uid(UUID)로 Profile 조회</li>
 *   <li>닉네임 중복 확인</li>
 *   <li>프로필 정보 업데이트</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public interface UserProfileJpaRepository extends JpaRepository<UserProfileJpaEntity, Long> {

    /**
     * uid (UUID 문자열)로 UserProfileJpaEntity를 조회합니다.
     *
     * <p>Domain의 UserProfileId.value().toString()에 해당하는 uid로 프로필을 찾습니다.
     * 프로필 조회 시 주로 사용됩니다.</p>
     *
     * <p><strong>쿼리:</strong></p>
     * <pre>
     * SELECT up FROM UserProfileJpaEntity up WHERE up.uid = :uid
     * </pre>
     *
     * @param uid 프로필 고유 식별자 (UUID 문자열, null 불가)
     * @return Optional로 감싼 UserProfileJpaEntity (존재하지 않으면 Empty)
     * @throws IllegalArgumentException uid가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Optional<UserProfileJpaEntity> findByUid(String uid);

    /**
     * userId (Long FK)로 UserProfileJpaEntity를 조회합니다.
     *
     * <p>Domain의 UserId를 Long으로 변환한 userId로 프로필을 찾습니다.
     * 사용자별 프로필 조회 시 주로 사용됩니다.</p>
     *
     * <p><strong>쿼리:</strong></p>
     * <pre>
     * SELECT up FROM UserProfileJpaEntity up WHERE up.userId = :userId
     * </pre>
     *
     * <p><strong>Zero-Tolerance 규칙:</strong> Long FK 전략 사용</p>
     * <ul>
     *   <li>✅ {@code findByUserId(Long userId)} (올바른 방식)</li>
     *   <li>❌ 관계 어노테이션 사용 (금지된 방식)</li>
     * </ul>
     *
     * @param userId 사용자 ID (Long FK, null 불가)
     * @return Optional로 감싼 UserProfileJpaEntity (존재하지 않으면 Empty)
     * @throws IllegalArgumentException userId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Optional<UserProfileJpaEntity> findByUserId(Long userId);

    /**
     * nickname으로 UserProfileJpaEntity 존재 여부를 확인합니다.
     *
     * <p>닉네임 중복 확인 시 사용합니다.
     * 전체 엔티티를 조회하지 않고 EXISTS 쿼리로 빠르게 확인합니다.</p>
     *
     * <p><strong>쿼리:</strong></p>
     * <pre>
     * SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END
     * FROM UserProfileJpaEntity up WHERE up.nickname = :nickname
     * </pre>
     *
     * @param nickname 닉네임 (null 불가)
     * @return nickname이 존재하면 true, 아니면 false
     * @throws IllegalArgumentException nickname이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    boolean existsByNickname(String nickname);

    /**
     * uid로 UserProfileJpaEntity 존재 여부를 확인합니다.
     *
     * <p>프로필이 이미 존재하는지 확인할 때 사용합니다.
     * 전체 엔티티를 조회하지 않고 EXISTS 쿼리로 빠르게 확인합니다.</p>
     *
     * <p><strong>쿼리:</strong></p>
     * <pre>
     * SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END
     * FROM UserProfileJpaEntity up WHERE up.uid = :uid
     * </pre>
     *
     * @param uid 프로필 고유 식별자 (UUID 문자열, null 불가)
     * @return uid가 존재하면 true, 아니면 false
     * @throws IllegalArgumentException uid가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    boolean existsByUid(String uid);

    /**
     * userId로 UserProfileJpaEntity 존재 여부를 확인합니다.
     *
     * <p>사용자가 이미 프로필을 가지고 있는지 확인할 때 사용합니다.
     * 전체 엔티티를 조회하지 않고 EXISTS 쿼리로 빠르게 확인합니다.</p>
     *
     * <p><strong>쿼리:</strong></p>
     * <pre>
     * SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END
     * FROM UserProfileJpaEntity up WHERE up.userId = :userId
     * </pre>
     *
     * @param userId 사용자 ID (Long FK, null 불가)
     * @return userId가 존재하면 true, 아니면 false
     * @throws IllegalArgumentException userId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    boolean existsByUserId(Long userId);
}
