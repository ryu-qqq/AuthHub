package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DuplicateUserPhoneNumberException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DuplicateUserPhoneNumberException 테스트")
class DuplicateUserPhoneNumberExceptionTest {

    @Nested
    @DisplayName("DuplicateUserPhoneNumberException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("String phoneNumber로 예외를 생성한다")
        void shouldCreateWithStringPhoneNumber() {
            // given
            String phoneNumber = "010-1234-5678";

            // when
            DuplicateUserPhoneNumberException exception =
                    new DuplicateUserPhoneNumberException(phoneNumber);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(UserErrorCode.DUPLICATE_USER_PHONE_NUMBER);
            assertThat(exception.code()).isEqualTo("USER-003");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("phoneNumber", phoneNumber);
        }

        @Test
        @DisplayName("PhoneNumber로 예외를 생성한다")
        void shouldCreateWithPhoneNumber() {
            // given
            PhoneNumber phoneNumber = PhoneNumber.of("010-1234-5678");

            // when
            DuplicateUserPhoneNumberException exception =
                    new DuplicateUserPhoneNumberException(phoneNumber);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(UserErrorCode.DUPLICATE_USER_PHONE_NUMBER);
            assertThat(exception.code()).isEqualTo("USER-003");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("phoneNumber", phoneNumber.value());
        }
    }

    @Nested
    @DisplayName("DuplicateUserPhoneNumberException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 DUPLICATE_USER_PHONE_NUMBER이다")
        void errorCodeShouldBeDuplicateUserPhoneNumber() {
            // given
            DuplicateUserPhoneNumberException exception =
                    new DuplicateUserPhoneNumberException("010-1234-5678");

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(UserErrorCode.DUPLICATE_USER_PHONE_NUMBER);
            assertThat(exception.code()).isEqualTo("USER-003");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void httpStatusShouldBe409() {
            // given
            DuplicateUserPhoneNumberException exception =
                    new DuplicateUserPhoneNumberException("010-1234-5678");

            // then
            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }
}
