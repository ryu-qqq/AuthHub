package com.ryuqq.authhub.adapter.out.persistence.organization.mapper;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

/**
 * OrganizationJpaEntityMapper - Organization Domain ↔ Entity 변환
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
public class OrganizationJpaEntityMapper {

    private static final ZoneId UTC = ZoneId.of("UTC");

    /**
     * Domain → Entity 변환
     *
     * @param organization Organization Domain 객체
     * @return OrganizationJpaEntity
     */
    public OrganizationJpaEntity toEntity(Organization organization) {
        return OrganizationJpaEntity.of(
                organization.organizationIdValue(),
                organization.organizationNameValue(),
                organization.tenantIdValue(),
                organization.getOrganizationStatus(),
                toLocalDateTime(organization.createdAt()),
                toLocalDateTime(organization.updatedAt()));
    }

    /**
     * Entity → Domain 변환
     *
     * <p>Domain.reconstitute() 메서드를 사용하여 영속화된 데이터를 복원합니다.
     *
     * @param entity OrganizationJpaEntity
     * @return Organization Domain 객체
     */
    public Organization toDomain(OrganizationJpaEntity entity) {
        return Organization.reconstitute(
                OrganizationId.of(entity.getId()),
                OrganizationName.of(entity.getName()),
                TenantId.of(entity.getTenantId()),
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
