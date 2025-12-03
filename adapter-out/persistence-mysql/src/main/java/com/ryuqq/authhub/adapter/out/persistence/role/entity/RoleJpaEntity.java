package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * RoleJpaEntity - Role JPA Entity
 *
 * <p>역할 정보를 저장합니다.
 *
 * <p><strong>Domain 매핑:</strong>
 *
 * <ul>
 *   <li>Long id ← RoleId
 *   <li>Long tenantId ← TenantId (Long FK 전략)
 *   <li>String name ← RoleName.value()
 *   <li>String description ← description
 *   <li>boolean isSystem ← isSystem
 * </ul>
 *
 * <p><strong>Permissions 처리:</strong> Role의 permissions는 role_permissions 조인 테이블을 통해 별도 조회합니다. Long
 * FK 전략에 따라 JPA 관계 매핑을 사용하지 않습니다.
 *
 * <p><strong>Audit 정보:</strong> Role 도메인은 createdAt/updatedAt을 가지지 않으므로 BaseAuditEntity를 상속하지 않습니다.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(name = "roles")
public class RoleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_system", nullable = false)
    private boolean isSystem;

    protected RoleJpaEntity() {}

    private RoleJpaEntity(
            Long id, Long tenantId, String name, String description, boolean isSystem) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.description = description;
        this.isSystem = isSystem;
    }

    public static RoleJpaEntity of(
            Long id, Long tenantId, String name, String description, boolean isSystem) {
        return new RoleJpaEntity(id, tenantId, name, description, isSystem);
    }

    public Long getId() {
        return id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSystem() {
        return isSystem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleJpaEntity that = (RoleJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
