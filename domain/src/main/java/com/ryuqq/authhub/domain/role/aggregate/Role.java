package com.ryuqq.authhub.domain.role.aggregate;

import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.PermissionCode;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Role Aggregate Root - 역할 도메인 모델
 *
 * <p>역할은 Permission의 집합이며, 사용자에게 할당됩니다.
 *
 * <p><strong>구조:</strong>
 *
 * <ul>
 *   <li>Role은 여러 Permission을 가짐
 *   <li>Role은 Tenant 범위로 제한됨 (null이면 시스템 전역 역할)
 *   <li>isSystem=true면 수정/삭제 불가
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>불변 객체 (Immutable)
 *   <li>Long FK 전략 - JPA 관계 어노테이션 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Role {

    private final RoleId roleId;
    private final TenantId tenantId;
    private final RoleName name;
    private final String description;
    private final boolean isSystem;
    private final Set<PermissionCode> permissions;

    private Role(
            RoleId roleId,
            TenantId tenantId,
            RoleName name,
            String description,
            boolean isSystem,
            Set<PermissionCode> permissions) {
        if (name == null) {
            throw new IllegalArgumentException("RoleName은 null일 수 없습니다");
        }
        this.roleId = roleId;
        this.tenantId = tenantId;
        this.name = name;
        this.description = description != null ? description : "";
        this.isSystem = isSystem;
        this.permissions =
                permissions != null
                        ? Collections.unmodifiableSet(new HashSet<>(permissions))
                        : Collections.emptySet();
    }

    /**
     * 새로운 Role 생성 (ID 미할당)
     *
     * @param tenantId 테넌트 ID (null이면 시스템 전역 역할)
     * @param name 역할 이름
     * @param description 설명
     * @param isSystem 시스템 역할 여부
     */
    public static Role forNew(
            TenantId tenantId, RoleName name, String description, boolean isSystem) {
        return new Role(null, tenantId, name, description, isSystem, Collections.emptySet());
    }

    /** DB에서 Role 재구성 (reconstitute) */
    public static Role reconstitute(
            RoleId roleId,
            TenantId tenantId,
            RoleName name,
            String description,
            boolean isSystem,
            Set<PermissionCode> permissions) {
        if (roleId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null roleId");
        }
        return new Role(roleId, tenantId, name, description, isSystem, permissions);
    }

    /** 모든 필드 지정하여 Role 생성 */
    public static Role of(
            RoleId roleId,
            TenantId tenantId,
            RoleName name,
            String description,
            boolean isSystem,
            Set<PermissionCode> permissions) {
        return new Role(roleId, tenantId, name, description, isSystem, permissions);
    }

    // ========== Business Methods ==========

    /**
     * Permission 추가
     *
     * @return Permission이 추가된 새로운 Role 인스턴스
     * @throws IllegalStateException 시스템 역할인 경우
     */
    public Role addPermission(PermissionCode permissionCode) {
        if (isSystem) {
            throw new IllegalStateException("시스템 역할은 수정할 수 없습니다");
        }
        Set<PermissionCode> newPermissions = new HashSet<>(this.permissions);
        newPermissions.add(permissionCode);
        return new Role(roleId, tenantId, name, description, isSystem, newPermissions);
    }

    /**
     * Permission 제거
     *
     * @return Permission이 제거된 새로운 Role 인스턴스
     * @throws IllegalStateException 시스템 역할인 경우
     */
    public Role removePermission(PermissionCode permissionCode) {
        if (isSystem) {
            throw new IllegalStateException("시스템 역할은 수정할 수 없습니다");
        }
        Set<PermissionCode> newPermissions = new HashSet<>(this.permissions);
        newPermissions.remove(permissionCode);
        return new Role(roleId, tenantId, name, description, isSystem, newPermissions);
    }

    /** 특정 Permission을 가지고 있는지 확인 (와일드카드 포함) */
    public boolean hasPermission(PermissionCode permissionCode) {
        return permissions.stream().anyMatch(p -> p.implies(permissionCode));
    }

    // ========== Helper Methods ==========

    public Long roleIdValue() {
        return roleId != null ? roleId.value() : null;
    }

    public Long tenantIdValue() {
        return tenantId != null ? tenantId.value() : null;
    }

    public String nameValue() {
        return name.value();
    }

    public boolean isNew() {
        return roleId == null;
    }

    public boolean isGlobalRole() {
        return tenantId == null;
    }

    /** Permission 코드 문자열 Set 반환 (JWT Claim용) */
    public Set<String> permissionValues() {
        Set<String> values = new HashSet<>();
        for (PermissionCode permission : permissions) {
            values.add(permission.value());
        }
        return Collections.unmodifiableSet(values);
    }

    // ========== Getter Methods ==========

    public RoleId getRoleId() {
        return roleId;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public RoleName getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public Set<PermissionCode> getPermissions() {
        return permissions;
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
        Role role = (Role) o;
        if (roleId == null || role.roleId == null) {
            return Objects.equals(name, role.name) && Objects.equals(tenantId, role.tenantId);
        }
        return Objects.equals(roleId, role.roleId);
    }

    @Override
    public int hashCode() {
        return roleId != null ? Objects.hash(roleId) : Objects.hash(name, tenantId);
    }

    @Override
    public String toString() {
        return "Role{"
                + "roleId="
                + roleId
                + ", tenantId="
                + tenantId
                + ", name="
                + name
                + ", isSystem="
                + isSystem
                + "}";
    }
}
