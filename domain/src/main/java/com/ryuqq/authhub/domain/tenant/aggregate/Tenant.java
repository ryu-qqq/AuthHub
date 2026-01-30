package com.ryuqq.authhub.domain.tenant.aggregate;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Instant;
import java.util.Objects;

/**
 * Tenant Aggregate Root - 테넌트 도메인 모델
 *
 * <p>멀티테넌시 시스템의 최상위 도메인입니다.
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
public final class Tenant {

    private final TenantId tenantId;
    private TenantName name;
    private TenantStatus status;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private Tenant(
            TenantId tenantId,
            TenantName name,
            TenantStatus status,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.tenantId = tenantId;
        this.name = name;
        this.status = status;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 Tenant 생성 (ID 할당)
     *
     * <p>Application Layer의 Factory에서 UUIDv7과 시간을 생성하여 전달합니다.
     *
     * @param tenantId 테넌트 ID (UUIDv7)
     * @param name 테넌트 이름
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 Tenant 인스턴스
     */
    public static Tenant create(TenantId tenantId, TenantName name, Instant now) {
        return new Tenant(tenantId, name, TenantStatus.ACTIVE, DeletionStatus.active(), now, now);
    }

    /**
     * DB에서 Tenant 재구성 (reconstitute)
     *
     * @param tenantId 테넌트 ID
     * @param name 테넌트 이름
     * @param status 테넌트 상태
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 Tenant 인스턴스
     */
    public static Tenant reconstitute(
            TenantId tenantId,
            TenantName name,
            TenantStatus status,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new Tenant(tenantId, name, status, deletionStatus, createdAt, updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 테넌트 이름 변경
     *
     * @param newName 새로운 이름
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void changeName(TenantName newName, Instant changedAt) {
        this.name = newName;
        this.updatedAt = changedAt;
    }

    /**
     * 테넌트 상태 변경
     *
     * @param targetStatus 목표 상태
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void changeStatus(TenantStatus targetStatus, Instant changedAt) {
        switch (targetStatus) {
            case ACTIVE -> activate(changedAt);
            case INACTIVE -> deactivate(changedAt);
        }
    }

    /**
     * 테넌트 활성화
     *
     * @param changedAt 변경 시간 (외부 주입)
     */
    private void activate(Instant changedAt) {
        this.status = TenantStatus.ACTIVE;
        this.updatedAt = changedAt;
    }

    /**
     * 테넌트 비활성화
     *
     * @param changedAt 변경 시간 (외부 주입)
     */
    private void deactivate(Instant changedAt) {
        this.status = TenantStatus.INACTIVE;
        this.updatedAt = changedAt;
    }

    /**
     * 테넌트 삭제 (소프트 삭제)
     *
     * @param now 삭제 시간 (외부 주입)
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 테넌트 복원
     *
     * @param now 복원 시간 (외부 주입)
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    // ========== Query Methods ==========

    public String tenantIdValue() {
        return tenantId.value();
    }

    public String nameValue() {
        return name.value();
    }

    public String statusValue() {
        return status.name();
    }

    public boolean isActive() {
        return status.isActive();
    }

    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    // ========== Getter Methods ==========

    public TenantId getTenantId() {
        return tenantId;
    }

    public TenantName getName() {
        return name;
    }

    public TenantStatus getStatus() {
        return status;
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
        Tenant tenant = (Tenant) o;
        return Objects.equals(tenantId, tenant.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId);
    }

    @Override
    public String toString() {
        return "Tenant{"
                + "tenantId="
                + tenantId
                + ", name="
                + name
                + ", status="
                + status
                + ", deleted="
                + deletionStatus.isDeleted()
                + "}";
    }
}
