package com.ryuqq.authhub.sdk.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AuthHub Exception Tests")
class AuthHubExceptionTest {

    @Nested
    @DisplayName("AuthHubException")
    class AuthHubExceptionBaseTest {

        @Test
        @DisplayName("기본 생성자로 예외 생성")
        void shouldCreateWithBasicConstructor() {
            AuthHubException exception = new AuthHubException(500, "SERVER_ERROR", "내부 서버 오류");

            assertThat(exception.getStatusCode()).isEqualTo(500);
            assertThat(exception.getErrorCode()).isEqualTo("SERVER_ERROR");
            assertThat(exception.getErrorMessage()).isEqualTo("내부 서버 오류");
            assertThat(exception.getMessage()).isEqualTo("[500] SERVER_ERROR: 내부 서버 오류");
        }

        @Test
        @DisplayName("원인 포함 생성자로 예외 생성")
        void shouldCreateWithCause() {
            RuntimeException cause = new RuntimeException("원인 예외");
            AuthHubException exception =
                    new AuthHubException(500, "SERVER_ERROR", "내부 서버 오류", cause);

            assertThat(exception.getStatusCode()).isEqualTo(500);
            assertThat(exception.getErrorCode()).isEqualTo("SERVER_ERROR");
            assertThat(exception.getErrorMessage()).isEqualTo("내부 서버 오류");
            assertThat(exception.getCause()).isEqualTo(cause);
        }

