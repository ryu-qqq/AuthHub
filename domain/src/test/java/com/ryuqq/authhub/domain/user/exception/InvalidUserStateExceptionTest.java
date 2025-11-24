package com.ryuqq.authhub.domain.user.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InvalidUserStateException 테스트")
class InvalidUserStateExceptionTest {

    @Test
    @DisplayName("userId와 reason으로 예외 생성 시 올바른 ErrorCode와 메시지를 가진다")
    void shouldCreateExceptionWithUserIdAndReason() {
        // Given
        Long userId = 123L;
        String reason = "User is already deleted";

        // When
        InvalidUserStateException exception = new InvalidUserStateException(userId, reason);

        // Then
        assertThat(exception.getCode()).isEqualTo("USER-004");
        assertThat(exception.getMessage()).isEqualTo("Invalid user status");
        assertThat(exception.getArgs()).containsEntry("userId", userId);
        assertThat(exception.getArgs()).containsEntry("reason", reason);
    }

    @Test
    @DisplayName("ErrorCode 정보를 올바르게 반환한다")
    void shouldReturnCorrectErrorCodeInfo() {
        // Given
        Long userId = 456L;
        String reason = "User is inactive";
        InvalidUserStateException exception = new InvalidUserStateException(userId, reason);

        // When
        UserErrorCode errorCode = UserErrorCode.INVALID_USER_STATUS;

        // Then
        assertThat(exception.getCode()).isEqualTo(errorCode.getCode());
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
    }
}
