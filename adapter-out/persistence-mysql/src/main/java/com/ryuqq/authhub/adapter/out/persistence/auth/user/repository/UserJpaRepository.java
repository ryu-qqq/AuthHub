package com.ryuqq.authhub.adapter.out.persistence.auth.user.repository;

import com.ryuqq.authhub.adapter.out.persistence.auth.user.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserJpaEntity를 위한 Spring Data JPA Repository.
 *
 * <p>User Aggregate를 위한 CRUD 및 조회 메서드를 제공합니다.
 * Spring Data JPA가 자동으로 구현체를 생성하며, QueryDSL과 함께 사용할 수 있습니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>UserJpaEntity 저장 및 조회</li>
 *   <li>uid (UUID 문자열) 기반 사용자 검색</li>
 *   <li>JPA 기반 영속성 관리</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Long FK 전략 준수 - 관계 어노테이션 사용 금지</li>
 *   <li>✅ 메서드 명명 규칙 - Spring Data JPA 쿼리 메서드 규칙 준수</li>
 *   <li>✅ Optional 반환 - 단건 조회 시 Optional 사용</li>
 * </ul>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>로그인 시 UserCredential에서 추출한 UserId(UUID)로 User 조회</li>
 *   <li>User 생성/수정 시 영속화</li>
 *   <li>사용자 상태 변경 후 저장</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    /**
     * uid (UUID 문자열)로 UserJpaEntity를 조회합니다.
     *
     * <p>Domain의 UserId.value().toString()에 해당하는 uid로 사용자를 찾습니다.
     * 로그인 시 UserCredential에서 추출한 UserId로 User를 조회할 때 주로 사용됩니다.</p>
     *
     * <p><strong>쿼리:</strong></p>
     * <pre>
     * SELECT u FROM UserJpaEntity u WHERE u.uid = :uid
     * </pre>
     *
     * @param uid 사용자 고유 식별자 (UUID 문자열, null 불가)
     * @return Optional로 감싼 UserJpaEntity (존재하지 않으면 Empty)
     * @throws IllegalArgumentException uid가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Optional<UserJpaEntity> findByUid(String uid);

    /**
     * uid로 UserJpaEntity 존재 여부를 확인합니다.
     *
     * <p>User가 이미 존재하는지 확인할 때 사용합니다.
     * 전체 엔티티를 조회하지 않고 EXISTS 쿼리로 빠르게 확인합니다.</p>
     *
     * <p><strong>쿼리:</strong></p>
     * <pre>
     * SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
     * FROM UserJpaEntity u WHERE u.uid = :uid
     * </pre>
     *
     * @param uid 사용자 고유 식별자 (UUID 문자열, null 불가)
     * @return uid가 존재하면 true, 아니면 false
     * @throws IllegalArgumentException uid가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    boolean existsByUid(String uid);
}
