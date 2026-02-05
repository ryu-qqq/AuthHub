package com.ryuqq.authhub.adapter.out.persistence.tenantservice.mapper;

import com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.TenantServiceJpaEntity;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import org.springframework.stereotype.Component;

/**
 * TenantServiceJpaEntityMapper - Entity <-> Domain 변환 Mapper
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 TenantService 간 변환을 담당합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantServiceJpaEntityMapper {

    /**
     * Domain -> Entity 변환
     *
     * @param domain TenantService 도메인
     * @return TenantServiceJpaEntity
     */
    public TenantServiceJpaEntity toEntity(TenantService domain) {
        return TenantServiceJpaEntity.of(
                domain.tenantServiceIdValue(),
                domain.tenantIdValue(),
                domain.serviceIdValue(),
                domain.getStatus(),
                domain.subscribedAt(),
                domain.createdAt(),
                domain.updatedAt());
    }

    /**
     * Entity -> Domain 변환
     *
     * @param entity TenantServiceJpaEntity
     * @return TenantService 도메인
     */
    public TenantService toDomain(TenantServiceJpaEntity entity) {
        return TenantService.reconstitute(
                TenantServiceId.of(entity.getId()),
                TenantId.of(entity.getTenantId()),
                ServiceId.of(entity.getServiceId()),
                entity.getStatus(),
                entity.getSubscribedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
