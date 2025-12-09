package com.ryuqq.authhub.domain.user.vo;

import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * UserRole Value Object - User와 Role 간의 N:M 매핑
 *
 * <p>Long FK 전략을 따라 JPA 관계 어노테이션 없이 ID만 보관합니다.
 *
 * <p><strong>불변 규칙:</strong>
 * <ul>
 *   <li>모든 필드는 final
 *   <li>Lombok 금지
 *   <li>equals/hashCode는 userId + roleId 조합으로 판단
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserRole {

    private final UserId userId;
    private final RoleId roleId;
    private final Instant assignedAt;

    private UserRole(UserId userId, RoleId roleId, Instant assignedAt) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId는 null일 수 없습니다");
        }
        if (roleId == null) {
            throw new IllegalArgumentException("RoleId는 null일 수 없습니다");
        }
        if (assignedAt == null) {
            throw new IllegalArgumentException("assignedAt은 null일 수 없습니다");
        }
        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = assignedAt;
    }

    /**
     * UserRole 생성
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @param assignedAt 할당 시간
     * @return 새로운 UserRole 인스턴스
     */
    public static UserRole of(UserId userId, RoleId roleId, Instant assignedAt) {
        return new UserRole(userId, roleId, assignedAt);
    }

    /**
     * UserRole 재구성 (영속성에서 복원)
     */
    public static UserRole reconstitute(UserId userId, RoleId roleId, Instant assignedAt) {
        return new UserRole(userId, roleId, assignedAt);
    }

    public UserId getUserId() {
        return userId;
    }

    public UUID userIdValue() {
        return userId.value();
    }

    public RoleId getRoleId() {
        return roleId;
    }

    public UUID roleIdValue() {
        return roleId.value();
    }

    public Instant getAssignedAt() {
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
        UserRole userRole = (UserRole) o;
        return Objects.equals(userId, userRole.userId)
                && Objects.equals(roleId, userRole.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }

    @Override
    public String toString() {
        return "UserRole{userId=" + userId + ", roleId=" + roleId + ", assignedAt=" + assignedAt + "}";
    }
}
