package com.ryuqq.authhub.domain.endpointpermission.fixture;

import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointDescription;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredPermissions;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredRoles;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

/**
 * EndpointPermission 테스트 Fixture
 *
 * <p>테스트에서 사용할 EndpointPermission 객체를 쉽게 생성할 수 있도록 도와주는 클래스입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class EndpointPermissionFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private static final String DEFAULT_SERVICE_NAME = "auth-hub";
    private static final String DEFAULT_PATH = "/api/v1/users";
    private static final HttpMethod DEFAULT_METHOD = HttpMethod.GET;

    private EndpointPermissionFixture() {}

    // ========== 기본 생성 메서드 ==========

    /** 기본 Public 엔드포인트 권한 생성 */
    public static EndpointPermission createPublic() {
        return EndpointPermission.createPublic(
                UUID.randomUUID(),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of(DEFAULT_PATH),
                DEFAULT_METHOD,
                EndpointDescription.of("사용자 목록 조회"),
                FIXED_CLOCK);
    }

    /** 지정된 path로 Public 엔드포인트 생성 */
    public static EndpointPermission createPublic(String path) {
        return EndpointPermission.createPublic(
                UUID.randomUUID(),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of(path),
                DEFAULT_METHOD,
                EndpointDescription.empty(),
                FIXED_CLOCK);
    }

    /** 지정된 path와 method로 Public 엔드포인트 생성 */
    public static EndpointPermission createPublic(String path, HttpMethod method) {
        return EndpointPermission.createPublic(
                UUID.randomUUID(),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of(path),
                method,
                EndpointDescription.empty(),
                FIXED_CLOCK);
    }

    // ========== Protected 엔드포인트 ==========

    /** 기본 Protected 엔드포인트 권한 생성 (user:read 권한 필요) */
    public static EndpointPermission createProtected() {
        return EndpointPermission.createProtected(
                UUID.randomUUID(),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of(DEFAULT_PATH),
                DEFAULT_METHOD,
                EndpointDescription.of("사용자 목록 조회 (인증 필요)"),
                RequiredPermissions.single("user:read"),
                RequiredRoles.empty(),
                FIXED_CLOCK);
    }

    /** 지정된 권한이 필요한 Protected 엔드포인트 생성 */
    public static EndpointPermission createProtected(String path, String requiredPermission) {
        return EndpointPermission.createProtected(
                UUID.randomUUID(),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of(path),
                DEFAULT_METHOD,
                EndpointDescription.empty(),
                RequiredPermissions.single(requiredPermission),
                RequiredRoles.empty(),
                FIXED_CLOCK);
    }

    /** 지정된 역할이 필요한 Protected 엔드포인트 생성 */
    public static EndpointPermission createProtectedWithRole(String path, String requiredRole) {
        return EndpointPermission.createProtected(
                UUID.randomUUID(),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of(path),
                DEFAULT_METHOD,
                EndpointDescription.empty(),
                RequiredPermissions.empty(),
                RequiredRoles.single(requiredRole),
                FIXED_CLOCK);
    }

    /** 여러 권한 중 하나가 필요한 Protected 엔드포인트 생성 */
    public static EndpointPermission createProtectedWithMultiplePermissions(
            String path, Set<String> permissions) {
        return EndpointPermission.createProtected(
                UUID.randomUUID(),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of(path),
                DEFAULT_METHOD,
                EndpointDescription.empty(),
                RequiredPermissions.of(permissions),
                RequiredRoles.empty(),
                FIXED_CLOCK);
    }

    /** 권한과 역할 모두 체크하는 Protected 엔드포인트 생성 */
    public static EndpointPermission createProtectedWithPermissionAndRole(
            String path, Set<String> permissions, Set<String> roles) {
        return EndpointPermission.createProtected(
                UUID.randomUUID(),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of(path),
                DEFAULT_METHOD,
                EndpointDescription.empty(),
                RequiredPermissions.of(permissions),
                RequiredRoles.of(roles),
                FIXED_CLOCK);
    }

    // ========== 특정 서비스 엔드포인트 ==========

    /** 특정 서비스의 엔드포인트 생성 */
    public static EndpointPermission createForService(
            String serviceName, String path, HttpMethod method) {
        return EndpointPermission.createProtected(
                UUID.randomUUID(),
                ServiceName.of(serviceName),
                EndpointPath.of(path),
                method,
                EndpointDescription.empty(),
                RequiredPermissions.empty(),
                RequiredRoles.empty(),
                FIXED_CLOCK);
    }

    // ========== 재구성 (DB 조회 시뮬레이션) ==========

    /** ID가 있는 엔드포인트 권한 (DB에서 조회된 것처럼) */
    public static EndpointPermission createReconstituted() {
        return EndpointPermission.reconstitute(
                EndpointPermissionId.of(UUID.randomUUID()),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of(DEFAULT_PATH),
                DEFAULT_METHOD,
                EndpointDescription.of("사용자 목록 조회"),
                false,
                RequiredPermissions.single("user:read"),
                RequiredRoles.empty(),
                1L,
                false,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    /** 지정된 ID로 엔드포인트 권한 재구성 */
    public static EndpointPermission createReconstituted(UUID id) {
        return EndpointPermission.reconstitute(
                EndpointPermissionId.of(id),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of(DEFAULT_PATH),
                DEFAULT_METHOD,
                EndpointDescription.of("사용자 목록 조회"),
                false,
                RequiredPermissions.single("user:read"),
                RequiredRoles.empty(),
                1L,
                false,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    /** Public 엔드포인트 재구성 */
    public static EndpointPermission createReconstitutedPublic(UUID id, String path) {
        return EndpointPermission.reconstitute(
                EndpointPermissionId.of(id),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of(path),
                DEFAULT_METHOD,
                EndpointDescription.empty(),
                true,
                RequiredPermissions.empty(),
                RequiredRoles.empty(),
                1L,
                false,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    /** 삭제된 엔드포인트 권한 */
    public static EndpointPermission createDeleted() {
        return EndpointPermission.reconstitute(
                EndpointPermissionId.of(UUID.randomUUID()),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of("/api/v1/deleted"),
                DEFAULT_METHOD,
                EndpointDescription.of("삭제된 엔드포인트"),
                false,
                RequiredPermissions.empty(),
                RequiredRoles.empty(),
                2L,
                true,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-02T00:00:00Z"));
    }

    // ========== Path Variable 엔드포인트 ==========

    /** Path Variable이 있는 엔드포인트 */
    public static EndpointPermission createWithPathVariable() {
        return EndpointPermission.createProtected(
                UUID.randomUUID(),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of("/api/v1/users/{userId}"),
                HttpMethod.GET,
                EndpointDescription.of("사용자 상세 조회"),
                RequiredPermissions.single("user:read"),
                RequiredRoles.empty(),
                FIXED_CLOCK);
    }

    /** 와일드카드가 있는 엔드포인트 */
    public static EndpointPermission createWithWildcard() {
        return EndpointPermission.createProtected(
                UUID.randomUUID(),
                ServiceName.of(DEFAULT_SERVICE_NAME),
                EndpointPath.of("/api/v1/admin/**"),
                HttpMethod.GET,
                EndpointDescription.of("관리자 API"),
                RequiredPermissions.empty(),
                RequiredRoles.single("ADMIN"),
                FIXED_CLOCK);
    }

    // ========== VO Fixture ==========

    /** ServiceName 생성 */
    public static ServiceName createServiceName() {
        return ServiceName.of(DEFAULT_SERVICE_NAME);
    }

    /** ServiceName 생성 */
    public static ServiceName createServiceName(String value) {
        return ServiceName.of(value);
    }

    /** EndpointPath 생성 */
    public static EndpointPath createEndpointPath() {
        return EndpointPath.of(DEFAULT_PATH);
    }

    /** EndpointPath 생성 */
    public static EndpointPath createEndpointPath(String value) {
        return EndpointPath.of(value);
    }

    /** EndpointDescription 생성 */
    public static EndpointDescription createDescription() {
        return EndpointDescription.of("테스트 엔드포인트");
    }

    /** EndpointDescription 생성 */
    public static EndpointDescription createDescription(String value) {
        return EndpointDescription.of(value);
    }

    /** RequiredPermissions 생성 */
    public static RequiredPermissions createRequiredPermissions() {
        return RequiredPermissions.single("user:read");
    }

    /** RequiredPermissions 생성 */
    public static RequiredPermissions createRequiredPermissions(Set<String> permissions) {
        return RequiredPermissions.of(permissions);
    }

    /** RequiredRoles 생성 */
    public static RequiredRoles createRequiredRoles() {
        return RequiredRoles.single("ADMIN");
    }

    /** RequiredRoles 생성 */
    public static RequiredRoles createRequiredRoles(Set<String> roles) {
        return RequiredRoles.of(roles);
    }

    /** EndpointPermissionId 생성 */
    public static EndpointPermissionId createEndpointPermissionId() {
        return EndpointPermissionId.of(UUID.randomUUID());
    }

    /** EndpointPermissionId 생성 */
    public static EndpointPermissionId createEndpointPermissionId(UUID uuid) {
        return EndpointPermissionId.of(uuid);
    }

    /** 테스트용 고정 Clock */
    public static Clock fixedClock() {
        return FIXED_CLOCK;
    }
}
