package com.ryuqq.authhub.domain.tenant.aggregate;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.common.model.AggregateRoot;
import com.ryuqq.authhub.domain.tenant.TenantStatus;
import com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException;
import com.ryuqq.authhub.domain.tenant.vo.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;

import java.time.Instant;
import java.util.Objects;

/**
 * Tenant Aggregate Root - 멀티 테넌시 도메인 객체
 *
 * <p>멀티 테넌트 시스템의 테넌트를 나타내는 Aggregate Root입니다.
 *
 * <p><strong>팩토리 메서드:</strong>
 * <ul>
 *   <li>{@code forNew()} - 새 Tenant 생성 (ID null, ACTIVE 상태)</li>
 *   <li>{@code of()} - 기존 Tenant 로드 (모든 필드 지정)</li>
 *   <li>{@code reconstitute()} - DB에서 Tenant 재구성</li>
 * </ul>
 *
 * <p><strong>비즈니스 규칙:</strong>
 * <ul>
 *   <li>DELETED 상태에서는 activate/deactivate 불가</li>
 *   <li>이미 DELETED 상태이면 delete 재시도 불가</li>
 *   <li>상태 변경 시 updatedAt 자동 갱신</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class Tenant implements AggregateRoot {

    private final TenantId tenantId;
    private final TenantName tenantName;
    private final TenantStatus tenantStatus;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Tenant(
            TenantId tenantId,
            TenantName tenantName,
            TenantStatus tenantStatus,
            Instant createdAt,
            Instant updatedAt
    ) {
        validateTenantName(tenantName);
        validateTenantStatus(tenantStatus);
        validateTimestamps(createdAt, updatedAt);

        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.tenantStatus = tenantStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * forNew - 새 Tenant 생성 (도메인 유스케이스)
     *
     * <p>ID는 null이며, ACTIVE 상태로 생성됩니다.
     * <p>생성 시간과 수정 시간이 동일하게 설정됩니다.
     *
     * @param tenantName Tenant 이름 (필수)
     * @param clock 시간 제공자
     * @return 새로 생성된 Tenant
     * @author development-team
     * @since 1.0.0
     */
    public static Tenant forNew(TenantName tenantName, Clock clock) {
        Instant now = clock.now();
        return new Tenant(
                null,
                tenantName,
                TenantStatus.ACTIVE,
                now,
                now
        );
    }

    /**
     * of - 기존 Tenant 로드 (애플리케이션 유스케이스)
     *
     * <p>모든 필드를 명시적으로 지정하여 Tenant를 생성합니다.
     *
     * @param tenantId Tenant ID (nullable - forNew의 경우)
     * @param tenantName Tenant 이름 (필수)
     * @param tenantStatus 테넌트 상태 (필수)
     * @param createdAt 생성 시간 (필수)
     * @param updatedAt 수정 시간 (필수)
     * @return 로드된 Tenant
     * @author development-team
     * @since 1.0.0
     */
    public static Tenant of(
            TenantId tenantId,
            TenantName tenantName,
            TenantStatus tenantStatus,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Tenant(
                tenantId,
                tenantName,
                tenantStatus,
                createdAt,
                updatedAt
        );
    }

    /**
     * reconstitute - DB에서 Tenant 재구성 (Persistence Adapter 전용)
     *
     * <p>DB에서 조회한 데이터로 Tenant를 재구성합니다.
     * <p>ID는 필수입니다.
     *
     * @param tenantId Tenant ID (필수)
     * @param tenantName Tenant 이름 (필수)
     * @param tenantStatus 테넌트 상태 (필수)
     * @param createdAt 생성 시간 (필수)
     * @param updatedAt 수정 시간 (필수)
     * @return 재구성된 Tenant
     * @author development-team
     * @since 1.0.0
     */
    public static Tenant reconstitute(
            TenantId tenantId,
            TenantName tenantName,
            TenantStatus tenantStatus,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (tenantId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null tenantId");
        }
        return new Tenant(
                tenantId,
                tenantName,
                tenantStatus,
                createdAt,
                updatedAt
        );
    }

    private void validateTenantName(TenantName tenantName) {
        if (tenantName == null) {
            throw new IllegalArgumentException("TenantName은 null일 수 없습니다");
        }
    }

    private void validateTenantStatus(TenantStatus tenantStatus) {
        if (tenantStatus == null) {
            throw new IllegalArgumentException("TenantStatus는 null일 수 없습니다");
        }
    }

    private void validateTimestamps(Instant createdAt, Instant updatedAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt는 null일 수 없습니다");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("updatedAt는 null일 수 없습니다");
        }
    }

    // ========== Business Methods ==========

    /**
     * activate - Tenant를 활성화 (INACTIVE → ACTIVE)
     *
     * <p>DELETED 상태에서는 activate 불가합니다.
     *
     * @param clock 시간 제공자
     * @return 활성화된 새 Tenant 인스턴스
     * @throws InvalidTenantStateException DELETED 상태에서 activate 시도 시
     * @author development-team
     * @since 1.0.0
     */
    public Tenant activate(Clock clock) {
        if (this.tenantStatus == TenantStatus.DELETED) {
            throw new InvalidTenantStateException(
                    tenantIdValue(),
                    "Cannot activate deleted tenant"
            );
        }
        return new Tenant(
                this.tenantId,
                this.tenantName,
                TenantStatus.ACTIVE,
                this.createdAt,
                clock.now()
        );
    }

    /**
     * deactivate - Tenant를 비활성화 (ACTIVE → INACTIVE)
     *
     * <p>DELETED 상태에서는 deactivate 불가합니다.
     *
     * @param clock 시간 제공자
     * @return 비활성화된 새 Tenant 인스턴스
     * @throws InvalidTenantStateException DELETED 상태에서 deactivate 시도 시
     * @author development-team
     * @since 1.0.0
     */
    public Tenant deactivate(Clock clock) {
        if (this.tenantStatus == TenantStatus.DELETED) {
            throw new InvalidTenantStateException(
                    tenantIdValue(),
                    "Cannot deactivate deleted tenant"
            );
        }
        return new Tenant(
                this.tenantId,
                this.tenantName,
                TenantStatus.INACTIVE,
                this.createdAt,
                clock.now()
        );
    }

    /**
     * delete - Tenant를 삭제 (ACTIVE/INACTIVE → DELETED)
     *
     * <p>이미 DELETED 상태이면 예외가 발생합니다.
     *
     * @param clock 시간 제공자
     * @return 삭제된 새 Tenant 인스턴스
     * @throws InvalidTenantStateException 이미 DELETED 상태일 경우
     * @author development-team
     * @since 1.0.0
     */
    public Tenant delete(Clock clock) {
        if (this.tenantStatus == TenantStatus.DELETED) {
            throw new InvalidTenantStateException(
                    tenantIdValue(),
                    "Tenant is already deleted"
            );
        }
        return new Tenant(
                this.tenantId,
                this.tenantName,
                TenantStatus.DELETED,
                this.createdAt,
                clock.now()
        );
    }

    // ========== Law of Demeter 준수: Primitive 값 접근 헬퍼 메서드 ==========

    /**
     * tenantIdValue - Tenant ID의 Long 값 반환
     *
     * <p>Getter 체이닝 방지 (tenant.getTenantId().value() ❌)
     *
     * @return Tenant ID Long 값 (nullable - forNew의 경우)
     * @author development-team
     * @since 1.0.0
     */
    public Long tenantIdValue() {
        return (tenantId == null) ? null : tenantId.value();
    }

    /**
     * tenantNameValue - Tenant Name의 String 값 반환
     *
     * @return Tenant Name String 값
     * @author development-team
     * @since 1.0.0
     */
    public String tenantNameValue() {
        return tenantName.value();
    }

    /**
     * statusValue - TenantStatus의 name() 값 반환
     *
     * @return TenantStatus name (예: "ACTIVE", "INACTIVE", "DELETED")
     * @author development-team
     * @since 1.0.0
     */
    public String statusValue() {
        return tenantStatus.name();
    }

    /**
     * isNew - Tenant가 새로 생성된 객체인지 확인
     *
     * @return tenantId가 null이면 true, 아니면 false
     * @author development-team
     * @since 1.0.0
     */
    public boolean isNew() {
        return tenantId == null;
    }

    /**
     * createdAt - 생성 시간 반환
     *
     * @return 생성 시간 (Instant)
     * @author development-team
     * @since 1.0.0
     */
    public Instant createdAt() {
        return createdAt;
    }

    /**
     * updatedAt - 수정 시간 반환
     *
     * @return 수정 시간 (Instant)
     * @author development-team
     * @since 1.0.0
     */
    public Instant updatedAt() {
        return updatedAt;
    }

    /**
     * isActive - Tenant가 활성 상태인지 확인
     *
     * @return 상태가 ACTIVE이면 true
     * @author development-team
     * @since 1.0.0
     */
    public boolean isActive() {
        return tenantStatus == TenantStatus.ACTIVE;
    }

    /**
     * isDeleted - Tenant가 삭제 상태인지 확인
     *
     * @return 상태가 DELETED이면 true
     * @author development-team
     * @since 1.0.0
     */
    public boolean isDeleted() {
        return tenantStatus == TenantStatus.DELETED;
    }

    // ========== Legacy Getters (호환성 유지) ==========

    public TenantId getTenantId() {
        return tenantId;
    }

    public TenantName getTenantName() {
        return tenantName;
    }

    public TenantStatus getTenantStatus() {
        return tenantStatus;
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
        return Objects.equals(tenantId, tenant.tenantId) &&
                Objects.equals(tenantName, tenant.tenantName) &&
                tenantStatus == tenant.tenantStatus &&
                Objects.equals(createdAt, tenant.createdAt) &&
                Objects.equals(updatedAt, tenant.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, tenantName, tenantStatus, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "tenantId=" + tenantId +
                ", tenantName=" + tenantName +
                ", tenantStatus=" + tenantStatus +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
