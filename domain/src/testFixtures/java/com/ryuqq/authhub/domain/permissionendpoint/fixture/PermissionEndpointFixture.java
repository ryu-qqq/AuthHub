package com.ryuqq.authhub.domain.permissionendpoint.fixture;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import java.time.Instant;

/**
 * PermissionEndpoint 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionEndpointFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_ENDPOINT_ID = 1L;
    private static final Long DEFAULT_PERMISSION_ID = 1L;
    private static final String DEFAULT_URL_PATTERN = "/api/v1/users";
    private static final String DEFAULT_DESCRIPTION = "사용자 목록 조회 엔드포인트";

    private PermissionEndpointFixture() {}

    /** 기본 엔드포인트 생성 (ID 할당됨, GET) */
    public static PermissionEndpoint create() {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                DEFAULT_URL_PATTERN,
                HttpMethod.GET,
                DEFAULT_DESCRIPTION,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** GET 엔드포인트 생성 */
    public static PermissionEndpoint createGetEndpoint() {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                DEFAULT_URL_PATTERN,
                HttpMethod.GET,
                "GET 엔드포인트",
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** POST 엔드포인트 생성 */
    public static PermissionEndpoint createPostEndpoint() {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                DEFAULT_URL_PATTERN,
                HttpMethod.POST,
                "POST 엔드포인트",
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** PUT 엔드포인트 생성 */
    public static PermissionEndpoint createPutEndpoint() {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                "/api/v1/users/{id}",
                HttpMethod.PUT,
                "PUT 엔드포인트",
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** DELETE 엔드포인트 생성 */
    public static PermissionEndpoint createDeleteEndpoint() {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                "/api/v1/users/{id}",
                HttpMethod.DELETE,
                "DELETE 엔드포인트",
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 URL 패턴으로 엔드포인트 생성 */
    public static PermissionEndpoint createWithPattern(String urlPattern) {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                urlPattern,
                HttpMethod.GET,
                urlPattern + " 엔드포인트",
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 URL 패턴과 HTTP 메서드로 엔드포인트 생성 */
    public static PermissionEndpoint createWithPatternAndMethod(
            String urlPattern, HttpMethod method) {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                urlPattern,
                method,
                urlPattern + " " + method.name() + " 엔드포인트",
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 권한 ID로 엔드포인트 생성 */
    public static PermissionEndpoint createWithPermission(Long permissionId) {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(permissionId),
                DEFAULT_URL_PATTERN,
                HttpMethod.GET,
                DEFAULT_DESCRIPTION,
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 경로 변수를 포함한 엔드포인트 생성 */
    public static PermissionEndpoint createWithPathVariable() {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                "/api/v1/users/{id}",
                HttpMethod.GET,
                "특정 사용자 조회 엔드포인트",
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 중첩 경로 변수를 포함한 엔드포인트 생성 */
    public static PermissionEndpoint createWithNestedPathVariable() {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                "/api/v1/organizations/{orgId}/members/{memberId}",
                HttpMethod.GET,
                "조직 멤버 조회 엔드포인트",
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 와일드카드 패턴 엔드포인트 생성 */
    public static PermissionEndpoint createWithWildcard() {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                "/api/v1/admin/**",
                HttpMethod.GET,
                "관리자 모든 하위 경로 엔드포인트",
                DeletionStatus.active(),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 새로운 엔드포인트 생성 (ID 없음) */
    public static PermissionEndpoint createNew() {
        return PermissionEndpoint.create(
                PermissionId.of(DEFAULT_PERMISSION_ID),
                DEFAULT_URL_PATTERN,
                HttpMethod.GET,
                DEFAULT_DESCRIPTION,
                FIXED_TIME);
    }

    /** 새로운 엔드포인트 생성 (지정된 패턴, ID 없음) */
    public static PermissionEndpoint createNewWithPattern(String urlPattern, HttpMethod method) {
        return PermissionEndpoint.create(
                PermissionId.of(DEFAULT_PERMISSION_ID),
                urlPattern,
                method,
                urlPattern + " 엔드포인트",
                FIXED_TIME);
    }

    /** 삭제된 엔드포인트 생성 */
    public static PermissionEndpoint createDeleted() {
        return PermissionEndpoint.reconstitute(
                PermissionEndpointId.of(DEFAULT_ENDPOINT_ID),
                PermissionId.of(DEFAULT_PERMISSION_ID),
                DEFAULT_URL_PATTERN,
                HttpMethod.GET,
                DEFAULT_DESCRIPTION,
                DeletionStatus.deletedAt(FIXED_TIME),
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 PermissionEndpointId 반환 */
    public static PermissionEndpointId defaultId() {
        return PermissionEndpointId.of(DEFAULT_ENDPOINT_ID);
    }

    /** 기본 Endpoint ID 값 반환 */
    public static Long defaultIdValue() {
        return DEFAULT_ENDPOINT_ID;
    }

    /** 기본 PermissionId 반환 */
    public static PermissionId defaultPermissionId() {
        return PermissionId.of(DEFAULT_PERMISSION_ID);
    }

    /** 기본 Permission ID 값 반환 */
    public static Long defaultPermissionIdValue() {
        return DEFAULT_PERMISSION_ID;
    }

    /** 기본 URL 패턴 반환 */
    public static String defaultUrlPattern() {
        return DEFAULT_URL_PATTERN;
    }
}
