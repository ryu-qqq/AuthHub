package com.ryuqq.authhub.adapter.out.persistence.tenant.mapper;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
 * <p><strong>시간 변환:</strong>
 *
 * <ul>
 *   <li>Domain: Instant (UTC)
 *   <li>Entity: LocalDateTime (UTC 기준)
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
     *   <li>신규 Tenant 저장 (ID가 null)
     *   <li>기존 Tenant 수정 (ID가 있음)
     * </ul>
     *
     * <p><strong>변환 규칙:</strong>
     *
     * <ul>
     *   <li>tenantId: Domain.tenantIdValue() → Entity.tenantId (UUID)
     *   <li>name: Domain.nameValue() → Entity.name
     *   <li>status: Domain.getStatus() → Entity.status
     *   <li>createdAt: Instant → LocalDateTime (UTC)
     *   <li>updatedAt: Instant → LocalDateTime (UTC)
     * </ul>
     *
     * @param domain Tenant 도메인
     * @return TenantJpaEntity
     */
    public TenantJpaEntity toEntity(Tenant domain) {
        return TenantJpaEntity.of(
                null,
                domain.tenantIdValue(),
                domain.nameValue(),
                domain.getStatus(),
                toLocalDateTime(domain.createdAt()),
                toLocalDateTime(domain.updatedAt()));
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
     *   <li>tenantId: Entity.tenantId → TenantId VO
     *   <li>name: Entity.name → TenantName VO
     *   <li>status: Entity.status → TenantStatus Enum
     *   <li>createdAt: LocalDateTime → Instant (UTC)
     *   <li>updatedAt: LocalDateTime → Instant (UTC)
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
                toInstant(entity.getCreatedAt()),
                toInstant(entity.getUpdatedAt()));
    }

    /** Instant → LocalDateTime 변환 (UTC 기준) */
    private LocalDateTime toLocalDateTime(java.time.Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    /** LocalDateTime → Instant 변환 (UTC 기준) */
    private java.time.Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
}
