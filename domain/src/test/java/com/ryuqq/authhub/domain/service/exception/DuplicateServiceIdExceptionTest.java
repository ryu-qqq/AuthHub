package com.ryuqq.authhub.domain.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * DuplicateServiceIdException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("DuplicateServiceIdException 테스트")
class DuplicateServiceIdExceptionTest {

    @Nested
    @DisplayName("DuplicateServiceIdException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("ServiceCode로 예외를 생성한다")
        void shouldCreateWithServiceCode() {
            // given
            ServiceCode serviceCode = ServiceCode.of("SVC_STORE");

            // when
            DuplicateServiceIdException exception = new DuplicateServiceIdException(serviceCode);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.DUPLICATE_SERVICE_CODE);
            assertThat(exception.code()).isEqualTo("SERVICE-003");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("serviceCode", serviceCode.value());
        }

        @Test
        @DisplayName("String serviceCode로 예외를 생성한다")
        void shouldCreateWithStringServiceCode() {
            // given
            String serviceCode = "SVC_B2B";

            // when
            DuplicateServiceIdException exception = new DuplicateServiceIdException(serviceCode);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.DUPLICATE_SERVICE_CODE);
            assertThat(exception.code()).isEqualTo("SERVICE-003");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("serviceCode", serviceCode);
        }
    }

    @Nested
    @DisplayName("DuplicateServiceIdException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 DUPLICATE_SERVICE_CODE이다")
        void errorCodeShouldBeDuplicateServiceCode() {
            // given
            DuplicateServiceIdException exception = new DuplicateServiceIdException("SVC_STORE");

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.DUPLICATE_SERVICE_CODE);
            assertThat(exception.code()).isEqualTo("SERVICE-003");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void httpStatusShouldBe409() {
            // given
            DuplicateServiceIdException exception = new DuplicateServiceIdException("SVC_STORE");

            // then
            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }
}
