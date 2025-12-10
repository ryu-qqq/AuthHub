package com.ryuqq.authhub.adapter.out.persistence.permission.repository;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
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
public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, Long> {}
