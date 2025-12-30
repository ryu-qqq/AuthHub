package com.ryuqq.authhub.adapter.out.persistence.permission.repository;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionUsageJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PermissionUsageJpaRepository - 권한 사용 이력 JPA Repository (Command 전용)
 *
 * <p>Spring Data JPA 인터페이스로 Command(CUD) 작업을 담당합니다.
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>usageId(UUID)를 PK로 사용
 *   <li>findById()로 UUID 직접 조회 가능
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>save() - 사용 이력 저장/수정
 *   <li>delete() - 사용 이력 삭제
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: PermissionUsageJpaRepository (JPA)
 *   <li>Query: PermissionUsageQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PermissionUsageJpaRepository
        extends JpaRepository<PermissionUsageJpaEntity, UUID> {}
