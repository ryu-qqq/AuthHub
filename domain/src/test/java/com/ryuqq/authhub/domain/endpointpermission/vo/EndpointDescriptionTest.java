package com.ryuqq.authhub.domain.endpointpermission.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** EndpointDescription Value Object 단위 테스트 */
@Tag("unit")
@Tag("domain")
@Tag("vo")
@DisplayName("EndpointDescription VO 단위 테스트")
class EndpointDescriptionTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTests {

        @Test
        @DisplayName("of() - 유효한 설명으로 생성 성공")
        void of_ValidDescription_ShouldCreate() {
            // Given
            String description = "사용자 목록 조회 API";

            // When
            EndpointDescription result = EndpointDescription.of(description);

            // Then
            assertThat(result.value()).isEqualTo(description);
        }

        @Test
        @DisplayName("of() - null 값 허용")
        void of_WithNull_ShouldCreate() {
            // When
            EndpointDescription result = EndpointDescription.of(null);

            // Then
            assertThat(result.value()).isNull();
            assertThat(result.hasValue()).isFalse();
        }

        @Test
        @DisplayName("of() - 빈 문자열은 null로 변환")
        void of_WithEmptyString_ShouldBeNull() {
            // When
            EndpointDescription result = EndpointDescription.of("");

            // Then
            assertThat(result.value()).isNull();
            assertThat(result.hasValue()).isFalse();
        }

        @Test
        @DisplayName("of() - 공백만 있는 문자열은 null로 변환")
        void of_WithWhitespaceOnly_ShouldBeNull() {
            // When
            EndpointDescription result = EndpointDescription.of("   ");

            // Then
            assertThat(result.value()).isNull();
            assertThat(result.hasValue()).isFalse();
        }

        @Test
        @DisplayName("of() - 공백이 포함된 문자열은 trim 처리")
        void of_WithWhitespace_ShouldTrim() {
            // When
            EndpointDescription result = EndpointDescription.of("  API 설명  ");

            // Then
            assertThat(result.value()).isEqualTo("API 설명");
        }

        @Test
        @DisplayName("empty() - 빈 설명 생성")
        void empty_ShouldCreateEmpty() {
            // When
            EndpointDescription result = EndpointDescription.empty();

            // Then
            assertThat(result.value()).isNull();
            assertThat(result.hasValue()).isFalse();
        }
    }

    @Nested
    @DisplayName("검증 실패 테스트")
    class ValidationFailureTests {

        @Test
        @DisplayName("501자 이상 설명은 예외 발생 (최대 500자)")
        void of_TooLong_ShouldThrowException() {
            // Given
            String longDescription = "a".repeat(501);

            // Then
            assertThatThrownBy(() -> EndpointDescription.of(longDescription))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최대 500자를 초과할 수 없습니다");
        }

        @Test
        @DisplayName("정확히 500자 설명은 허용")
        void of_ExactMaxLength_ShouldCreate() {
            // Given
            String exactMaxDescription = "a".repeat(500);

            // When
            EndpointDescription result = EndpointDescription.of(exactMaxDescription);

            // Then
            assertThat(result.value()).hasSize(500);
        }
    }

    @Nested
    @DisplayName("hasValue 테스트")
    class HasValueTests {

        @Test
        @DisplayName("hasValue() - 값이 있으면 true")
        void hasValue_WithValue_ShouldReturnTrue() {
            // Given
            EndpointDescription description = EndpointDescription.of("설명");

            // Then
            assertThat(description.hasValue()).isTrue();
        }

        @Test
        @DisplayName("hasValue() - 값이 없으면 false")
        void hasValue_WithoutValue_ShouldReturnFalse() {
            // Given
            EndpointDescription description = EndpointDescription.empty();

            // Then
            assertThat(description.hasValue()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTests {

        @Test
        @DisplayName("동일한 값을 가진 EndpointDescription은 동등하다")
        void equals_SameValue_ShouldBeEqual() {
            // Given
            EndpointDescription d1 = EndpointDescription.of("설명");
            EndpointDescription d2 = EndpointDescription.of("설명");

            // Then
            assertThat(d1).isEqualTo(d2);
            assertThat(d1.hashCode()).isEqualTo(d2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 EndpointDescription은 동등하지 않다")
        void equals_DifferentValue_ShouldNotBeEqual() {
            // Given
            EndpointDescription d1 = EndpointDescription.of("설명1");
            EndpointDescription d2 = EndpointDescription.of("설명2");

            // Then
            assertThat(d1).isNotEqualTo(d2);
        }

        @Test
        @DisplayName("빈 EndpointDescription들은 동등하다")
        void equals_EmptyDescriptions_ShouldBeEqual() {
            // Given
            EndpointDescription d1 = EndpointDescription.empty();
            EndpointDescription d2 = EndpointDescription.of(null);

            // Then
            assertThat(d1).isEqualTo(d2);
        }
    }
}
