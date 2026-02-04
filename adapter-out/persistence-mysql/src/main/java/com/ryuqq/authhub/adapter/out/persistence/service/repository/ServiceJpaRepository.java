package com.ryuqq.authhub.adapter.out.persistence.service.repository;

import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ServiceJpaRepository - 서비스 JPA Repository (Command 전용)
 *
 * <p>Spring Data JPA 인터페이스로 Command(CUD) 작업을 담당합니다.
 *
 * <p><strong>Long PK 전략:</strong>
 *
 * <ul>
 *   <li>serviceId(Long) Auto Increment PK
 *   <li>findById()로 Long ID 직접 조회 가능
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>save() - 서비스 저장/수정
 *   <li>delete() - 서비스 삭제
 *   <li>findById() - ID로 서비스 조회
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: ServiceJpaRepository (JPA)
 *   <li>Query: ServiceQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface ServiceJpaRepository extends JpaRepository<ServiceJpaEntity, Long> {}
