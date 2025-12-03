package com.ryuqq.authhub.domain.role.aggregate;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Instant;
import java.util.Objects;

/**
 * UserRole - 사용자-역할 매핑 도메인 모델
 *
 * <p>사용자에게 할당된 역할을 나타냅니다.
 *
 * <p><strong>Long FK 전략:</strong>
 *
 * <ul>
 *   <li>User, Role과 JPA 관계 대신 ID만 저장
 *   <li>조회 시 별도 쿼리로 조인
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserRole {

    private final Long id;
    private final UserId userId;
    private final RoleId roleId;
    private final Instant assignedAt;

    private UserRole(Long id, UserId userId, RoleId roleId, Instant assignedAt) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId는 null일 수 없습니다");
        }
        if (roleId == null) {
            throw new IllegalArgumentException("RoleId는 null일 수 없습니다");
        }
        if (assignedAt == null) {
            throw new IllegalArgumentException("assignedAt는 null일 수 없습니다");
        }
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = assignedAt;
    }

    /** 새로운 UserRole 생성 (ID 미할당) */
    public static UserRole forNew(UserId userId, RoleId roleId, Clock clock) {
        return new UserRole(null, userId, roleId, clock.now());
    }

    /** DB에서 UserRole 재구성 (reconstitute) */
    public static UserRole reconstitute(Long id, UserId userId, RoleId roleId, Instant assignedAt) {
        if (id == null) {
            throw new IllegalArgumentException("reconstitute requires non-null id");
        }
        return new UserRole(id, userId, roleId, assignedAt);
    }

    /** 모든 필드 지정하여 UserRole 생성 */
    public static UserRole of(Long id, UserId userId, RoleId roleId, Instant assignedAt) {
        return new UserRole(id, userId, roleId, assignedAt);
    }

    // ========== Helper Methods ==========

    public boolean isNew() {
        return id == null;
    }

    // ========== Getter Methods ==========

    public Long getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public RoleId getRoleId() {
        return roleId;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    // ========== Object Methods ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserRole userRole = (UserRole) o;
        if (id == null || userRole.id == null) {
            return Objects.equals(userId, userRole.userId)
                    && Objects.equals(roleId, userRole.roleId);
        }
        return Objects.equals(id, userRole.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(userId, roleId);
    }

    @Override
    public String toString() {
        return "UserRole{" + "id=" + id + ", userId=" + userId + ", roleId=" + roleId + "}";
    }
}
