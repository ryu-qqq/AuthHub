package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * UserRoleJpaEntity - User-Role 매핑 JPA Entity
 *
 * <p>사용자에게 할당된 역할을 저장합니다.
 *
 * <p><strong>Domain 매핑:</strong>
 *
 * <ul>
 *   <li>Long id ← UserRole.id
 *   <li>UUID userId ← UserId.value()
 *   <li>Long roleId ← RoleId.value()
 *   <li>LocalDateTime assignedAt ← Instant (Mapper에서 변환)
 * </ul>
 *
 * <p><strong>Long FK 전략:</strong>
 *
 * <ul>
 *   <li>JPA 관계 어노테이션(@ManyToOne 등) 사용 금지
 *   <li>userId는 UUID, roleId는 Long으로 직접 저장
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(name = "user_roles")
public class UserRoleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    protected UserRoleJpaEntity() {}

    private UserRoleJpaEntity(Long id, UUID userId, Long roleId, LocalDateTime assignedAt) {
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = assignedAt;
    }

    public static UserRoleJpaEntity of(
            Long id, UUID userId, Long roleId, LocalDateTime assignedAt) {
        return new UserRoleJpaEntity(id, userId, roleId, assignedAt);
    }

    public Long getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserRoleJpaEntity that = (UserRoleJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
