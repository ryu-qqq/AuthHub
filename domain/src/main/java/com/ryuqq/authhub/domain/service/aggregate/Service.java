package com.ryuqq.authhub.domain.service.aggregate;

import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import com.ryuqq.authhub.domain.service.vo.ServiceDescription;
import com.ryuqq.authhub.domain.service.vo.ServiceName;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
import java.time.Instant;
import java.util.Objects;

/**
 * Service Aggregate Root - 서비스 도메인 모델
 *
 * <p>멀티테넌시 시스템에서 각 서비스(자사몰, B2B, AuthHub 등)를 정의합니다. Permission과 Role은 Service에 종속되어 서비스별 권한 분리를
 * 가능하게 합니다.
 *
 * <p><strong>PK 전략:</strong> Long Auto Increment (serviceId) + 비즈니스 코드 (serviceCode UNIQUE)
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
public final class Service {

    private final ServiceId serviceId;
    private final ServiceCode serviceCode;
    private ServiceName name;
    private ServiceDescription description;
    private ServiceStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Service(
            ServiceId serviceId,
            ServiceCode serviceCode,
            ServiceName name,
            ServiceDescription description,
            ServiceStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.serviceId = serviceId;
        this.serviceCode = serviceCode;
        this.name = name;
        this.description = description != null ? description : ServiceDescription.empty();
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 Service 생성 (ID 미할당, DB에서 Auto Increment)
     *
     * <p>Application Layer의 Factory에서 시간을 생성하여 전달합니다.
     *
     * @param serviceCode 서비스 코드 (예: SVC_STORE, SVC_B2B)
     * @param name 서비스 이름
     * @param description 서비스 설명 (nullable)
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 Service 인스턴스
     */
    public static Service create(
            ServiceCode serviceCode,
            ServiceName name,
            ServiceDescription description,
            Instant now) {
        return new Service(null, serviceCode, name, description, ServiceStatus.ACTIVE, now, now);
    }

    /**
     * DB에서 Service 재구성 (reconstitute)
     *
     * @param serviceId 서비스 ID (Long PK)
     * @param serviceCode 서비스 코드 (비즈니스 식별자)
     * @param name 서비스 이름
     * @param description 서비스 설명
     * @param status 서비스 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 Service 인스턴스
     */
    public static Service reconstitute(
            ServiceId serviceId,
            ServiceCode serviceCode,
            ServiceName name,
            ServiceDescription description,
            ServiceStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new Service(serviceId, serviceCode, name, description, status, createdAt, updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 서비스 수정 (UpdateData 기반)
     *
     * <p>ServiceUpdateData의 hasXxx() 메서드로 변경 여부를 확인하여 선택적으로 수정합니다.
     *
     * @param updateData 수정 데이터
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void update(ServiceUpdateData updateData, Instant changedAt) {
        if (updateData.hasName()) {
            this.name = ServiceName.of(updateData.name());
        }
        if (updateData.hasDescription()) {
            this.description = ServiceDescription.of(updateData.description());
        }
        if (updateData.hasStatus()) {
            this.status = ServiceStatus.valueOf(updateData.status());
        }
        this.updatedAt = changedAt;
    }

    /**
     * 서비스 이름 변경
     *
     * @param newName 새로운 이름
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void changeName(ServiceName newName, Instant changedAt) {
        this.name = newName;
        this.updatedAt = changedAt;
    }

    /**
     * 서비스 설명 변경
     *
     * @param newDescription 새로운 설명
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void changeDescription(ServiceDescription newDescription, Instant changedAt) {
        this.description = newDescription != null ? newDescription : ServiceDescription.empty();
        this.updatedAt = changedAt;
    }

    /**
     * 서비스 상태 변경
     *
     * @param targetStatus 목표 상태
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void changeStatus(ServiceStatus targetStatus, Instant changedAt) {
        switch (targetStatus) {
            case ACTIVE -> activate(changedAt);
            case INACTIVE -> deactivate(changedAt);
        }
    }

    private void activate(Instant changedAt) {
        this.status = ServiceStatus.ACTIVE;
        this.updatedAt = changedAt;
    }

    private void deactivate(Instant changedAt) {
        this.status = ServiceStatus.INACTIVE;
        this.updatedAt = changedAt;
    }

    // ========== Query Methods ==========

    public Long serviceIdValue() {
        return serviceId != null ? serviceId.value() : null;
    }

    public String serviceCodeValue() {
        return serviceCode.value();
    }

    public String nameValue() {
        return name.value();
    }

    public String descriptionValue() {
        return description.value();
    }

    public String statusValue() {
        return status.name();
    }

    public boolean isActive() {
        return status.isActive();
    }

    public boolean isNew() {
        return serviceId == null;
    }

    // ========== Getter Methods ==========

    public ServiceId getServiceId() {
        return serviceId;
    }

    public ServiceCode getServiceCode() {
        return serviceCode;
    }

    public ServiceName getName() {
        return name;
    }

    public ServiceDescription getDescription() {
        return description;
    }

    public ServiceStatus getStatus() {
        return status;
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
        Service service = (Service) o;
        if (serviceId != null && service.serviceId != null) {
            return Objects.equals(serviceId, service.serviceId);
        }
        return Objects.equals(serviceCode, service.serviceCode);
    }

    @Override
    public int hashCode() {
        return serviceId != null ? Objects.hash(serviceId) : Objects.hash(serviceCode);
    }

    @Override
    public String toString() {
        return "Service{"
                + "serviceId="
                + serviceId
                + ", serviceCode="
                + serviceCode
                + ", name="
                + name
                + ", status="
                + status
                + "}";
    }
}
