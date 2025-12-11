package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
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
 * RoleJpaEntity - 역할 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>Role 범위 전략:</strong>
 *
 * <ul>
 *   <li>GLOBAL: 전체 시스템 범위 (tenantId = null)
 *   <li>TENANT: 테넌트 범위 (tenantId 필수)
 *   <li>ORGANIZATION: 조직 범위 (tenantId 필수)
 * </ul>
 *
 * <p><strong>Unique 제약:</strong>
 *
 * <ul>
 *   <li>tenantId + name 조합으로 유니크 (같은 테넌트 내 동일 역할명 금지)
 *   <li>GLOBAL 역할은 tenantId가 null이므로 전역 유니크
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

    /** 기본 키 - AUTO_INCREMENT (내부 Long ID) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 역할 UUID - UUIDv7 (외부 식별자) */
    @Column(name = "role_id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
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
            Long id,
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
        this.id = id;
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
     * <p>Mapper에서 Domain → Entity 변환 시 사용합니다.
     *
     * @param id 내부 기본 키 (신규 생성 시 null)
     * @param roleId 역할 UUID
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
            Long id,
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
                id,
                roleId,
                tenantId,
                name,
                description,
                scope,
                type,
                deleted,
                createdAt,
                updatedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getId() {
        return id;
    }

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
