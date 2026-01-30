package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RoleJpaRepository - 역할 JPA Repository (Command 전용)
 *
 * <p>Spring Data JPA 인터페이스로 Command(CUD) 작업을 담당합니다.
 *
 * <p><strong>Long PK 전략:</strong>
 *
 * <ul>
 *   <li>roleId(Long)를 PK로 사용 (Auto Increment)
 *   <li>findById()로 Long ID 직접 조회 가능
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>save() - 역할 저장/수정
 *   <li>delete() - 역할 삭제
 *   <li>deleteById() - ID로 삭제
 *   <li>findById() - ID로 역할 조회
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
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Long> {}