        @Test
        @DisplayName("메시지 포맷 확인")
        void shouldFormatMessageCorrectly() {
            AuthHubException exception = new AuthHubException(404, "NOT_FOUND", "리소스를 찾을 수 없습니다");

            assertThat(exception.getMessage()).contains("[404]");
            assertThat(exception.getMessage()).contains("NOT_FOUND");
            assertThat(exception.getMessage()).contains("리소스를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("AuthHubBadRequestException")
    class AuthHubBadRequestExceptionTest {

        @Test
        @DisplayName("400 상태 코드로 생성")
        void shouldCreateWith400StatusCode() {
            AuthHubBadRequestException exception =
                    new AuthHubBadRequestException("INVALID_PARAM", "잘못된 파라미터");

            assertThat(exception.getStatusCode()).isEqualTo(400);
            assertThat(exception.getErrorCode()).isEqualTo("INVALID_PARAM");
            assertThat(exception.getErrorMessage()).isEqualTo("잘못된 파라미터");
        }

        @Test
        @DisplayName("원인 포함 생성")
        void shouldCreateWithCause() {
            IllegalArgumentException cause = new IllegalArgumentException("원인");
            AuthHubBadRequestException exception =
                    new AuthHubBadRequestException("INVALID_PARAM", "잘못된 파라미터", cause);

            assertThat(exception.getStatusCode()).isEqualTo(400);
            assertThat(exception.getCause()).isEqualTo(cause);
        }

        @Test
        @DisplayName("AuthHubException 상속 확인")
        void shouldExtendAuthHubException() {
            AuthHubBadRequestException exception =
                    new AuthHubBadRequestException("INVALID", "invalid");

            assertThat(exception).isInstanceOf(AuthHubException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("AuthHubUnauthorizedException")
    class AuthHubUnauthorizedExceptionTest {

        @Test
        @DisplayName("401 상태 코드로 생성")
        void shouldCreateWith401StatusCode() {
            AuthHubUnauthorizedException exception =
                    new AuthHubUnauthorizedException("INVALID_TOKEN", "유효하지 않은 토큰");

            assertThat(exception.getStatusCode()).isEqualTo(401);
            assertThat(exception.getErrorCode()).isEqualTo("INVALID_TOKEN");
            assertThat(exception.getErrorMessage()).isEqualTo("유효하지 않은 토큰");
        }

        @Test
        @DisplayName("원인 포함 생성")
        void shouldCreateWithCause() {
            SecurityException cause = new SecurityException("보안 오류");
            AuthHubUnauthorizedException exception =
                    new AuthHubUnauthorizedException("INVALID_TOKEN", "유효하지 않은 토큰", cause);

            assertThat(exception.getStatusCode()).isEqualTo(401);
            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }

    @Nested
    @DisplayName("AuthHubForbiddenException")
    class AuthHubForbiddenExceptionTest {

        @Test
        @DisplayName("403 상태 코드로 생성")
        void shouldCreateWith403StatusCode() {
            AuthHubForbiddenException exception =
                    new AuthHubForbiddenException("ACCESS_DENIED", "접근 권한이 없습니다");

            assertThat(exception.getStatusCode()).isEqualTo(403);
            assertThat(exception.getErrorCode()).isEqualTo("ACCESS_DENIED");
            assertThat(exception.getErrorMessage()).isEqualTo("접근 권한이 없습니다");
        }

        @Test
        @DisplayName("원인 포함 생성")
        void shouldCreateWithCause() {
            SecurityException cause = new SecurityException("권한 없음");
            AuthHubForbiddenException exception =
                    new AuthHubForbiddenException("ACCESS_DENIED", "접근 권한이 없습니다", cause);

            assertThat(exception.getStatusCode()).isEqualTo(403);
            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }

    @Nested
    @DisplayName("AuthHubNotFoundException")
    class AuthHubNotFoundExceptionTest {

        @Test
        @DisplayName("404 상태 코드로 생성")
        void shouldCreateWith404StatusCode() {
            AuthHubNotFoundException exception =
                    new AuthHubNotFoundException("USER_NOT_FOUND", "사용자를 찾을 수 없습니다");

            assertThat(exception.getStatusCode()).isEqualTo(404);
            assertThat(exception.getErrorCode()).isEqualTo("USER_NOT_FOUND");
            assertThat(exception.getErrorMessage()).isEqualTo("사용자를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("원인 포함 생성")
        void shouldCreateWithCause() {
            RuntimeException cause = new RuntimeException("not found");
            AuthHubNotFoundException exception =
                    new AuthHubNotFoundException("NOT_FOUND", "리소스를 찾을 수 없습니다", cause);

            assertThat(exception.getStatusCode()).isEqualTo(404);
            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }

    @Nested
    @DisplayName("AuthHubServerException")
    class AuthHubServerExceptionTest {

        @Test
        @DisplayName("5xx 상태 코드로 생성")
        void shouldCreateWith5xxStatusCode() {
            AuthHubServerException exception =
                    new AuthHubServerException(500, "INTERNAL_ERROR", "내부 서버 오류가 발생했습니다");

            assertThat(exception.getStatusCode()).isEqualTo(500);
            assertThat(exception.getErrorCode()).isEqualTo("INTERNAL_ERROR");
            assertThat(exception.getErrorMessage()).isEqualTo("내부 서버 오류가 발생했습니다");
        }

        @Test
        @DisplayName("502 Bad Gateway로 생성")
        void shouldCreateWith502StatusCode() {
            AuthHubServerException exception =
                    new AuthHubServerException(502, "BAD_GATEWAY", "업스트림 서버 오류");

            assertThat(exception.getStatusCode()).isEqualTo(502);
            assertThat(exception.getErrorCode()).isEqualTo("BAD_GATEWAY");
        }

        @Test
        @DisplayName("원인 포함 생성")
        void shouldCreateWithCause() {
            RuntimeException cause = new RuntimeException("서버 오류");
            AuthHubServerException exception =
                    new AuthHubServerException(500, "INTERNAL_ERROR", "내부 서버 오류", cause);

            assertThat(exception.getStatusCode()).isEqualTo(500);
            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }

    @Nested
    @DisplayName("Exception Hierarchy")
    class ExceptionHierarchyTest {

        @Test
        @DisplayName("모든 예외가 AuthHubException 상속")
        void allExceptionsShouldExtendAuthHubException() {
            assertThat(new AuthHubBadRequestException("E", "M"))
                    .isInstanceOf(AuthHubException.class);
            assertThat(new AuthHubUnauthorizedException("E", "M"))
                    .isInstanceOf(AuthHubException.class);
            assertThat(new AuthHubForbiddenException("E", "M"))
                    .isInstanceOf(AuthHubException.class);
            assertThat(new AuthHubNotFoundException("E", "M")).isInstanceOf(AuthHubException.class);
            assertThat(new AuthHubServerException(500, "E", "M"))
                    .isInstanceOf(AuthHubException.class);
        }

        @Test
        @DisplayName("모든 예외가 RuntimeException 상속")
        void allExceptionsShouldExtendRuntimeException() {
            assertThat(new AuthHubBadRequestException("E", "M"))
                    .isInstanceOf(RuntimeException.class);
            assertThat(new AuthHubUnauthorizedException("E", "M"))
                    .isInstanceOf(RuntimeException.class);
            assertThat(new AuthHubForbiddenException("E", "M"))
                    .isInstanceOf(RuntimeException.class);
            assertThat(new AuthHubNotFoundException("E", "M")).isInstanceOf(RuntimeException.class);
            assertThat(new AuthHubServerException(500, "E", "M"))
                    .isInstanceOf(RuntimeException.class);
        }
    }
}
