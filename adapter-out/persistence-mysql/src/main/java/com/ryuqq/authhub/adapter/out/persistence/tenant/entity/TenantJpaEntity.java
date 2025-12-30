package com.ryuqq.authhub.adapter.out.persistence.tenant.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TenantJpaEntity - 테넌트 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>tenantId(UUID)를 PK로 사용
 *   <li>UUIDv7은 시간순 정렬 가능하여 B-tree 인덱스 성능 우수
 *   <li>분산 환경에서 충돌 없는 고유 ID 생성
 * </ul>
 *
 * <p><strong>Lombok 금지:</strong>
 *
 * <ul>
 *   <li>Plain Java getter 사용
 *   <li>Setter 제공 금지
 *   <li>명시적 생성자 제공
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Entity
@Table(name = "tenants")
public class TenantJpaEntity extends BaseAuditEntity {

    /** 테넌트 UUID - UUIDv7 (Primary Key) */
    @Id
    @Column(name = "tenant_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID tenantId;

    /** 테넌트 이름 */
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    /** 테넌트 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TenantStatus status;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected TenantJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     *
     * @param tenantId 테넌트 UUID (PK)
     * @param name 테넌트 이름
     * @param status 테넌트 상태
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     */
    private TenantJpaEntity(
            UUID tenantId,
            String name,
            TenantStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.tenantId = tenantId;
        this.name = name;
        this.status = status;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * @param tenantId 테넌트 UUID (PK)
     * @param name 테넌트 이름
     * @param status 테넌트 상태
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return TenantJpaEntity 인스턴스
     */
    public static TenantJpaEntity of(
            UUID tenantId,
            String name,
            TenantStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new TenantJpaEntity(tenantId, name, status, createdAt, updatedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public UUID getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public TenantStatus getStatus() {
        return status;
    }
}
