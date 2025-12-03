package com.ryuqq.authhub.adapter.out.persistence.tenant.mapper;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

/**
 * TenantJpaEntityMapper - Tenant Domain ↔ Entity 변환
 *
 * <p>Domain 객체와 JPA Entity 간의 양방향 변환을 담당합니다.
 *
 * <p><strong>시간 변환 전략:</strong>
 *
 * <ul>
 *   <li>Domain: Instant (UTC)
 *   <li>Entity: LocalDateTime (UTC Zone 기준 변환)
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class TenantJpaEntityMapper {

    private static final ZoneId UTC = ZoneId.of("UTC");

    /**
     * Domain → Entity 변환
     *
     * @param tenant Tenant Domain 객체
     * @return TenantJpaEntity
     */
    public TenantJpaEntity toEntity(Tenant tenant) {
        return TenantJpaEntity.of(
                tenant.tenantIdValue(),
                tenant.tenantNameValue(),
                tenant.getTenantStatus(),
                toLocalDateTime(tenant.createdAt()),
                toLocalDateTime(tenant.updatedAt()));
    }

    /**
     * Entity → Domain 변환
     *
     * <p>Domain.reconstitute() 메서드를 사용하여 영속화된 데이터를 복원합니다.
     *
     * @param entity TenantJpaEntity
     * @return Tenant Domain 객체
     */
    public Tenant toDomain(TenantJpaEntity entity) {
        return Tenant.reconstitute(
                TenantId.of(entity.getId()),
                TenantName.of(entity.getName()),
                entity.getStatus(),
                toInstant(entity.getCreatedAt()),
                toInstant(entity.getUpdatedAt()));
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, UTC);
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(UTC).toInstant();
    }
}
