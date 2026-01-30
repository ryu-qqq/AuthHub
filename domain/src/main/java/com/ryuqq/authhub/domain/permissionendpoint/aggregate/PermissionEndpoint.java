package com.ryuqq.authhub.domain.permissionendpoint.aggregate;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointUpdateData;
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
 *   <li>Null 검증은 생성 시점에서 처리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionEndpoint {

    private final PermissionEndpointId permissionEndpointId;
    private final PermissionId permissionId;
    private String urlPattern;
    private HttpMethod httpMethod;
    private String description;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private PermissionEndpoint(
            PermissionEndpointId permissionEndpointId,
            PermissionId permissionId,
            String urlPattern,
            HttpMethod httpMethod,
            String description,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        validateRequired(permissionId, urlPattern, httpMethod);
        this.permissionEndpointId = permissionEndpointId;
        this.permissionId = permissionId;
        this.urlPattern = urlPattern;
        this.httpMethod = httpMethod;
        this.description = description;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private void validateRequired(
            PermissionId permissionId, String urlPattern, HttpMethod httpMethod) {
        if (permissionId == null) {
            throw new IllegalArgumentException("permissionId는 null일 수 없습니다");
        }
        if (urlPattern == null || urlPattern.isBlank()) {
            throw new IllegalArgumentException("urlPattern은 null이거나 빈 값일 수 없습니다");
        }
        if (!urlPattern.startsWith("/")) {
            throw new IllegalArgumentException("urlPattern은 '/'로 시작해야 합니다");
        }
        if (httpMethod == null) {
            throw new IllegalArgumentException("httpMethod는 null일 수 없습니다");
        }
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 PermissionEndpoint 생성
     *
     * @param permissionId 권한 ID
     * @param urlPattern URL 패턴 (예: /api/v1/users/{id})
     * @param httpMethod HTTP 메서드
     * @param description 설명
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 PermissionEndpoint 인스턴스
     */
    public static PermissionEndpoint create(
            PermissionId permissionId,
            String urlPattern,
            HttpMethod httpMethod,
            String description,
            Instant now) {
        return new PermissionEndpoint(
                null,
                permissionId,
                urlPattern,
                httpMethod,
                description,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * Long permissionId로 새로운 PermissionEndpoint 생성 (편의 메서드)
     *
     * @param permissionId 권한 ID (Long)
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @param description 설명
     * @param now 현재 시간
     * @return 새로운 PermissionEndpoint 인스턴스
     */
    public static PermissionEndpoint create(
            Long permissionId,
            String urlPattern,
            HttpMethod httpMethod,
            String description,
            Instant now) {
        return create(PermissionId.of(permissionId), urlPattern, httpMethod, description, now);
    }

    /**
     * DB에서 PermissionEndpoint 재구성 (reconstitute)
     *
     * @param permissionEndpointId 엔드포인트 ID
     * @param permissionId 권한 ID
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @param description 설명
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 PermissionEndpoint 인스턴스
     */
    public static PermissionEndpoint reconstitute(
            PermissionEndpointId permissionEndpointId,
            PermissionId permissionId,
            String urlPattern,
            HttpMethod httpMethod,
            String description,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new PermissionEndpoint(
                permissionEndpointId,
                permissionId,
                urlPattern,
                httpMethod,
                description,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 엔드포인트 정보 수정 (UpdateData 패턴)
     *
     * <p>Permission 도메인과 동일한 UpdateData 패턴을 사용합니다.
     *
     * @param updateData 수정할 데이터
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void update(PermissionEndpointUpdateData updateData, Instant changedAt) {
        if (updateData.hasUrlPattern()) {
            validateUrlPattern(updateData.urlPattern());
            this.urlPattern = updateData.urlPattern();
        }
        if (updateData.hasHttpMethod()) {
            this.httpMethod = updateData.httpMethodEnum();
        }
        if (updateData.hasDescription()) {
            this.description = updateData.description();
        }
        this.updatedAt = changedAt;
    }

    /**
     * 엔드포인트 정보 수정 (개별 파라미터 - 하위 호환성)
     *
     * @param newUrlPattern 새 URL 패턴
     * @param newHttpMethod 새 HTTP 메서드
     * @param newDescription 새 설명
     * @param changedAt 변경 시간 (외부 주입)
     * @deprecated UpdateData 패턴 사용을 권장합니다
     */
    @Deprecated
    public void update(
            String newUrlPattern,
            HttpMethod newHttpMethod,
            String newDescription,
            Instant changedAt) {
        if (newUrlPattern != null && !newUrlPattern.isBlank()) {
            validateUrlPattern(newUrlPattern);
            this.urlPattern = newUrlPattern;
        }
        if (newHttpMethod != null) {
            this.httpMethod = newHttpMethod;
        }
        if (newDescription != null) {
            this.description = newDescription;
        }
        this.updatedAt = changedAt;
    }

    private void validateUrlPattern(String pattern) {
        if (!pattern.startsWith("/")) {
            throw new IllegalArgumentException("urlPattern은 '/'로 시작해야 합니다");
        }
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
     * URL 패턴 값 반환
     *
     * @return URL 패턴
     */
    public String urlPatternValue() {
        return urlPattern;
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
        return matchesUrlPattern(requestUrl);
    }

    private boolean matchesUrlPattern(String requestUrl) {
        String pattern = this.urlPattern;
        String regex =
                pattern.replaceAll("\\{[^}]+\\}", "[^/]+")
                        .replace("**", "\0")
                        .replace("*", "[^/]*")
                        .replace("\0", ".*");
        return requestUrl.matches("^" + regex + "$");
    }

    // ========== Getter Methods ==========

    public PermissionEndpointId getPermissionEndpointId() {
        return permissionEndpointId;
    }

    public PermissionId getPermissionId() {
        return permissionId;
    }

    public String getUrlPattern() {
        return urlPattern;
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
                + ", urlPattern='"
                + urlPattern
                + '\''
                + ", httpMethod="
                + httpMethod
                + ", deleted="
                + deletionStatus.isDeleted()
                + '}';
    }
}
