package com.ryuqq.authhub.domain.endpointpermission.aggregate;

import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointDescription;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredPermissions;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredRoles;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * EndpointPermission Aggregate Root - 엔드포인트 권한 매핑 도메인 모델
 *
 * <p>API 엔드포인트와 필요한 권한/역할 간의 매핑을 정의하는 Aggregate입니다.
 *
 * <p><strong>비즈니스 규칙</strong>:
 *
 * <ul>
 *   <li>BR-001: serviceName으로 멀티 서비스 관리
 *   <li>BR-002: serviceName + path + method 유니크
 *   <li>BR-003: isPublic=true면 권한 체크 스킵
 *   <li>BR-004: requiredPermissions OR requiredRoles 만족 시 허용
 *   <li>BR-005: 변경 시 version 자동 증가
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙</strong>:
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
public final class EndpointPermission {

    private final EndpointPermissionId endpointPermissionId;
    private final ServiceName serviceName;
    private final EndpointPath path;
    private final HttpMethod method;
    private final EndpointDescription description;
    private final boolean isPublic;
    private final RequiredPermissions requiredPermissions;
    private final RequiredRoles requiredRoles;
    private final long version;
    private final boolean deleted;
    private final Instant createdAt;
    private final Instant updatedAt;

    private EndpointPermission(
            EndpointPermissionId endpointPermissionId,
            ServiceName serviceName,
            EndpointPath path,
            HttpMethod method,
            EndpointDescription description,
            boolean isPublic,
            RequiredPermissions requiredPermissions,
            RequiredRoles requiredRoles,
            long version,
            boolean deleted,
            Instant createdAt,
            Instant updatedAt) {
        validateRequired(serviceName, path, method, createdAt, updatedAt);
        this.endpointPermissionId = endpointPermissionId;
        this.serviceName = serviceName;
        this.path = path;
        this.method = method;
        this.description = description != null ? description : EndpointDescription.empty();
        this.isPublic = isPublic;
        this.requiredPermissions =
                requiredPermissions != null ? requiredPermissions : RequiredPermissions.empty();
        this.requiredRoles = requiredRoles != null ? requiredRoles : RequiredRoles.empty();
        this.version = version;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private void validateRequired(
            ServiceName serviceName,
            EndpointPath path,
            HttpMethod method,
            Instant createdAt,
            Instant updatedAt) {
        if (serviceName == null) {
            throw new IllegalArgumentException("ServiceName은 null일 수 없습니다");
        }
        if (path == null) {
            throw new IllegalArgumentException("EndpointPath는 null일 수 없습니다");
        }
        if (method == null) {
            throw new IllegalArgumentException("HttpMethod는 null일 수 없습니다");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt은 null일 수 없습니다");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("updatedAt은 null일 수 없습니다");
        }
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 엔드포인트 권한 생성 (Public)
     *
     * @param id Application Layer에서 생성된 UUID
     * @param serviceName 서비스 이름
     * @param path 엔드포인트 경로
     * @param method HTTP 메서드
     * @param description 설명
     * @param clock 시간 제공자
     * @return 새로운 Public EndpointPermission 인스턴스
     */
    public static EndpointPermission createPublic(
            UUID id,
            ServiceName serviceName,
            EndpointPath path,
            HttpMethod method,
            EndpointDescription description,
            Clock clock) {
        Instant now = clock.instant();
        return new EndpointPermission(
                EndpointPermissionId.forNew(id),
                serviceName,
                path,
                method,
                description,
                true,
                RequiredPermissions.empty(),
                RequiredRoles.empty(),
                1L,
                false,
                now,
                now);
    }

    /**
     * 새로운 엔드포인트 권한 생성 (Protected - 권한 필요)
     *
     * @param id Application Layer에서 생성된 UUID
     * @param serviceName 서비스 이름
     * @param path 엔드포인트 경로
     * @param method HTTP 메서드
     * @param description 설명
     * @param requiredPermissions 필수 권한
     * @param requiredRoles 필수 역할
     * @param clock 시간 제공자
     * @return 새로운 Protected EndpointPermission 인스턴스
     */
    public static EndpointPermission createProtected(
            UUID id,
            ServiceName serviceName,
            EndpointPath path,
            HttpMethod method,
            EndpointDescription description,
            RequiredPermissions requiredPermissions,
            RequiredRoles requiredRoles,
            Clock clock) {
        Instant now = clock.instant();
        return new EndpointPermission(
                EndpointPermissionId.forNew(id),
                serviceName,
                path,
                method,
                description,
                false,
                requiredPermissions,
                requiredRoles,
                1L,
                false,
                now,
                now);
    }

    /**
     * DB에서 EndpointPermission 재구성 (reconstitute)
     *
     * @param endpointPermissionId 엔드포인트 권한 ID
     * @param serviceName 서비스 이름
     * @param path 엔드포인트 경로
     * @param method HTTP 메서드
     * @param description 설명
     * @param isPublic 공개 여부
     * @param requiredPermissions 필수 권한
     * @param requiredRoles 필수 역할
     * @param version 버전
     * @param deleted 삭제 여부
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 EndpointPermission 인스턴스
     */
    public static EndpointPermission reconstitute(
            EndpointPermissionId endpointPermissionId,
            ServiceName serviceName,
            EndpointPath path,
            HttpMethod method,
            EndpointDescription description,
            boolean isPublic,
            RequiredPermissions requiredPermissions,
            RequiredRoles requiredRoles,
            long version,
            boolean deleted,
            Instant createdAt,
            Instant updatedAt) {
        if (endpointPermissionId == null) {
            throw new IllegalArgumentException(
                    "reconstitute requires non-null endpointPermissionId");
        }
        return new EndpointPermission(
                endpointPermissionId,
                serviceName,
                path,
                method,
                description,
                isPublic,
                requiredPermissions,
                requiredRoles,
                version,
                deleted,
                createdAt,
                updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 설명 변경
     *
     * @param newDescription 새로운 설명
     * @param clock 시간 제공자
     * @return 설명이 변경된 새로운 인스턴스 (version 증가)
     */
    public EndpointPermission changeDescription(EndpointDescription newDescription, Clock clock) {
        return new EndpointPermission(
                this.endpointPermissionId,
                this.serviceName,
                this.path,
                this.method,
                newDescription,
                this.isPublic,
                this.requiredPermissions,
                this.requiredRoles,
                this.version + 1,
                this.deleted,
                this.createdAt,
                clock.instant());
    }

    /**
     * Public으로 변경 (권한 체크 스킵)
     *
     * @param clock 시간 제공자
     * @return Public으로 변경된 새로운 인스턴스
     */
    public EndpointPermission makePublic(Clock clock) {
        return new EndpointPermission(
                this.endpointPermissionId,
                this.serviceName,
                this.path,
                this.method,
                this.description,
                true,
                RequiredPermissions.empty(),
                RequiredRoles.empty(),
                this.version + 1,
                this.deleted,
                this.createdAt,
                clock.instant());
    }

    /**
     * Protected로 변경 (권한 체크 필요)
     *
     * @param newPermissions 새로운 필수 권한
     * @param newRoles 새로운 필수 역할
     * @param clock 시간 제공자
     * @return Protected로 변경된 새로운 인스턴스
     */
    public EndpointPermission makeProtected(
            RequiredPermissions newPermissions, RequiredRoles newRoles, Clock clock) {
        return new EndpointPermission(
                this.endpointPermissionId,
                this.serviceName,
                this.path,
                this.method,
                this.description,
                false,
                newPermissions,
                newRoles,
                this.version + 1,
                this.deleted,
                this.createdAt,
                clock.instant());
    }

    /**
     * 필수 권한 변경
     *
     * @param newPermissions 새로운 필수 권한
     * @param clock 시간 제공자
     * @return 권한이 변경된 새로운 인스턴스
     */
    public EndpointPermission changeRequiredPermissions(
            RequiredPermissions newPermissions, Clock clock) {
        return new EndpointPermission(
                this.endpointPermissionId,
                this.serviceName,
                this.path,
                this.method,
                this.description,
                this.isPublic,
                newPermissions,
                this.requiredRoles,
                this.version + 1,
                this.deleted,
                this.createdAt,
                clock.instant());
    }

    /**
     * 필수 역할 변경
     *
     * @param newRoles 새로운 필수 역할
     * @param clock 시간 제공자
     * @return 역할이 변경된 새로운 인스턴스
     */
    public EndpointPermission changeRequiredRoles(RequiredRoles newRoles, Clock clock) {
        return new EndpointPermission(
                this.endpointPermissionId,
                this.serviceName,
                this.path,
                this.method,
                this.description,
                this.isPublic,
                this.requiredPermissions,
                newRoles,
                this.version + 1,
                this.deleted,
                this.createdAt,
                clock.instant());
    }

    /**
     * 삭제 (소프트 삭제)
     *
     * @param clock 시간 제공자
     * @return 삭제된 새로운 인스턴스
     */
    public EndpointPermission delete(Clock clock) {
        return new EndpointPermission(
                this.endpointPermissionId,
                this.serviceName,
                this.path,
                this.method,
                this.description,
                this.isPublic,
                this.requiredPermissions,
                this.requiredRoles,
                this.version + 1,
                true,
                this.createdAt,
                clock.instant());
    }

    // ========== Authorization Check Methods ==========

    /**
     * 사용자가 이 엔드포인트에 접근 가능한지 확인
     *
     * <p>접근 허용 조건 (OR 조건):
     *
     * <ul>
     *   <li>Public 엔드포인트인 경우
     *   <li>사용자가 requiredPermissions 중 하나라도 가진 경우
     *   <li>사용자가 requiredRoles 중 하나라도 가진 경우
     * </ul>
     *
     * @param userPermissions 사용자가 가진 권한 목록
     * @param userRoles 사용자가 가진 역할 목록
     * @return 접근 가능하면 true
     */
    public boolean canAccess(Set<String> userPermissions, Set<String> userRoles) {
        if (isPublic) {
            return true;
        }

        boolean hasPermission = requiredPermissions.hasAnyOf(userPermissions);
        boolean hasRole = requiredRoles.hasAnyOf(userRoles);

        return hasPermission || hasRole;
    }

    /**
     * 경로가 일치하는지 확인 (Path Variable 고려)
     *
     * @param requestPath 요청 경로
     * @return 일치하면 true
     */
    public boolean matchesPath(String requestPath) {
        if (requestPath == null) {
            return false;
        }

        String pattern = path.value();

        if (pattern.equals(requestPath)) {
            return true;
        }

        if (path.hasPathVariable()) {
            String regex = pattern.replaceAll("\\{[^}]+}", "[^/]+");
            return requestPath.matches(regex);
        }

        if (path.hasWildcard()) {
            // ** 처리를 먼저 하고, 그 다음 * 처리 (순서 중요)
            String tempPattern = pattern.replace("**", "§§");
            String singleWildcardReplaced = tempPattern.replace("*", "[^/]+");
            String regex = singleWildcardReplaced.replace("§§", ".*");
            return requestPath.matches(regex);
        }

        return false;
    }

    // ========== Helper Methods ==========

    public UUID endpointPermissionIdValue() {
        return endpointPermissionId != null ? endpointPermissionId.value() : null;
    }

    public String serviceNameValue() {
        return serviceName.value();
    }

    public String pathValue() {
        return path.value();
    }

    public String methodValue() {
        return method.value();
    }

    public String descriptionValue() {
        return description.value();
    }

    public Set<String> requiredPermissionValues() {
        return requiredPermissions.values();
    }

    public Set<String> requiredRoleValues() {
        return requiredRoles.values();
    }

    public boolean isNew() {
        return endpointPermissionId == null;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isProtected() {
        return !isPublic;
    }

    public boolean isDeleted() {
        return deleted;
    }

    // ========== Getter Methods ==========

    public EndpointPermissionId getEndpointPermissionId() {
        return endpointPermissionId;
    }

    public ServiceName getServiceName() {
        return serviceName;
    }

    public EndpointPath getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public EndpointDescription getDescription() {
        return description;
    }

    public RequiredPermissions getRequiredPermissions() {
        return requiredPermissions;
    }

    public RequiredRoles getRequiredRoles() {
        return requiredRoles;
    }

    public long getVersion() {
        return version;
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
        EndpointPermission that = (EndpointPermission) o;
        if (endpointPermissionId == null || that.endpointPermissionId == null) {
            return false;
        }
        return Objects.equals(endpointPermissionId, that.endpointPermissionId);
    }

    @Override
    public int hashCode() {
        return endpointPermissionId != null
                ? Objects.hash(endpointPermissionId)
                : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "EndpointPermission{"
                + "endpointPermissionId="
                + endpointPermissionId
                + ", serviceName="
                + serviceName
                + ", path="
                + path
                + ", method="
                + method
                + ", isPublic="
                + isPublic
                + ", version="
                + version
                + ", deleted="
                + deleted
                + "}";
    }
}
