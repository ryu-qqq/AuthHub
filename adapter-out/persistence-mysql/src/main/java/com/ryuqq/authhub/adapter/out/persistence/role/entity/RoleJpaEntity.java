package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RoleJpaEntity - 역할 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>roleId(UUID)를 PK로 사용
 *   <li>UUIDv7은 시간순 정렬 가능하여 B-tree 인덱스 성능 우수
 *   <li>분산 환경에서 충돌 없는 고유 ID 생성
 * </ul>
 *
 * <p><strong>Role 범위 전략:</strong>
 *
 * <ul>
 *   <li>GLOBAL: 전체 시스템 범위 (tenantId = null)
 *   <li>TENANT: 테넌트 범위 (tenantId 필수)
 *   <li>ORGANIZATION: 조직 범위 (tenantId 필수)
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
        name = "roles",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_roles_tenant_name",
                    columnNames = {"tenant_id", "name"})
        },
        indexes = {
            @Index(name = "idx_roles_tenant_id", columnList = "tenant_id"),
            @Index(name = "idx_roles_scope", columnList = "scope"),
            @Index(name = "idx_roles_type", columnList = "type"),
            @Index(name = "idx_roles_deleted", columnList = "deleted")
        })
public class RoleJpaEntity extends BaseAuditEntity {

    /** 역할 UUID - UUIDv7 (Primary Key) */
    @Id
    @Column(name = "role_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID roleId;

    /** 테넌트 UUID (GLOBAL 범위일 경우 null) */
    @Column(name = "tenant_id", columnDefinition = "BINARY(16)")
    private UUID tenantId;

    /** 역할 이름 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 역할 설명 */
    @Column(name = "description", length = 500)
    private String description;

    /** 역할 범위 */
    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, length = 20)
    private RoleScope scope;

    /** 역할 유형 */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private RoleType type;

    /** 삭제 여부 (Soft Delete) */
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected RoleJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     */
    private RoleJpaEntity(
            UUID roleId,
            UUID tenantId,
            String name,
            String description,
            RoleScope scope,
            RoleType type,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.roleId = roleId;
        this.tenantId = tenantId;
        this.name = name;
        this.description = description;
        this.scope = scope;
        this.type = type;
        this.deleted = deleted;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * @param roleId 역할 UUID (PK)
     * @param tenantId 테넌트 UUID (GLOBAL 범위일 경우 null)
     * @param name 역할 이름
     * @param description 역할 설명
     * @param scope 역할 범위
     * @param type 역할 유형
     * @param deleted 삭제 여부
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return RoleJpaEntity 인스턴스
     */
    public static RoleJpaEntity of(
            UUID roleId,
            UUID tenantId,
            String name,
            String description,
            RoleScope scope,
            RoleType type,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new RoleJpaEntity(
                roleId, tenantId, name, description, scope, type, deleted, createdAt, updatedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public UUID getRoleId() {
        return roleId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public RoleScope getScope() {
        return scope;
    }

    public RoleType getType() {
        return type;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
