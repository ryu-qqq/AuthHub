package com.ryuqq.authhub.domain.organization.aggregate;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.Instant;
import java.util.Objects;

/**
 * Organization Aggregate Root - 조직 도메인 모델
 *
 * <p>테넌트 하위의 조직 단위입니다. 하나의 Tenant는 여러 Organization을 가질 수 있습니다.
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
public final class Organization {

    private final OrganizationId organizationId;
    private final TenantId tenantId;
    private OrganizationName name;
    private OrganizationStatus status;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private Organization(
            OrganizationId organizationId,
            TenantId tenantId,
            OrganizationName name,
            OrganizationStatus status,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.organizationId = organizationId;
        this.tenantId = tenantId;
        this.name = name;
        this.status = status;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 Organization 생성 (ID 할당)
     *
     * <p>Application Layer의 Factory에서 UUIDv7과 시간을 생성하여 전달합니다.
     *
     * @param organizationId 조직 ID (UUIDv7)
     * @param tenantId 소속 테넌트 ID
     * @param name 조직 이름
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 Organization 인스턴스
     */
    public static Organization create(
            OrganizationId organizationId, TenantId tenantId, OrganizationName name, Instant now) {
        return new Organization(
                organizationId,
                tenantId,
                name,
                OrganizationStatus.ACTIVE,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * DB에서 Organization 재구성 (reconstitute)
     *
     * @param organizationId 조직 ID
     * @param tenantId 테넌트 ID
     * @param name 조직 이름
     * @param status 조직 상태
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 Organization 인스턴스
     */
    public static Organization reconstitute(
            OrganizationId organizationId,
            TenantId tenantId,
            OrganizationName name,
            OrganizationStatus status,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new Organization(
                organizationId, tenantId, name, status, deletionStatus, createdAt, updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 조직 이름 변경
     *
     * @param newName 새로운 이름
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void changeName(OrganizationName newName, Instant changedAt) {
        this.name = newName;
        this.updatedAt = changedAt;
    }

    /**
     * 조직 상태 변경
     *
     * @param targetStatus 목표 상태
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void changeStatus(OrganizationStatus targetStatus, Instant changedAt) {
        switch (targetStatus) {
            case ACTIVE -> activate(changedAt);
            case INACTIVE -> deactivate(changedAt);
        }
    }

    /**
     * 조직 활성화
     *
     * @param changedAt 변경 시간 (외부 주입)
     */
    private void activate(Instant changedAt) {
        this.status = OrganizationStatus.ACTIVE;
        this.updatedAt = changedAt;
    }

    /**
     * 조직 비활성화
     *
     * @param changedAt 변경 시간 (외부 주입)
     */
    private void deactivate(Instant changedAt) {
        this.status = OrganizationStatus.INACTIVE;
        this.updatedAt = changedAt;
    }

    /**
     * 조직 삭제 (소프트 삭제)
     *
     * @param now 삭제 시간 (외부 주입)
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 조직 복원
     *
     * @param now 복원 시간 (외부 주입)
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    // ========== Query Methods ==========

    public String organizationIdValue() {
        return organizationId.value();
    }

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

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public OrganizationName getName() {
        return name;
    }

    public OrganizationStatus getStatus() {
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
        Organization that = (Organization) o;
        return Objects.equals(organizationId, that.organizationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizationId);
    }

    @Override
    public String toString() {
        return "Organization{"
                + "organizationId="
                + organizationId
                + ", tenantId="
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
