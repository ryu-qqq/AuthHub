package com.ryuqq.authhub.domain.endpointpermission.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** EndpointPath Value Object 단위 테스트 */
@Tag("unit")
@Tag("domain")
@Tag("vo")
@DisplayName("EndpointPath VO 단위 테스트")
class EndpointPathTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTests {

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "/",
                    "/api",
                    "/api/v1/users",
                    "/api/v1/users/{userId}",
                    "/api/v1/orders/{orderId}/items/{itemId}",
                    "/api/v1/admin/**",
                    "/api/v1/resources/*"
                })
        @DisplayName("유효한 경로로 생성 성공")
        void of_ValidPath_ShouldCreate(String path) {
            // When
            EndpointPath endpointPath = EndpointPath.of(path);

            // Then
            assertThat(endpointPath.value()).isEqualTo(path);
        }

        @Test
        @DisplayName("공백이 포함된 경로는 trim 처리")
        void of_WithWhitespace_ShouldTrim() {
            // When
            EndpointPath path = EndpointPath.of("  /api/v1/users  ");

            // Then
            assertThat(path.value()).isEqualTo("/api/v1/users");
        }
    }

    @Nested
    @DisplayName("검증 실패 테스트")
    class ValidationFailureTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("null 또는 빈 문자열은 예외 발생")
        void of_NullOrEmpty_ShouldThrowException(String path) {
            assertThatThrownBy(() -> EndpointPath.of(path))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("EndpointPath는 null이거나 빈 문자열일 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {"api/users", "users", "api", "v1/users"})
        @DisplayName("/로 시작하지 않는 경로는 예외 발생")
        void of_NotStartingWithSlash_ShouldThrowException(String path) {
            assertThatThrownBy(() -> EndpointPath.of(path))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("/로 시작해야 합니다");
        }

        @Test
        @DisplayName("501자 이상 경로는 예외 발생 (최대 500자)")
        void of_TooLong_ShouldThrowException() {
            String longPath = "/" + "a".repeat(500);
            assertThatThrownBy(() -> EndpointPath.of(longPath))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최대 500자를 초과할 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {"/api/users?query=1", "/api/users#section", "/api/users@admin"})
        @DisplayName("허용되지 않는 특수문자가 포함된 경로는 예외 발생")
        void of_InvalidCharacters_ShouldThrowException(String path) {
            assertThatThrownBy(() -> EndpointPath.of(path))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("형식이 올바르지 않습니다");
        }
    }

    @Nested
    @DisplayName("Path Variable 테스트")
    class PathVariableTests {

        @Test
        @DisplayName("Path Variable이 있으면 hasPathVariable()은 true")
        void hasPathVariable_WithVariable_ShouldReturnTrue() {
            // Given
            EndpointPath path = EndpointPath.of("/api/v1/users/{userId}");

            // Then
            assertThat(path.hasPathVariable()).isTrue();
        }

        @Test
        @DisplayName("Path Variable이 없으면 hasPathVariable()은 false")
        void hasPathVariable_WithoutVariable_ShouldReturnFalse() {
            // Given
            EndpointPath path = EndpointPath.of("/api/v1/users");

            // Then
            assertThat(path.hasPathVariable()).isFalse();
        }

        @Test
        @DisplayName("와일드카드가 있으면 hasWildcard()는 true")
        void hasWildcard_WithWildcard_ShouldReturnTrue() {
            // Given
            EndpointPath path = EndpointPath.of("/api/v1/admin/**");

            // Then
            assertThat(path.hasWildcard()).isTrue();
        }

        @Test
        @DisplayName("와일드카드가 없으면 hasWildcard()는 false")
        void hasWildcard_WithoutWildcard_ShouldReturnFalse() {
            // Given
            EndpointPath path = EndpointPath.of("/api/v1/users/{userId}");

            // Then
            assertThat(path.hasWildcard()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTests {

        @Test
        @DisplayName("동일한 값을 가진 EndpointPath는 동등하다")
        void equals_SameValue_ShouldBeEqual() {
            // Given
            EndpointPath path1 = EndpointPath.of("/api/v1/users");
            EndpointPath path2 = EndpointPath.of("/api/v1/users");

            // Then
            assertThat(path1).isEqualTo(path2);
            assertThat(path1.hashCode()).isEqualTo(path2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 EndpointPath는 동등하지 않다")
        void equals_DifferentValue_ShouldNotBeEqual() {
            // Given
            EndpointPath path1 = EndpointPath.of("/api/v1/users");
            EndpointPath path2 = EndpointPath.of("/api/v1/orders");

            // Then
            assertThat(path1).isNotEqualTo(path2);
        }
    }
}
