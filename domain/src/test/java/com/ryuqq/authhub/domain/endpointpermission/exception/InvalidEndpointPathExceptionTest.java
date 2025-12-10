package com.ryuqq.authhub.domain.endpointpermission.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InvalidEndpointPathException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InvalidEndpointPathException 테스트")
class InvalidEndpointPathExceptionTest {

    @Nested
    @DisplayName("생성자")
    class ConstructorTest {

        @Test
        @DisplayName("경로로 예외를 생성한다")
        void shouldCreateExceptionWithPath() {
            // given
            String invalidPath = "invalid-path-without-slash";

            // when
            InvalidEndpointPathException exception = new InvalidEndpointPathException(invalidPath);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ENDPOINT-PERMISSION-003");
            assertThat(exception.args()).containsEntry("path", invalidPath);
        }

        @Test
        @DisplayName("빈 문자열 경로로 예외를 생성할 수 있다")
        void shouldCreateExceptionWithEmptyPath() {
            // given
            String emptyPath = "";

            // when
            InvalidEndpointPathException exception = new InvalidEndpointPathException(emptyPath);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.args()).containsEntry("path", emptyPath);
        }

        @Test
        @DisplayName("특수문자가 포함된 경로로 예외를 생성할 수 있다")
        void shouldCreateExceptionWithSpecialCharactersPath() {
            // given
            String specialPath = "path with spaces & special <chars>";

            // when
            InvalidEndpointPathException exception = new InvalidEndpointPathException(specialPath);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.args()).containsEntry("path", specialPath);
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            InvalidEndpointPathException exception = new InvalidEndpointPathException("invalid");

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            InvalidEndpointPathException exception = new InvalidEndpointPathException("invalid");

            // then
            assertThat(exception.code())
                    .isEqualTo(EndpointPermissionErrorCode.INVALID_ENDPOINT_PATH.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(EndpointPermissionErrorCode.INVALID_ENDPOINT_PATH.getMessage());
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 가진다")
        void shouldHaveCorrectHttpStatus() {
            // given
            InvalidEndpointPathException exception = new InvalidEndpointPathException("invalid");

            // then
            assertThat(exception.httpStatus()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("에러 메시지")
    class ErrorMessageTest {

        @Test
        @DisplayName("유효하지 않은 경로 형식에 대한 메시지를 포함한다")
        void shouldContainMessageAboutInvalidPathFormat() {
            // given
            InvalidEndpointPathException exception = new InvalidEndpointPathException("bad-path");

            // then
            assertThat(exception.getMessage())
                    .contains("Invalid")
                    .contains("path");
        }
    }
}
