package com.ryuqq.auth.common.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("SecurityHeaders")
class SecurityHeadersTest {

    @Nested
    @DisplayName("사용자 컨텍스트 헤더 상수")
    class UserContextHeaders {

        @Test
        @DisplayName("USER_ID 헤더")
        void userIdHeader() {
            assertThat(SecurityHeaders.USER_ID).isEqualTo("X-User-Id");
        }

        @Test
        @DisplayName("TENANT_ID 헤더")
        void tenantIdHeader() {
            assertThat(SecurityHeaders.TENANT_ID).isEqualTo("X-Tenant-Id");
        }

        @Test
        @DisplayName("ORGANIZATION_ID 헤더")
        void organizationIdHeader() {
            assertThat(SecurityHeaders.ORGANIZATION_ID).isEqualTo("X-Organization-Id");
        }

        @Test
        @DisplayName("ROLES 헤더")
        void rolesHeader() {
            assertThat(SecurityHeaders.ROLES).isEqualTo("X-User-Roles");
        }

        @Test
        @DisplayName("PERMISSIONS 헤더")
        void permissionsHeader() {
            assertThat(SecurityHeaders.PERMISSIONS).isEqualTo("X-User-Permissions");
        }

        @Test
        @DisplayName("USER_EMAIL 헤더")
        void userEmailHeader() {
            assertThat(SecurityHeaders.USER_EMAIL).isEqualTo("X-User-Email");
        }
    }

    @Nested
    @DisplayName("서비스 인증 헤더 상수")
    class ServiceAuthHeaders {

        @Test
        @DisplayName("SERVICE_TOKEN 헤더")
        void serviceTokenHeader() {
            assertThat(SecurityHeaders.SERVICE_TOKEN).isEqualTo("X-Service-Token");
        }

        @Test
        @DisplayName("SERVICE_NAME 헤더")
        void serviceNameHeader() {
            assertThat(SecurityHeaders.SERVICE_NAME).isEqualTo("X-Service-Name");
        }
    }

    @Nested
    @DisplayName("서비스간 통신 확장 헤더 상수")
    class ServiceExtensionHeaders {

        @Test
        @DisplayName("ORIGINAL_USER_ID 헤더")
        void originalUserIdHeader() {
            assertThat(SecurityHeaders.ORIGINAL_USER_ID).isEqualTo("X-Original-User-Id");
        }

        @Test
        @DisplayName("ORIGINAL_TENANT_ID 헤더")
        void originalTenantIdHeader() {
            assertThat(SecurityHeaders.ORIGINAL_TENANT_ID).isEqualTo("X-Original-Tenant-Id");
        }

        @Test
        @DisplayName("ORIGINAL_ORGANIZATION_ID 헤더")
        void originalOrganizationIdHeader() {
            assertThat(SecurityHeaders.ORIGINAL_ORGANIZATION_ID)
                    .isEqualTo("X-Original-Organization-Id");
        }
    }

    @Nested
    @DisplayName("추적 헤더 상수")
    class TraceHeaders {

        @Test
        @DisplayName("CORRELATION_ID 헤더")
        void correlationIdHeader() {
            assertThat(SecurityHeaders.CORRELATION_ID).isEqualTo("X-Correlation-Id");
        }

        @Test
        @DisplayName("REQUEST_SOURCE 헤더")
        void requestSourceHeader() {
            assertThat(SecurityHeaders.REQUEST_SOURCE).isEqualTo("X-Request-Source");
        }

        @Test
        @DisplayName("REQUEST_ID 헤더")
        void requestIdHeader() {
            assertThat(SecurityHeaders.REQUEST_ID).isEqualTo("X-Request-Id");
        }
    }

    @Nested
    @DisplayName("Gateway 내부 헤더 상수")
    class GatewayHeaders {

        @Test
        @DisplayName("AUTHENTICATED 헤더")
        void authenticatedHeader() {
            assertThat(SecurityHeaders.AUTHENTICATED).isEqualTo("X-Authenticated");
        }

        @Test
        @DisplayName("AUTH_TYPE 헤더")
        void authTypeHeader() {
            assertThat(SecurityHeaders.AUTH_TYPE).isEqualTo("X-Auth-Type");
        }
    }

