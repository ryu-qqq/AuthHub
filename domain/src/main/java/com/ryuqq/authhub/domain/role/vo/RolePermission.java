package com.ryuqq.authhub.domain.role.vo;

import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * RolePermission Value Object - Role과 Permission 간의 N:M 매핑
 *
 * <p>Long FK 전략을 따라 JPA 관계 어노테이션 없이 ID만 보관합니다.
 *
 * <p><strong>불변 규칙:</strong>
 *
 * <ul>
 *   <li>모든 필드는 final
 *   <li>Lombok 금지
 *   <li>equals/hashCode는 roleId + permissionId 조합으로 판단
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RolePermission {

    private final RoleId roleId;
    private final PermissionId permissionId;
    private final Instant grantedAt;

    private RolePermission(RoleId roleId, PermissionId permissionId, Instant grantedAt) {
        if (roleId == null) {
            throw new IllegalArgumentException("RoleId는 null일 수 없습니다");
        }
        if (permissionId == null) {
            throw new IllegalArgumentException("PermissionId는 null일 수 없습니다");
        }
        if (grantedAt == null) {
            throw new IllegalArgumentException("grantedAt은 null일 수 없습니다");
        }
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.grantedAt = grantedAt;
    }

    /**
     * RolePermission 생성
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @param grantedAt 부여 시간
     * @return 새로운 RolePermission 인스턴스
     */
    public static RolePermission of(RoleId roleId, PermissionId permissionId, Instant grantedAt) {
        return new RolePermission(roleId, permissionId, grantedAt);
    }

    /** RolePermission 재구성 (영속성에서 복원) */
    public static RolePermission reconstitute(
            RoleId roleId, PermissionId permissionId, Instant grantedAt) {
        return new RolePermission(roleId, permissionId, grantedAt);
    }

    public RoleId getRoleId() {
        return roleId;
    }

    public UUID roleIdValue() {
        return roleId.value();
    }

    public PermissionId getPermissionId() {
        return permissionId;
    }

    public UUID permissionIdValue() {
        return permissionId.value();
    }

    public Instant getGrantedAt() {
        return grantedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RolePermission that = (RolePermission) o;
        return Objects.equals(roleId, that.roleId)
                && Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, permissionId);
    }

    @Override
    public String toString() {
        return "RolePermission{roleId="
                + roleId
                + ", permissionId="
                + permissionId
                + ", grantedAt="
                + grantedAt
                + "}";
    }
}
