package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InvalidUserStateException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InvalidUserStateException 테스트")
class InvalidUserStateExceptionTest {

    @Nested
    @DisplayName("currentStatus + reason 생성자")
    class StatusReasonConstructorTest {

        @Test
        @DisplayName("현재 상태와 이유로 예외를 생성한다")
        void shouldCreateExceptionWithStatusAndReason() {
            // given
            UserStatus currentStatus = UserStatus.DELETED;
            String reason = "Cannot modify deleted user";

            // when
            InvalidUserStateException exception =
                    new InvalidUserStateException(currentStatus, reason);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("USER-002");
            assertThat(exception.args()).containsEntry("currentStatus", "DELETED");
            assertThat(exception.args()).containsEntry("reason", reason);
        }
    }

    @Nested
    @DisplayName("currentStatus + targetStatus 생성자")
    class StatusTransitionConstructorTest {

        @Test
        @DisplayName("현재 상태와 대상 상태로 예외를 생성한다")
        void shouldCreateExceptionWithStatusTransition() {
            // given
            UserStatus currentStatus = UserStatus.DELETED;
            UserStatus targetStatus = UserStatus.ACTIVE;

            // when
            InvalidUserStateException exception =
                    new InvalidUserStateException(currentStatus, targetStatus);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("USER-002");
            assertThat(exception.args()).containsEntry("currentStatus", "DELETED");
            assertThat(exception.args()).containsEntry("targetStatus", "ACTIVE");
        }

        @Test
        @DisplayName("LOCKED에서 DELETED로 전이 시도 시 예외 정보를 포함한다")
        void shouldContainTransitionInfoForLockedToDeleted() {
            // given
            UserStatus currentStatus = UserStatus.LOCKED;
            UserStatus targetStatus = UserStatus.DELETED;

            // when
            InvalidUserStateException exception =
                    new InvalidUserStateException(currentStatus, targetStatus);

            // then
            assertThat(exception.args()).containsEntry("currentStatus", "LOCKED");
            assertThat(exception.args()).containsEntry("targetStatus", "DELETED");
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            InvalidUserStateException exception =
                    new InvalidUserStateException(UserStatus.DELETED, "test");

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            InvalidUserStateException exception =
                    new InvalidUserStateException(UserStatus.DELETED, "test");

            // then
            assertThat(exception.code()).isEqualTo(UserErrorCode.INVALID_USER_STATE.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(UserErrorCode.INVALID_USER_STATE.getMessage());
        }
    }
}
