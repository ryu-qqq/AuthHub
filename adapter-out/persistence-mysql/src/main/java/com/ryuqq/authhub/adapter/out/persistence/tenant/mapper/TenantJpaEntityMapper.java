package com.ryuqq.authhub.adapter.out.persistence.tenant.mapper;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.springframework.stereotype.Component;

/**
 * TenantJpaEntityMapper - Entity ↔ Domain 변환 Mapper
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 Tenant 간 변환을 담당합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>Tenant → TenantJpaEntity (저장용)
 *   <li>TenantJpaEntity → Tenant (조회용)
 * </ul>
 *
 * <p><strong>시간 처리:</strong>
 *
 * <ul>
 *   <li>Domain: Instant (UTC)
 *   <li>Entity: Instant (UTC) - 변환 없이 직접 전달
 * </ul>
 *
 * <p><strong>Hexagonal Architecture 관점:</strong>
 *
 * <ul>
 *   <li>Adapter Layer의 책임
 *   <li>Domain과 Infrastructure 기술 분리
 *   <li>Domain은 JPA 의존성 없음
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantJpaEntityMapper {

    /**
     * Domain → Entity 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>신규 Tenant 저장
     *   <li>기존 Tenant 수정 (Hibernate Dirty Checking)
     * </ul>
     *
     * <p><strong>변환 규칙:</strong>
     *
     * <ul>
     *   <li>tenantId: Domain.tenantIdValue().toString() → Entity.tenantId (String)
     *   <li>name: Domain.nameValue() → Entity.name
     *   <li>status: Domain.getStatus() → Entity.status
     *   <li>createdAt: Instant → Instant (직접 전달)
     *   <li>updatedAt: Instant → Instant (직접 전달)
     *   <li>deletedAt: DeletionStatus.deletedAt() → Instant (직접 전달)
     * </ul>
     *
     * @param domain Tenant 도메인
     * @return TenantJpaEntity
     */
    public TenantJpaEntity toEntity(Tenant domain) {
        DeletionStatus deletionStatus = domain.getDeletionStatus();
        return TenantJpaEntity.of(
                domain.tenantIdValue().toString(),
                domain.nameValue(),
                domain.getStatus(),
                domain.createdAt(),
                domain.updatedAt(),
                deletionStatus.deletedAt());
    }

    /**
     * Entity → Domain 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>데이터베이스에서 조회한 Entity를 Domain으로 변환
     *   <li>Application Layer로 전달
     * </ul>
     *
     * <p><strong>변환 규칙:</strong>
     *
     * <ul>
     *   <li>tenantId: Entity.tenantId (String) → TenantId VO
     *   <li>name: Entity.name → TenantName VO
     *   <li>status: Entity.status → TenantStatus Enum
     *   <li>deletedAt: Entity.getDeletedAt() → DeletionStatus
     *   <li>createdAt: Instant → Instant (직접 전달)
     *   <li>updatedAt: Instant → Instant (직접 전달)
     * </ul>
     *
     * @param entity TenantJpaEntity
     * @return Tenant 도메인
     */
    public Tenant toDomain(TenantJpaEntity entity) {
        return Tenant.reconstitute(
                TenantId.of(entity.getTenantId()),
                TenantName.of(entity.getName()),
                entity.getStatus(),
                DeletionStatus.reconstitute(entity.isDeleted(), entity.getDeletedAt()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
