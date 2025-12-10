package com.ryuqq.authhub.domain.auth.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * AuthErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("AuthErrorCode 테스트")
class AuthErrorCodeTest {

    @Nested
    @DisplayName("INVALID_CREDENTIALS")
    class InvalidCredentialsTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(AuthErrorCode.INVALID_CREDENTIALS.getCode()).isEqualTo("AUTH-001");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(AuthErrorCode.INVALID_CREDENTIALS.getHttpStatus()).isEqualTo(401);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(AuthErrorCode.INVALID_CREDENTIALS.getMessage())
                    .isEqualTo("Invalid credentials");
        }
    }

    @Nested
    @DisplayName("INVALID_REFRESH_TOKEN")
    class InvalidRefreshTokenTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(AuthErrorCode.INVALID_REFRESH_TOKEN.getCode()).isEqualTo("AUTH-002");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(AuthErrorCode.INVALID_REFRESH_TOKEN.getHttpStatus()).isEqualTo(401);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage())
                    .isEqualTo("Invalid refresh token");
        }
    }

    @Nested
    @DisplayName("EXPIRED_REFRESH_TOKEN")
    class ExpiredRefreshTokenTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(AuthErrorCode.EXPIRED_REFRESH_TOKEN.getCode()).isEqualTo("AUTH-003");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(AuthErrorCode.EXPIRED_REFRESH_TOKEN.getHttpStatus()).isEqualTo(401);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(AuthErrorCode.EXPIRED_REFRESH_TOKEN.getMessage())
                    .isEqualTo("Refresh token has expired");
        }
    }

    @Nested
    @DisplayName("INVALID_ACCESS_TOKEN")
    class InvalidAccessTokenTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(AuthErrorCode.INVALID_ACCESS_TOKEN.getCode()).isEqualTo("AUTH-004");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(AuthErrorCode.INVALID_ACCESS_TOKEN.getHttpStatus()).isEqualTo(401);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(AuthErrorCode.INVALID_ACCESS_TOKEN.getMessage())
                    .isEqualTo("Invalid access token");
        }
    }

    @Nested
    @DisplayName("EXPIRED_ACCESS_TOKEN")
    class ExpiredAccessTokenTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(AuthErrorCode.EXPIRED_ACCESS_TOKEN.getCode()).isEqualTo("AUTH-005");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(AuthErrorCode.EXPIRED_ACCESS_TOKEN.getHttpStatus()).isEqualTo(401);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(AuthErrorCode.EXPIRED_ACCESS_TOKEN.getMessage())
                    .isEqualTo("Access token has expired");
        }
    }

    @Nested
    @DisplayName("UNAUTHORIZED")
    class UnauthorizedTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(AuthErrorCode.UNAUTHORIZED.getCode()).isEqualTo("AUTH-006");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(AuthErrorCode.UNAUTHORIZED.getHttpStatus()).isEqualTo(401);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(AuthErrorCode.UNAUTHORIZED.getMessage()).isEqualTo("Unauthorized access");
        }
    }

    @Nested
    @DisplayName("FORBIDDEN")
    class ForbiddenTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(AuthErrorCode.FORBIDDEN.getCode()).isEqualTo("AUTH-007");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(AuthErrorCode.FORBIDDEN.getHttpStatus()).isEqualTo(403);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(AuthErrorCode.FORBIDDEN.getMessage()).isEqualTo("Access forbidden");
        }
    }

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface() {
            assertThat(AuthErrorCode.INVALID_CREDENTIALS).isInstanceOf(ErrorCode.class);
        }

        @Test
        @DisplayName("모든 에러 코드가 존재한다")
        void shouldHaveAllErrorCodes() {
            AuthErrorCode[] values = AuthErrorCode.values();

            assertThat(values).hasSize(7);
            assertThat(values)
                    .containsExactly(
                            AuthErrorCode.INVALID_CREDENTIALS,
                            AuthErrorCode.INVALID_REFRESH_TOKEN,
                            AuthErrorCode.EXPIRED_REFRESH_TOKEN,
                            AuthErrorCode.INVALID_ACCESS_TOKEN,
                            AuthErrorCode.EXPIRED_ACCESS_TOKEN,
                            AuthErrorCode.UNAUTHORIZED,
                            AuthErrorCode.FORBIDDEN);
        }

        @Test
        @DisplayName("모든 에러 코드는 AUTH- 접두사를 가진다")
        void shouldHaveAuthPrefix() {
            for (AuthErrorCode errorCode : AuthErrorCode.values()) {
                assertThat(errorCode.getCode()).startsWith("AUTH-");
            }
        }

        @Test
        @DisplayName("모든 에러 코드는 4xx HTTP 상태 코드를 가진다")
        void shouldHave4xxHttpStatus() {
            for (AuthErrorCode errorCode : AuthErrorCode.values()) {
                assertThat(errorCode.getHttpStatus()).isBetween(400, 499);
            }
        }
    }
}
