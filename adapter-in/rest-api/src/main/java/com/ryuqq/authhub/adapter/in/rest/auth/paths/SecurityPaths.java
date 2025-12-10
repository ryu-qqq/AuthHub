package com.ryuqq.authhub.adapter.in.rest.auth.paths;

import java.util.List;

/**
 * 보안 경로 분류 상수 정의
 *
 * <p>Public, Admin 등 접근 권한별 경로를 정의합니다. SecurityConfig에서 참조하여 권한 설정에 사용됩니다.
 *
 * <p>경로 분류:
 *
 * <ul>
 *   <li>PUBLIC: 인증 불필요 (로그인, JWKS, 헬스체크)
 *   <li>SUPER_ADMIN_ONLY: SUPER_ADMIN 전용
 *   <li>TENANT_ADMIN_OR_HIGHER: TENANT_ADMIN 이상
 *   <li>AUTHENTICATED: 인증된 사용자 (기본값)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@SuppressWarnings("PMD.MissingStaticMethodInNonInstantiatableClass")
public final class SecurityPaths {

    private SecurityPaths() {}

    /**
     * 인증 불필요 경로 (Public)
     *
     * <p>Gateway에서 JWT 검증을 스킵하는 경로입니다. X-User-Id 헤더 없이 접근 가능합니다.
     */
    public static final class Public {

        /** Public 경로 목록 */
        public static final List<String> PATTERNS =
                List.of(
                        // 인증 API
                        ApiPaths.Auth.BASE + ApiPaths.Auth.LOGIN,
                        ApiPaths.Auth.BASE + ApiPaths.Auth.JWKS,

                        // 헬스체크
                        ApiPaths.Actuator.BASE + "/**",

                        // OpenAPI 문서
                        ApiPaths.OpenApi.DOCS,
                        ApiPaths.OpenApi.SWAGGER_UI,
                        ApiPaths.OpenApi.SWAGGER_UI_HTML);

        private Public() {}
    }

    /**
     * SUPER_ADMIN 전용 경로
     *
     * <p>시스템 전체 관리를 위한 경로입니다. SUPER_ADMIN 역할만 접근 가능합니다.
     */
    public static final class SuperAdminOnly {

        /** SUPER_ADMIN 전용 경로 목록 */
        public static final List<String> PATTERNS =
                List.of(
                        // 테넌트 생성/삭제는 SUPER_ADMIN만 가능
                        // (실제 권한 검증은 @PreAuthorize에서 수행)
                        );

        private SuperAdminOnly() {}
    }

    /**
     * TENANT_ADMIN 이상 접근 가능 경로
     *
     * <p>테넌트 관리를 위한 경로입니다. TENANT_ADMIN, SUPER_ADMIN 역할이 접근 가능합니다.
     */
    public static final class TenantAdminOrHigher {

        /** TENANT_ADMIN 이상 접근 가능 경로 목록 */
        public static final List<String> PATTERNS =
                List.of(ApiPaths.Tenants.BASE + "/**", ApiPaths.Organizations.BASE + "/**");

        private TenantAdminOrHigher() {}
    }

    /**
     * 헤더 상수
     *
     * <p>Gateway에서 전달하는 인증 정보 헤더입니다.
     */
    public static final class Headers {

        /** 사용자 ID 헤더 - Gateway에서 JWT userId 클레임 추출 */
        public static final String USER_ID = "X-User-Id";

        /** 테넌트 ID 헤더 - Gateway에서 JWT tenantId 클레임 추출 */
        public static final String TENANT_ID = "X-Tenant-Id";

        /** 역할 헤더 - JSON 배열 형식 (예: ["ROLE_ADMIN", "ROLE_USER"]) */
        public static final String ROLES = "X-Roles";

        /** 추적 ID 헤더 - 분산 추적용 */
        public static final String TRACE_ID = "X-Trace-Id";

        private Headers() {}
    }
}
