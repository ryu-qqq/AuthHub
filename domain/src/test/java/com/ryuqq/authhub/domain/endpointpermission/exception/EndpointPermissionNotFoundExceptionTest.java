package com.ryuqq.authhub.domain.endpointpermission.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * EndpointPermissionNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("EndpointPermissionNotFoundException 테스트")
class EndpointPermissionNotFoundExceptionTest {

    @Nested
    @DisplayName("UUID 생성자")
    class UuidConstructorTest {

        @Test
        @DisplayName("UUID로 예외를 생성한다")
        void shouldCreateExceptionWithUuid() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            EndpointPermissionNotFoundException exception =
                    new EndpointPermissionNotFoundException(uuid);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ENDPOINT-PERMISSION-001");
            assertThat(exception.args()).containsEntry("endpointPermissionId", uuid);
        }
    }

    @Nested
    @DisplayName("EndpointPermissionId 생성자")
    class EndpointPermissionIdConstructorTest {

        @Test
        @DisplayName("EndpointPermissionId로 예외를 생성한다")
        void shouldCreateExceptionWithEndpointPermissionId() {
            // given
            UUID uuid = UUID.randomUUID();
            EndpointPermissionId endpointPermissionId = EndpointPermissionId.of(uuid);

            // when
            EndpointPermissionNotFoundException exception =
                    new EndpointPermissionNotFoundException(endpointPermissionId);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ENDPOINT-PERMISSION-001");
            assertThat(exception.args()).containsEntry("endpointPermissionId", uuid);
        }
    }

    @Nested
    @DisplayName("serviceName + path + method 생성자")
    class ServiceNamePathMethodConstructorTest {

        @Test
        @DisplayName("서비스명, 경로, 메서드로 예외를 생성한다")
        void shouldCreateExceptionWithServiceNamePathAndMethod() {
            // given
            String serviceName = "auth-service";
            String path = "/api/v1/users";
            String method = "GET";

            // when
            EndpointPermissionNotFoundException exception =
                    new EndpointPermissionNotFoundException(serviceName, path, method);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ENDPOINT-PERMISSION-001");
            assertThat(exception.args()).containsEntry("serviceName", serviceName);
            assertThat(exception.args()).containsEntry("path", path);
            assertThat(exception.args()).containsEntry("method", method);
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            EndpointPermissionNotFoundException exception =
                    new EndpointPermissionNotFoundException(UUID.randomUUID());

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            EndpointPermissionNotFoundException exception =
                    new EndpointPermissionNotFoundException(UUID.randomUUID());

            // then
            assertThat(exception.code())
                    .isEqualTo(EndpointPermissionErrorCode.ENDPOINT_PERMISSION_NOT_FOUND.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(
                            EndpointPermissionErrorCode.ENDPOINT_PERMISSION_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 가진다")
        void shouldHaveCorrectHttpStatus() {
            // given
            EndpointPermissionNotFoundException exception =
                    new EndpointPermissionNotFoundException(UUID.randomUUID());

            // then
            assertThat(exception.httpStatus()).isEqualTo(404);
        }
    }
}
