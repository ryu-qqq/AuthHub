package com.ryuqq.authhub.adapter.out.persistence.service.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * ServiceJpaEntity - 서비스 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>Long PK + ServiceCode 전략:</strong>
 *
 * <ul>
 *   <li>serviceId(Long) Auto Increment PK - FK 참조용
 *   <li>serviceCode(String) UNIQUE 비즈니스 식별자 (예: SVC_STORE, SVC_B2B)
 * </ul>
 *
 * <p><strong>BaseAuditEntity 상속:</strong>
 *
 * <ul>
 *   <li>createdAt, updatedAt (BaseAuditEntity)
 *   <li>SoftDelete 미사용 (ACTIVE/INACTIVE 상태로 관리)
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
@Table(name = "services")
public class ServiceJpaEntity extends BaseAuditEntity {

    /** 서비스 ID (Primary Key, Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    /** 서비스 코드 (비즈니스 식별자, UNIQUE) */
    @Column(name = "service_code", nullable = false, unique = true, length = 50)
    private String serviceCode;

    /** 서비스 이름 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 서비스 설명 */
    @Column(name = "description", length = 500)
    private String description;

    /** 서비스 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ServiceStatus status;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected ServiceJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     *
     * @param serviceId 서비스 ID (PK, nullable for new)
     * @param serviceCode 서비스 코드 (비즈니스 식별자)
     * @param name 서비스 이름
     * @param description 서비스 설명
     * @param status 서비스 상태
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     */
    private ServiceJpaEntity(
            Long serviceId,
            String serviceCode,
            String name,
            String description,
            ServiceStatus status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.serviceId = serviceId;
        this.serviceCode = serviceCode;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * @param serviceId 서비스 ID (PK, nullable for new)
     * @param serviceCode 서비스 코드 (비즈니스 식별자)
     * @param name 서비스 이름
     * @param description 서비스 설명
     * @param status 서비스 상태
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @return ServiceJpaEntity 인스턴스
     */
    public static ServiceJpaEntity of(
            Long serviceId,
            String serviceCode,
            String name,
            String description,
            ServiceStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new ServiceJpaEntity(
                serviceId, serviceCode, name, description, status, createdAt, updatedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getServiceId() {
        return serviceId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ServiceStatus getStatus() {
        return status;
    }
}
