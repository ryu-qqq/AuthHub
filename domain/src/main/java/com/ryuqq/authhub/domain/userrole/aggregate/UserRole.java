package com.ryuqq.authhub.domain.userrole.aggregate;

import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.id.UserRoleId;
import java.time.Instant;
import java.util.Objects;

/**
 * UserRole Aggregate Root - 사용자-역할 관계 도메인 모델
 *
 * <p>User와 Role 간의 다대다 관계를 관리하는 Aggregate입니다.
 *
 * <p><strong>관계 특성:</strong>
 *
 * <ul>
 *   <li>한 User는 여러 Role을 가질 수 있음
 *   <li>한 Role은 여러 User에 할당될 수 있음
 *   <li>userId + roleId 조합은 유니크 (중복 할당 불가)
 * </ul>
 *
 * <p><strong>삭제 정책:</strong>
 *
 * <ul>
 *   <li>Role 삭제 시: UserRole 존재하면 삭제 불가 또는 Cascade
 *   <li>User 삭제 시: UserRole Cascade 삭제
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>Long FK 전략 - JPA 관계 어노테이션 금지
 *   <li>Soft Delete 미적용 (관계 테이블이므로 Hard Delete)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserRole {

    private final UserRoleId userRoleId;
    private final UserId userId;
    private final RoleId roleId;
    private final Instant createdAt;

    private UserRole(UserRoleId userRoleId, UserId userId, RoleId roleId, Instant createdAt) {
        this.userRoleId = userRoleId;
        this.userId = userId;
        this.roleId = roleId;
        this.createdAt = createdAt;
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 사용자-역할 관계 생성
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 UserRole 인스턴스
     */
    public static UserRole create(UserId userId, RoleId roleId, Instant now) {
        return new UserRole(null, userId, roleId, now);
    }

    /**
     * DB에서 사용자-역할 관계 재구성 (reconstitute)
     *
     * @param userRoleId 관계 ID
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @param createdAt 생성 시간
     * @return 재구성된 UserRole 인스턴스
     */
    public static UserRole reconstitute(
            UserRoleId userRoleId, UserId userId, RoleId roleId, Instant createdAt) {
        return new UserRole(userRoleId, userId, roleId, createdAt);
    }

    // ========== Query Methods ==========

    /**
     * 사용자-역할 관계 ID 값 반환
     *
     * @return 관계 ID (Long) 또는 null (신규 생성 시)
     */
    public Long userRoleIdValue() {
        return userRoleId != null ? userRoleId.value() : null;
    }

    /**
     * 사용자 ID 값 반환
     *
     * @return 사용자 ID (String)
     */
    public String userIdValue() {
        return userId.value();
    }

    /**
     * 역할 ID 값 반환
     *
     * @return 역할 ID (Long)
     */
    public Long roleIdValue() {
        return roleId.value();
    }

    /**
     * 신규 생성 여부 확인
     *
     * @return ID가 없으면 true (신규)
     */
    public boolean isNew() {
        return userRoleId == null;
    }

    // ========== Getter Methods ==========

    public UserRoleId getUserRoleId() {
        return userRoleId;
    }

    public UserId getUserId() {
        return userId;
    }

    public RoleId getRoleId() {
        return roleId;
    }

    public Instant createdAt() {
        return createdAt;
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
        UserRole that = (UserRole) o;
        if (userRoleId == null || that.userRoleId == null) {
            return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
        }
        return Objects.equals(userRoleId, that.userRoleId);
    }

    @Override
    public int hashCode() {
        if (userRoleId != null) {
            return Objects.hash(userRoleId);
        }
        return Objects.hash(userId, roleId);
    }

    @Override
    public String toString() {
        return "UserRole{"
                + "userRoleId="
                + userRoleId
                + ", userId="
                + userId
                + ", roleId="
                + roleId
                + '}';
    }
}
