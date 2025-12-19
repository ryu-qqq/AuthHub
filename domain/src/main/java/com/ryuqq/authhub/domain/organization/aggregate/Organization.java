package com.ryuqq.authhub.domain.organization.aggregate;

import com.ryuqq.authhub.domain.organization.exception.InvalidOrganizationStateException;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Organization Aggregate Root - 조직 도메인 모델
 *
 * <p>테넌트 하위의 조직 단위입니다. 하나의 Tenant는 여러 Organization을 가질 수 있습니다.
 *
 * <p><strong>상태 전이:</strong>
 *
 * <pre>
 * ACTIVE ↔ INACTIVE → DELETED
 * </pre>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>Law of Demeter 준수 - Getter 체이닝 금지
 *   <li>Tell, Don't Ask 패턴 - 상태 질의 대신 행위 위임
 *   <li>Long FK 전략 - JPA 관계 어노테이션 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Organization {

    private final OrganizationId organizationId;
    private final TenantId tenantId;
    private final OrganizationName name;
    private final OrganizationStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Organization(
            OrganizationId organizationId,
            TenantId tenantId,
            OrganizationName name,
            OrganizationStatus status,
            Instant createdAt,
            Instant updatedAt) {
        validateRequired(tenantId, name, status, createdAt, updatedAt);
        this.organizationId = organizationId;
        this.tenantId = tenantId;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private void validateRequired(
            TenantId tenantId,
            OrganizationName name,
            OrganizationStatus status,
            Instant createdAt,
            Instant updatedAt) {
        if (tenantId == null) {
            throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
        }
        if (name == null) {
            throw new IllegalArgumentException("OrganizationName은 null일 수 없습니다");
        }
        if (status == null) {
            throw new IllegalArgumentException("OrganizationStatus는 null일 수 없습니다");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt는 null일 수 없습니다");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("updatedAt는 null일 수 없습니다");
        }
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 Organization 생성 (ID 할당)
     *
     * <p>Application Layer의 Factory에서 UUIDv7을 생성하여 전달합니다.
     *
     * @param organizationId 조직 ID (UUIDv7)
     * @param tenantId 소속 테넌트 ID
     * @param name 조직 이름
     * @param clock 시간 제공자
     * @return 새로운 Organization 인스턴스
     * @throws IllegalArgumentException organizationId가 null인 경우
     */
    public static Organization create(
            OrganizationId organizationId, TenantId tenantId, OrganizationName name, Clock clock) {
        if (organizationId == null) {
            throw new IllegalArgumentException("OrganizationId는 null일 수 없습니다");
        }
        Instant now = clock.instant();
        return new Organization(
                organizationId, tenantId, name, OrganizationStatus.ACTIVE, now, now);
    }

    /**
     * DB에서 Organization 재구성 (reconstitute)
     *
     * @param organizationId 조직 ID
     * @param tenantId 테넌트 ID
     * @param name 조직 이름
     * @param status 조직 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 Organization 인스턴스
     */
    public static Organization reconstitute(
            OrganizationId organizationId,
            TenantId tenantId,
            OrganizationName name,
            OrganizationStatus status,
            Instant createdAt,
            Instant updatedAt) {
        if (organizationId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null organizationId");
        }
        return new Organization(organizationId, tenantId, name, status, createdAt, updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 조직 이름 변경
     *
     * @param newName 새로운 이름
     * @param clock 시간 제공자
     * @return 이름이 변경된 새로운 Organization 인스턴스
     * @throws InvalidOrganizationStateException 삭제된 조직인 경우
     */
    public Organization changeName(OrganizationName newName, Clock clock) {
        if (status == OrganizationStatus.DELETED) {
            throw new InvalidOrganizationStateException(status, "삭제된 조직의 이름을 변경할 수 없습니다");
        }
        return new Organization(
                this.organizationId,
                this.tenantId,
                newName,
                this.status,
                this.createdAt,
                clock.instant());
    }

    /**
     * 조직 활성화
     *
     * @param clock 시간 제공자
     * @return 활성화된 새로운 Organization 인스턴스
     * @throws InvalidOrganizationStateException 상태 전이가 불가능한 경우
     */
    public Organization activate(Clock clock) {
        if (!status.canTransitionTo(OrganizationStatus.ACTIVE)) {
            throw new InvalidOrganizationStateException(status, OrganizationStatus.ACTIVE);
        }
        return new Organization(
                this.organizationId,
                this.tenantId,
                this.name,
                OrganizationStatus.ACTIVE,
                this.createdAt,
                clock.instant());
    }

    /**
     * 조직 비활성화
     *
     * @param clock 시간 제공자
     * @return 비활성화된 새로운 Organization 인스턴스
     * @throws InvalidOrganizationStateException 상태 전이가 불가능한 경우
     */
    public Organization deactivate(Clock clock) {
        if (!status.canTransitionTo(OrganizationStatus.INACTIVE)) {
            throw new InvalidOrganizationStateException(status, OrganizationStatus.INACTIVE);
        }
        return new Organization(
                this.organizationId,
                this.tenantId,
                this.name,
                OrganizationStatus.INACTIVE,
                this.createdAt,
                clock.instant());
    }

    /**
     * 조직 삭제 (소프트 삭제)
     *
     * @param clock 시간 제공자
     * @return 삭제된 새로운 Organization 인스턴스
     * @throws InvalidOrganizationStateException 상태 전이가 불가능한 경우
     */
    public Organization delete(Clock clock) {
        if (!status.canTransitionTo(OrganizationStatus.DELETED)) {
            throw new InvalidOrganizationStateException(status, OrganizationStatus.DELETED);
        }
        return new Organization(
                this.organizationId,
                this.tenantId,
                this.name,
                OrganizationStatus.DELETED,
                this.createdAt,
                clock.instant());
    }

    // ========== Helper Methods ==========

    public UUID organizationIdValue() {
        return organizationId != null ? organizationId.value() : null;
    }

    public UUID tenantIdValue() {
        return tenantId.value();
    }

    public String nameValue() {
        return name.value();
    }

    public String statusValue() {
        return status.name();
    }

    public boolean isNew() {
        return organizationId == null;
    }

    public boolean isActive() {
        return status == OrganizationStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return status == OrganizationStatus.DELETED;
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
        if (organizationId == null || that.organizationId == null) {
            return false;
        }
        return Objects.equals(organizationId, that.organizationId);
    }

    @Override
    public int hashCode() {
        return organizationId != null
                ? Objects.hash(organizationId)
                : System.identityHashCode(this);
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
                + "}";
    }
}
