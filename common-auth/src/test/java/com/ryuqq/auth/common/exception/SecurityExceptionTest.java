package com.ryuqq.auth.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Security Exceptions")
class SecurityExceptionTest {

    @Nested
    @DisplayName("SecurityErrorCode")
    class SecurityErrorCodeTest {

        @Test
        @DisplayName("인증 오류 코드")
        void authenticationErrorCodes() {
            assertThat(SecurityErrorCode.UNAUTHENTICATED.getCode()).isEqualTo("AUTH_001");
            assertThat(SecurityErrorCode.UNAUTHENTICATED.getHttpStatus()).isEqualTo(401);
            assertThat(SecurityErrorCode.UNAUTHENTICATED.isAuthenticationError()).isTrue();
            assertThat(SecurityErrorCode.UNAUTHENTICATED.isAuthorizationError()).isFalse();
        }

        @Test
        @DisplayName("인가 오류 코드")
        void authorizationErrorCodes() {
            assertThat(SecurityErrorCode.ACCESS_DENIED.getCode()).isEqualTo("AUTHZ_001");
            assertThat(SecurityErrorCode.ACCESS_DENIED.getHttpStatus()).isEqualTo(403);
            assertThat(SecurityErrorCode.ACCESS_DENIED.isAuthenticationError()).isFalse();
            assertThat(SecurityErrorCode.ACCESS_DENIED.isAuthorizationError()).isTrue();
        }

        @Test
        @DisplayName("토큰 오류 코드")
        void tokenErrorCodes() {
            assertThat(SecurityErrorCode.TOKEN_EXPIRED.getCode()).isEqualTo("TOKEN_003");
            assertThat(SecurityErrorCode.TOKEN_EXPIRED.getDefaultMessage())
                    .isEqualTo("Token has expired");
        }

        @Test
        @DisplayName("모든 에러 코드 확인")
        void allErrorCodes() {
            for (SecurityErrorCode code : SecurityErrorCode.values()) {
                assertThat(code.getCode()).isNotBlank();
                assertThat(code.getDefaultMessage()).isNotBlank();
                assertThat(code.getHttpStatus()).isIn(401, 403);
            }
        }
    }

    @Nested
    @DisplayName("AuthenticationException")
    class AuthenticationExceptionTest {

        @Test
        @DisplayName("에러 코드로 생성")
        void createWithErrorCode() {
            AuthenticationException ex =
                    new AuthenticationException(SecurityErrorCode.UNAUTHENTICATED);

            assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.UNAUTHENTICATED);
            assertThat(ex.getCode()).isEqualTo("AUTH_001");
            assertThat(ex.getHttpStatus()).isEqualTo(401);
            assertThat(ex.getMessage()).isEqualTo("Authentication required");
            assertThat(ex.isAuthenticationError()).isTrue();
        }

        @Test
        @DisplayName("커스텀 메시지로 생성")
        void createWithCustomMessage() {
            AuthenticationException ex =
                    new AuthenticationException(
                            SecurityErrorCode.TOKEN_EXPIRED, "Token expired at 2024-01-01");

            assertThat(ex.getMessage()).isEqualTo("Token expired at 2024-01-01");
        }

        @Test
        @DisplayName("원인 예외로 생성")
        void createWithCause() {
            RuntimeException cause = new RuntimeException("Original error");
            AuthenticationException ex =
                    new AuthenticationException(SecurityErrorCode.TOKEN_INVALID, cause);

            assertThat(ex.getCause()).isEqualTo(cause);
        }

        @Test
        @DisplayName("메시지와 원인 예외로 생성")
        void createWithMessageAndCause() {
            RuntimeException cause = new RuntimeException("Original error");
            AuthenticationException ex =
                    new AuthenticationException(
                            SecurityErrorCode.TOKEN_INVALID, "Custom message", cause);

            assertThat(ex.getMessage()).isEqualTo("Custom message");
            assertThat(ex.getCause()).isEqualTo(cause);
        }

