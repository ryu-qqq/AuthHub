package com.ryuqq.authhub.adapter.out.persistence.tenant.repository;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TenantJpaRepository - 테넌트 JPA Repository (Command 전용)
 *
 * <p>Spring Data JPA 인터페이스로 Command(CUD) 작업을 담당합니다.
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>tenantId(UUID)를 PK로 사용
 *   <li>findById()로 UUID 직접 조회 가능
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>save() - 테넌트 저장/수정
 *   <li>delete() - 테넌트 삭제
 *   <li>deleteById() - UUID로 삭제
 *   <li>findById() - UUID로 테넌트 조회
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: TenantJpaRepository (JPA)
 *   <li>Query: TenantQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface TenantJpaRepository extends JpaRepository<TenantJpaEntity, UUID> {}
