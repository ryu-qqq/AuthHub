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

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrganizationStatus status;

    protected OrganizationJpaEntity() {}

    private OrganizationJpaEntity(
            Long id,
            String name,
            String description,
            OrganizationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public static OrganizationJpaEntity of(
            Long id,
            String name,
            String description,
            OrganizationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new OrganizationJpaEntity(id, name, description, status, createdAt, updatedAt);
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

    public OrganizationStatus getStatus() {
        return status;
    }
}
