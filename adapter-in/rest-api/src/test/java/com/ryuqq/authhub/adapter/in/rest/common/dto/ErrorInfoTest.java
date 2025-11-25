package com.ryuqq.authhub.adapter.in.rest.common.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ErrorInfo DTO 테스트")
class ErrorInfoTest {

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("[constructor] 유효한 값으로 ErrorInfo 생성 성공")
        void constructor_shouldCreateErrorInfoWithValidValues() {
            // Given
            String errorCode = "TENANT_NOT_FOUND";
            String message = "존재하지 않는 테넌트입니다";

            // When
            ErrorInfo errorInfo = new ErrorInfo(errorCode, message);

            // Then
            assertThat(errorInfo).isNotNull();
            assertThat(errorInfo.errorCode()).isEqualTo(errorCode);
            assertThat(errorInfo.message()).isEqualTo(message);
        }

        @Test
        @DisplayName("[constructor] errorCode가 null이면 예외 발생")
        void constructor_shouldThrowExceptionWhenErrorCodeIsNull() {
            // Given
            String nullErrorCode = null;
            String message = "에러 메시지";

            // When & Then
            assertThatThrownBy(() -> new ErrorInfo(nullErrorCode, message))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("errorCode는 필수입니다");
        }

        @Test
        @DisplayName("[constructor] errorCode가 빈 문자열이면 예외 발생")
        void constructor_shouldThrowExceptionWhenErrorCodeIsEmpty() {
            // Given
            String emptyErrorCode = "";
            String message = "에러 메시지";

            // When & Then
            assertThatThrownBy(() -> new ErrorInfo(emptyErrorCode, message))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("errorCode는 필수입니다");
        }

        @Test
        @DisplayName("[constructor] errorCode가 공백 문자열이면 예외 발생")
        void constructor_shouldThrowExceptionWhenErrorCodeIsBlank() {
            // Given
            String blankErrorCode = "   ";
            String message = "에러 메시지";

            // When & Then
            assertThatThrownBy(() -> new ErrorInfo(blankErrorCode, message))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("errorCode는 필수입니다");
        }

        @Test
        @DisplayName("[constructor] message가 null이면 예외 발생")
        void constructor_shouldThrowExceptionWhenMessageIsNull() {
            // Given
            String errorCode = "TENANT_NOT_FOUND";
            String nullMessage = null;

            // When & Then
            assertThatThrownBy(() -> new ErrorInfo(errorCode, nullMessage))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("message는 필수입니다");
        }

        @Test
        @DisplayName("[constructor] message가 빈 문자열이면 예외 발생")
        void constructor_shouldThrowExceptionWhenMessageIsEmpty() {
            // Given
            String errorCode = "TENANT_NOT_FOUND";
            String emptyMessage = "";

            // When & Then
            assertThatThrownBy(() -> new ErrorInfo(errorCode, emptyMessage))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("message는 필수입니다");
        }

        @Test
        @DisplayName("[constructor] message가 공백 문자열이면 예외 발생")
        void constructor_shouldThrowExceptionWhenMessageIsBlank() {
            // Given
            String errorCode = "TENANT_NOT_FOUND";
            String blankMessage = "   ";

            // When & Then
            assertThatThrownBy(() -> new ErrorInfo(errorCode, blankMessage))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("message는 필수입니다");
        }
    }

    @Nested
    @DisplayName("Record 동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("[equals] 동일한 값을 가진 ErrorInfo는 동등함")
        void equals_shouldReturnTrueForSameValues() {
            // Given
            ErrorInfo errorInfo1 = new ErrorInfo("USER_NOT_FOUND", "사용자를 찾을 수 없습니다");
            ErrorInfo errorInfo2 = new ErrorInfo("USER_NOT_FOUND", "사용자를 찾을 수 없습니다");

            // Then
            assertThat(errorInfo1).isEqualTo(errorInfo2);
            assertThat(errorInfo1.hashCode()).isEqualTo(errorInfo2.hashCode());
        }

        @Test
        @DisplayName("[equals] 다른 errorCode를 가진 ErrorInfo는 동등하지 않음")
        void equals_shouldReturnFalseForDifferentErrorCode() {
            // Given
            ErrorInfo errorInfo1 = new ErrorInfo("USER_NOT_FOUND", "사용자를 찾을 수 없습니다");
            ErrorInfo errorInfo2 = new ErrorInfo("TENANT_NOT_FOUND", "사용자를 찾을 수 없습니다");

            // Then
            assertThat(errorInfo1).isNotEqualTo(errorInfo2);
        }

        @Test
        @DisplayName("[equals] 다른 message를 가진 ErrorInfo는 동등하지 않음")
        void equals_shouldReturnFalseForDifferentMessage() {
            // Given
            ErrorInfo errorInfo1 = new ErrorInfo("USER_NOT_FOUND", "사용자를 찾을 수 없습니다");
            ErrorInfo errorInfo2 = new ErrorInfo("USER_NOT_FOUND", "해당 사용자가 없습니다");

            // Then
            assertThat(errorInfo1).isNotEqualTo(errorInfo2);
        }
    }
}
