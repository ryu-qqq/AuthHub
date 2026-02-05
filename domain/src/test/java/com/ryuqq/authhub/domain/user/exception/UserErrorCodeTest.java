package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserErrorCode 테스트")
class UserErrorCodeTest {

    @Nested
    @DisplayName("UserErrorCode getCode 테스트")
    class GetCodeTests {

        @Test
        @DisplayName("USER_NOT_FOUND의 코드는 USER-001이다")
        void userNotFoundCodeShouldBeUser001() {
            assertThat(UserErrorCode.USER_NOT_FOUND.getCode()).isEqualTo("USER-001");
        }

        @Test
        @DisplayName("DUPLICATE_USER_IDENTIFIER의 코드는 USER-002이다")
        void duplicateUserIdentifierCodeShouldBeUser002() {
            assertThat(UserErrorCode.DUPLICATE_USER_IDENTIFIER.getCode()).isEqualTo("USER-002");
        }

        @Test
        @DisplayName("DUPLICATE_USER_PHONE_NUMBER의 코드는 USER-003이다")
        void duplicateUserPhoneNumberCodeShouldBeUser003() {
            assertThat(UserErrorCode.DUPLICATE_USER_PHONE_NUMBER.getCode()).isEqualTo("USER-003");
        }

        @Test
        @DisplayName("USER_NOT_ACTIVE의 코드는 USER-004이다")
        void userNotActiveCodeShouldBeUser004() {
            assertThat(UserErrorCode.USER_NOT_ACTIVE.getCode()).isEqualTo("USER-004");
        }

        @Test
        @DisplayName("USER_SUSPENDED의 코드는 USER-005이다")
        void userSuspendedCodeShouldBeUser005() {
            assertThat(UserErrorCode.USER_SUSPENDED.getCode()).isEqualTo("USER-005");
        }

        @Test
        @DisplayName("USER_DELETED의 코드는 USER-006이다")
        void userDeletedCodeShouldBeUser006() {
            assertThat(UserErrorCode.USER_DELETED.getCode()).isEqualTo("USER-006");
        }

        @Test
        @DisplayName("INVALID_PASSWORD의 코드는 USER-007이다")
        void invalidPasswordCodeShouldBeUser007() {
            assertThat(UserErrorCode.INVALID_PASSWORD.getCode()).isEqualTo("USER-007");
        }
    }

    @Nested
    @DisplayName("UserErrorCode getHttpStatus 테스트")
    class GetHttpStatusTests {

        @Test
        @DisplayName("USER_NOT_FOUND의 HTTP 상태는 404이다")
        void userNotFoundHttpStatusShouldBe404() {
            assertThat(UserErrorCode.USER_NOT_FOUND.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("DUPLICATE_USER_IDENTIFIER의 HTTP 상태는 409이다")
        void duplicateUserIdentifierHttpStatusShouldBe409() {
            assertThat(UserErrorCode.DUPLICATE_USER_IDENTIFIER.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("USER_NOT_ACTIVE의 HTTP 상태는 403이다")
        void userNotActiveHttpStatusShouldBe403() {
            assertThat(UserErrorCode.USER_NOT_ACTIVE.getHttpStatus()).isEqualTo(403);
        }

        @Test
        @DisplayName("INVALID_PASSWORD의 HTTP 상태는 401이다")
        void invalidPasswordHttpStatusShouldBe401() {
            assertThat(UserErrorCode.INVALID_PASSWORD.getHttpStatus()).isEqualTo(401);
        }
    }

    @Nested
    @DisplayName("UserErrorCode getMessage 테스트")
    class GetMessageTests {

        @Test
        @DisplayName("USER_NOT_FOUND의 메시지를 반환한다")
        void userNotFoundShouldHaveMessage() {
            assertThat(UserErrorCode.USER_NOT_FOUND.getMessage()).isEqualTo("User not found");
        }

        @Test
        @DisplayName("DUPLICATE_USER_IDENTIFIER의 메시지를 반환한다")
        void duplicateUserIdentifierShouldHaveMessage() {
            assertThat(UserErrorCode.DUPLICATE_USER_IDENTIFIER.getMessage())
                    .isEqualTo("User identifier already exists");
        }

        @Test
        @DisplayName("INVALID_PASSWORD의 메시지를 반환한다")
        void invalidPasswordShouldHaveMessage() {
            assertThat(UserErrorCode.INVALID_PASSWORD.getMessage()).isEqualTo("Invalid password");
        }
    }

    @Nested
    @DisplayName("UserErrorCode ErrorCode 인터페이스 테스트")
    class ErrorCodeInterfaceTests {

        @Test
        @DisplayName("모든 UserErrorCode는 ErrorCode 인터페이스를 구현한다")
        void allErrorCodesShouldImplementErrorCodeInterface() {
            assertThat(UserErrorCode.USER_NOT_FOUND).isInstanceOf(ErrorCode.class);
            assertThat(UserErrorCode.DUPLICATE_USER_IDENTIFIER).isInstanceOf(ErrorCode.class);
            assertThat(UserErrorCode.INVALID_PASSWORD).isInstanceOf(ErrorCode.class);
        }
    }
}
