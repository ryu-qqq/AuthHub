package com.ryuqq.authhub.domain.tenant.aggregate;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Tenant Aggregate Root - 테넌트 도메인 모델
 *
 * <p>멀티테넌시 시스템의 최상위 도메인입니다.
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
public final class Tenant {

    private final TenantId tenantId;
    private final TenantName name;
    private final TenantStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Tenant(
            TenantId tenantId,
            TenantName name,
            TenantStatus status,
            Instant createdAt,
            Instant updatedAt) {
        validateRequired(name, status, createdAt, updatedAt);
        this.tenantId = tenantId;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private void validateRequired(
            TenantName name, TenantStatus status, Instant createdAt, Instant updatedAt) {
        if (name == null) {
            throw new IllegalArgumentException("TenantName은 null일 수 없습니다");
        }
        if (status == null) {
            throw new IllegalArgumentException("TenantStatus는 null일 수 없습니다");
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
     * 새로운 Tenant 생성 (ID 미할당)
     *
     * @param name 테넌트 이름
     * @param clock 시간 제공자
     * @return 새로운 Tenant 인스턴스
     */
    public static Tenant create(TenantName name, Clock clock) {
        Instant now = clock.now();
        return new Tenant(null, name, TenantStatus.ACTIVE, now, now);
    }

    /**
     * DB에서 Tenant 재구성 (reconstitute)
     *
     * @param tenantId 테넌트 ID
     * @param name 테넌트 이름
     * @param status 테넌트 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 Tenant 인스턴스
     */
    public static Tenant reconstitute(
            TenantId tenantId,
            TenantName name,
            TenantStatus status,
            Instant createdAt,
            Instant updatedAt) {
        if (tenantId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null tenantId");
        }
        return new Tenant(tenantId, name, status, createdAt, updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 테넌트 이름 변경
     *
     * @param newName 새로운 이름
     * @param clock 시간 제공자
     * @return 이름이 변경된 새로운 Tenant 인스턴스
     * @throws InvalidTenantStateException 삭제된 테넌트인 경우
     */
    public Tenant changeName(TenantName newName, Clock clock) {
        if (status == TenantStatus.DELETED) {
            throw new InvalidTenantStateException(status, "삭제된 테넌트의 이름을 변경할 수 없습니다");
        }
        return new Tenant(this.tenantId, newName, this.status, this.createdAt, clock.now());
    }

    /**
     * 테넌트 활성화
     *
     * @param clock 시간 제공자
     * @return 활성화된 새로운 Tenant 인스턴스
     * @throws InvalidTenantStateException 상태 전이가 불가능한 경우
     */
    public Tenant activate(Clock clock) {
        if (!status.canTransitionTo(TenantStatus.ACTIVE)) {
            throw new InvalidTenantStateException(status, TenantStatus.ACTIVE);
        }
        return new Tenant(
                this.tenantId, this.name, TenantStatus.ACTIVE, this.createdAt, clock.now());
    }

    /**
     * 테넌트 비활성화
     *
     * @param clock 시간 제공자
     * @return 비활성화된 새로운 Tenant 인스턴스
     * @throws InvalidTenantStateException 상태 전이가 불가능한 경우
     */
    public Tenant deactivate(Clock clock) {
        if (!status.canTransitionTo(TenantStatus.INACTIVE)) {
            throw new InvalidTenantStateException(status, TenantStatus.INACTIVE);
        }
        return new Tenant(
                this.tenantId, this.name, TenantStatus.INACTIVE, this.createdAt, clock.now());
    }

    /**
     * 테넌트 삭제 (소프트 삭제)
     *
     * @param clock 시간 제공자
     * @return 삭제된 새로운 Tenant 인스턴스
     * @throws InvalidTenantStateException 상태 전이가 불가능한 경우
     */
    public Tenant delete(Clock clock) {
        if (!status.canTransitionTo(TenantStatus.DELETED)) {
            throw new InvalidTenantStateException(status, TenantStatus.DELETED);
        }
        return new Tenant(
                this.tenantId, this.name, TenantStatus.DELETED, this.createdAt, clock.now());
    }

    // ========== Helper Methods ==========

    public UUID tenantIdValue() {
        return tenantId != null ? tenantId.value() : null;
    }

    public String nameValue() {
        return name.value();
    }

    public String statusValue() {
        return status.name();
    }

    public boolean isNew() {
        return tenantId == null;
    }

    public boolean isActive() {
        return status == TenantStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return status == TenantStatus.DELETED;
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
        if (tenantId == null || tenant.tenantId == null) {
            return false;
        }
        return Objects.equals(tenantId, tenant.tenantId);
    }

    @Override
    public int hashCode() {
        return tenantId != null ? Objects.hash(tenantId) : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "Tenant{" + "tenantId=" + tenantId + ", name=" + name + ", status=" + status + "}";
    }
}
