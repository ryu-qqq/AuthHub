package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.SoftDeletableEntity;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * RoleJpaEntity - 역할 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>Long FK 전략:</strong>
 *
 * <ul>
 *   <li>roleId(Long)를 Auto Increment PK로 사용
 *   <li>tenantId는 String FK로 저장 (nullable - Global 역할일 경우)
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
@Table(name = "roles")
public class RoleJpaEntity extends SoftDeletableEntity {

    /** 역할 ID - Auto Increment (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /** 테넌트 ID FK (null이면 Global 역할) */
    @Column(name = "tenant_id", length = 36)
    private String tenantId;

    /** 역할 이름 (예: SUPER_ADMIN, 유니크) */
    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;

    /** 표시 이름 (예: "슈퍼 관리자") */
    @Column(name = "display_name", length = 100)
    private String displayName;

    /** 역할 설명 */
    @Column(name = "description", length = 500)
    private String description;

    /** 역할 유형 (SYSTEM, CUSTOM) */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private RoleType type;

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
     *
     * @param roleId 역할 ID (PK, Long - null이면 신규)
     * @param tenantId 테넌트 ID FK (null이면 Global)
     * @param name 역할 이름
     * @param displayName 표시 이름
     * @param description 역할 설명
     * @param type 역할 유형
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @param deletedAt 삭제 일시 (Instant, UTC)
     */
    private RoleJpaEntity(
            Long roleId,
            String tenantId,
            String name,
            String displayName,
            String description,
            RoleType type,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.roleId = roleId;
        this.tenantId = tenantId;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * @param roleId 역할 ID (PK, Long - null이면 신규)
     * @param tenantId 테넌트 ID FK (null이면 Global)
     * @param name 역할 이름
     * @param displayName 표시 이름
     * @param description 역할 설명
     * @param type 역할 유형
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @param deletedAt 삭제 일시 (Instant, UTC)
     * @return RoleJpaEntity 인스턴스
     */
    public static RoleJpaEntity of(
            Long roleId,
            String tenantId,
            String name,
            String displayName,
            String description,
            RoleType type,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new RoleJpaEntity(
                roleId,
                tenantId,
                name,
                displayName,
                description,
                type,
                createdAt,
                updatedAt,
                deletedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getRoleId() {
        return roleId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public RoleType getType() {
        return type;
    }
}
