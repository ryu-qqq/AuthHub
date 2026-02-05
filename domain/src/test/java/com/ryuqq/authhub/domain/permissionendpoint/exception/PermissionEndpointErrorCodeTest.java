package com.ryuqq.authhub.domain.permissionendpoint.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionEndpointErrorCode Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointErrorCode Enum 테스트")
class PermissionEndpointErrorCodeTest {

    @Nested
    @DisplayName("PermissionEndpointErrorCode ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTests {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface() {
            // when
            PermissionEndpointErrorCode errorCode =
                    PermissionEndpointErrorCode.PERMISSION_ENDPOINT_NOT_FOUND;

            // then
            assertThat(errorCode).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("PermissionEndpointErrorCode getCode() 테스트")
    class GetCodeTests {

        @Test
        @DisplayName("PERMISSION_ENDPOINT_NOT_FOUND의 코드는 PERM-EP-001이다")
        void permissionEndpointNotFoundShouldReturnCorrectCode() {
            // when & then
            assertThat(PermissionEndpointErrorCode.PERMISSION_ENDPOINT_NOT_FOUND.getCode())
                    .isEqualTo("PERM-EP-001");
        }

        @Test
        @DisplayName("DUPLICATE_PERMISSION_ENDPOINT의 코드는 PERM-EP-002이다")
        void duplicatePermissionEndpointShouldReturnCorrectCode() {
            // when & then
            assertThat(PermissionEndpointErrorCode.DUPLICATE_PERMISSION_ENDPOINT.getCode())
                    .isEqualTo("PERM-EP-002");
        }

        @Test
        @DisplayName("INVALID_URL_PATTERN의 코드는 PERM-EP-003이다")
        void invalidUrlPatternShouldReturnCorrectCode() {
            // when & then
            assertThat(PermissionEndpointErrorCode.INVALID_URL_PATTERN.getCode())
                    .isEqualTo("PERM-EP-003");
        }

        @Test
        @DisplayName("PERMISSION_NOT_FOUND_FOR_ENDPOINT의 코드는 PERM-EP-004이다")
        void permissionNotFoundForEndpointShouldReturnCorrectCode() {
            // when & then
            assertThat(PermissionEndpointErrorCode.PERMISSION_NOT_FOUND_FOR_ENDPOINT.getCode())
                    .isEqualTo("PERM-EP-004");
        }
    }

    @Nested
    @DisplayName("PermissionEndpointErrorCode getHttpStatus() 테스트")
    class GetHttpStatusTests {

        @Test
        @DisplayName("PERMISSION_ENDPOINT_NOT_FOUND의 HTTP 상태는 404이다")
        void permissionEndpointNotFoundShouldReturn404() {
            // when & then
            assertThat(PermissionEndpointErrorCode.PERMISSION_ENDPOINT_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
        }

        @Test
        @DisplayName("DUPLICATE_PERMISSION_ENDPOINT의 HTTP 상태는 409이다")
        void duplicatePermissionEndpointShouldReturn409() {
            // when & then
            assertThat(PermissionEndpointErrorCode.DUPLICATE_PERMISSION_ENDPOINT.getHttpStatus())
                    .isEqualTo(409);
        }

        @Test
        @DisplayName("INVALID_URL_PATTERN의 HTTP 상태는 400이다")
        void invalidUrlPatternShouldReturn400() {
            // when & then
            assertThat(PermissionEndpointErrorCode.INVALID_URL_PATTERN.getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("PERMISSION_NOT_FOUND_FOR_ENDPOINT의 HTTP 상태는 404이다")
        void permissionNotFoundForEndpointShouldReturn404() {
            // when & then
            assertThat(
                            PermissionEndpointErrorCode.PERMISSION_NOT_FOUND_FOR_ENDPOINT
                                    .getHttpStatus())
                    .isEqualTo(404);
        }
    }

    @Nested
    @DisplayName("PermissionEndpointErrorCode getMessage() 테스트")
    class GetMessageTests {

        @Test
        @DisplayName("모든 ErrorCode가 메시지를 반환한다")
        void allErrorCodesShouldReturnMessage() {
            // when & then
            assertThat(PermissionEndpointErrorCode.PERMISSION_ENDPOINT_NOT_FOUND.getMessage())
                    .isNotBlank();
            assertThat(PermissionEndpointErrorCode.DUPLICATE_PERMISSION_ENDPOINT.getMessage())
                    .isNotBlank();
            assertThat(PermissionEndpointErrorCode.INVALID_URL_PATTERN.getMessage()).isNotBlank();
            assertThat(PermissionEndpointErrorCode.PERMISSION_NOT_FOUND_FOR_ENDPOINT.getMessage())
                    .isNotBlank();
        }
    }

    @Nested
    @DisplayName("PermissionEndpointErrorCode Enum 값 테스트")
    class EnumValuesTests {

        @Test
        @DisplayName("모든 enum 값이 존재한다")
        void shouldHaveAllExpectedValues() {
            // when
            PermissionEndpointErrorCode[] values = PermissionEndpointErrorCode.values();

            // then
            assertThat(values).hasSize(4);
            assertThat(values)
                    .containsExactlyInAnyOrder(
                            PermissionEndpointErrorCode.PERMISSION_ENDPOINT_NOT_FOUND,
                            PermissionEndpointErrorCode.DUPLICATE_PERMISSION_ENDPOINT,
                            PermissionEndpointErrorCode.INVALID_URL_PATTERN,
                            PermissionEndpointErrorCode.PERMISSION_NOT_FOUND_FOR_ENDPOINT);
        }
    }
}
