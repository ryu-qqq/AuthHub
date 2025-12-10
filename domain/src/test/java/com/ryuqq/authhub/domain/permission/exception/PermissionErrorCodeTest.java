package com.ryuqq.authhub.domain.permission.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * PermissionErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionErrorCode 테스트")
class PermissionErrorCodeTest {

    @Nested
    @DisplayName("에러 코드 값")
    class ErrorCodeValueTest {

        @Test
        @DisplayName("PERMISSION_NOT_FOUND 에러 코드를 반환한다")
        void shouldReturnPermissionNotFoundErrorCode() {
            // when
            PermissionErrorCode errorCode = PermissionErrorCode.PERMISSION_NOT_FOUND;

            // then
            assertThat(errorCode.getCode()).isEqualTo("PERMISSION-001");
            assertThat(errorCode.getHttpStatus()).isEqualTo(404);
            assertThat(errorCode.getMessage()).isEqualTo("Permission not found");
        }

        @Test
        @DisplayName("DUPLICATE_PERMISSION_KEY 에러 코드를 반환한다")
        void shouldReturnDuplicatePermissionKeyErrorCode() {
            // when
            PermissionErrorCode errorCode = PermissionErrorCode.DUPLICATE_PERMISSION_KEY;

            // then
            assertThat(errorCode.getCode()).isEqualTo("PERMISSION-002");
            assertThat(errorCode.getHttpStatus()).isEqualTo(409);
            assertThat(errorCode.getMessage()).isEqualTo("Permission key already exists");
        }

        @Test
        @DisplayName("SYSTEM_PERMISSION_NOT_MODIFIABLE 에러 코드를 반환한다")
        void shouldReturnSystemPermissionNotModifiableErrorCode() {
            // when
            PermissionErrorCode errorCode = PermissionErrorCode.SYSTEM_PERMISSION_NOT_MODIFIABLE;

            // then
            assertThat(errorCode.getCode()).isEqualTo("PERMISSION-003");
            assertThat(errorCode.getHttpStatus()).isEqualTo(400);
            assertThat(errorCode.getMessage()).isEqualTo("System permission cannot be modified");
        }

        @Test
        @DisplayName("SYSTEM_PERMISSION_NOT_DELETABLE 에러 코드를 반환한다")
        void shouldReturnSystemPermissionNotDeletableErrorCode() {
            // when
            PermissionErrorCode errorCode = PermissionErrorCode.SYSTEM_PERMISSION_NOT_DELETABLE;

            // then
            assertThat(errorCode.getCode()).isEqualTo("PERMISSION-004");
            assertThat(errorCode.getHttpStatus()).isEqualTo(400);
            assertThat(errorCode.getMessage()).isEqualTo("System permission cannot be deleted");
        }

        @Test
        @DisplayName("INVALID_PERMISSION_KEY 에러 코드를 반환한다")
        void shouldReturnInvalidPermissionKeyErrorCode() {
            // when
            PermissionErrorCode errorCode = PermissionErrorCode.INVALID_PERMISSION_KEY;

            // then
            assertThat(errorCode.getCode()).isEqualTo("PERMISSION-005");
            assertThat(errorCode.getHttpStatus()).isEqualTo(400);
            assertThat(errorCode.getMessage()).isEqualTo("Invalid permission key format");
        }
    }

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현")
    class ErrorCodeInterfaceTest {

        @ParameterizedTest
        @EnumSource(PermissionErrorCode.class)
        @DisplayName("모든 에러 코드가 ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface(PermissionErrorCode errorCode) {
            // then
            assertThat(errorCode).isInstanceOf(ErrorCode.class);
        }

        @ParameterizedTest
        @EnumSource(PermissionErrorCode.class)
        @DisplayName("모든 에러 코드가 PERMISSION- 접두사로 시작한다")
        void shouldStartWithPermissionPrefix(PermissionErrorCode errorCode) {
            // then
            assertThat(errorCode.getCode()).startsWith("PERMISSION-");
        }

        @ParameterizedTest
        @EnumSource(PermissionErrorCode.class)
        @DisplayName("모든 에러 코드가 유효한 HTTP 상태 코드를 가진다")
        void shouldHaveValidHttpStatus(PermissionErrorCode errorCode) {
            // then
            assertThat(errorCode.getHttpStatus()).isBetween(100, 599);
        }

        @ParameterizedTest
        @EnumSource(PermissionErrorCode.class)
        @DisplayName("모든 에러 코드가 비어있지 않은 메시지를 가진다")
        void shouldHaveNonEmptyMessage(PermissionErrorCode errorCode) {
            // then
            assertThat(errorCode.getMessage()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("Enum 기본 동작")
    class EnumBasicBehaviorTest {

        @Test
        @DisplayName("5개의 에러 코드가 정의되어 있다")
        void shouldHaveFiveErrorCodes() {
            // when
            PermissionErrorCode[] values = PermissionErrorCode.values();

            // then
            assertThat(values).hasSize(5);
        }

        @Test
        @DisplayName("valueOf로 에러 코드를 조회할 수 있다")
        void shouldFindByValueOf() {
            // when
            PermissionErrorCode errorCode = PermissionErrorCode.valueOf("PERMISSION_NOT_FOUND");

            // then
            assertThat(errorCode).isEqualTo(PermissionErrorCode.PERMISSION_NOT_FOUND);
        }
    }
}
