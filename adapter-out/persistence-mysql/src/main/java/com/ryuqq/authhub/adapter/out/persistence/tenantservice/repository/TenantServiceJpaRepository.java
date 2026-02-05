package com.ryuqq.authhub.adapter.out.persistence.tenantservice.repository;

import com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.TenantServiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TenantServiceJpaRepository - 테넌트-서비스 JPA Repository (Command 전용)
 *
 * <p>Spring Data JPA 인터페이스로 Command(CUD) 작업을 담당합니다.
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: TenantServiceJpaRepository (JPA)
 *   <li>Query: TenantServiceQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface TenantServiceJpaRepository extends JpaRepository<TenantServiceJpaEntity, Long> {}
