package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import org.springframework.stereotype.Component;

/**
 * RoleJpaEntityMapper - Entity ↔ Domain 변환 Mapper
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 Role 간 변환을 담당합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>Role → RoleJpaEntity (저장용)
 *   <li>RoleJpaEntity → Role (조회용)
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
public class RoleJpaEntityMapper {

    /**
     * Domain → Entity 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>신규 Role 저장
     *   <li>기존 Role 수정 (Hibernate Dirty Checking)
     * </ul>
     *
     * <p><strong>변환 규칙:</strong>
     *
     * <ul>
     *   <li>roleId: Domain.roleIdValue() → Entity.roleId (Long)
     *   <li>tenantId: Domain.tenantIdValue() → Entity.tenantId (String)
     *   <li>name: Domain.nameValue() → Entity.name
     *   <li>displayName: Domain.displayNameValue() → Entity.displayName
     *   <li>description: Domain.descriptionValue() → Entity.description
     *   <li>type: Domain.getType() → Entity.type
     *   <li>createdAt: Instant → Instant (직접 전달)
     *   <li>updatedAt: Instant → Instant (직접 전달)
     *   <li>deletedAt: DeletionStatus.deletedAt() → Instant (직접 전달)
     * </ul>
     *
     * @param domain Role 도메인
     * @return RoleJpaEntity
     */
    public RoleJpaEntity toEntity(Role domain) {
        DeletionStatus deletionStatus = domain.getDeletionStatus();
        return RoleJpaEntity.of(
                domain.roleIdValue(),
                domain.tenantIdValue(),
                domain.serviceIdValue(),
                domain.nameValue(),
                domain.displayNameValue(),
                domain.descriptionValue(),
                domain.getType(),
                domain.getScope(),
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
     *   <li>roleId: Entity.roleId (Long) → RoleId VO
     *   <li>tenantId: Entity.tenantId (String) → TenantId VO (nullable)
     *   <li>name: Entity.name → RoleName VO
     *   <li>displayName: Entity.displayName → String
     *   <li>description: Entity.description → String
     *   <li>type: Entity.type → RoleType Enum
     *   <li>deletedAt: Entity.getDeletedAt() → DeletionStatus
     *   <li>createdAt: Instant → Instant (직접 전달)
     *   <li>updatedAt: Instant → Instant (직접 전달)
     * </ul>
     *
     * @param entity RoleJpaEntity
     * @return Role 도메인
     */
    public Role toDomain(RoleJpaEntity entity) {
        return Role.reconstitute(
                RoleId.of(entity.getRoleId()),
                parseTenantId(entity.getTenantId()),
                ServiceId.fromNullable(entity.getServiceId()),
                RoleName.of(entity.getName()),
                entity.getDisplayName(),
                entity.getDescription(),
                entity.getType(),
                entity.getScope(),
                DeletionStatus.reconstitute(entity.isDeleted(), entity.getDeletedAt()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * nullable한 문자열로부터 TenantId 변환
     *
     * @param tenantId 테넌트 ID 문자열 (nullable)
     * @return TenantId 또는 null
     */
    private TenantId parseTenantId(String tenantId) {
        return TenantId.fromNullable(tenantId);
    }
}
