package com.ryuqq.authhub.adapter.out.persistence.permission.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PermissionUsageJpaEntity - 권한 사용 이력 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>용도:</strong>
 *
 * <ul>
 *   <li>특정 권한이 어떤 서비스에서 사용되는지 추적
 *   <li>n8n 승인 시 사용 이력 등록
 *   <li>미사용 권한 감지를 위한 lastScannedAt 관리
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
        name = "permission_usages",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_permission_usages_key_service",
                    columnNames = {"permission_key", "service_name"})
        },
        indexes = {
            @Index(name = "idx_permission_usages_key", columnList = "permission_key"),
            @Index(name = "idx_permission_usages_service", columnList = "service_name"),
            @Index(name = "idx_permission_usages_scanned", columnList = "last_scanned_at")
        })
public class PermissionUsageJpaEntity extends BaseAuditEntity {

    /** 기본 키 - AUTO_INCREMENT (내부 Long ID) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 사용 이력 UUID - UUIDv7 (외부 식별자) */
    @Column(name = "usage_id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID usageId;

    /** 권한 키 - Permission.permissionKey와 동일 */
    @Column(name = "permission_key", nullable = false, length = 100)
    private String permissionKey;

    /** 서비스명 - 권한을 사용하는 서비스 (예: product-service) */
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    /** 코드 위치 목록 - JSON 배열로 저장 (예: ["ProductController.java:45"]) */
    @Column(name = "locations", columnDefinition = "TEXT")
    private String locations;

    /** 마지막 스캔 시간 - CI/CD에서 마지막으로 스캔된 시간 */
    @Column(name = "last_scanned_at", nullable = false)
    private LocalDateTime lastScannedAt;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected PermissionUsageJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     */
    private PermissionUsageJpaEntity(
            Long id,
            UUID usageId,
            String permissionKey,
            String serviceName,
            String locations,
            LocalDateTime lastScannedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.usageId = usageId;
        this.permissionKey = permissionKey;
        this.serviceName = serviceName;
        this.locations = locations;
        this.lastScannedAt = lastScannedAt;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * @param id 내부 기본 키 (신규 생성 시 null)
     * @param usageId 사용 이력 UUID
     * @param permissionKey 권한 키
     * @param serviceName 서비스명
     * @param locations 코드 위치 목록 (JSON)
     * @param lastScannedAt 마지막 스캔 시간
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return PermissionUsageJpaEntity 인스턴스
     */
    public static PermissionUsageJpaEntity of(
            Long id,
            UUID usageId,
            String permissionKey,
            String serviceName,
            String locations,
            LocalDateTime lastScannedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new PermissionUsageJpaEntity(
                id,
                usageId,
                permissionKey,
                serviceName,
                locations,
                lastScannedAt,
                createdAt,
                updatedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getId() {
        return id;
    }

    public UUID getUsageId() {
        return usageId;
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getLocations() {
        return locations;
    }

    public LocalDateTime getLastScannedAt() {
        return lastScannedAt;
    }
}
