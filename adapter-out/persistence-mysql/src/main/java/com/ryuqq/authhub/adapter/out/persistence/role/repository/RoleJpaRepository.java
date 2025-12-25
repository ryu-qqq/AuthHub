package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RoleJpaRepository - 역할 JPA Repository (Command 전용)
 *
 * <p>Spring Data JPA 인터페이스로 Command(CUD) 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>save() - 역할 저장/수정
 *   <li>delete() - 역할 삭제
 *   <li>deleteById() - ID로 삭제
 *   <li>findByRoleId() - UUID로 역할 조회 (UPDATE용)
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: RoleJpaRepository (JPA)
 *   <li>Query: RoleQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Long> {

    /**
     * UUID로 역할 조회 (UPDATE 작업용)
     *
     * <p>UPDATE 작업 시 기존 Entity의 JPA internal ID를 유지하기 위해 사용합니다.
     *
     * @param roleId 역할 UUID
     * @return Optional containing the entity if found
     */
    Optional<RoleJpaEntity> findByRoleId(UUID roleId);
}
