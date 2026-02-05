package com.ryuqq.authhub.domain.permission.aggregate;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotDeletableException;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotModifiableException;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.Action;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.permission.vo.Resource;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import java.time.Instant;
import java.util.Objects;

/**
 * Permission Aggregate Root - 권한 도메인 모델
 *
 * <p>시스템 내 권한을 정의하는 Aggregate입니다. 권한은 Service에 종속되며, 동일 Service 내에서 "{resource}:{action}" 형식의 유니크
 * 키를 가집니다.
 *
 * <p><strong>Service 기반 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 특정 Service에 종속됩니다 (serviceId NOT NULL)
 *   <li>동일 permissionKey가 다른 서비스에 존재할 수 있습니다 (UNIQUE(service_id, permission_key))
 *   <li>테넌트별 권한 분리는 Permission이 아닌 Role 레벨에서 처리됩니다
 * </ul>
 *
 * <p><strong>권한 키 예시:</strong>
 *
 * <ul>
 *   <li>product:create - 상품 생성 권한 (SVC_STORE)
 *   <li>product:create - 상품 생성 권한 (SVC_B2B) - 다른 서비스에서 동일 키 가능
 *   <li>user:manage - 사용자 관리 권한 (SVC_AUTHHUB)
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
 *   <li>Null 검증은 VO 생성 시점에서 처리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@SuppressWarnings("PMD.GodClass")
public final class Permission {

    private final PermissionId permissionId;
    private final ServiceId serviceId;
    private final PermissionKey permissionKey;
    private final Resource resource;
    private final Action action;
    private String description;
    private final PermissionType type;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private Permission(
            PermissionId permissionId,
            ServiceId serviceId,
            PermissionKey permissionKey,
            Resource resource,
            Action action,
            String description,
            PermissionType type,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.permissionId = permissionId;
        this.serviceId = serviceId;
        this.permissionKey = permissionKey;
        this.resource = resource;
        this.action = action;
        this.description = description;
        this.type = type;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 시스템 권한 생성
     *
     * <p>시스템 권한은 수정/삭제가 불가능합니다.
     *
     * @param serviceId 서비스 ID (필수)
     * @param resource 리소스 (예: user, role, organization)
     * @param action 행위 (예: read, create, update, delete, manage)
     * @param description 권한 설명
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 시스템 Permission 인스턴스
     */
    public static Permission createSystem(
            ServiceId serviceId, String resource, String action, String description, Instant now) {
        Resource resourceVo = Resource.of(resource);
        Action actionVo = Action.of(action);
        PermissionKey permissionKey = PermissionKey.of(resourceVo, actionVo);
        return new Permission(
                null,
                serviceId,
                permissionKey,
                resourceVo,
                actionVo,
                description,
                PermissionType.SYSTEM,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 새로운 커스텀 권한 생성
     *
     * <p>커스텀 권한은 수정/삭제가 가능합니다.
     *
     * @param serviceId 서비스 ID (필수)
     * @param resource 리소스 (예: user, role, organization)
     * @param action 행위 (예: read, create, update, delete, manage)
     * @param description 권한 설명
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 커스텀 Permission 인스턴스
     */
    public static Permission createCustom(
            ServiceId serviceId, String resource, String action, String description, Instant now) {
        Resource resourceVo = Resource.of(resource);
        Action actionVo = Action.of(action);
        PermissionKey permissionKey = PermissionKey.of(resourceVo, actionVo);
        return new Permission(
                null,
                serviceId,
                permissionKey,
                resourceVo,
                actionVo,
                description,
                PermissionType.CUSTOM,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 통합 권한 생성 (권한 유형을 Permission이 내부적으로 판단)
     *
     * <p>isSystem이 true면 SYSTEM 권한, false면 CUSTOM 권한.
     *
     * @param serviceId 서비스 ID (필수)
     * @param resource 리소스 (예: user, role, organization)
     * @param action 행위 (예: read, create, update, delete, manage)
     * @param description 권한 설명
     * @param isSystem 시스템 권한 여부
     * @param now 현재 시간 (외부 주입)
     * @return Permission 인스턴스
     */
    public static Permission create(
            ServiceId serviceId,
            String resource,
            String action,
            String description,
            boolean isSystem,
            Instant now) {
        PermissionType type = isSystem ? PermissionType.SYSTEM : PermissionType.CUSTOM;
        Resource resourceVo = Resource.of(resource);
        Action actionVo = Action.of(action);
        PermissionKey permissionKey = PermissionKey.of(resourceVo, actionVo);
        return new Permission(
                null,
                serviceId,
                permissionKey,
                resourceVo,
                actionVo,
                description,
                type,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * DB에서 Permission 재구성 (reconstitute) - VO 타입
     *
     * @param permissionId 권한 ID
     * @param serviceId 서비스 ID
     * @param permissionKey 권한 키
     * @param resource 리소스
     * @param action 행위
     * @param description 권한 설명
     * @param type 권한 유형
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 Permission 인스턴스
     */
    public static Permission reconstitute(
            PermissionId permissionId,
            ServiceId serviceId,
            PermissionKey permissionKey,
            Resource resource,
            Action action,
            String description,
            PermissionType type,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new Permission(
                permissionId,
                serviceId,
                permissionKey,
                resource,
                action,
                description,
                type,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * DB에서 Permission 재구성 (reconstitute) - String 타입 편의 메서드
     *
     * @param permissionId 권한 ID (Long)
     * @param serviceId 서비스 ID (Long)
     * @param permissionKey 권한 키 (String)
     * @param resource 리소스 (String)
     * @param action 행위 (String)
     * @param description 권한 설명
     * @param type 권한 유형
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 Permission 인스턴스
     */
    public static Permission reconstitute(
            Long permissionId,
            Long serviceId,
            String permissionKey,
            String resource,
            String action,
            String description,
            PermissionType type,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return reconstitute(
                PermissionId.of(permissionId),
                ServiceId.fromNullable(serviceId),
                PermissionKey.of(permissionKey),
                Resource.of(resource),
                Action.of(action),
                description,
                type,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 권한 정보 수정 (UpdateData 패턴)
     *
     * @param updateData 수정할 데이터
     * @param changedAt 변경 시간 (외부 주입)
     * @throws SystemPermissionNotModifiableException 시스템 권한인 경우
     */
    public void update(PermissionUpdateData updateData, Instant changedAt) {
        validateModifiable();
        if (updateData.description() != null) {
            this.description = updateData.description();
        }
        this.updatedAt = changedAt;
    }

    /**
     * 권한 삭제 (소프트 삭제)
     *
     * @param now 삭제 시간 (외부 주입)
     * @throws SystemPermissionNotDeletableException 시스템 권한인 경우
     */
    public void delete(Instant now) {
        if (type.isSystem()) {
            throw new SystemPermissionNotDeletableException(permissionKey.value());
        }
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 권한 복원
     *
     * @param now 복원 시간 (외부 주입)
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    private void validateModifiable() {
        if (type.isSystem()) {
            throw new SystemPermissionNotModifiableException(permissionKey.value());
        }
    }

    // ========== Query Methods ==========

    /**
     * 권한 ID 값 반환
     *
     * @return 권한 ID (Long) 또는 null (신규 생성 시)
     */
    public Long permissionIdValue() {
        return permissionId != null ? permissionId.value() : null;
    }

    /**
     * 서비스 ID 값 반환
     *
     * @return 서비스 ID (Long)
     */
    public Long serviceIdValue() {
        return serviceId != null ? serviceId.value() : null;
    }

    /**
     * 권한 키 값 반환
     *
     * @return 권한 키 (예: "user:read")
     */
    public String permissionKeyValue() {
        return permissionKey.value();
    }

    /**
     * 리소스 값 반환
     *
     * @return 리소스 (예: "user")
     */
    public String resourceValue() {
        return resource.value();
    }

    /**
     * 행위 값 반환
     *
     * @return 행위 (예: "read")
     */
    public String actionValue() {
        return action.value();
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
     * 권한 유형 값 반환
     *
     * @return 권한 유형 문자열 (SYSTEM 또는 CUSTOM)
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
        return permissionId == null;
    }

    /**
     * 시스템 권한 여부 확인
     *
     * @return 시스템 권한이면 true
     */
    public boolean isSystem() {
        return type.isSystem();
    }

    /**
     * 커스텀 권한 여부 확인
     *
     * @return 커스텀 권한이면 true
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

    // ========== Getter Methods ==========

    public PermissionId getPermissionId() {
        return permissionId;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public PermissionKey getPermissionKey() {
        return permissionKey;
    }

    public Resource getResource() {
        return resource;
    }

    public Action getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public PermissionType getType() {
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
        Permission that = (Permission) o;
        if (permissionId == null || that.permissionId == null) {
            return Objects.equals(serviceId, that.serviceId)
                    && Objects.equals(permissionKey, that.permissionKey);
        }
        return Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        if (permissionId != null) {
            return Objects.hash(permissionId);
        }
        return Objects.hash(serviceId, permissionKey);
    }

    @Override
    public String toString() {
        return "Permission{"
                + "permissionId="
                + permissionId
                + ", serviceId="
                + serviceId
                + ", permissionKey='"
                + permissionKey
                + '\''
                + ", type="
                + type
                + ", deleted="
                + deletionStatus.isDeleted()
                + '}';
    }
}
