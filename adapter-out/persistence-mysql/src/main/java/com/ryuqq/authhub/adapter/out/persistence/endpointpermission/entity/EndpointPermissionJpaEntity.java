package com.ryuqq.authhub.adapter.out.persistence.endpointpermission.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EndpointPermissionJpaEntity - 엔드포인트 권한 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 endpoint_permissions 테이블과 매핑됩니다.
 *
 * <p><strong>Endpoint Permission 전략:</strong>
 *
 * <ul>
 *   <li>serviceName + path + method 조합이 유니크
 *   <li>requiredPermissions, requiredRoles는 콤마(,) 구분 문자열로 저장
 *   <li>path에 와일드카드(**)나 Path Variable({id}) 포함 가능
 * </ul>
 *
 * <p><strong>Soft Delete 전략:</strong>
 *
 * <ul>
 *   <li>deleted 컬럼으로 논리 삭제 관리
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
        name = "endpoint_permissions",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_endpoint_permissions_service_path_method",
                    columnNames = {"service_name", "path", "method"})
        },
        indexes = {
            @Index(name = "idx_endpoint_permissions_service_name", columnList = "service_name"),
            @Index(name = "idx_endpoint_permissions_method", columnList = "method"),
            @Index(name = "idx_endpoint_permissions_is_public", columnList = "is_public"),
            @Index(name = "idx_endpoint_permissions_deleted", columnList = "deleted")
        })
public class EndpointPermissionJpaEntity extends BaseAuditEntity {

    /** 기본 키 - AUTO_INCREMENT (내부 Long ID) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 엔드포인트 권한 UUID - UUIDv7 (외부 식별자) */
    @Column(
            name = "endpoint_permission_id",
            nullable = false,
            unique = true,
            columnDefinition = "BINARY(16)")
    private UUID endpointPermissionId;

    /** 서비스 이름 (예: auth-hub, user-service) */
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    /** 엔드포인트 경로 (예: /api/v1/users/{userId}) */
    @Column(name = "path", nullable = false, length = 500)
    private String path;

    /** HTTP 메서드 (GET, POST, PUT, DELETE 등) */
    @Column(name = "method", nullable = false, length = 10)
    private String method;

    /** 설명 */
    @Column(name = "description", length = 500)
    private String description;

    /** 공개 여부 (true: 인증 불필요) */
    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    /** 필요 권한 목록 (콤마 구분, OR 조건) */
    @Column(name = "required_permissions", length = 2000)
    private String requiredPermissions;

    /** 필요 역할 목록 (콤마 구분, OR 조건) */
    @Column(name = "required_roles", length = 1000)
    private String requiredRoles;

    /** 버전 (낙관적 락) */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /** 삭제 여부 (Soft Delete) */
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected EndpointPermissionJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     */
    private EndpointPermissionJpaEntity(
            Long id,
            UUID endpointPermissionId,
            String serviceName,
            String path,
            String method,
            String description,
            boolean isPublic,
            String requiredPermissions,
            String requiredRoles,
            Long version,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.endpointPermissionId = endpointPermissionId;
        this.serviceName = serviceName;
        this.path = path;
        this.method = method;
        this.description = description;
        this.isPublic = isPublic;
        this.requiredPermissions = requiredPermissions;
        this.requiredRoles = requiredRoles;
        this.version = version;
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
     * @param endpointPermissionId 엔드포인트 권한 UUID
     * @param serviceName 서비스 이름
     * @param path 엔드포인트 경로
     * @param method HTTP 메서드
     * @param description 설명
     * @param isPublic 공개 여부
     * @param requiredPermissions 필요 권한 (콤마 구분)
     * @param requiredRoles 필요 역할 (콤마 구분)
     * @param version 버전
     * @param deleted 삭제 여부
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return EndpointPermissionJpaEntity 인스턴스
     */
    public static EndpointPermissionJpaEntity of(
            Long id,
            UUID endpointPermissionId,
            String serviceName,
            String path,
            String method,
            String description,
            boolean isPublic,
            String requiredPermissions,
            String requiredRoles,
            Long version,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new EndpointPermissionJpaEntity(
                id,
                endpointPermissionId,
                serviceName,
                path,
                method,
                description,
                isPublic,
                requiredPermissions,
                requiredRoles,
                version,
                deleted,
                createdAt,
                updatedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getId() {
        return id;
    }

    public UUID getEndpointPermissionId() {
        return endpointPermissionId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getRequiredPermissions() {
        return requiredPermissions;
    }

    public String getRequiredRoles() {
        return requiredRoles;
    }

    public Long getVersion() {
        return version;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
