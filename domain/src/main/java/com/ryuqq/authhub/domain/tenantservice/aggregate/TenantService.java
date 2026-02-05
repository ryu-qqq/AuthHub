package com.ryuqq.authhub.domain.tenantservice.aggregate;

import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import java.time.Instant;
import java.util.Objects;

/**
 * TenantService Aggregate Root - 테넌트-서비스 구독 도메인 모델
 *
 * <p>테넌트가 특정 서비스에 가입(구독)한 관계를 표현합니다. Permission과 Role의 서비스별 분리를 위한 기반 도메인입니다.
 *
 * <p><strong>PK 전략:</strong> Long Auto Increment (tenantServiceId)
 *
 * <p><strong>UNIQUE 제약:</strong> (tenantId, serviceId) 복합 유니크
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>Law of Demeter 준수 - Getter 체이닝 금지
 *   <li>Tell, Don't Ask 패턴 - 상태 질의 대신 행위 위임
 *   <li>Long FK 전략 - JPA 관계 어노테이션 금지
 *   <li>Null 검증은 VO 레벨에서 처리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantService {

    private final TenantServiceId tenantServiceId;
    private final TenantId tenantId;
    private final ServiceId serviceId;
    private TenantServiceStatus status;
    private final Instant subscribedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private TenantService(
            TenantServiceId tenantServiceId,
            TenantId tenantId,
            ServiceId serviceId,
            TenantServiceStatus status,
            Instant subscribedAt,
            Instant createdAt,
            Instant updatedAt) {
        this.tenantServiceId = tenantServiceId;
        this.tenantId = tenantId;
        this.serviceId = serviceId;
        this.status = status;
        this.subscribedAt = subscribedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 TenantService 생성 (ID 미할당, DB에서 Auto Increment)
     *
     * <p>Application Layer의 Factory에서 시간을 생성하여 전달합니다.
     *
     * @param tenantId 테넌트 ID (FK)
     * @param serviceId 서비스 ID (FK)
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 TenantService 인스턴스
     */
    public static TenantService create(TenantId tenantId, ServiceId serviceId, Instant now) {
        return new TenantService(
                null, tenantId, serviceId, TenantServiceStatus.ACTIVE, now, now, now);
    }

    /**
     * DB에서 TenantService 재구성 (reconstitute)
     *
     * @param tenantServiceId 테넌트-서비스 ID (Long PK)
     * @param tenantId 테넌트 ID (FK)
     * @param serviceId 서비스 ID (FK)
     * @param status 구독 상태
     * @param subscribedAt 구독 일시
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 TenantService 인스턴스
     */
    public static TenantService reconstitute(
            TenantServiceId tenantServiceId,
            TenantId tenantId,
            ServiceId serviceId,
            TenantServiceStatus status,
            Instant subscribedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new TenantService(
                tenantServiceId, tenantId, serviceId, status, subscribedAt, createdAt, updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 구독 활성화
     *
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void activate(Instant changedAt) {
        this.status = TenantServiceStatus.ACTIVE;
        this.updatedAt = changedAt;
    }

    /**
     * 구독 비활성화 (해지)
     *
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void deactivate(Instant changedAt) {
        this.status = TenantServiceStatus.INACTIVE;
        this.updatedAt = changedAt;
    }

    /**
     * 구독 일시 중지
     *
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void suspend(Instant changedAt) {
        this.status = TenantServiceStatus.SUSPENDED;
        this.updatedAt = changedAt;
    }

    /**
     * 구독 상태 변경
     *
     * @param targetStatus 목표 상태
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void changeStatus(TenantServiceStatus targetStatus, Instant changedAt) {
        switch (targetStatus) {
            case ACTIVE -> activate(changedAt);
            case INACTIVE -> deactivate(changedAt);
            case SUSPENDED -> suspend(changedAt);
        }
    }

    // ========== Query Methods ==========

    public Long tenantServiceIdValue() {
        return tenantServiceId != null ? tenantServiceId.value() : null;
    }

    public String tenantIdValue() {
        return tenantId.value();
    }

    public Long serviceIdValue() {
        return serviceId.value();
    }

    public String statusValue() {
        return status.name();
    }

    public boolean isActive() {
        return status.isActive();
    }

    public boolean isNew() {
        return tenantServiceId == null;
    }

    // ========== Getter Methods ==========

    public TenantServiceId getTenantServiceId() {
        return tenantServiceId;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public TenantServiceStatus getStatus() {
        return status;
    }

    public Instant subscribedAt() {
        return subscribedAt;
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
        TenantService that = (TenantService) o;
        if (tenantServiceId != null && that.tenantServiceId != null) {
            return Objects.equals(tenantServiceId, that.tenantServiceId);
        }
        return Objects.equals(tenantId, that.tenantId) && Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return tenantServiceId != null
                ? Objects.hash(tenantServiceId)
                : Objects.hash(tenantId, serviceId);
    }

    @Override
    public String toString() {
        return "TenantService{"
                + "tenantServiceId="
                + tenantServiceId
                + ", tenantId="
                + tenantId
                + ", serviceId="
                + serviceId
                + ", status="
                + status
                + "}";
    }
}
