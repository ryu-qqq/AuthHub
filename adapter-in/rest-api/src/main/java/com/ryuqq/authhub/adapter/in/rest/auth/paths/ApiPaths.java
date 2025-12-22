package com.ryuqq.authhub.adapter.in.rest.auth.paths;

/**
 * API 경로 상수 정의
 *
 * <p>모든 REST API 엔드포인트 경로를 중앙 집중 관리합니다. Controller에서 @RequestMapping에 사용됩니다.
 *
 * <p>경로 구조:
 *
 * <ul>
 *   <li>/api/v1/auth/* - 모든 AuthHub API
 *   <li>인증 API: login, logout, refresh, jwks (Public)
 *   <li>관리 API: tenants, organizations, users, roles, permissions (@PreAuthorize 권한 검사)
 * </ul>
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * @RestController
 * @RequestMapping(ApiPaths.Tenants.BASE)
 * public class TenantController { ... }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@SuppressWarnings("PMD.DataClass")
public final class ApiPaths {

    public static final String API_VERSION = "/api/v1";

    /** Auth 서비스 기본 경로 - Gateway 라우팅용 */
    public static final String AUTH_SERVICE_BASE = API_VERSION + "/auth";

    private ApiPaths() {}

    /**
     * 인증 관련 API 경로 (Public)
     *
     * <p>api.connectly.com에서 접근 가능한 공개 인증 API입니다.
     */
    public static final class Auth {
        public static final String BASE = AUTH_SERVICE_BASE;
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String REFRESH = "/refresh";
        public static final String JWKS = "/jwks";

        private Auth() {}
    }

    /**
     * 테넌트 관련 API 경로
     *
     * <p>테넌트 관리 API입니다. @PreAuthorize로 권한 검사를 수행합니다.
     */
    public static final class Tenants {
        public static final String BASE = AUTH_SERVICE_BASE + "/tenants";
        public static final String BY_ID = "/{tenantId}";

        private Tenants() {}
    }

    /**
     * 조직 관련 API 경로
     *
     * <p>조직 관리 API입니다. @PreAuthorize로 권한 검사를 수행합니다.
     */
    public static final class Organizations {
        public static final String BASE = AUTH_SERVICE_BASE + "/organizations";
        public static final String BY_ID = "/{organizationId}";
        public static final String BY_TENANT = "/tenants/{tenantId}/organizations";
        public static final String USERS = "/{organizationId}/users";

        private Organizations() {}
    }

    /**
     * 역할 관련 API 경로
     *
     * <p>역할 관리 API입니다. @PreAuthorize로 권한 검사를 수행합니다.
     */
    public static final class Roles {
        public static final String BASE = AUTH_SERVICE_BASE + "/roles";
        public static final String BY_ID = "/{roleId}";
        public static final String PERMISSIONS = "/{roleId}/permissions";
        public static final String USERS = "/{roleId}/users";

        private Roles() {}
    }

    /**
     * 사용자 관련 API 경로
     *
     * <p>사용자 관리 API입니다. @PreAuthorize로 권한 검사를 수행합니다.
     */
    public static final class Users {
        public static final String BASE = AUTH_SERVICE_BASE + "/users";
        public static final String BY_ID = "/{userId}";
        public static final String ME = "/me";
        public static final String ROLES = "/{userId}/roles";

        private Users() {}
    }

    /**
     * 권한 관련 API 경로
     *
     * <p>권한 관리 API입니다. @PreAuthorize로 권한 검사를 수행합니다.
     */
    public static final class Permissions {
        public static final String BASE = AUTH_SERVICE_BASE + "/permissions";
        public static final String BY_ID = "/{permissionId}";

        private Permissions() {}
    }

    /** 헬스체크 및 모니터링 API 경로 */
    public static final class Actuator {
        public static final String BASE = "/actuator";
        public static final String HEALTH = "/health";
        public static final String INFO = "/info";

        private Actuator() {}
    }

    /** OpenAPI (Swagger) 문서 경로 */
    public static final class OpenApi {
        public static final String DOCS = AUTH_SERVICE_BASE + "/api-docs/**";
        public static final String SWAGGER_UI = AUTH_SERVICE_BASE + "/swagger-ui/**";
        public static final String SWAGGER_UI_HTML = AUTH_SERVICE_BASE + "/swagger-ui.html";
        public static final String SWAGGER_REDIRECT = AUTH_SERVICE_BASE + "/swagger";

        private OpenApi() {}
    }

    /** REST Docs 문서 경로 (Gateway 라우팅용 /api/v1/auth prefix) */
    public static final class Docs {
        public static final String BASE = AUTH_SERVICE_BASE + "/docs";
        public static final String ALL = AUTH_SERVICE_BASE + "/docs/**";
        public static final String INDEX = AUTH_SERVICE_BASE + "/docs/index.html";

        private Docs() {}
    }

    /** OAuth2 경로 */
    public static final class OAuth2 {
        public static final String BASE = "/oauth2/**";
        public static final String LOGIN = "/login/oauth2/**";

        private OAuth2() {}
    }

    /** Health Check 경로 */
    public static final class Health {
        public static final String CHECK = API_VERSION + "/health";

        private Health() {}
    }
}
