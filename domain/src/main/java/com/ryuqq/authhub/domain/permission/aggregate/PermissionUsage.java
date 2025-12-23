package com.ryuqq.authhub.domain.permission.aggregate;

import com.ryuqq.authhub.domain.permission.identifier.PermissionUsageId;
import com.ryuqq.authhub.domain.permission.vo.CodeLocation;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.ServiceName;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * PermissionUsage Aggregate Root - 권한 사용 이력 도메인 모델
 *
 * <p>특정 권한이 어떤 서비스에서 사용되는지 추적합니다.
 *
 * <p><strong>사용 시나리오:</strong>
 *
 * <ol>
 *   <li>CI/CD에서 권한 검증 후 승인 시 사용 이력 등록
 *   <li>권한별로 어떤 서비스들이 사용하는지 조회
 *   <li>미사용 권한 감지 (lastScannedAt 기반)
 * </ol>
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
public final class PermissionUsage {

    private final PermissionUsageId usageId;
    private final PermissionKey permissionKey;
    private final ServiceName serviceName;
    private final List<CodeLocation> locations;
    private final Instant lastScannedAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    private PermissionUsage(
            PermissionUsageId usageId,
            PermissionKey permissionKey,
            ServiceName serviceName,
            List<CodeLocation> locations,
            Instant lastScannedAt,
            Instant createdAt,
            Instant updatedAt) {
        validateRequired(permissionKey, serviceName, createdAt, updatedAt);
        this.usageId = usageId;
        this.permissionKey = permissionKey;
        this.serviceName = serviceName;
        this.locations = locations != null ? new ArrayList<>(locations) : new ArrayList<>();
        this.lastScannedAt = lastScannedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private void validateRequired(
            PermissionKey permissionKey,
            ServiceName serviceName,
            Instant createdAt,
            Instant updatedAt) {
        if (permissionKey == null) {
            throw new IllegalArgumentException("PermissionKey는 null일 수 없습니다");
        }
        if (serviceName == null) {
            throw new IllegalArgumentException("ServiceName은 null일 수 없습니다");
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
     * 새로운 권한 사용 이력 생성
     *
     * @param permissionKey 권한 키
     * @param serviceName 서비스명
     * @param locations 코드 위치 목록
     * @param clock 시간 제공자
     * @return 새로운 PermissionUsage 인스턴스
     */
    public static PermissionUsage create(
            PermissionKey permissionKey,
            ServiceName serviceName,
            List<CodeLocation> locations,
            Clock clock) {
        Instant now = clock.instant();
        return new PermissionUsage(null, permissionKey, serviceName, locations, now, now, now);
    }

    /**
     * DB에서 PermissionUsage 재구성 (reconstitute)
     *
     * @param usageId 사용 이력 ID
     * @param permissionKey 권한 키
     * @param serviceName 서비스명
     * @param locations 코드 위치 목록
     * @param lastScannedAt 마지막 스캔 시간
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 PermissionUsage 인스턴스
     */
    public static PermissionUsage reconstitute(
            PermissionUsageId usageId,
            PermissionKey permissionKey,
            ServiceName serviceName,
            List<CodeLocation> locations,
            Instant lastScannedAt,
            Instant createdAt,
            Instant updatedAt) {
        if (usageId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null usageId");
        }
        return new PermissionUsage(
                usageId,
                permissionKey,
                serviceName,
                locations,
                lastScannedAt,
                createdAt,
                updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 사용 이력 업데이트 (스캔 시간 갱신 및 위치 목록 업데이트)
     *
     * @param newLocations 새로운 코드 위치 목록
     * @param clock 시간 제공자
     * @return 업데이트된 PermissionUsage 인스턴스
     */
    public PermissionUsage updateUsage(List<CodeLocation> newLocations, Clock clock) {
        Instant now = clock.instant();
        return new PermissionUsage(
                this.usageId,
                this.permissionKey,
                this.serviceName,
                newLocations,
                now,
                this.createdAt,
                now);
    }

    /**
     * 스캔 시간만 갱신
     *
     * @param clock 시간 제공자
     * @return 스캔 시간이 갱신된 PermissionUsage 인스턴스
     */
    public PermissionUsage refreshScanTime(Clock clock) {
        Instant now = clock.instant();
        return new PermissionUsage(
                this.usageId,
                this.permissionKey,
                this.serviceName,
                this.locations,
                now,
                this.createdAt,
                now);
    }

    // ========== Query Methods ==========

    /**
     * 지정된 기간 동안 스캔되지 않았는지 확인
     *
     * @param days 기준 일수
     * @param clock 시간 제공자
     * @return 미스캔 여부
     */
    public boolean isStale(int days, Clock clock) {
        if (lastScannedAt == null) {
            return true;
        }
        Instant threshold = clock.instant().minusSeconds(days * 24L * 60 * 60);
        return lastScannedAt.isBefore(threshold);
    }

    // ========== Helper Methods ==========

    public UUID usageIdValue() {
        return usageId != null ? usageId.value() : null;
    }

    public String permissionKeyValue() {
        return permissionKey.value();
    }

    public String serviceNameValue() {
        return serviceName.value();
    }

    public List<String> locationValues() {
        return locations.stream().map(CodeLocation::value).toList();
    }

    public boolean isNew() {
        return usageId == null;
    }

    // ========== Getter Methods ==========

    public PermissionUsageId getUsageId() {
        return usageId;
    }

    public PermissionKey getPermissionKey() {
        return permissionKey;
    }

    public ServiceName getServiceName() {
        return serviceName;
    }

    public List<CodeLocation> getLocations() {
        return Collections.unmodifiableList(locations);
    }

    public Instant getLastScannedAt() {
        return lastScannedAt;
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
        PermissionUsage that = (PermissionUsage) o;
        if (usageId == null || that.usageId == null) {
            return false;
        }
        return Objects.equals(usageId, that.usageId);
    }

    @Override
    public int hashCode() {
        return usageId != null ? Objects.hash(usageId) : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "PermissionUsage{"
                + "usageId="
                + usageId
                + ", permissionKey="
                + permissionKey
                + ", serviceName="
                + serviceName
                + ", locations="
                + locations.size()
                + ", lastScannedAt="
                + lastScannedAt
                + "}";
    }
}
