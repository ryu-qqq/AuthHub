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
 * <p>멀티테넌시 지원을 위한 테넌트 정보를 저장합니다.
 *
 * <p><strong>Domain 매핑:</strong>
 *
 * <ul>
 *   <li>Long id ← TenantId
 *   <li>String name ← TenantName
 *   <li>TenantStatus status ← TenantStatus
 *   <li>LocalDateTime createdAt ← Instant (Mapper에서 변환)
 *   <li>LocalDateTime updatedAt ← Instant (Mapper에서 변환)
 * </ul>
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TenantStatus status;

    protected TenantJpaEntity() {}

    private TenantJpaEntity(
            Long id,
            String name,
            TenantStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public static TenantJpaEntity of(
            Long id,
            String name,
            TenantStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new TenantJpaEntity(id, name, status, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TenantStatus getStatus() {
        return status;
    }
}
