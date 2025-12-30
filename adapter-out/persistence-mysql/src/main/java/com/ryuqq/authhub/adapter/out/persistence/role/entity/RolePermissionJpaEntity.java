package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

/**
 * RolePermissionJpaEntity - 역할 권한 매핑 JPA Entity
 *
 * <p>Role과 Permission 간의 N:M 관계를 나타내는 중간 테이블입니다.
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>rolePermissionId(UUID)를 PK로 사용
 *   <li>UUIDv7은 시간순 정렬 가능하여 B-tree 인덱스 성능 우수
 *   <li>분산 환경에서 충돌 없는 고유 ID 생성
 * </ul>
 *
 * <p><strong>Unique 제약:</strong>
 *
 * <ul>
 *   <li>roleId + permissionId 조합으로 유니크
 *   <li>동일 권한 중복 부여 방지
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
        name = "role_permissions",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_role_permissions_role_permission",
                    columnNames = {"role_id", "permission_id"})
        },
        indexes = {
            @Index(name = "idx_role_permissions_role_id", columnList = "role_id"),
            @Index(name = "idx_role_permissions_permission_id", columnList = "permission_id")
        })
public class RolePermissionJpaEntity {

    /** 역할 권한 매핑 UUID - UUIDv7 (Primary Key) */
    @Id
    @Column(name = "role_permission_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID rolePermissionId;

    /** 역할 UUID */
    @Column(name = "role_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID roleId;

    /** 권한 UUID */
    @Column(name = "permission_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID permissionId;

    /** 부여 시간 */
    @Column(name = "granted_at", nullable = false)
    private Instant grantedAt;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected RolePermissionJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     */
    private RolePermissionJpaEntity(
            UUID rolePermissionId, UUID roleId, UUID permissionId, Instant grantedAt) {
        this.rolePermissionId = rolePermissionId;
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.grantedAt = grantedAt;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * @param rolePermissionId 역할 권한 매핑 UUID (PK)
     * @param roleId 역할 UUID
     * @param permissionId 권한 UUID
     * @param grantedAt 부여 시간
     * @return RolePermissionJpaEntity 인스턴스
     */
    public static RolePermissionJpaEntity of(
            UUID rolePermissionId, UUID roleId, UUID permissionId, Instant grantedAt) {
        return new RolePermissionJpaEntity(rolePermissionId, roleId, permissionId, grantedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public UUID getRolePermissionId() {
        return rolePermissionId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    public Instant getGrantedAt() {
        return grantedAt;
    }
}
