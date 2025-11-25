package com.ryuqq.authhub.domain.organization.aggregate;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.exception.InvalidOrganizationStateException;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Instant;
import java.util.Objects;

/**
 * Organization Aggregate Root - 조직 도메인 객체
 *
 * <p>Tenant에 속하는 조직을 나타내는 Aggregate Root입니다.
 *
 * <p><strong>팩토리 메서드:</strong>
 *
 * <ul>
 *   <li>{@code forNew()} - 새 Organization 생성 (ID null, ACTIVE 상태)
 *   <li>{@code of()} - 기존 Organization 로드 (모든 필드 지정)
 *   <li>{@code reconstitute()} - DB에서 Organization 재구성
 * </ul>
 *
 * <p><strong>비즈니스 규칙:</strong>
 *
 * <ul>
 *   <li>DELETED 상태에서는 activate/deactivate 불가
 *   <li>이미 DELETED 상태이면 delete 재시도 불가
 *   <li>상태 변경 시 updatedAt 자동 갱신
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class Organization {

    private final OrganizationId organizationId;
    private final OrganizationName organizationName;
    private final TenantId tenantId;
    private final OrganizationStatus organizationStatus;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Organization(
            OrganizationId organizationId,
            OrganizationName organizationName,
            TenantId tenantId,
            OrganizationStatus organizationStatus,
            Instant createdAt,
            Instant updatedAt) {
        validateOrganizationName(organizationName);
        validateTenantId(tenantId);
        validateOrganizationStatus(organizationStatus);
        validateTimestamps(createdAt, updatedAt);

        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.tenantId = tenantId;
        this.organizationStatus = organizationStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * forNew - 새 Organization 생성 (도메인 유스케이스)
     *
     * <p>ID는 null이며, ACTIVE 상태로 생성됩니다.
     *
     * <p>생성 시간과 수정 시간이 동일하게 설정됩니다.
     *
     * @param organizationName Organization 이름 (필수)
     * @param tenantId 소속 Tenant ID (필수)
     * @param clock 시간 제공자
     * @return 새로 생성된 Organization
     * @author development-team
     * @since 1.0.0
     */
    public static Organization forNew(
            OrganizationName organizationName, TenantId tenantId, Clock clock) {
        Instant now = clock.now();
        return new Organization(
                null, organizationName, tenantId, OrganizationStatus.ACTIVE, now, now);
    }

    /**
     * of - 기존 Organization 로드 (애플리케이션 유스케이스)
     *
     * <p>모든 필드를 명시적으로 지정하여 Organization을 생성합니다.
     *
     * @param organizationId Organization ID (nullable - forNew의 경우)
     * @param organizationName Organization 이름 (필수)
     * @param tenantId 소속 Tenant ID (필수)
     * @param organizationStatus 조직 상태 (필수)
     * @param createdAt 생성 시간 (필수)
     * @param updatedAt 수정 시간 (필수)
     * @return 로드된 Organization
     * @author development-team
     * @since 1.0.0
     */
    public static Organization of(
            OrganizationId organizationId,
            OrganizationName organizationName,
            TenantId tenantId,
            OrganizationStatus organizationStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new Organization(
                organizationId,
                organizationName,
                tenantId,
                organizationStatus,
                createdAt,
                updatedAt);
    }

    /**
     * reconstitute - DB에서 Organization 재구성 (Persistence Adapter 전용)
     *
     * <p>DB에서 조회한 데이터로 Organization을 재구성합니다.
     *
     * <p>ID는 필수입니다.
     *
     * @param organizationId Organization ID (필수)
     * @param organizationName Organization 이름 (필수)
     * @param tenantId 소속 Tenant ID (필수)
     * @param organizationStatus 조직 상태 (필수)
     * @param createdAt 생성 시간 (필수)
     * @param updatedAt 수정 시간 (필수)
     * @return 재구성된 Organization
     * @author development-team
     * @since 1.0.0
     */
    public static Organization reconstitute(
            OrganizationId organizationId,
            OrganizationName organizationName,
            TenantId tenantId,
            OrganizationStatus organizationStatus,
            Instant createdAt,
            Instant updatedAt) {
        if (organizationId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null organizationId");
        }
        return new Organization(
                organizationId,
                organizationName,
                tenantId,
                organizationStatus,
                createdAt,
                updatedAt);
    }

    private void validateOrganizationName(OrganizationName organizationName) {
        if (organizationName == null) {
            throw new IllegalArgumentException("OrganizationName은 null일 수 없습니다");
        }
    }

    private void validateTenantId(TenantId tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
        }
    }

    private void validateOrganizationStatus(OrganizationStatus organizationStatus) {
        if (organizationStatus == null) {
            throw new IllegalArgumentException("OrganizationStatus는 null일 수 없습니다");
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
     * activate - Organization을 활성화 (INACTIVE → ACTIVE)
     *
     * <p>DELETED 상태에서는 activate 불가합니다.
     *
     * @param clock 시간 제공자
     * @return 활성화된 새 Organization 인스턴스
     * @throws InvalidOrganizationStateException DELETED 상태에서 activate 시도 시
     * @author development-team
     * @since 1.0.0
     */
    public Organization activate(Clock clock) {
        if (this.organizationStatus == OrganizationStatus.DELETED) {
            throw new InvalidOrganizationStateException(
                    organizationIdValue(), "Cannot activate deleted organization");
        }
        return new Organization(
                this.organizationId,
                this.organizationName,
                this.tenantId,
                OrganizationStatus.ACTIVE,
                this.createdAt,
                clock.now());
    }

    /**
     * deactivate - Organization을 비활성화 (ACTIVE → INACTIVE)
     *
     * <p>DELETED 상태에서는 deactivate 불가합니다.
     *
     * @param clock 시간 제공자
     * @return 비활성화된 새 Organization 인스턴스
     * @throws InvalidOrganizationStateException DELETED 상태에서 deactivate 시도 시
     * @author development-team
     * @since 1.0.0
     */
    public Organization deactivate(Clock clock) {
        if (this.organizationStatus == OrganizationStatus.DELETED) {
            throw new InvalidOrganizationStateException(
                    organizationIdValue(), "Cannot deactivate deleted organization");
        }
        return new Organization(
                this.organizationId,
                this.organizationName,
                this.tenantId,
                OrganizationStatus.INACTIVE,
                this.createdAt,
                clock.now());
    }

    /**
     * delete - Organization을 삭제 (ACTIVE/INACTIVE → DELETED)
     *
     * <p>이미 DELETED 상태이면 예외가 발생합니다.
     *
     * @param clock 시간 제공자
     * @return 삭제된 새 Organization 인스턴스
     * @throws InvalidOrganizationStateException 이미 DELETED 상태일 경우
     * @author development-team
     * @since 1.0.0
     */
    public Organization delete(Clock clock) {
        if (this.organizationStatus == OrganizationStatus.DELETED) {
            throw new InvalidOrganizationStateException(
                    organizationIdValue(), "Organization is already deleted");
        }
        return new Organization(
                this.organizationId,
                this.organizationName,
                this.tenantId,
                OrganizationStatus.DELETED,
                this.createdAt,
                clock.now());
    }

    // ========== Law of Demeter 준수: Primitive 값 접근 헬퍼 메서드 ==========

    /**
     * organizationIdValue - Organization ID의 Long 값 반환
     *
     * <p>Getter 체이닝 방지 (organization.getOrganizationId().value() ❌)
     *
     * @return Organization ID Long 값 (nullable - forNew의 경우)
     * @author development-team
     * @since 1.0.0
     */
    public Long organizationIdValue() {
        return (organizationId == null) ? null : organizationId.value();
    }

    /**
     * organizationNameValue - Organization Name의 String 값 반환
     *
     * @return Organization Name String 값
     * @author development-team
     * @since 1.0.0
     */
    public String organizationNameValue() {
        return organizationName.value();
    }

    /**
     * tenantIdValue - Tenant ID의 Long 값 반환
     *
     * <p>Getter 체이닝 방지 (organization.getTenantId().value() ❌)
     *
     * @return Tenant ID Long 값
     * @author development-team
     * @since 1.0.0
     */
    public Long tenantIdValue() {
        return tenantId.value();
    }

    /**
     * statusValue - OrganizationStatus의 name() 값 반환
     *
     * @return OrganizationStatus name (예: "ACTIVE", "INACTIVE", "DELETED")
     * @author development-team
     * @since 1.0.0
     */
    public String statusValue() {
        return organizationStatus.name();
    }

    /**
     * isNew - Organization이 새로 생성된 객체인지 확인
     *
     * @return organizationId가 null이면 true, 아니면 false
     * @author development-team
     * @since 1.0.0
     */
    public boolean isNew() {
        return organizationId == null;
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
     * isActive - Organization이 활성 상태인지 확인
     *
     * @return 상태가 ACTIVE이면 true
     * @author development-team
     * @since 1.0.0
     */
    public boolean isActive() {
        return organizationStatus == OrganizationStatus.ACTIVE;
    }

    /**
     * isDeleted - Organization이 삭제 상태인지 확인
     *
     * @return 상태가 DELETED이면 true
     * @author development-team
     * @since 1.0.0
     */
    public boolean isDeleted() {
        return organizationStatus == OrganizationStatus.DELETED;
    }

    // ========== Legacy Getters (호환성 유지) ==========

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public OrganizationName getOrganizationName() {
        return organizationName;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public OrganizationStatus getOrganizationStatus() {
        return organizationStatus;
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
        return Objects.equals(organizationId, that.organizationId)
                && Objects.equals(organizationName, that.organizationName)
                && Objects.equals(tenantId, that.tenantId)
                && organizationStatus == that.organizationStatus
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                organizationId,
                organizationName,
                tenantId,
                organizationStatus,
                createdAt,
                updatedAt);
    }

    @Override
    public String toString() {
        return "Organization{"
                + "organizationId="
                + organizationId
                + ", organizationName="
                + organizationName
                + ", tenantId="
                + tenantId
                + ", organizationStatus="
                + organizationStatus
                + ", createdAt="
                + createdAt
                + ", updatedAt="
                + updatedAt
                + '}';
    }
}
