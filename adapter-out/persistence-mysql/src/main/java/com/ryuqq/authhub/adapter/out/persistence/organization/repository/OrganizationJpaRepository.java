package com.ryuqq.authhub.adapter.out.persistence.organization.repository;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * OrganizationJpaRepository - 조직 JPA Repository (Command 전용)
 *
 * <p>Spring Data JPA 인터페이스로 Command(CUD) 작업을 담당합니다.
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>organizationId(UUID)를 PK로 사용
 *   <li>findById()로 UUID 직접 조회 가능
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>save() - 조직 저장/수정
 *   <li>delete() - 조직 삭제
 *   <li>deleteById() - UUID로 삭제
 *   <li>findById() - UUID로 조직 조회
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
public interface OrganizationJpaRepository extends JpaRepository<OrganizationJpaEntity, String> {}
