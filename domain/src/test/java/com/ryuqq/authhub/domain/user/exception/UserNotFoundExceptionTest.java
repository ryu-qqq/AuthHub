package com.ryuqq.authhub.domain.user.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserNotFoundException 테스트")
class UserNotFoundExceptionTest {

    @Test
    @DisplayName("userId로 예외 생성 시 올바른 ErrorCode와 메시지를 가진다")
    void shouldCreateExceptionWithUserId() {
        // Given
        Long userId = 123L;

        // When
        UserNotFoundException exception = new UserNotFoundException(userId);

        // Then
        assertThat(exception.getCode()).isEqualTo("USER-001");
        assertThat(exception.getMessage()).isEqualTo("User not found");
        assertThat(exception.getArgs()).containsEntry("userId", userId);
    }

    @Test
    @DisplayName("ErrorCode 정보를 올바르게 반환한다")
    void shouldReturnCorrectErrorCodeInfo() {
        // Given
        Long userId = 456L;
        UserNotFoundException exception = new UserNotFoundException(userId);

        // When
        UserErrorCode errorCode = UserErrorCode.USER_NOT_FOUND;

        // Then
        assertThat(exception.getCode()).isEqualTo(errorCode.getCode());
        assertThat(errorCode.getHttpStatus()).isEqualTo(404);
    }
}
