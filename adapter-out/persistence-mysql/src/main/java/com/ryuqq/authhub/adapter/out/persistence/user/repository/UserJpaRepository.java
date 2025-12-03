package com.ryuqq.authhub.adapter.out.persistence.user.repository;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserJpaRepository - User JPA Repository (Command)
 *
 * <p>Command 작업 전용 Repository입니다. JpaRepository의 기본 메서드만 사용합니다.
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
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {}
