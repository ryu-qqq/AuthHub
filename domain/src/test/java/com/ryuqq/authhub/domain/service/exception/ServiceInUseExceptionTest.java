package com.ryuqq.authhub.domain.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ServiceInUseException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ServiceInUseException 테스트")
class ServiceInUseExceptionTest {

    @Nested
    @DisplayName("ServiceInUseException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("ServiceId로 예외를 생성한다")
        void shouldCreateWithServiceId() {
            // given
            ServiceId serviceId = ServiceId.of(1L);

            // when
            ServiceInUseException exception = new ServiceInUseException(serviceId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.SERVICE_IN_USE);
            assertThat(exception.code()).isEqualTo("SERVICE-004");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.args()).containsEntry("serviceId", serviceId.value());
        }

        @Test
        @DisplayName("String serviceId로 예외를 생성한다")
        void shouldCreateWithStringServiceId() {
            // given
            String serviceId = "1";

            // when
            ServiceInUseException exception = new ServiceInUseException(serviceId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.SERVICE_IN_USE);
            assertThat(exception.code()).isEqualTo("SERVICE-004");
            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.args()).containsEntry("serviceId", serviceId);
        }
    }

    @Nested
    @DisplayName("ServiceInUseException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 SERVICE_IN_USE이다")
        void errorCodeShouldBeServiceInUse() {
            // given
            ServiceInUseException exception = new ServiceInUseException(ServiceId.of(1L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.SERVICE_IN_USE);
            assertThat(exception.code()).isEqualTo("SERVICE-004");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 400이다")
        void httpStatusShouldBe400() {
            // given
            ServiceInUseException exception = new ServiceInUseException(ServiceId.of(1L));

            // then
            assertThat(exception.httpStatus()).isEqualTo(400);
        }
    }
}
