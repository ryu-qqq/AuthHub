package com.ryuqq.authhub.domain.role.aggregate;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotDeletableException;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotModifiableException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.Instant;
import java.util.Objects;

/**
 * Role Aggregate Root - 역할 도메인 모델
 *
 * <p>시스템 내 역할을 정의하는 Aggregate입니다. 역할은 유니크한 이름을 가집니다.
 *
 * <p><strong>역할 예시:</strong>
 *
 * <ul>
 *   <li>SUPER_ADMIN - 슈퍼 관리자 (전체 시스템 관리)
 *   <li>TENANT_ADMIN - 테넌트 관리자 (테넌트 내 관리)
 *   <li>USER_MANAGER - 사용자 관리자
 *   <li>VIEWER - 조회만 가능
 * </ul>
 *
 * <p><strong>역할 유형 (RoleType):</strong>
 *
 * <ul>
 *   <li>SYSTEM: 시스템 기본 역할 (수정/삭제 불가)
 *   <li>CUSTOM: 사용자 정의 역할 (수정/삭제 가능)
 * </ul>
 *
 * <p><strong>테넌트 범위:</strong>
 *
 * <ul>
 *   <li>tenantId가 null이면 Global 역할 (전체 시스템 공유)
 *   <li>tenantId가 있으면 해당 테넌트 전용 역할
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>Law of Demeter 준수 - Getter 체이닝 금지
 *   <li>Tell, Don't Ask 패턴 - 상태 질의 대신 행위 위임
 *   <li>Long FK 전략 - JPA 관계 어노테이션 금지
 *   <li>Null 검증은 생성 시점에서 처리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Role {

    private final RoleId roleId;
    private final TenantId tenantId;
    private final RoleName name;
    private String displayName;
    private String description;
    private final RoleType type;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private Role(
            RoleId roleId,
            TenantId tenantId,
            RoleName name,
            String displayName,
            String description,
            RoleType type,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        validateRequired(name, type);
        this.roleId = roleId;
        this.tenantId = tenantId;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private void validateRequired(RoleName name, RoleType type) {
        if (name == null) {
            throw new IllegalArgumentException("name은 null일 수 없습니다");
        }
        if (type == null) {
            throw new IllegalArgumentException("type은 null일 수 없습니다");
        }
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 Global 시스템 역할 생성
     *
     * <p>시스템 역할은 수정/삭제가 불가능합니다. tenantId가 null이므로 전체 시스템에서 공유됩니다.
     *
     * @param name 역할 이름 (예: SUPER_ADMIN)
     * @param displayName 표시 이름 (예: "슈퍼 관리자")
     * @param description 역할 설명
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 시스템 Role 인스턴스
     */
    public static Role createSystem(
            RoleName name, String displayName, String description, Instant now) {
        return new Role(
                null,
                null,
                name,
                displayName,
                description,
                RoleType.SYSTEM,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 새로운 Global 커스텀 역할 생성
     *
     * <p>커스텀 역할은 수정/삭제가 가능합니다. tenantId가 null이므로 전체 시스템에서 공유됩니다.
     *
     * @param name 역할 이름
     * @param displayName 표시 이름
     * @param description 역할 설명
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 커스텀 Role 인스턴스
     */
    public static Role createCustom(
            RoleName name, String displayName, String description, Instant now) {
        return new Role(
                null,
                null,
                name,
                displayName,
                description,
                RoleType.CUSTOM,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 테넌트 전용 커스텀 역할 생성
     *
     * <p>특정 테넌트에서만 사용 가능한 커스텀 역할을 생성합니다.
     *
     * @param tenantId 테넌트 ID (필수)
     * @param name 역할 이름
     * @param displayName 표시 이름
     * @param description 역할 설명
     * @param now 현재 시간 (외부 주입)
     * @return 테넌트 전용 Role 인스턴스
     */
    public static Role createTenantCustom(
            TenantId tenantId, RoleName name, String displayName, String description, Instant now) {
        if (tenantId == null) {
            throw new IllegalArgumentException("테넌트 역할 생성 시 tenantId는 필수입니다");
        }
        return new Role(
                null,
                tenantId,
                name,
                displayName,
                description,
                RoleType.CUSTOM,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 통합 역할 생성 (역할 유형과 테넌트 범위를 Role이 내부적으로 판단)
     *
     * <p>isSystem이 true면 SYSTEM 역할 (Global), tenantId가 null이면 Global CUSTOM, tenantId가 있으면 Tenant
     * CUSTOM.
     *
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param name 역할 이름
     * @param displayName 표시 이름
     * @param description 역할 설명
     * @param isSystem 시스템 역할 여부
     * @param now 현재 시간 (외부 주입)
     * @return Role 인스턴스
     */
    public static Role create(
            TenantId tenantId,
            RoleName name,
            String displayName,
            String description,
            boolean isSystem,
            Instant now) {
        RoleType type = isSystem ? RoleType.SYSTEM : RoleType.CUSTOM;
        return new Role(
                null,
                tenantId,
                name,
                displayName,
                description,
                type,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * DB에서 Role 재구성 (reconstitute)
     *
     * @param roleId 역할 ID
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param name 역할 이름
     * @param displayName 표시 이름
     * @param description 역할 설명
     * @param type 역할 유형
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 Role 인스턴스
     */
    public static Role reconstitute(
            RoleId roleId,
            TenantId tenantId,
            RoleName name,
            String displayName,
            String description,
            RoleType type,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new Role(
                roleId,
                tenantId,
                name,
                displayName,
                description,
                type,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 역할 정보 수정 (UpdateData 패턴)
     *
     * @param updateData 수정할 데이터
     * @param changedAt 변경 시간 (외부 주입)
     * @throws SystemRoleNotModifiableException 시스템 역할인 경우
     */
    public void update(RoleUpdateData updateData, Instant changedAt) {
        validateModifiable();
        if (updateData.hasDisplayName()) {
            this.displayName = updateData.displayName();
        }
        if (updateData.hasDescription()) {
            this.description = updateData.description();
        }
        this.updatedAt = changedAt;
    }

    /**
     * 역할 삭제 (소프트 삭제)
     *
     * @param now 삭제 시간 (외부 주입)
     * @throws SystemRoleNotDeletableException 시스템 역할인 경우
     */
    public void delete(Instant now) {
        if (type.isSystem()) {
            throw new SystemRoleNotDeletableException(name);
        }
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 역할 복원
     *
     * @param now 복원 시간 (외부 주입)
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    private void validateModifiable() {
        if (type.isSystem()) {
            throw new SystemRoleNotModifiableException(name);
        }
    }

    // ========== Query Methods ==========

    /**
     * 역할 ID 값 반환
     *
     * @return 역할 ID (Long) 또는 null (신규 생성 시)
     */
    public Long roleIdValue() {
        return roleId != null ? roleId.value() : null;
    }

    /**
     * 테넌트 ID 값 반환
     *
     * @return 테넌트 ID (String) 또는 null (Global 역할)
     */
    public String tenantIdValue() {
        return tenantId != null ? tenantId.value() : null;
    }

    /**
     * 역할 이름 값 반환
     *
     * @return 역할 이름 (예: "SUPER_ADMIN")
     */
    public String nameValue() {
        return name.value();
    }

    /**
     * 표시 이름 값 반환
     *
     * @return 표시 이름 (예: "슈퍼 관리자")
     */
    public String displayNameValue() {
        return displayName;
    }

    /**
     * 설명 값 반환
     *
     * @return 설명
     */
    public String descriptionValue() {
        return description;
    }

    /**
     * 역할 유형 값 반환
     *
     * @return 역할 유형 문자열 (SYSTEM 또는 CUSTOM)
     */
    public String typeValue() {
        return type.name();
    }

    /**
     * 신규 생성 여부 확인
     *
     * @return ID가 없으면 true (신규)
     */
    public boolean isNew() {
        return roleId == null;
    }

    /**
     * 시스템 역할 여부 확인
     *
     * @return 시스템 역할이면 true
     */
    public boolean isSystem() {
        return type.isSystem();
    }

    /**
     * 커스텀 역할 여부 확인
     *
     * @return 커스텀 역할이면 true
     */
    public boolean isCustom() {
        return type.isCustom();
    }

    /**
     * 삭제 여부 확인
     *
     * @return 삭제되었으면 true
     */
    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    /**
     * 활성 여부 확인
     *
     * @return 활성 상태면 true
     */
    public boolean isActive() {
        return deletionStatus.isActive();
    }

    /**
     * Global 역할 여부 확인
     *
     * @return tenantId가 null이면 true (Global)
     */
    public boolean isGlobal() {
        return tenantId == null;
    }

    /**
     * 테넌트 전용 역할 여부 확인
     *
     * @return tenantId가 있으면 true (테넌트 전용)
     */
    public boolean isTenantSpecific() {
        return tenantId != null;
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

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public RoleType getType() {
        return type;
    }

    public DeletionStatus getDeletionStatus() {
        return deletionStatus;
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
            return Objects.equals(name, that.name) && Objects.equals(tenantId, that.tenantId);
        }
        return Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        if (roleId != null) {
            return Objects.hash(roleId);
        }
        return Objects.hash(name, tenantId);
    }

    @Override
    public String toString() {
        return "Role{"
                + "roleId="
                + roleId
                + ", name='"
                + name.value()
                + '\''
                + ", type="
                + type
                + ", deleted="
                + deletionStatus.isDeleted()
                + '}';
    }
}
