package com.ryuqq.authhub.adapter.out.persistence.permission.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.SoftDeletableEntity;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

/**
 * PermissionJpaEntity - 권한 JPA Entity (Global Only)
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트별 권한 분리는 Permission이 아닌 Role 레벨에서 처리됩니다
 *   <li>Gateway에서 URL-Permission 매핑은 PermissionEndpoint에서 관리됩니다
 * </ul>
 *
 * <p><strong>Long FK 전략:</strong>
 *
 * <ul>
 *   <li>permissionId(Long)를 Auto Increment PK로 사용
 * </ul>
 *
 * <p><strong>SoftDeletableEntity 상속:</strong>
 *
 * <ul>
 *   <li>createdAt, updatedAt (BaseAuditEntity)
 *   <li>deletedAt (SoftDeletableEntity)
 * </ul>
 *
 * <p><strong>Lombok 금지:</strong>
 *
 * <ul>
 *   <li>Plain Java getter 사용
 *   <li>Setter 제공 금지
 *   <li>명시적 생성자 제공
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Entity
@Table(
        name = "permissions",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_permission_service_key",
                    columnNames = {"service_id", "permission_key"})
        })
public class PermissionJpaEntity extends SoftDeletableEntity {

    /** 권한 ID - Auto Increment (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    /** 서비스 ID FK (null이면 서비스 무관) */
    @Column(name = "service_id")
    private Long serviceId;

    /** 권한 키 (resource:action 형식, 서비스 내 유니크) */
    @Column(name = "permission_key", nullable = false, length = 100)
    private String permissionKey;

    /** 리소스 (예: user, role, organization) */
    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    /** 행위 (예: read, create, update, delete, manage) */
    @Column(name = "action", nullable = false, length = 50)
    private String action;

    /** 권한 설명 */
    @Column(name = "description", length = 500)
    private String description;

    /** 권한 유형 (SYSTEM, CUSTOM) */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PermissionType type;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected PermissionJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     *
     * @param permissionId 권한 ID (PK, Long - null이면 신규)
     * @param serviceId 서비스 ID FK (NOT NULL)
     * @param permissionKey 권한 키
     * @param resource 리소스
     * @param action 행위
     * @param description 권한 설명
     * @param type 권한 유형
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @param deletedAt 삭제 일시 (Instant, UTC)
     */
    private PermissionJpaEntity(
            Long permissionId,
            Long serviceId,
            String permissionKey,
            String resource,
            String action,
            String description,
            PermissionType type,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.permissionId = permissionId;
        this.serviceId = serviceId;
        this.permissionKey = permissionKey;
        this.resource = resource;
        this.action = action;
        this.description = description;
        this.type = type;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * @param permissionId 권한 ID (PK, Long - null이면 신규)
     * @param serviceId 서비스 ID FK (NOT NULL)
     * @param permissionKey 권한 키
     * @param resource 리소스
     * @param action 행위
     * @param description 권한 설명
     * @param type 권한 유형
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @param deletedAt 삭제 일시 (Instant, UTC)
     * @return PermissionJpaEntity 인스턴스
     */
    public static PermissionJpaEntity of(
            Long permissionId,
            Long serviceId,
            String permissionKey,
            String resource,
            String action,
            String description,
            PermissionType type,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new PermissionJpaEntity(
                permissionId,
                serviceId,
                permissionKey,
                resource,
                action,
                description,
                type,
                createdAt,
                updatedAt,
                deletedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getPermissionId() {
        return permissionId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    public String getResource() {
        return resource;
    }

    public String getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public PermissionType getType() {
        return type;
    }
}
