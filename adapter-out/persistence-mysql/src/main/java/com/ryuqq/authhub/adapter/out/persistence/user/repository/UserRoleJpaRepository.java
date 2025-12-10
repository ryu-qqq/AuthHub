package com.ryuqq.authhub.adapter.out.persistence.user.repository;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserRoleJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserRoleJpaRepository - 사용자 역할 JPA Repository
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>JpaRepository 상속 필수
 *   <li>Long 타입 ID 사용
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserRoleJpaRepository extends JpaRepository<UserRoleJpaEntity, Long> {

    /**
     * 사용자 ID와 역할 ID로 사용자 역할을 조회합니다.
     *
     * @param userId 사용자 UUID
     * @param roleId 역할 UUID
     * @return 사용자 역할 (없으면 Optional.empty())
     */
    Optional<UserRoleJpaEntity> findByUserIdAndRoleId(UUID userId, UUID roleId);

    /**
     * 사용자 ID로 모든 역할을 조회합니다.
     *
     * @param userId 사용자 UUID
     * @return 사용자 역할 목록
     */
    List<UserRoleJpaEntity> findAllByUserId(UUID userId);

    /**
     * 사용자에게 특정 역할이 할당되어 있는지 확인합니다.
     *
     * @param userId 사용자 UUID
     * @param roleId 역할 UUID
     * @return 할당 여부
     */
    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);

    /**
     * 사용자 역할을 삭제합니다.
     *
     * @param userId 사용자 UUID
     * @param roleId 역할 UUID
     */
    void deleteByUserIdAndRoleId(UUID userId, UUID roleId);
}
