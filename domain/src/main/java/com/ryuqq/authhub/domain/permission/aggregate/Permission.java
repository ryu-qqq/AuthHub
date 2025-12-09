package com.ryuqq.authhub.domain.permission.aggregate;

import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotDeletableException;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotModifiableException;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.Action;
import com.ryuqq.authhub.domain.permission.vo.PermissionDescription;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.permission.vo.Resource;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Permission Aggregate Root - 권한 도메인 모델
 *
 * <p>시스템 내 권한을 정의하는 Aggregate입니다.
 * 권한은 "{resource}:{action}" 형식의 유니크 키를 가집니다.
 *
 * <p><strong>권한 키 예시:</strong>
 *
 * <ul>
 *   <li>user:read - 사용자 조회 권한
 *   <li>user:create - 사용자 생성 권한
 *   <li>organization:manage - 조직 관리 권한
 *   <li>tenant:admin - 테넌트 관리자 권한
 * </ul>
 *
 * <p><strong>권한 유형 (PermissionType):</strong>
 *
 * <ul>
 *   <li>SYSTEM: 시스템 기본 권한 (수정/삭제 불가)
 *   <li>CUSTOM: 사용자 정의 권한 (수정/삭제 가능)
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
public final class Permission {

    private final PermissionId permissionId;
    private final PermissionKey key;
    private final PermissionDescription description;
    private final PermissionType type;
    private final boolean deleted;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Permission(
            PermissionId permissionId,
            PermissionKey key,
            PermissionDescription description,
            PermissionType type,
            boolean deleted,
            Instant createdAt,
            Instant updatedAt) {
        validateRequired(key, type, createdAt, updatedAt);
        this.permissionId = permissionId;
        this.key = key;
        this.description = description != null ? description : PermissionDescription.empty();
        this.type = type;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private void validateRequired(
            PermissionKey key,
            PermissionType type,
            Instant createdAt,
            Instant updatedAt) {
        if (key == null) {
            throw new IllegalArgumentException("PermissionKey는 null일 수 없습니다");
        }
        if (type == null) {
            throw new IllegalArgumentException("PermissionType은 null일 수 없습니다");
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
     * 새로운 시스템 권한 생성
     *
     * @param resource 리소스
     * @param action 행위
     * @param description 권한 설명
     * @param clock 시간 제공자
     * @return 새로운 시스템 권한 인스턴스
     */
    public static Permission createSystem(
            Resource resource, Action action, PermissionDescription description, Clock clock) {
        Instant now = clock.instant();
        PermissionKey key = PermissionKey.of(resource, action);
        return new Permission(null, key, description, PermissionType.SYSTEM, false, now, now);
    }

    /**
     * 새로운 커스텀 권한 생성
     *
     * @param resource 리소스
     * @param action 행위
     * @param description 권한 설명
     * @param clock 시간 제공자
     * @return 새로운 커스텀 권한 인스턴스
     */
    public static Permission createCustom(
            Resource resource, Action action, PermissionDescription description, Clock clock) {
        Instant now = clock.instant();
        PermissionKey key = PermissionKey.of(resource, action);
        return new Permission(null, key, description, PermissionType.CUSTOM, false, now, now);
    }

    /**
     * PermissionKey로 새로운 시스템 권한 생성
     *
     * @param key 권한 키
     * @param description 권한 설명
     * @param clock 시간 제공자
     * @return 새로운 시스템 권한 인스턴스
     */
    public static Permission createSystemWithKey(
            PermissionKey key, PermissionDescription description, Clock clock) {
        Instant now = clock.instant();
        return new Permission(null, key, description, PermissionType.SYSTEM, false, now, now);
    }

    /**
     * PermissionKey로 새로운 커스텀 권한 생성
     *
     * @param key 권한 키
     * @param description 권한 설명
     * @param clock 시간 제공자
     * @return 새로운 커스텀 권한 인스턴스
     */
    public static Permission createCustomWithKey(
            PermissionKey key, PermissionDescription description, Clock clock) {
        Instant now = clock.instant();
        return new Permission(null, key, description, PermissionType.CUSTOM, false, now, now);
    }

    /**
     * DB에서 Permission 재구성 (reconstitute)
     *
     * @param permissionId 권한 ID
     * @param key 권한 키
     * @param description 권한 설명
     * @param type 권한 유형
     * @param deleted 삭제 여부
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 Permission 인스턴스
     */
    public static Permission reconstitute(
            PermissionId permissionId,
            PermissionKey key,
            PermissionDescription description,
            PermissionType type,
            boolean deleted,
            Instant createdAt,
            Instant updatedAt) {
        if (permissionId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null permissionId");
        }
        return new Permission(permissionId, key, description, type, deleted, createdAt, updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 권한 설명 변경
     *
     * @param newDescription 새로운 설명
     * @param clock 시간 제공자
     * @return 설명이 변경된 새로운 Permission 인스턴스
     * @throws SystemPermissionNotModifiableException 시스템 권한인 경우
     */
    public Permission changeDescription(PermissionDescription newDescription, Clock clock) {
        validateModifiable();
        return new Permission(
                this.permissionId,
                this.key,
                newDescription,
                this.type,
                this.deleted,
                this.createdAt,
                clock.instant());
    }

    /**
     * 권한 삭제 (소프트 삭제)
     *
     * @param clock 시간 제공자
     * @return 삭제된 새로운 Permission 인스턴스
     * @throws SystemPermissionNotDeletableException 시스템 권한인 경우
     */
    public Permission delete(Clock clock) {
        if (type.isSystem()) {
            throw new SystemPermissionNotDeletableException(key.value());
        }
        return new Permission(
                this.permissionId,
                this.key,
                this.description,
                this.type,
                true,
                this.createdAt,
                clock.instant());
    }

    private void validateModifiable() {
        if (type.isSystem()) {
            throw new SystemPermissionNotModifiableException(key.value());
        }
    }

    // ========== Helper Methods ==========

    public UUID permissionIdValue() {
        return permissionId != null ? permissionId.value() : null;
    }

    public String keyValue() {
        return key.value();
    }

    public String resourceValue() {
        return key.resource().value();
    }

    public String actionValue() {
        return key.action().value();
    }

    public String descriptionValue() {
        return description.value();
    }

    public String typeValue() {
        return type.name();
    }

    public boolean isNew() {
        return permissionId == null;
    }

    public boolean isSystem() {
        return type.isSystem();
    }

    public boolean isCustom() {
        return type.isCustom();
    }

    public boolean isDeleted() {
        return deleted;
    }

    // ========== Getter Methods ==========

    public PermissionId getPermissionId() {
        return permissionId;
    }

    public PermissionKey getKey() {
        return key;
    }

    public Resource getResource() {
        return key.resource();
    }

    public Action getAction() {
        return key.action();
    }

    public PermissionDescription getDescription() {
        return description;
    }

    public PermissionType getType() {
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
        Permission that = (Permission) o;
        if (permissionId == null || that.permissionId == null) {
            return false;
        }
        return Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return permissionId != null ? Objects.hash(permissionId) : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "Permission{"
                + "permissionId="
                + permissionId
                + ", key="
                + key
                + ", type="
                + type
                + ", deleted="
                + deleted
                + "}";
    }
}
