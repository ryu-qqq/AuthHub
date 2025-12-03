package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * RolePermissionJpaEntity - Role-Permission 매핑 JPA Entity
 *
 * <p>Role과 Permission의 다대다 관계를 저장합니다.
 *
 * <p><strong>Long FK 전략:</strong>
 *
 * <ul>
 *   <li>JPA 관계 어노테이션(@ManyToOne 등) 사용 금지
 *   <li>roleId, permissionId를 Long으로 직접 저장
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(name = "role_permissions")
public class RolePermissionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    protected RolePermissionJpaEntity() {}

    private RolePermissionJpaEntity(Long id, Long roleId, Long permissionId) {
        this.id = id;
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public static RolePermissionJpaEntity of(Long id, Long roleId, Long permissionId) {
        return new RolePermissionJpaEntity(id, roleId, permissionId);
    }

    public static RolePermissionJpaEntity forNew(Long roleId, Long permissionId) {
        return new RolePermissionJpaEntity(null, roleId, permissionId);
    }

    public Long getId() {
        return id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RolePermissionJpaEntity that = (RolePermissionJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
