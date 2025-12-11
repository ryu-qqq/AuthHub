package com.ryuqq.authhub.adapter.out.persistence.organization.repository;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * OrganizationJpaRepository - 조직 JPA Repository (Command 전용)
 *
 * <p>Spring Data JPA 인터페이스로 Command(CUD) 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>save() - 조직 저장/수정
 *   <li>delete() - 조직 삭제
 *   <li>deleteById() - ID로 삭제
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: OrganizationJpaRepository (JPA)
 *   <li>Query: OrganizationQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface OrganizationJpaRepository extends JpaRepository<OrganizationJpaEntity, Long> {}
