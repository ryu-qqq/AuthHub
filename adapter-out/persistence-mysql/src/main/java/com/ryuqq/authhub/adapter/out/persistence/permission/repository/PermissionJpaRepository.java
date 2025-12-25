package com.ryuqq.authhub.adapter.out.persistence.permission.repository;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PermissionJpaRepository - 권한 JPA Repository (Command 전용)
 *
 * <p>Spring Data JPA 인터페이스로 Command(CUD) 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>save() - 권한 저장/수정
 *   <li>delete() - 권한 삭제
 *   <li>deleteById() - ID로 삭제
 *   <li>findByPermissionId() - UUID로 권한 조회 (UPDATE용)
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: PermissionJpaRepository (JPA)
 *   <li>Query: PermissionQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, Long> {

    /**
     * UUID로 권한 조회 (UPDATE 작업용)
     *
     * <p>UPDATE 작업 시 기존 Entity의 JPA internal ID를 유지하기 위해 사용합니다.
     *
     * @param permissionId 권한 UUID
     * @return Optional containing the entity if found
     */
    Optional<PermissionJpaEntity> findByPermissionId(UUID permissionId);
}
