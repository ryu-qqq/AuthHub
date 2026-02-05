package com.ryuqq.authhub.domain.permissionendpoint.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionEndpointNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointNotFoundException 테스트")
class PermissionEndpointNotFoundExceptionTest {

    @Nested
    @DisplayName("PermissionEndpointNotFoundException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("Long ID로 예외를 생성한다")
        void shouldCreateWithLongId() {
            // when
            PermissionEndpointNotFoundException exception =
                    new PermissionEndpointNotFoundException(1L);

            // then
            assertThat(exception.getErrorCode()).isInstanceOf(ErrorCode.class);
            assertThat(exception.getErrorCode().getCode()).isEqualTo("PERM-EP-001");
            assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("PermissionEndpointId로 예외를 생성한다")
        void shouldCreateWithPermissionEndpointId() {
            // given
            PermissionEndpointId endpointId = PermissionEndpointId.of(1L);

            // when
            PermissionEndpointNotFoundException exception =
                    new PermissionEndpointNotFoundException(endpointId);

            // then
            assertThat(exception.getErrorCode()).isInstanceOf(ErrorCode.class);
            assertThat(exception.getErrorCode().getCode()).isEqualTo("PERM-EP-001");
        }

        @Test
        @DisplayName("URL 패턴과 HTTP 메서드로 예외를 생성한다")
        void shouldCreateWithUrlPatternAndHttpMethod() {
            // when
            PermissionEndpointNotFoundException exception =
                    new PermissionEndpointNotFoundException("/api/v1/users", "GET");

            // then
            assertThat(exception.getErrorCode()).isInstanceOf(ErrorCode.class);
            assertThat(exception.getErrorCode().getCode()).isEqualTo("PERM-EP-001");
            // URL과 method는 args(컨텍스트 정보)에 저장, message는 ErrorCode에서 제공
            assertThat(exception.args()).containsEntry("urlPattern", "/api/v1/users");
            assertThat(exception.args()).containsEntry("httpMethod", "GET");
        }
    }

    @Nested
    @DisplayName("PermissionEndpointNotFoundException ErrorCode 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("ErrorCode는 PERMISSION_ENDPOINT_NOT_FOUND이다")
        void shouldHaveCorrectErrorCode() {
            // when
            PermissionEndpointNotFoundException exception =
                    new PermissionEndpointNotFoundException(1L);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(PermissionEndpointErrorCode.PERMISSION_ENDPOINT_NOT_FOUND);
            assertThat(exception.getErrorCode().getCode()).isEqualTo("PERM-EP-001");
            assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(404);
        }
    }
}
