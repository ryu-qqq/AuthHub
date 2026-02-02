package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.SoftDeletableEntity;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
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
import java.time.Instant;

/**
 * PermissionEndpointJpaEntity - 권한 엔드포인트 매핑 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>설계 목적:</strong>
 *
 * <ul>
 *   <li>Gateway가 요청 URL에 대한 필요 권한을 조회할 때 사용
 *   <li>하나의 Permission에 여러 Endpoint를 매핑 가능 (1:N)
 *   <li>URL 패턴과 HTTP Method 조합으로 권한 식별
 * </ul>
 *
 * <p><strong>Long FK 전략:</strong>
 *
 * <ul>
 *   <li>permissionEndpointId(Long)를 Auto Increment PK로 사용
 *   <li>permissionId(Long)를 FK로 사용 (JPA 관계 어노테이션 금지)
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
        name = "permission_endpoints",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_permission_endpoints_service_url_method",
                    columnNames = {"service_name", "url_pattern", "http_method"})
        },
        indexes = {
            @Index(name = "idx_permission_endpoints_permission_id", columnList = "permission_id"),
            @Index(name = "idx_permission_endpoints_service_name", columnList = "service_name"),
            @Index(name = "idx_permission_endpoints_url_pattern", columnList = "url_pattern"),
            @Index(name = "idx_permission_endpoints_http_method", columnList = "http_method"),
            @Index(name = "idx_permission_endpoints_is_public", columnList = "is_public")
        })
public class PermissionEndpointJpaEntity extends SoftDeletableEntity {

    /** 권한 엔드포인트 ID - Auto Increment (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_endpoint_id", nullable = false)
    private Long permissionEndpointId;

    /** 권한 ID (FK - Long FK 전략, JPA 관계 금지) */
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    /** 서비스 이름 (예: product-service, order-api) */
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    /** URL 패턴 (예: /api/v1/users/{id}) */
    @Column(name = "url_pattern", nullable = false, length = 500)
    private String urlPattern;

    /** HTTP 메서드 (GET, POST, PUT, DELETE 등) */
    @Enumerated(EnumType.STRING)
    @Column(name = "http_method", nullable = false, length = 10)
    private HttpMethod httpMethod;

    /** 설명 */
    @Column(name = "description", length = 500)
    private String description;

    /** 공개 엔드포인트 여부 (인증 없이 접근 가능) */
    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected PermissionEndpointJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     *
     * @param permissionEndpointId 엔드포인트 ID (PK, Long - null이면 신규)
     * @param permissionId 권한 ID (FK)
     * @param serviceName 서비스 이름
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @param description 설명
     * @param isPublic 공개 엔드포인트 여부
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @param deletedAt 삭제 일시 (Instant, UTC)
     */
    private PermissionEndpointJpaEntity(
            Long permissionEndpointId,
            Long permissionId,
            String serviceName,
            String urlPattern,
            HttpMethod httpMethod,
            String description,
            boolean isPublic,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.permissionEndpointId = permissionEndpointId;
        this.permissionId = permissionId;
        this.serviceName = serviceName;
        this.urlPattern = urlPattern;
        this.httpMethod = httpMethod;
        this.description = description;
        this.isPublic = isPublic;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * @param permissionEndpointId 엔드포인트 ID (PK, Long - null이면 신규)
     * @param permissionId 권한 ID (FK)
     * @param serviceName 서비스 이름
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @param description 설명
     * @param isPublic 공개 엔드포인트 여부
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @param deletedAt 삭제 일시 (Instant, UTC)
     * @return PermissionEndpointJpaEntity 인스턴스
     */
    public static PermissionEndpointJpaEntity of(
            Long permissionEndpointId,
            Long permissionId,
            String serviceName,
            String urlPattern,
            HttpMethod httpMethod,
            String description,
            boolean isPublic,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new PermissionEndpointJpaEntity(
                permissionEndpointId,
                permissionId,
                serviceName,
                urlPattern,
                httpMethod,
                description,
                isPublic,
                createdAt,
                updatedAt,
                deletedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getPermissionEndpointId() {
        return permissionEndpointId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPublic() {
        return isPublic;
    }
}
