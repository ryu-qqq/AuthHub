package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.fixture;

import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import java.time.Instant;

/**
 * PermissionEndpointJpaEntity 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionEndpointJpaEntityFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_ENDPOINT_ID = 1L;
    private static final Long DEFAULT_PERMISSION_ID = 1L;
    private static final String DEFAULT_SERVICE_NAME = "authhub";
    private static final String DEFAULT_URL_PATTERN = "/api/v1/users";
    private static final String DEFAULT_DESCRIPTION = "사용자 목록 조회 엔드포인트";
    private static final boolean DEFAULT_IS_PUBLIC = false;

    private PermissionEndpointJpaEntityFixture() {}

    /** 기본 PermissionEndpointJpaEntity 생성 */
    public static PermissionEndpointJpaEntity create() {
        return PermissionEndpointJpaEntity.of(
                DEFAULT_ENDPOINT_ID,
                DEFAULT_PERMISSION_ID,
                DEFAULT_SERVICE_NAME,
                DEFAULT_URL_PATTERN,
                HttpMethod.GET,
                DEFAULT_DESCRIPTION,
                DEFAULT_IS_PUBLIC,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 지정된 ID로 Entity 생성 */
    public static PermissionEndpointJpaEntity createWithId(Long permissionEndpointId) {
        return PermissionEndpointJpaEntity.of(
                permissionEndpointId,
                DEFAULT_PERMISSION_ID,
                DEFAULT_SERVICE_NAME,
                DEFAULT_URL_PATTERN,
                HttpMethod.GET,
                DEFAULT_DESCRIPTION,
                DEFAULT_IS_PUBLIC,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 지정된 URL 패턴과 HTTP 메서드로 생성 */
    public static PermissionEndpointJpaEntity createWithPatternAndMethod(
            String urlPattern, HttpMethod httpMethod) {
        return PermissionEndpointJpaEntity.of(
                DEFAULT_ENDPOINT_ID,
                DEFAULT_PERMISSION_ID,
                DEFAULT_SERVICE_NAME,
                urlPattern,
                httpMethod,
                urlPattern + " " + httpMethod.name(),
                DEFAULT_IS_PUBLIC,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 삭제된 Entity 생성 */
    public static PermissionEndpointJpaEntity createDeleted() {
        return PermissionEndpointJpaEntity.of(
                DEFAULT_ENDPOINT_ID,
                DEFAULT_PERMISSION_ID,
                DEFAULT_SERVICE_NAME,
                DEFAULT_URL_PATTERN,
                HttpMethod.GET,
                DEFAULT_DESCRIPTION,
                DEFAULT_IS_PUBLIC,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 공개 엔드포인트 Entity 생성 */
    public static PermissionEndpointJpaEntity createPublic() {
        return PermissionEndpointJpaEntity.of(
                DEFAULT_ENDPOINT_ID,
                DEFAULT_PERMISSION_ID,
                DEFAULT_SERVICE_NAME,
                DEFAULT_URL_PATTERN,
                HttpMethod.GET,
                "공개 엔드포인트",
                true,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 PermissionEndpoint ID 반환 */
    public static Long defaultPermissionEndpointId() {
        return DEFAULT_ENDPOINT_ID;
    }
}
