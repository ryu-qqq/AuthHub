package com.ryuqq.authhub.adapter.out.persistence.endpointpermission.repository;

import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.entity.EndpointPermissionJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * EndpointPermissionJpaRepository - 엔드포인트 권한 JPA Repository (Command 전용)
 *
 * <p>JPA 기반 저장/수정/삭제 작업을 담당합니다.
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: EndpointPermissionJpaRepository (JPA) - save, delete
 *   <li>Query: EndpointPermissionQueryDslRepository (QueryDSL) - 복잡한 조회
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>단순 저장/조회 메서드만 제공
 *   <li>복잡한 쿼리는 QueryDsl Repository에서 처리
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public interface EndpointPermissionJpaRepository
        extends JpaRepository<EndpointPermissionJpaEntity, Long> {

    /**
     * UUID로 엔드포인트 권한 조회
     *
     * @param endpointPermissionId 엔드포인트 권한 UUID
     * @return Optional<EndpointPermissionJpaEntity>
     */
    Optional<EndpointPermissionJpaEntity> findByEndpointPermissionId(UUID endpointPermissionId);
}