    @Nested
    @DisplayName("isSecurityHeader")
    class IsSecurityHeader {

        @Test
        @DisplayName("X-User- 접두사 헤더는 보안 헤더")
        void userPrefixedHeadersAreSecurityHeaders() {
            assertThat(SecurityHeaders.isSecurityHeader("X-User-Id")).isTrue();
            assertThat(SecurityHeaders.isSecurityHeader("X-User-Roles")).isTrue();
            assertThat(SecurityHeaders.isSecurityHeader("X-User-Custom")).isTrue();
        }

        @Test
        @DisplayName("X-Service- 접두사 헤더는 보안 헤더")
        void servicePrefixedHeadersAreSecurityHeaders() {
            assertThat(SecurityHeaders.isSecurityHeader("X-Service-Token")).isTrue();
            assertThat(SecurityHeaders.isSecurityHeader("X-Service-Name")).isTrue();
        }

        @Test
        @DisplayName("X-Original- 접두사 헤더는 보안 헤더")
        void originalPrefixedHeadersAreSecurityHeaders() {
            assertThat(SecurityHeaders.isSecurityHeader("X-Original-User-Id")).isTrue();
            assertThat(SecurityHeaders.isSecurityHeader("X-Original-Tenant-Id")).isTrue();
        }

        @Test
        @DisplayName("추적 헤더는 보안 헤더")
        void traceHeadersAreSecurityHeaders() {
            assertThat(SecurityHeaders.isSecurityHeader(SecurityHeaders.CORRELATION_ID)).isTrue();
            assertThat(SecurityHeaders.isSecurityHeader(SecurityHeaders.REQUEST_SOURCE)).isTrue();
            assertThat(SecurityHeaders.isSecurityHeader(SecurityHeaders.REQUEST_ID)).isTrue();
        }

        @Test
        @DisplayName("Gateway 헤더는 보안 헤더")
        void gatewayHeadersAreSecurityHeaders() {
            assertThat(SecurityHeaders.isSecurityHeader(SecurityHeaders.AUTHENTICATED)).isTrue();
            assertThat(SecurityHeaders.isSecurityHeader(SecurityHeaders.AUTH_TYPE)).isTrue();
        }

        @Test
        @DisplayName("일반 헤더는 보안 헤더 아님")
        void regularHeadersAreNotSecurityHeaders() {
            assertThat(SecurityHeaders.isSecurityHeader("Content-Type")).isFalse();
            assertThat(SecurityHeaders.isSecurityHeader("Authorization")).isFalse();
            assertThat(SecurityHeaders.isSecurityHeader("X-Custom-Header")).isFalse();
        }

        @Test
        @DisplayName("null은 보안 헤더 아님")
        void nullIsNotSecurityHeader() {
            assertThat(SecurityHeaders.isSecurityHeader(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("isSensitiveHeader")
    class IsSensitiveHeader {

        @Test
        @DisplayName("SERVICE_TOKEN은 민감한 헤더")
        void serviceTokenIsSensitive() {
            assertThat(SecurityHeaders.isSensitiveHeader(SecurityHeaders.SERVICE_TOKEN)).isTrue();
        }

        @Test
        @DisplayName("다른 보안 헤더는 민감하지 않음")
        void otherSecurityHeadersAreNotSensitive() {
            assertThat(SecurityHeaders.isSensitiveHeader(SecurityHeaders.USER_ID)).isFalse();
            assertThat(SecurityHeaders.isSensitiveHeader(SecurityHeaders.ROLES)).isFalse();
            assertThat(SecurityHeaders.isSensitiveHeader(SecurityHeaders.SERVICE_NAME)).isFalse();
        }

        @Test
        @DisplayName("일반 헤더는 민감하지 않음")
        void regularHeadersAreNotSensitive() {
            assertThat(SecurityHeaders.isSensitiveHeader("Authorization")).isFalse();
            assertThat(SecurityHeaders.isSensitiveHeader(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("유틸리티 클래스")
    class UtilityClass {

        @Test
        @DisplayName("인스턴스화 불가")
        void cannotInstantiate() throws Exception {
            Constructor<SecurityHeaders> constructor =
                    SecurityHeaders.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(AssertionError.class);
        }
    }
}