        @Test
        @DisplayName("팩토리 메서드")
        void factoryMethods() {
            assertThat(AuthenticationException.unauthenticated().getErrorCode())
                    .isEqualTo(SecurityErrorCode.UNAUTHENTICATED);

            assertThat(AuthenticationException.tokenMissing().getErrorCode())
                    .isEqualTo(SecurityErrorCode.TOKEN_MISSING);

            assertThat(AuthenticationException.tokenExpired().getErrorCode())
                    .isEqualTo(SecurityErrorCode.TOKEN_EXPIRED);

            assertThat(AuthenticationException.tokenInvalid().getErrorCode())
                    .isEqualTo(SecurityErrorCode.TOKEN_INVALID);

            assertThat(AuthenticationException.invalidCredentials().getErrorCode())
                    .isEqualTo(SecurityErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Nested
    @DisplayName("AuthorizationException")
    class AuthorizationExceptionTest {

        @Test
        @DisplayName("에러 코드로 생성")
        void createWithErrorCode() {
            AuthorizationException ex = new AuthorizationException(SecurityErrorCode.ACCESS_DENIED);

            assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.ACCESS_DENIED);
            assertThat(ex.getCode()).isEqualTo("AUTHZ_001");
            assertThat(ex.getHttpStatus()).isEqualTo(403);
            assertThat(ex.isAuthorizationError()).isTrue();
        }

        @Test
        @DisplayName("권한과 리소스 정보 포함")
        void createWithPermissionAndResource() {
            AuthorizationException ex =
                    new AuthorizationException(
                            SecurityErrorCode.INSUFFICIENT_PERMISSION, "user:delete", "user-123");

            assertThat(ex.getRequiredPermission()).isEqualTo("user:delete");
            assertThat(ex.getTargetResource()).isEqualTo("user-123");
            assertThat(ex.getMessage()).contains("user:delete");
            assertThat(ex.getMessage()).contains("user-123");
        }

        @Test
        @DisplayName("원인 예외로 생성")
        void createWithCause() {
            RuntimeException cause = new RuntimeException("Original error");
            AuthorizationException ex =
                    new AuthorizationException(SecurityErrorCode.ACCESS_DENIED, cause);

            assertThat(ex.getCause()).isEqualTo(cause);
        }

        @Test
        @DisplayName("팩토리 메서드 - accessDenied")
        void accessDeniedFactory() {
            AuthorizationException ex = AuthorizationException.accessDenied();

            assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.ACCESS_DENIED);
        }

        @Test
        @DisplayName("팩토리 메서드 - accessDenied with message")
        void accessDeniedWithMessage() {
            AuthorizationException ex = AuthorizationException.accessDenied("Custom message");

            assertThat(ex.getMessage()).isEqualTo("Custom message");
        }

        @Test
        @DisplayName("팩토리 메서드 - insufficientPermission")
        void insufficientPermissionFactory() {
            AuthorizationException ex =
                    AuthorizationException.insufficientPermission("user:delete");

            assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.INSUFFICIENT_PERMISSION);
            assertThat(ex.getRequiredPermission()).isEqualTo("user:delete");
        }

        @Test
        @DisplayName("팩토리 메서드 - resourceForbidden")
        void resourceForbiddenFactory() {
            AuthorizationException ex = AuthorizationException.resourceForbidden("resource-123");

            assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.RESOURCE_FORBIDDEN);
            assertThat(ex.getTargetResource()).isEqualTo("resource-123");
        }

        @Test
        @DisplayName("팩토리 메서드 - tenantAccessDenied")
        void tenantAccessDeniedFactory() {
            AuthorizationException ex = AuthorizationException.tenantAccessDenied("tenant-123");

            assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.TENANT_ACCESS_DENIED);
            assertThat(ex.getMessage()).contains("tenant-123");
        }

        @Test
        @DisplayName("팩토리 메서드 - organizationAccessDenied")
        void organizationAccessDeniedFactory() {
            AuthorizationException ex = AuthorizationException.organizationAccessDenied("org-123");

            assertThat(ex.getErrorCode()).isEqualTo(SecurityErrorCode.ORGANIZATION_ACCESS_DENIED);
            assertThat(ex.getMessage()).contains("org-123");
        }
    }
}
