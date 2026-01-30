package com.ryuqq.authhub.adapter.out.persistence.organization.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.SoftDeletableEntity;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

/**
 * OrganizationJpaEntity - 조직 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>organizationId(String)를 PK로 사용
 *   <li>UUIDv7은 시간순 정렬 가능하여 B-tree 인덱스 성능 우수
 *   <li>분산 환경에서 충돌 없는 고유 ID 생성
 * </ul>
 *
 * <p><strong>String FK 전략:</strong>
 *
 * <ul>
 *   <li>tenantId는 String 타입으로 관리 (JPA 관계 어노테이션 금지)
 *   <li>테넌트와의 관계는 String ID를 통해 애플리케이션에서 관리
 * </ul>
 *
 * <p><strong>SoftDeletableEntity 상속:</strong>
 *
 * <ul>
 *   <li>createdAt, updatedAt (BaseAuditEntity)
 *   <li>deletedAt (SoftDeletableEntity)
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
public class OrganizationJpaEntity extends SoftDeletableEntity {

    /** 조직 UUID - UUIDv7 (Primary Key, String 저장) */
    @Id
    @Column(name = "organization_id", nullable = false, length = 36)
    private String organizationId;

    /** 테넌트 UUID - FK (String FK 전략: JPA 관계 어노테이션 금지) */
    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

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
     * @param organizationId 조직 UUID (PK, String)
     * @param tenantId 테넌트 UUID (String)
     * @param name 조직 이름
     * @param status 조직 상태
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @param deletedAt 삭제 일시 (Instant, UTC)
     */
    private OrganizationJpaEntity(
            String organizationId,
            String tenantId,
            String name,
            OrganizationStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
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
     * @param organizationId 조직 UUID (PK, String)
     * @param tenantId 테넌트 UUID (String)
     * @param name 조직 이름
     * @param status 조직 상태
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @param deletedAt 삭제 일시 (Instant, UTC)
     * @return OrganizationJpaEntity 인스턴스
     */
    public static OrganizationJpaEntity of(
            String organizationId,
            String tenantId,
            String name,
            OrganizationStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new OrganizationJpaEntity(
                organizationId, tenantId, name, status, createdAt, updatedAt, deletedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public String getOrganizationId() {
        return organizationId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public OrganizationStatus getStatus() {
        return status;
    }
}
