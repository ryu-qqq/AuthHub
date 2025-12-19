package com.ryuqq.authhub.domain.role.aggregate;

import com.ryuqq.authhub.domain.role.exception.SystemRoleNotDeletableException;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotModifiableException;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleDescription;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Role Aggregate Root - 역할 도메인 모델
 *
 * <p>시스템 내 역할을 정의하는 Aggregate입니다.
 *
 * <p><strong>역할 범위 (RoleScope):</strong>
 *
 * <ul>
 *   <li>GLOBAL: 전체 시스템 범위 (예: SUPER_ADMIN)
 *   <li>TENANT: 테넌트 범위 (예: TENANT_ADMIN)
 *   <li>ORGANIZATION: 조직 범위 (예: ORG_ADMIN, USER)
 * </ul>
 *
 * <p><strong>역할 유형 (RoleType):</strong>
 *
 * <ul>
 *   <li>SYSTEM: 시스템 기본 역할 (수정/삭제 불가)
 *   <li>CUSTOM: 사용자 정의 역할 (수정/삭제 가능)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>Law of Demeter 준수 - Getter 체이닝 금지
 *   <li>Tell, Don't Ask 패턴 - 상태 질의 대신 행위 위임
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
    private final RoleDescription description;
    private final RoleScope scope;
    private final RoleType type;
    private final boolean deleted;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Role(
            RoleId roleId,
            TenantId tenantId,
            RoleName name,
            RoleDescription description,
            RoleScope scope,
            RoleType type,
            boolean deleted,
            Instant createdAt,
            Instant updatedAt) {
        validateRequired(name, scope, type, createdAt, updatedAt);
        this.roleId = roleId;
        this.tenantId = tenantId;
        this.name = name;
        this.description = description != null ? description : RoleDescription.empty();
        this.scope = scope;
        this.type = type;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private void validateRequired(
            RoleName name, RoleScope scope, RoleType type, Instant createdAt, Instant updatedAt) {
        if (name == null) {
            throw new IllegalArgumentException("RoleName은 null일 수 없습니다");
        }
        if (scope == null) {
            throw new IllegalArgumentException("RoleScope는 null일 수 없습니다");
        }
        if (type == null) {
            throw new IllegalArgumentException("RoleType은 null일 수 없습니다");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt는 null일 수 없습니다");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("updatedAt는 null일 수 없습니다");
        }
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 시스템 역할 생성 (GLOBAL 범위)
     *
     * <p>Application Layer의 Factory에서 UUIDv7을 생성하여 전달합니다.
     *
     * @param roleId 역할 ID (UUIDv7)
     * @param name 역할 이름
     * @param description 역할 설명
     * @param clock 시간 제공자
     * @return 새로운 시스템 역할 인스턴스
     * @throws IllegalArgumentException roleId가 null인 경우
     */
    public static Role createSystemGlobal(
            RoleId roleId, RoleName name, RoleDescription description, Clock clock) {
        if (roleId == null) {
            throw new IllegalArgumentException("RoleId는 null일 수 없습니다");
        }
        Instant now = clock.instant();
        return new Role(
                roleId,
                null,
                name,
                description,
                RoleScope.GLOBAL,
                RoleType.SYSTEM,
                false,
                now,
                now);
    }

    /**
     * 새로운 커스텀 역할 생성 (TENANT 범위)
     *
     * <p>Application Layer의 Factory에서 UUIDv7을 생성하여 전달합니다.
     *
     * @param roleId 역할 ID (UUIDv7)
     * @param tenantId 테넌트 ID (TENANT 범위인 경우 필수)
     * @param name 역할 이름
     * @param description 역할 설명
     * @param clock 시간 제공자
     * @return 새로운 커스텀 역할 인스턴스
     * @throws IllegalArgumentException roleId 또는 tenantId가 null인 경우
     */
    public static Role createCustomTenant(
            RoleId roleId,
            TenantId tenantId,
            RoleName name,
            RoleDescription description,
            Clock clock) {
        if (roleId == null) {
            throw new IllegalArgumentException("RoleId는 null일 수 없습니다");
        }
        if (tenantId == null) {
            throw new IllegalArgumentException("TENANT 범위 역할은 tenantId가 필수입니다");
        }
        Instant now = clock.instant();
        return new Role(
                roleId,
                tenantId,
                name,
                description,
                RoleScope.TENANT,
                RoleType.CUSTOM,
                false,
                now,
                now);
    }

    /**
     * 새로운 커스텀 역할 생성 (ORGANIZATION 범위)
     *
     * <p>Application Layer의 Factory에서 UUIDv7을 생성하여 전달합니다.
     *
     * @param roleId 역할 ID (UUIDv7)
     * @param tenantId 테넌트 ID
     * @param name 역할 이름
     * @param description 역할 설명
     * @param clock 시간 제공자
     * @return 새로운 커스텀 역할 인스턴스
     * @throws IllegalArgumentException roleId 또는 tenantId가 null인 경우
     */
    public static Role createCustomOrganization(
            RoleId roleId,
            TenantId tenantId,
            RoleName name,
            RoleDescription description,
            Clock clock) {
        if (roleId == null) {
            throw new IllegalArgumentException("RoleId는 null일 수 없습니다");
        }
        if (tenantId == null) {
            throw new IllegalArgumentException("ORGANIZATION 범위 역할은 tenantId가 필수입니다");
        }
        Instant now = clock.instant();
        return new Role(
                roleId,
                tenantId,
                name,
                description,
                RoleScope.ORGANIZATION,
                RoleType.CUSTOM,
                false,
                now,
                now);
    }

    /**
     * DB에서 Role 재구성 (reconstitute)
     *
     * @param roleId 역할 ID
     * @param tenantId 테넌트 ID (GLOBAL 범위일 경우 null 가능)
     * @param name 역할 이름
     * @param description 역할 설명
     * @param scope 역할 범위
     * @param type 역할 유형
     * @param deleted 삭제 여부
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 Role 인스턴스
     */
    public static Role reconstitute(
            RoleId roleId,
            TenantId tenantId,
            RoleName name,
            RoleDescription description,
            RoleScope scope,
            RoleType type,
            boolean deleted,
            Instant createdAt,
            Instant updatedAt) {
        if (roleId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null roleId");
        }
        return new Role(
                roleId, tenantId, name, description, scope, type, deleted, createdAt, updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 역할 이름 변경
     *
     * @param newName 새로운 이름
     * @param clock 시간 제공자
     * @return 이름이 변경된 새로운 Role 인스턴스
     * @throws SystemRoleNotModifiableException 시스템 역할인 경우
     */
    public Role changeName(RoleName newName, Clock clock) {
        validateModifiable();
        return new Role(
                this.roleId,
                this.tenantId,
                newName,
                this.description,
                this.scope,
                this.type,
                this.deleted,
                this.createdAt,
                clock.instant());
    }

    /**
     * 역할 설명 변경
     *
     * @param newDescription 새로운 설명
     * @param clock 시간 제공자
     * @return 설명이 변경된 새로운 Role 인스턴스
     * @throws SystemRoleNotModifiableException 시스템 역할인 경우
     */
    public Role changeDescription(RoleDescription newDescription, Clock clock) {
        validateModifiable();
        return new Role(
                this.roleId,
                this.tenantId,
                this.name,
                newDescription,
                this.scope,
                this.type,
                this.deleted,
                this.createdAt,
                clock.instant());
    }

    /**
     * 역할 삭제 (소프트 삭제)
     *
     * @param clock 시간 제공자
     * @return 삭제된 새로운 Role 인스턴스
     * @throws SystemRoleNotDeletableException 시스템 역할인 경우
     */
    public Role delete(Clock clock) {
        if (type.isSystem()) {
            throw new SystemRoleNotDeletableException(name.value());
        }
        return new Role(
                this.roleId,
                this.tenantId,
                this.name,
                this.description,
                this.scope,
                this.type,
                true,
                this.createdAt,
                clock.instant());
    }

    private void validateModifiable() {
        if (type.isSystem()) {
            throw new SystemRoleNotModifiableException(name.value());
        }
    }

    // ========== Helper Methods ==========

    public UUID roleIdValue() {
        return roleId != null ? roleId.value() : null;
    }

    public UUID tenantIdValue() {
        return tenantId != null ? tenantId.value() : null;
    }

    public String nameValue() {
        return name.value();
    }

    public String descriptionValue() {
        return description.value();
    }

    public String scopeValue() {
        return scope.name();
    }

    public String typeValue() {
        return type.name();
    }

    public boolean isNew() {
        return roleId == null;
    }

    public boolean isSystem() {
        return type.isSystem();
    }

    public boolean isCustom() {
        return type.isCustom();
    }

    public boolean isGlobal() {
        return scope == RoleScope.GLOBAL;
    }

    public boolean isTenantScoped() {
        return scope == RoleScope.TENANT;
    }

    public boolean isOrganizationScoped() {
        return scope == RoleScope.ORGANIZATION;
    }

    public boolean isDeleted() {
        return deleted;
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

    public RoleDescription getDescription() {
        return description;
    }

    public RoleScope getScope() {
        return scope;
    }

    public RoleType getType() {
        return type;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
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
        Role that = (Role) o;
        if (roleId == null || that.roleId == null) {
            return false;
        }
        return Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return roleId != null ? Objects.hash(roleId) : System.identityHashCode(this);
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
                + ", scope="
                + scope
                + ", type="
                + type
                + ", deleted="
                + deleted
                + "}";
    }
}
