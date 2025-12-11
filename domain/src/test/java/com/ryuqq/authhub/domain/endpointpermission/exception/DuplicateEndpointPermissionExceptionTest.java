package com.ryuqq.authhub.domain.endpointpermission.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DuplicateEndpointPermissionException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DuplicateEndpointPermissionException 테스트")
class DuplicateEndpointPermissionExceptionTest {

    @Nested
    @DisplayName("생성자")
    class ConstructorTest {

        @Test
        @DisplayName("서비스명, 경로, 메서드로 예외를 생성한다")
        void shouldCreateExceptionWithServiceNamePathAndMethod() {
            // given
            String serviceName = "auth-service";
            String path = "/api/v1/users";
            String method = "POST";

            // when
            DuplicateEndpointPermissionException exception =
                    new DuplicateEndpointPermissionException(serviceName, path, method);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ENDPOINT-PERMISSION-002");
            assertThat(exception.args()).containsEntry("serviceName", serviceName);
            assertThat(exception.args()).containsEntry("path", path);
            assertThat(exception.args()).containsEntry("method", method);
        }

        @Test
        @DisplayName("다양한 HTTP 메서드로 예외를 생성할 수 있다")
        void shouldCreateExceptionWithVariousHttpMethods() {
            // given
            String serviceName = "api-gateway";
            String path = "/api/v1/orders";

            // when & then
            String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};
            for (String method : methods) {
                DuplicateEndpointPermissionException exception =
                        new DuplicateEndpointPermissionException(serviceName, path, method);

                assertThat(exception.args()).containsEntry("method", method);
            }
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            DuplicateEndpointPermissionException exception =
                    new DuplicateEndpointPermissionException("service", "/path", "GET");

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            DuplicateEndpointPermissionException exception =
                    new DuplicateEndpointPermissionException("service", "/path", "GET");

            // then
            assertThat(exception.code())
                    .isEqualTo(EndpointPermissionErrorCode.DUPLICATE_ENDPOINT_PERMISSION.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(
                            EndpointPermissionErrorCode.DUPLICATE_ENDPOINT_PERMISSION.getMessage());
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 가진다")
        void shouldHaveCorrectHttpStatus() {
            // given
            DuplicateEndpointPermissionException exception =
                    new DuplicateEndpointPermissionException("service", "/path", "GET");

            // then
            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("에러 메시지")
    class ErrorMessageTest {

        @Test
        @DisplayName("중복 엔드포인트 권한에 대한 명확한 메시지를 포함한다")
        void shouldContainClearMessageAboutDuplication() {
            // given
            DuplicateEndpointPermissionException exception =
                    new DuplicateEndpointPermissionException("auth-service", "/api/users", "POST");

            // then
            assertThat(exception.getMessage()).contains("already exists");
        }
    }
}
