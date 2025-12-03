package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RolePermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RolePermissionJpaRepository - Role-Permission 매핑 JPA Repository (Command)
 *
 * <p>Command 작업 전용 Repository입니다. JpaRepository의 기본 메서드만 사용합니다.
 *
 * <p><strong>사용 가능한 메서드:</strong>
 *
 * <ul>
 *   <li>save(entity) - 신규 저장 및 수정
 *   <li>saveAll(entities) - 다중 저장
 *   <li>deleteById(id) - ID로 삭제
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>커스텀 쿼리 메서드 정의 금지
 *   <li>@Query 어노테이션 사용 금지
 *   <li>Query 작업은 QueryDslRepository로 분리
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionJpaEntity, Long> {}
