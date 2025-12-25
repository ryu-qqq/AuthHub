package com.ryuqq.authhub.adapter.out.persistence.user.repository;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserJpaRepository - 사용자 JPA Repository (Command 전용)
 *
 * <p>Spring Data JPA 인터페이스로 Command(CUD) 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>save() - 사용자 저장/수정
 *   <li>delete() - 사용자 삭제
 *   <li>deleteById() - ID로 삭제
 *   <li>findByUserId() - UUID로 사용자 조회 (UPDATE용)
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: UserJpaRepository (JPA)
 *   <li>Query: UserQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    /**
     * UUID로 사용자 조회 (UPDATE 작업용)
     *
     * <p>UPDATE 작업 시 기존 Entity의 JPA internal ID를 유지하기 위해 사용합니다.
     *
     * @param userId 사용자 UUID
     * @return Optional containing the entity if found
     */
    Optional<UserJpaEntity> findByUserId(UUID userId);
}
