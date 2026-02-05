package com.ryuqq.authhub.domain.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceErrorCode 테스트")
class ServiceErrorCodeTest {

    @Nested
    @DisplayName("ServiceErrorCode getCode 테스트")
    class GetCodeTests {

        @Test
        @DisplayName("각 에러 코드는 올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            // when & then
            assertThat(ServiceErrorCode.SERVICE_NOT_FOUND.getCode()).isEqualTo("SERVICE-001");
            assertThat(ServiceErrorCode.INVALID_SERVICE_STATE.getCode()).isEqualTo("SERVICE-002");
            assertThat(ServiceErrorCode.DUPLICATE_SERVICE_CODE.getCode()).isEqualTo("SERVICE-003");
            assertThat(ServiceErrorCode.SERVICE_IN_USE.getCode()).isEqualTo("SERVICE-004");
        }
    }

    @Nested
    @DisplayName("ServiceErrorCode getHttpStatus 테스트")
    class GetHttpStatusTests {

        @Test
        @DisplayName("각 에러 코드는 올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            // when & then
            assertThat(ServiceErrorCode.SERVICE_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(ServiceErrorCode.INVALID_SERVICE_STATE.getHttpStatus()).isEqualTo(400);
            assertThat(ServiceErrorCode.DUPLICATE_SERVICE_CODE.getHttpStatus()).isEqualTo(409);
            assertThat(ServiceErrorCode.SERVICE_IN_USE.getHttpStatus()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("ServiceErrorCode getMessage 테스트")
    class GetMessageTests {

        @Test
        @DisplayName("각 에러 코드는 올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            // when & then
            assertThat(ServiceErrorCode.SERVICE_NOT_FOUND.getMessage())
                    .isEqualTo("Service not found");
            assertThat(ServiceErrorCode.INVALID_SERVICE_STATE.getMessage())
                    .isEqualTo("Invalid service state transition");
            assertThat(ServiceErrorCode.DUPLICATE_SERVICE_CODE.getMessage())
                    .isEqualTo("Service code already exists");
            assertThat(ServiceErrorCode.SERVICE_IN_USE.getMessage())
                    .isEqualTo("Service is in use and cannot be deleted");
        }
    }

    @Nested
    @DisplayName("ServiceErrorCode ErrorCode 인터페이스 테스트")
    class ErrorCodeInterfaceTests {

        @Test
        @DisplayName("ServiceErrorCode는 ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface() {
            // when & then
            assertThat(ServiceErrorCode.SERVICE_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }
}
