package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserNotFoundException 테스트")
class UserNotFoundExceptionTest {

    @Nested
    @DisplayName("UserId 생성자")
    class UserIdConstructorTest {

        @Test
        @DisplayName("UserId로 예외를 생성한다")
        void shouldCreateExceptionWithUserId() {
            // given
            UUID uuid = UUID.randomUUID();
            UserId userId = UserId.of(uuid);

            // when
            UserNotFoundException exception = new UserNotFoundException(userId);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("USER-001");
            assertThat(exception.args()).containsEntry("userId", uuid);
        }
    }

    @Nested
    @DisplayName("UUID 생성자")
    class UuidConstructorTest {

        @Test
        @DisplayName("UUID로 예외를 생성한다")
        void shouldCreateExceptionWithUuid() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            UserNotFoundException exception = new UserNotFoundException(uuid);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("USER-001");
            assertThat(exception.args()).containsEntry("userId", uuid);
        }
    }

    @Nested
    @DisplayName("String 생성자")
    class StringConstructorTest {

        @Test
        @DisplayName("문자열 식별자로 예외를 생성한다")
        void shouldCreateExceptionWithStringIdentifier() {
            // given
            String identifier = "test-user@example.com";

            // when
            UserNotFoundException exception = new UserNotFoundException(identifier);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("USER-001");
            assertThat(exception.args()).containsEntry("identifier", identifier);
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            UserNotFoundException exception = new UserNotFoundException(UUID.randomUUID());

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            UserNotFoundException exception = new UserNotFoundException(UUID.randomUUID());

            // then
            assertThat(exception.code()).isEqualTo(UserErrorCode.USER_NOT_FOUND.getCode());
            assertThat(exception.getMessage()).isEqualTo(UserErrorCode.USER_NOT_FOUND.getMessage());
        }
    }
}
