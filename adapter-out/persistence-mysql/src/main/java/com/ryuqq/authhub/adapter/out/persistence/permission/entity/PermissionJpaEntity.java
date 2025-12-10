package com.ryuqq.authhub.adapter.out.persistence.permission.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PermissionJpaEntity - 권한 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>Permission Key 전략:</strong>
 *
 * <ul>
 *   <li>key: "{resource}:{action}" 형식 (예: "user:read", "organization:manage")
 *   <li>resource와 action을 개별 컬럼으로 저장하여 검색 최적화
 * </ul>
 *
 * <p><strong>Soft Delete 전략:</strong>
 *
 * <ul>
 *   <li>deleted 컬럼으로 논리 삭제 관리
 *   <li>SYSTEM 권한은 삭제 불가 (도메인 레벨 검증)
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
                    name = "uk_permissions_key",
                    columnNames = {"permission_key"})
        },
        indexes = {
            @Index(name = "idx_permissions_resource", columnList = "resource"),
            @Index(name = "idx_permissions_type", columnList = "type"),
            @Index(name = "idx_permissions_deleted", columnList = "deleted")
        })
public class PermissionJpaEntity extends BaseAuditEntity {

    /** 기본 키 - AUTO_INCREMENT (내부 Long ID) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 권한 UUID - UUIDv7 (외부 식별자) */
    @Column(
            name = "permission_id",
            nullable = false,
            unique = true,
            columnDefinition = "BINARY(16)")
    private UUID permissionId;

    /** 권한 키 - "{resource}:{action}" 형식 */
    @Column(name = "permission_key", nullable = false, length = 100)
    private String permissionKey;

    /** 리소스 - 권한 대상 (예: user, organization, tenant) */
    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    /** 액션 - 권한 행위 (예: read, create, update, delete, manage) */
    @Column(name = "action", nullable = false, length = 50)
    private String action;

    /** 권한 설명 */
    @Column(name = "description", length = 500)
    private String description;

    /** 권한 유형 */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PermissionType type;

    /** 삭제 여부 (Soft Delete) */
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

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
     */
    private PermissionJpaEntity(
            Long id,
            UUID permissionId,
            String permissionKey,
            String resource,
            String action,
            String description,
            PermissionType type,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.permissionId = permissionId;
        this.permissionKey = permissionKey;
        this.resource = resource;
        this.action = action;
        this.description = description;
        this.type = type;
        this.deleted = deleted;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * <p>Mapper에서 Domain → Entity 변환 시 사용합니다.
     *
     * @param id 내부 기본 키 (신규 생성 시 null)
     * @param permissionId 권한 UUID
     * @param permissionKey 권한 키
     * @param resource 리소스
     * @param action 액션
     * @param description 권한 설명
     * @param type 권한 유형
     * @param deleted 삭제 여부
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return PermissionJpaEntity 인스턴스
     */
    public static PermissionJpaEntity of(
            Long id,
            UUID permissionId,
            String permissionKey,
            String resource,
            String action,
            String description,
            PermissionType type,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new PermissionJpaEntity(
                id,
                permissionId,
                permissionKey,
                resource,
                action,
                description,
                type,
                deleted,
                createdAt,
                updatedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getId() {
        return id;
    }

    public UUID getPermissionId() {
        return permissionId;
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

    public boolean isDeleted() {
        return deleted;
    }
}
