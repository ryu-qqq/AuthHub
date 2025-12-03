package com.ryuqq.authhub.adapter.out.persistence.organization.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * OrganizationJpaEntity - Organization JPA Entity
 *
 * <p>테넌트 내의 조직 정보를 저장합니다.
 *
 * <p><strong>Domain 매핑:</strong>
 *
 * <ul>
 *   <li>Long id ← OrganizationId
 *   <li>String name ← OrganizationName
 *   <li>Long tenantId ← TenantId (Long FK 전략)
 *   <li>OrganizationStatus status ← OrganizationStatus
 *   <li>LocalDateTime createdAt ← Instant (Mapper에서 변환)
 *   <li>LocalDateTime updatedAt ← Instant (Mapper에서 변환)
 * </ul>
 *
 * <p><strong>Long FK 전략:</strong> JPA 관계 어노테이션 대신 Long 타입으로 외래 키를 직접 관리합니다.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(name = "organizations")
public class OrganizationJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrganizationStatus status;

    protected OrganizationJpaEntity() {}

    private OrganizationJpaEntity(
            Long id,
            String name,
            Long tenantId,
            OrganizationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.name = name;
        this.tenantId = tenantId;
        this.status = status;
    }

    public static OrganizationJpaEntity of(
            Long id,
            String name,
            Long tenantId,
            OrganizationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new OrganizationJpaEntity(id, name, tenantId, status, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public OrganizationStatus getStatus() {
        return status;
    }
}
