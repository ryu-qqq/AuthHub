package com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

/**
 * TenantServiceJpaEntity - 테넌트-서비스 구독 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>Long PK 전략:</strong>
 *
 * <ul>
 *   <li>id(Long) Auto Increment PK
 *   <li>(tenant_id, service_id) UNIQUE 복합 유니크
 * </ul>
 *
 * <p><strong>BaseAuditEntity 상속:</strong>
 *
 * <ul>
 *   <li>createdAt, updatedAt (BaseAuditEntity)
 *   <li>SoftDelete 미사용 (ACTIVE/INACTIVE/SUSPENDED 상태로 관리)
 * </ul>
 *
 * <p><strong>Long FK 전략:</strong>
 *
 * <ul>
 *   <li>tenantId: String FK (Tenant PK)
 *   <li>serviceId: Long FK (Service PK)
 *   <li>JPA 관계 어노테이션 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Entity
@Table(
        name = "tenant_services",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "uk_tenant_service",
                        columnNames = {"tenant_id", "service_id"}))
public class TenantServiceJpaEntity extends BaseAuditEntity {

    /** 테넌트-서비스 ID (Primary Key, Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** 테넌트 ID (FK - String) */
    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    /** 서비스 ID (FK - Long) */
    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    /** 구독 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TenantServiceStatus status;

    /** 구독 일시 */
    @Column(name = "subscribed_at", nullable = false)
    private Instant subscribedAt;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected TenantServiceJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     */
    private TenantServiceJpaEntity(
            Long id,
            String tenantId,
            Long serviceId,
            TenantServiceStatus status,
            Instant subscribedAt,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.tenantId = tenantId;
        this.serviceId = serviceId;
        this.status = status;
        this.subscribedAt = subscribedAt;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * @param id 테넌트-서비스 ID (PK, nullable for new)
     * @param tenantId 테넌트 ID (FK)
     * @param serviceId 서비스 ID (FK)
     * @param status 구독 상태
     * @param subscribedAt 구독 일시
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return TenantServiceJpaEntity 인스턴스
     */
    public static TenantServiceJpaEntity of(
            Long id,
            String tenantId,
            Long serviceId,
            TenantServiceStatus status,
            Instant subscribedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new TenantServiceJpaEntity(
                id, tenantId, serviceId, status, subscribedAt, createdAt, updatedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public TenantServiceStatus getStatus() {
        return status;
    }

    public Instant getSubscribedAt() {
        return subscribedAt;
    }
}
