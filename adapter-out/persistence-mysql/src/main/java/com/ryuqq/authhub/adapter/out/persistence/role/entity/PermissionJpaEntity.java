package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * PermissionJpaEntity - Permission JPA Entity
 *
 * <p>권한 정보를 저장합니다.
 *
 * <p><strong>Domain 매핑:</strong>
 *
 * <ul>
 *   <li>Long id ← PermissionId
 *   <li>String code ← PermissionCode.value()
 *   <li>String description ← description
 * </ul>
 *
 * <p><strong>Audit 정보:</strong> Permission 도메인은 createdAt/updatedAt을 가지지 않으므로 BaseAuditEntity를 상속하지
 * 않습니다.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(name = "permissions")
public class PermissionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "description", length = 500)
    private String description;

    protected PermissionJpaEntity() {}

    private PermissionJpaEntity(Long id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public static PermissionJpaEntity of(Long id, String code, String description) {
        return new PermissionJpaEntity(id, code, description);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionJpaEntity that = (PermissionJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
