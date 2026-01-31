package com.ryuqq.authhub.adapter.in.rest.common;

/**
 * ApiVersionPaths - API 버전 및 공통 경로 상수
 *
 * <p>모든 도메인별 ApiEndpoints에서 공통으로 사용하는 기본 경로를 정의합니다.
 *
 * <p><strong>경로 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth  (AUTH_SERVICE_BASE)
 *   ├── /tenants      (TenantApiEndpoints)
 *   ├── /users        (UserApiEndpoints)
 *   ├── /roles        (RoleApiEndpoints)
 *   ├── /permissions  (PermissionApiEndpoints)
 *   └── ...
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@SuppressWarnings("PMD.DataClass")
public final class ApiVersionPaths {

    /** API 버전 */
    public static final String API_VERSION = "/api/v1";

    /** Auth 서비스 기본 경로 */
    public static final String AUTH_SERVICE_BASE = API_VERSION + "/auth";

    /** Actuator 경로 */
    public static final String ACTUATOR_BASE = "/actuator";

    /** Health Check 경로 */
    public static final String HEALTH = API_VERSION + "/health";

    private ApiVersionPaths() {}

    /** OpenAPI (Swagger) 문서 경로 */
    @SuppressWarnings("PMD.DataClass")
    public static final class OpenApi {
        public static final String DOCS = AUTH_SERVICE_BASE + "/api-docs/**";
        public static final String SWAGGER_UI = AUTH_SERVICE_BASE + "/swagger-ui/**";
        public static final String SWAGGER_UI_HTML = AUTH_SERVICE_BASE + "/swagger-ui.html";
        public static final String SWAGGER_REDIRECT = AUTH_SERVICE_BASE + "/swagger";

        private OpenApi() {}
    }

    /** REST Docs 문서 경로 */
    public static final class Docs {
        public static final String BASE = AUTH_SERVICE_BASE + "/docs";
        public static final String ALL = AUTH_SERVICE_BASE + "/docs/**";

        private Docs() {}
    }

    /** OAuth2 경로 */
    public static final class OAuth2 {
        public static final String BASE = "/oauth2/**";
        public static final String LOGIN = "/login/oauth2/**";

        private OAuth2() {}
    }
}
