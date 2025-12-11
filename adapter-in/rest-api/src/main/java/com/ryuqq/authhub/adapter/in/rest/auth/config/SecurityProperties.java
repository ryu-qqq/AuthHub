package com.ryuqq.authhub.adapter.in.rest.auth.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Security 설정 Properties
 *
 * <p>rest-api.yml의 public-endpoints와 cors 설정을 매핑합니다.
 *
 * <p>사용 예시:
 *
 * <pre>
 * api:
 *   public-endpoints:
 *     - method: POST
 *       pattern: /api/v1/auth/login
 *     - pattern: /actuator/**
 *   cors:
 *     allowed-origins:
 *       - http://localhost:3000
 * </pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "api")
public class SecurityProperties {

    private List<PublicEndpoint> publicEndpoints = new ArrayList<>();
    private List<AdminEndpoint> adminEndpoints = new ArrayList<>();
    private CorsProperties cors = new CorsProperties();

    public List<PublicEndpoint> getPublicEndpoints() {
        return publicEndpoints;
    }

    public void setPublicEndpoints(List<PublicEndpoint> publicEndpoints) {
        this.publicEndpoints = publicEndpoints;
    }

    public List<AdminEndpoint> getAdminEndpoints() {
        return adminEndpoints;
    }

    public void setAdminEndpoints(List<AdminEndpoint> adminEndpoints) {
        this.adminEndpoints = adminEndpoints;
    }

    public CorsProperties getCors() {
        return cors;
    }

    public void setCors(CorsProperties cors) {
        this.cors = cors;
    }

    /**
     * Public 엔드포인트 설정
     *
     * <p>인증 없이 접근 가능한 엔드포인트를 정의합니다.
     */
    public static class PublicEndpoint {
        private String method;
        private String pattern;

        public PublicEndpoint() {}

        public PublicEndpoint(String method, String pattern) {
            this.method = method;
            this.pattern = pattern;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        /**
         * HTTP 메서드가 지정되었는지 확인
         *
         * @return 메서드가 지정되었으면 true
         */
        public boolean hasMethod() {
            return method != null && !method.isBlank();
        }
    }

    /**
     * Admin 엔드포인트 설정
     *
     * <p>관리자 권한이 필요한 엔드포인트를 정의합니다.
     */
    public static class AdminEndpoint {
        private String method;
        private String pattern;
        private String role = "ADMIN";

        public AdminEndpoint() {}

        public AdminEndpoint(String method, String pattern, String role) {
            this.method = method;
            this.pattern = pattern;
            this.role = role;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        /**
         * HTTP 메서드가 지정되었는지 확인
         *
         * @return 메서드가 지정되었으면 true
         */
        public boolean hasMethod() {
            return method != null && !method.isBlank();
        }
    }

    /** CORS 설정 */
    public static class CorsProperties {
        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedMethods = new ArrayList<>();
        private List<String> allowedHeaders = new ArrayList<>();
        private List<String> exposedHeaders = new ArrayList<>();
        private boolean allowCredentials;

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public List<String> getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(List<String> allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public List<String> getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(List<String> allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public List<String> getExposedHeaders() {
            return exposedHeaders;
        }

        public void setExposedHeaders(List<String> exposedHeaders) {
            this.exposedHeaders = exposedHeaders;
        }

        public boolean isAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }
    }
}
