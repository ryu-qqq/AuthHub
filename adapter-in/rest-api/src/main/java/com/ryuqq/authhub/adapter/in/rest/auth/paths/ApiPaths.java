package com.ryuqq.authhub.adapter.in.rest.auth.paths;

/**
 * API 경로 상수 정의
 *
 * <p>모든 REST API 엔드포인트 경로를 중앙 집중 관리합니다. Controller에서 @RequestMapping에 사용됩니다.
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
public final class ApiPaths {

    public static final String API_VERSION = "/api/v1";

    private ApiPaths() {}

    /** 인증 관련 API 경로 */
    public static final class Auth {
        public static final String BASE = API_VERSION + "/auth";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String REFRESH = "/refresh";
        public static final String JWKS = "/.well-known/jwks.json";

        private Auth() {}
    }

    /** 테넌트 관련 API 경로 */
    public static final class Tenants {
        public static final String BASE = API_VERSION + "/tenants";
        public static final String BY_ID = "/{tenantId}";

        private Tenants() {}
    }

    /** 조직 관련 API 경로 */
    public static final class Organizations {
        public static final String BASE = API_VERSION + "/organizations";
        public static final String BY_ID = "/{organizationId}";
        public static final String BY_TENANT = "/tenants/{tenantId}/organizations";

        private Organizations() {}
    }

    /** 역할 관련 API 경로 */
    public static final class Roles {
        public static final String BASE = API_VERSION + "/roles";
        public static final String BY_ID = "/{roleId}";

        private Roles() {}
    }

    /** 사용자 관련 API 경로 */
    public static final class Users {
        public static final String BASE = API_VERSION + "/users";
        public static final String BY_ID = "/{userId}";
        public static final String ME = "/me";

        private Users() {}
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
        public static final String DOCS = "/v3/api-docs/**";
        public static final String SWAGGER_UI = "/swagger-ui/**";
        public static final String SWAGGER_UI_HTML = "/swagger-ui.html";

        private OpenApi() {}
    }
}
