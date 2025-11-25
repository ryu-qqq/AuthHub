package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InvalidUserStateException 테스트")
class InvalidUserStateExceptionTest {

    @Test
    @DisplayName("userId와 reason으로 예외 생성 시 올바른 ErrorCode와 메시지를 가진다")
    void shouldCreateExceptionWithUserIdAndReason() {
        // Given
        UUID userId = UUID.randomUUID();
        String reason = "User is already deleted";

        // When
        InvalidUserStateException exception = new InvalidUserStateException(userId, reason);

        // Then
        assertThat(exception.code()).isEqualTo("USER-004");
        assertThat(exception.getMessage()).isEqualTo("Invalid user status");
        assertThat(exception.args()).containsEntry("userId", userId);
        assertThat(exception.args()).containsEntry("reason", reason);
    }

    @Test
    @DisplayName("ErrorCode 정보를 올바르게 반환한다")
    void shouldReturnCorrectErrorCodeInfo() {
        // Given
        UUID userId = UUID.randomUUID();
        String reason = "User is inactive";
        InvalidUserStateException exception = new InvalidUserStateException(userId, reason);

        // When
        UserErrorCode errorCode = UserErrorCode.INVALID_USER_STATUS;

        // Then
        assertThat(exception.code()).isEqualTo(errorCode.getCode());
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
    }
}
