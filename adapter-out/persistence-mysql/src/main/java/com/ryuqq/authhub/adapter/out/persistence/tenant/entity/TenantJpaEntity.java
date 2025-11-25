package com.ryuqq.authhub.adapter.out.persistence.tenant.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
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
 * TenantJpaEntity - Tenant JPA Entity
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(name = "tenants")
public class TenantJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TenantStatus status;

    protected TenantJpaEntity() {}

    private TenantJpaEntity(
            Long id,
            String name,
            String description,
            Long organizationId,
            TenantStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
        this.status = status;
    }

    public static TenantJpaEntity of(
            Long id,
            String name,
            String description,
            Long organizationId,
            TenantStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new TenantJpaEntity(
                id, name, description, organizationId, status, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public TenantStatus getStatus() {
        return status;
    }
}
