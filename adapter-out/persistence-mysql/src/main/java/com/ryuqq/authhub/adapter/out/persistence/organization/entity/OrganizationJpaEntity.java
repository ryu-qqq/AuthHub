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
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OrganizationJpaEntity - 조직 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>Long FK 전략:</strong>
 *
 * <ul>
 *   <li>tenantId는 UUID 타입으로 관리 (JPA 관계 어노테이션 금지)
 *   <li>테넌트와의 관계는 UUID를 통해 애플리케이션에서 관리
 * </ul>
 *
 * <p><strong>BaseAuditEntity 상속:</strong>
 *
 * <ul>
 *   <li>공통 감사 필드 상속: createdAt, updatedAt
 * </ul>
 *
 * <p><strong>UUIDv7 식별자:</strong>
 *
 * <ul>
 *   <li>organizationId는 UUID 타입으로 관리
 *   <li>애플리케이션에서 UUIDv7 생성
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
@Table(
        name = "organizations",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_organizations_tenant_name",
                    columnNames = {"tenant_id", "name"})
        },
        indexes = {
            @Index(name = "idx_organizations_tenant_id", columnList = "tenant_id"),
            @Index(name = "idx_organizations_status", columnList = "status")
        })
public class OrganizationJpaEntity extends BaseAuditEntity {

    /** 기본 키 - AUTO_INCREMENT (내부 Long ID) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 조직 UUID - UUIDv7 (외부 식별자) */
    @Column(
            name = "organization_id",
            nullable = false,
            unique = true,
            columnDefinition = "BINARY(16)")
    private UUID organizationId;

    /** 테넌트 UUID - FK (Long FK 전략: JPA 관계 어노테이션 금지) */
    @Column(name = "tenant_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID tenantId;

    /** 조직 이름 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 조직 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrganizationStatus status;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected OrganizationJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     *
     * @param id 내부 기본 키
     * @param organizationId 조직 UUID
     * @param tenantId 테넌트 UUID
     * @param name 조직 이름
     * @param status 조직 상태
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     */
    private OrganizationJpaEntity(
            Long id,
            UUID organizationId,
            UUID tenantId,
            String name,
            OrganizationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.organizationId = organizationId;
        this.tenantId = tenantId;
        this.name = name;
        this.status = status;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * <p>Mapper에서 Domain → Entity 변환 시 사용합니다.
     *
     * @param id 내부 기본 키 (신규 생성 시 null)
     * @param organizationId 조직 UUID
     * @param tenantId 테넌트 UUID
     * @param name 조직 이름
     * @param status 조직 상태
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return OrganizationJpaEntity 인스턴스
     */
    public static OrganizationJpaEntity of(
            Long id,
            UUID organizationId,
            UUID tenantId,
            String name,
            OrganizationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new OrganizationJpaEntity(
                id, organizationId, tenantId, name, status, createdAt, updatedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public OrganizationStatus getStatus() {
        return status;
    }
}
