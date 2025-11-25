package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserErrorCode 테스트")
class UserErrorCodeTest {

    @Test
    @DisplayName("USER_NOT_FOUND 에러 코드 검증")
    void shouldHaveUserNotFoundErrorCode() {
        // When
        UserErrorCode errorCode = UserErrorCode.USER_NOT_FOUND;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("USER-001");
        assertThat(errorCode.getHttpStatus()).isEqualTo(404);
        assertThat(errorCode.getMessage()).isEqualTo("User not found");
    }

    @Test
    @DisplayName("INVALID_USER_ID 에러 코드 검증")
    void shouldHaveInvalidUserIdErrorCode() {
        // When
        UserErrorCode errorCode = UserErrorCode.INVALID_USER_ID;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("USER-002");
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
        assertThat(errorCode.getMessage()).isEqualTo("Invalid user ID");
    }

    @Test
    @DisplayName("INVALID_USER_TYPE 에러 코드 검증")
    void shouldHaveInvalidUserTypeErrorCode() {
        // When
        UserErrorCode errorCode = UserErrorCode.INVALID_USER_TYPE;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("USER-003");
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
        assertThat(errorCode.getMessage()).isEqualTo("Invalid user type");
    }

    @Test
    @DisplayName("INVALID_USER_STATUS 에러 코드 검증")
    void shouldHaveInvalidUserStatusErrorCode() {
        // When
        UserErrorCode errorCode = UserErrorCode.INVALID_USER_STATUS;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("USER-004");
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
        assertThat(errorCode.getMessage()).isEqualTo("Invalid user status");
    }
}
