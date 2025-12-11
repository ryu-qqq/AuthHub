package com.ryuqq.authhub.domain.endpointpermission.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * EndpointPermissionErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("EndpointPermissionErrorCode 테스트")
class EndpointPermissionErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현")
    class ErrorCodeInterfaceTest {

        @ParameterizedTest
        @EnumSource(EndpointPermissionErrorCode.class)
        @DisplayName("모든 에러 코드는 ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface(EndpointPermissionErrorCode errorCode) {
            // then
            assertThat(errorCode).isInstanceOf(ErrorCode.class);
        }

        @ParameterizedTest
        @EnumSource(EndpointPermissionErrorCode.class)
        @DisplayName("모든 에러 코드는 null이 아닌 코드를 가진다")
        void shouldHaveNonNullCode(EndpointPermissionErrorCode errorCode) {
            // then
            assertThat(errorCode.getCode()).isNotNull().isNotEmpty();
        }

        @ParameterizedTest
        @EnumSource(EndpointPermissionErrorCode.class)
        @DisplayName("모든 에러 코드는 null이 아닌 메시지를 가진다")
        void shouldHaveNonNullMessage(EndpointPermissionErrorCode errorCode) {
            // then
            assertThat(errorCode.getMessage()).isNotNull().isNotEmpty();
        }

        @ParameterizedTest
        @EnumSource(EndpointPermissionErrorCode.class)
        @DisplayName("모든 에러 코드는 유효한 HTTP 상태 코드를 가진다")
        void shouldHaveValidHttpStatus(EndpointPermissionErrorCode errorCode) {
            // then
            assertThat(errorCode.getHttpStatus()).isBetween(100, 599);
        }
    }

    @Nested
    @DisplayName("ENDPOINT_PERMISSION_NOT_FOUND")
    class EndpointPermissionNotFoundTest {

        @Test
        @DisplayName("올바른 에러 코드를 반환한다")
        void shouldReturnCorrectCode() {
            // then
            assertThat(EndpointPermissionErrorCode.ENDPOINT_PERMISSION_NOT_FOUND.getCode())
                    .isEqualTo("ENDPOINT-PERMISSION-001");
        }

        @Test
        @DisplayName("404 HTTP 상태 코드를 반환한다")
        void shouldReturnNotFoundHttpStatus() {
            // then
            assertThat(EndpointPermissionErrorCode.ENDPOINT_PERMISSION_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
        }

        @Test
        @DisplayName("올바른 에러 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            // then
            assertThat(EndpointPermissionErrorCode.ENDPOINT_PERMISSION_NOT_FOUND.getMessage())
                    .isEqualTo("Endpoint permission not found");
        }
    }

    @Nested
    @DisplayName("DUPLICATE_ENDPOINT_PERMISSION")
    class DuplicateEndpointPermissionTest {

        @Test
        @DisplayName("올바른 에러 코드를 반환한다")
        void shouldReturnCorrectCode() {
            // then
            assertThat(EndpointPermissionErrorCode.DUPLICATE_ENDPOINT_PERMISSION.getCode())
                    .isEqualTo("ENDPOINT-PERMISSION-002");
        }

        @Test
        @DisplayName("409 HTTP 상태 코드를 반환한다")
        void shouldReturnConflictHttpStatus() {
            // then
            assertThat(EndpointPermissionErrorCode.DUPLICATE_ENDPOINT_PERMISSION.getHttpStatus())
                    .isEqualTo(409);
        }

        @Test
        @DisplayName("올바른 에러 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            // then
            assertThat(EndpointPermissionErrorCode.DUPLICATE_ENDPOINT_PERMISSION.getMessage())
                    .isEqualTo(
                            "Endpoint permission already exists for this service, path, and"
                                    + " method");
        }
    }

    @Nested
    @DisplayName("INVALID_ENDPOINT_PATH")
    class InvalidEndpointPathTest {

        @Test
        @DisplayName("올바른 에러 코드를 반환한다")
        void shouldReturnCorrectCode() {
            // then
            assertThat(EndpointPermissionErrorCode.INVALID_ENDPOINT_PATH.getCode())
                    .isEqualTo("ENDPOINT-PERMISSION-003");
        }

        @Test
        @DisplayName("400 HTTP 상태 코드를 반환한다")
        void shouldReturnBadRequestHttpStatus() {
            // then
            assertThat(EndpointPermissionErrorCode.INVALID_ENDPOINT_PATH.getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("올바른 에러 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            // then
            assertThat(EndpointPermissionErrorCode.INVALID_ENDPOINT_PATH.getMessage())
                    .isEqualTo("Invalid endpoint path format");
        }
    }

    @Nested
    @DisplayName("에러 코드 고유성")
    class UniquenessTest {

        @Test
        @DisplayName("모든 에러 코드는 서로 다른 코드 값을 가진다")
        void shouldHaveUniqueCodeValues() {
            // given
            EndpointPermissionErrorCode[] errorCodes = EndpointPermissionErrorCode.values();

            // then
            long uniqueCodeCount =
                    java.util.Arrays.stream(errorCodes)
                            .map(EndpointPermissionErrorCode::getCode)
                            .distinct()
                            .count();

            assertThat(uniqueCodeCount).isEqualTo(errorCodes.length);
        }
    }
}
