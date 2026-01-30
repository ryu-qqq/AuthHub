package com.ryuqq.authhub.domain.rolepermission.aggregate;

import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.id.RolePermissionId;
import java.time.Instant;
import java.util.Objects;

/**
 * RolePermission Aggregate Root - 역할-권한 관계 도메인 모델
 *
 * <p>Role과 Permission 간의 다대다 관계를 관리하는 Aggregate입니다.
 *
 * <p><strong>관계 특성:</strong>
 *
 * <ul>
 *   <li>한 Role은 여러 Permission을 가질 수 있음
 *   <li>한 Permission은 여러 Role에 부여될 수 있음
 *   <li>roleId + permissionId 조합은 유니크 (중복 부여 불가)
 * </ul>
 *
 * <p><strong>삭제 정책:</strong>
 *
 * <ul>
 *   <li>Permission 삭제 시: RolePermission 존재하면 삭제 불가
 *   <li>Role 삭제 시: RolePermission Cascade 삭제
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
public final class RolePermission {

    private final RolePermissionId rolePermissionId;
    private final RoleId roleId;
    private final PermissionId permissionId;
    private final Instant createdAt;

    private RolePermission(
            RolePermissionId rolePermissionId,
            RoleId roleId,
            PermissionId permissionId,
            Instant createdAt) {
        validateRequired(roleId, permissionId);
        this.rolePermissionId = rolePermissionId;
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.createdAt = createdAt;
    }

    private void validateRequired(RoleId roleId, PermissionId permissionId) {
        if (roleId == null) {
            throw new IllegalArgumentException("roleId는 null일 수 없습니다");
        }
        if (permissionId == null) {
            throw new IllegalArgumentException("permissionId는 null일 수 없습니다");
        }
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 역할-권한 관계 생성
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 RolePermission 인스턴스
     */
    public static RolePermission create(RoleId roleId, PermissionId permissionId, Instant now) {
        return new RolePermission(null, roleId, permissionId, now);
    }

    /**
     * DB에서 역할-권한 관계 재구성 (reconstitute)
     *
     * @param rolePermissionId 관계 ID
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @param createdAt 생성 시간
     * @return 재구성된 RolePermission 인스턴스
     */
    public static RolePermission reconstitute(
            RolePermissionId rolePermissionId,
            RoleId roleId,
            PermissionId permissionId,
            Instant createdAt) {
        return new RolePermission(rolePermissionId, roleId, permissionId, createdAt);
    }

    // ========== Query Methods ==========

    /**
     * 역할-권한 관계 ID 값 반환
     *
     * @return 관계 ID (Long) 또는 null (신규 생성 시)
     */
    public Long rolePermissionIdValue() {
        return rolePermissionId != null ? rolePermissionId.value() : null;
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
     * 권한 ID 값 반환
     *
     * @return 권한 ID (Long)
     */
    public Long permissionIdValue() {
        return permissionId.value();
    }

    /**
     * 신규 생성 여부 확인
     *
     * @return ID가 없으면 true (신규)
     */
    public boolean isNew() {
        return rolePermissionId == null;
    }

    // ========== Getter Methods ==========

    public RolePermissionId getRolePermissionId() {
        return rolePermissionId;
    }

    public RoleId getRoleId() {
        return roleId;
    }

    public PermissionId getPermissionId() {
        return permissionId;
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
        RolePermission that = (RolePermission) o;
        if (rolePermissionId == null || that.rolePermissionId == null) {
            return Objects.equals(roleId, that.roleId)
                    && Objects.equals(permissionId, that.permissionId);
        }
        return Objects.equals(rolePermissionId, that.rolePermissionId);
    }

    @Override
    public int hashCode() {
        if (rolePermissionId != null) {
            return Objects.hash(rolePermissionId);
        }
        return Objects.hash(roleId, permissionId);
    }

    @Override
    public String toString() {
        return "RolePermission{"
                + "rolePermissionId="
                + rolePermissionId
                + ", roleId="
                + roleId
                + ", permissionId="
                + permissionId
                + '}';
    }
}
