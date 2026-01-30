package com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

/**
 * RolePermissionJpaEntity - 역할-권한 관계 JPA Entity
 *
 * <p>Role과 Permission 간의 다대다 관계를 저장하는 Entity입니다.
 *
 * <p><strong>Long PK 전략:</strong>
 *
 * <ul>
 *   <li>rolePermissionId(Long)를 PK로 사용 (Auto Increment)
 *   <li>roleId + permissionId는 Unique Constraint로 관리
 *   <li>Long FK 전략 - JPA 관계 어노테이션 금지
 * </ul>
 *
 * <p><strong>삭제 정책:</strong>
 *
 * <ul>
 *   <li>Soft Delete 미적용 (관계 테이블이므로 Hard Delete)
 *   <li>BaseAuditEntity 상속 안함
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>JPA 관계 어노테이션 금지 (@ManyToOne, @OneToMany 등)
 *   <li>protected 기본 생성자 필수 (JPA 스펙)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Entity
@Table(
        name = "role_permissions",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_role_permission",
                    columnNames = {"role_id", "permission_id"})
        },
        indexes = {
            @Index(name = "idx_role_permission_role_id", columnList = "role_id"),
            @Index(name = "idx_role_permission_permission_id", columnList = "permission_id")
        })
public class RolePermissionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_permission_id")
    private Long rolePermissionId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** JPA 스펙 - protected 기본 생성자 필수 */
    protected RolePermissionJpaEntity() {}

    private RolePermissionJpaEntity(
            Long rolePermissionId, Long roleId, Long permissionId, Instant createdAt) {
        this.rolePermissionId = rolePermissionId;
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.createdAt = createdAt;
    }

    /**
     * 새로운 역할-권한 관계 생성
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @param createdAt 생성 시간
     * @return RolePermissionJpaEntity
     */
    public static RolePermissionJpaEntity create(
            Long roleId, Long permissionId, Instant createdAt) {
        return new RolePermissionJpaEntity(null, roleId, permissionId, createdAt);
    }

    /**
     * 기존 역할-권한 관계 재구성
     *
     * @param rolePermissionId 관계 ID
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @param createdAt 생성 시간
     * @return RolePermissionJpaEntity
     */
    public static RolePermissionJpaEntity of(
            Long rolePermissionId, Long roleId, Long permissionId, Instant createdAt) {
        return new RolePermissionJpaEntity(rolePermissionId, roleId, permissionId, createdAt);
    }

    // ========== Getter Methods ==========

    public Long getRolePermissionId() {
        return rolePermissionId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
