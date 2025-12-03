package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RoleJpaRepository - Role JPA Repository (Command)
 *
 * <p>Command 작업 전용 Repository입니다. JpaRepository의 기본 메서드만 사용합니다.
 *
 * <p><strong>사용 가능한 메서드:</strong>
 *
 * <ul>
 *   <li>save(entity) - 신규 저장 및 수정
 *   <li>findById(id) - ID로 조회 (Command에서 수정 전 조회용)
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
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Long> {}
