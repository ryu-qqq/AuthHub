package com.ryuqq.authhub.domain.role.aggregate;

import com.ryuqq.authhub.domain.role.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.vo.PermissionCode;
import java.util.Objects;

/**
 * Permission - 권한 도메인 모델
 *
 * <p>시스템 내 개별 권한을 나타냅니다.
 *
 * <p><strong>권한 예시:</strong>
 *
 * <ul>
 *   <li>user:read - 사용자 조회 권한
 *   <li>user:write - 사용자 생성/수정 권한
 *   <li>organization:* - 조직 관련 모든 권한
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>불변 객체 (Immutable)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Permission {

    private final PermissionId permissionId;
    private final PermissionCode code;
    private final String description;

    private Permission(PermissionId permissionId, PermissionCode code, String description) {
        if (code == null) {
            throw new IllegalArgumentException("PermissionCode는 null일 수 없습니다");
        }
        this.permissionId = permissionId;
        this.code = code;
        this.description = description != null ? description : "";
    }

    /** 새로운 Permission 생성 (ID 미할당) */
    public static Permission forNew(PermissionCode code, String description) {
        return new Permission(null, code, description);
    }

    /** DB에서 Permission 재구성 (reconstitute) */
    public static Permission reconstitute(
            PermissionId permissionId, PermissionCode code, String description) {
        if (permissionId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null permissionId");
        }
        return new Permission(permissionId, code, description);
    }

    /** 모든 필드 지정하여 Permission 생성 */
    public static Permission of(
            PermissionId permissionId, PermissionCode code, String description) {
        return new Permission(permissionId, code, description);
    }

    // ========== Helper Methods ==========

    public Long permissionIdValue() {
        return permissionId != null ? permissionId.value() : null;
    }

    public String codeValue() {
        return code.value();
    }

    public boolean isNew() {
        return permissionId == null;
    }

    /** 주어진 권한을 이 권한이 포함하는지 확인 */
    public boolean implies(Permission other) {
        return this.code.implies(other.code);
    }

    // ========== Getter Methods ==========

    public PermissionId getPermissionId() {
        return permissionId;
    }

    public PermissionCode getCode() {
        return code;
    }

    public String getDescription() {
        return description;
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
        Permission that = (Permission) o;
        if (permissionId == null || that.permissionId == null) {
            return Objects.equals(code, that.code);
        }
        return Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return permissionId != null ? Objects.hash(permissionId) : Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Permission{" + "permissionId=" + permissionId + ", code=" + code + "}";
    }
}
