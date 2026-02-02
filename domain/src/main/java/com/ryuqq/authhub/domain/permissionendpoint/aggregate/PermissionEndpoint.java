package com.ryuqq.authhub.domain.permissionendpoint.aggregate;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.ServiceName;
import com.ryuqq.authhub.domain.permissionendpoint.vo.UrlPattern;
import java.time.Instant;
import java.util.Objects;

/**
 * PermissionEndpoint Aggregate Root - 권한 엔드포인트 매핑 도메인 모델
 *
 * <p>Gateway에서 URL-Permission 매핑 정보를 관리하는 Aggregate입니다.
 *
 * <p><strong>설계 목적:</strong>
 *
 * <ul>
 *   <li>Gateway가 요청 URL에 대한 필요 권한을 조회할 때 사용
 *   <li>하나의 Permission에 여러 Endpoint를 매핑 가능 (1:N)
 *   <li>URL 패턴과 HTTP Method 조합으로 권한 식별
 * </ul>
 *
 * <p><strong>URL 패턴 예시:</strong>
 *
 * <ul>
 *   <li>/api/v1/users - 사용자 목록 조회
 *   <li>/api/v1/users/{id} - 특정 사용자 조회/수정/삭제
 *   <li>/api/v1/organizations/{orgId}/members - 조직 멤버 관리
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>Law of Demeter 준수 - Getter 체이닝 금지
 *   <li>Long FK 전략 - JPA 관계 어노테이션 금지
 *   <li>Null 검증은 VO 생성 시점에서 처리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionEndpoint {

    private final PermissionEndpointId permissionEndpointId;
    private final PermissionId permissionId;
    private ServiceName serviceName;
    private UrlPattern urlPattern;
    private HttpMethod httpMethod;
    private String description;
    private boolean isPublic;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private PermissionEndpoint(
            PermissionEndpointId permissionEndpointId,
            PermissionId permissionId,
            ServiceName serviceName,
            UrlPattern urlPattern,
            HttpMethod httpMethod,
            String description,
            boolean isPublic,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.permissionEndpointId = permissionEndpointId;
        this.permissionId = permissionId;
        this.serviceName = serviceName;
        this.urlPattern = urlPattern;
        this.httpMethod = httpMethod;
        this.description = description;
        this.isPublic = isPublic;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 PermissionEndpoint 생성
     *
     * @param permissionId 권한 ID
     * @param serviceName 서비스 이름
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @param description 설명
     * @param isPublic 공개 엔드포인트 여부
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 PermissionEndpoint 인스턴스
     */
    public static PermissionEndpoint create(
            PermissionId permissionId,
            ServiceName serviceName,
            UrlPattern urlPattern,
            HttpMethod httpMethod,
            String description,
            boolean isPublic,
            Instant now) {
        return new PermissionEndpoint(
                null,
                permissionId,
                serviceName,
                urlPattern,
                httpMethod,
                description,
                isPublic,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * Long permissionId로 새로운 PermissionEndpoint 생성 (편의 메서드)
     *
     * @param permissionId 권한 ID (Long)
     * @param serviceName 서비스 이름 (String)
     * @param urlPattern URL 패턴 (String)
     * @param httpMethod HTTP 메서드
     * @param description 설명
     * @param isPublic 공개 엔드포인트 여부
     * @param now 현재 시간
     * @return 새로운 PermissionEndpoint 인스턴스
     */
    public static PermissionEndpoint create(
            Long permissionId,
            String serviceName,
            String urlPattern,
            HttpMethod httpMethod,
            String description,
            boolean isPublic,
            Instant now) {
        return create(
                PermissionId.of(permissionId),
                ServiceName.of(serviceName),
                UrlPattern.of(urlPattern),
                httpMethod,
                description,
                isPublic,
                now);
    }

    /**
     * DB에서 PermissionEndpoint 재구성 (reconstitute)
     *
     * @param permissionEndpointId 엔드포인트 ID
     * @param permissionId 권한 ID
     * @param serviceName 서비스 이름
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @param description 설명
     * @param isPublic 공개 엔드포인트 여부
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 PermissionEndpoint 인스턴스
     */
    public static PermissionEndpoint reconstitute(
            PermissionEndpointId permissionEndpointId,
            PermissionId permissionId,
            ServiceName serviceName,
            UrlPattern urlPattern,
            HttpMethod httpMethod,
            String description,
            boolean isPublic,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new PermissionEndpoint(
                permissionEndpointId,
                permissionId,
                serviceName,
                urlPattern,
                httpMethod,
                description,
                isPublic,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * DB에서 PermissionEndpoint 재구성 (문자열 파라미터 편의 메서드)
     *
     * @param permissionEndpointId 엔드포인트 ID (Long)
     * @param permissionId 권한 ID (Long)
     * @param serviceName 서비스 이름 (String)
     * @param urlPattern URL 패턴 (String)
     * @param httpMethod HTTP 메서드
     * @param description 설명
     * @param isPublic 공개 엔드포인트 여부
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 PermissionEndpoint 인스턴스
     */
    public static PermissionEndpoint reconstitute(
            Long permissionEndpointId,
            Long permissionId,
            String serviceName,
            String urlPattern,
            HttpMethod httpMethod,
            String description,
            boolean isPublic,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return reconstitute(
                PermissionEndpointId.of(permissionEndpointId),
                PermissionId.of(permissionId),
                ServiceName.of(serviceName),
                UrlPattern.of(urlPattern),
                httpMethod,
                description,
                isPublic,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 엔드포인트 정보 수정
     *
     * @param updateData 수정할 데이터 (모든 필드가 채워져 있어야 함)
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void update(PermissionEndpointUpdateData updateData, Instant changedAt) {
        this.serviceName = updateData.serviceName();
        this.urlPattern = updateData.urlPattern();
        this.httpMethod = updateData.httpMethod();
        this.description = updateData.description();
        this.isPublic = updateData.isPublic();
        this.updatedAt = changedAt;
    }

    /**
     * 엔드포인트 삭제 (소프트 삭제)
     *
     * @param now 삭제 시간 (외부 주입)
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 엔드포인트 복원
     *
     * @param now 복원 시간 (외부 주입)
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    // ========== Query Methods ==========

    /**
     * 엔드포인트 ID 값 반환
     *
     * @return 엔드포인트 ID (Long) 또는 null (신규 생성 시)
     */
    public Long permissionEndpointIdValue() {
        return permissionEndpointId != null ? permissionEndpointId.value() : null;
    }

    /**
     * 권한 ID 값 반환
     *
     * @return 권한 ID (Long)
     */
    public Long permissionIdValue() {
        return permissionId.value();
    }

    /**
     * 서비스 이름 값 반환
     *
     * @return 서비스 이름 문자열
     */
    public String serviceNameValue() {
        return serviceName.value();
    }

    /**
     * URL 패턴 값 반환
     *
     * @return URL 패턴 문자열
     */
    public String urlPatternValue() {
        return urlPattern.value();
    }

    /**
     * 공개 엔드포인트 여부 반환
     *
     * @return 공개 엔드포인트이면 true
     */
    public boolean isPublicEndpoint() {
        return isPublic;
    }

    /**
     * HTTP 메서드 값 반환
     *
     * @return HTTP 메서드 문자열
     */
    public String httpMethodValue() {
        return httpMethod.name();
    }

    /**
     * 설명 값 반환
     *
     * @return 설명
     */
    public String descriptionValue() {
        return description;
    }

    /**
     * 신규 생성 여부 확인
     *
     * @return ID가 없으면 true (신규)
     */
    public boolean isNew() {
        return permissionEndpointId == null;
    }

    /**
     * 삭제 여부 확인
     *
     * @return 삭제되었으면 true
     */
    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    /**
     * 활성 여부 확인
     *
     * @return 활성 상태면 true
     */
    public boolean isActive() {
        return deletionStatus.isActive();
    }

    /**
     * 주어진 URL과 매칭되는지 확인
     *
     * @param requestUrl 요청 URL
     * @param requestMethod 요청 HTTP 메서드
     * @return 매칭되면 true
     */
    public boolean matches(String requestUrl, HttpMethod requestMethod) {
        if (!this.httpMethod.equals(requestMethod)) {
            return false;
        }
        return urlPattern.matches(requestUrl);
    }

    // ========== Getter Methods ==========

    public PermissionEndpointId getPermissionEndpointId() {
        return permissionEndpointId;
    }

    public PermissionId getPermissionId() {
        return permissionId;
    }

    public ServiceName getServiceName() {
        return serviceName;
    }

    public UrlPattern getUrlPattern() {
        return urlPattern;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getDescription() {
        return description;
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
        PermissionEndpoint that = (PermissionEndpoint) o;
        if (permissionEndpointId == null || that.permissionEndpointId == null) {
            return Objects.equals(permissionId, that.permissionId)
                    && Objects.equals(urlPattern, that.urlPattern)
                    && Objects.equals(httpMethod, that.httpMethod);
        }
        return Objects.equals(permissionEndpointId, that.permissionEndpointId);
    }

    @Override
    public int hashCode() {
        if (permissionEndpointId != null) {
            return Objects.hash(permissionEndpointId);
        }
        return Objects.hash(permissionId, urlPattern, httpMethod);
    }

    @Override
    public String toString() {
        return "PermissionEndpoint{"
                + "permissionEndpointId="
                + permissionEndpointId
                + ", permissionId="
                + permissionId
                + ", serviceName="
                + serviceName
                + ", urlPattern="
                + urlPattern
                + ", httpMethod="
                + httpMethod
                + ", isPublic="
                + isPublic
                + ", deleted="
                + deletionStatus.isDeleted()
                + '}';
    }
}
