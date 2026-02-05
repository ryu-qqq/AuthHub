package com.ryuqq.authhub.domain.permission.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Resource Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("Resource 테스트")
class ResourceTest {

    @Nested
    @DisplayName("Resource 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaOfFactoryMethod() {
            // when
            Resource resource = Resource.of("user");

            // then
            assertThat(resource.value()).isEqualTo("user");
        }

        @Test
        @DisplayName("forNew() 팩토리 메서드로 생성한다")
        void shouldCreateViaForNewFactoryMethod() {
            // when
            Resource resource = Resource.forNew("user");

            // then
            assertThat(resource.value()).isEqualTo("user");
        }

        @Test
        @DisplayName("reconstitute() 팩토리 메서드로 생성한다")
        void shouldCreateViaReconstituteFactoryMethod() {
            // when
            Resource resource = Resource.reconstitute("user");

            // then
            assertThat(resource.value()).isEqualTo("user");
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            Resource resource = new Resource("user");

            // then
            assertThat(resource.value()).isEqualTo("user");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> Resource.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> Resource.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsWhitespace() {
            // when & then
            assertThatThrownBy(() -> Resource.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("최대 길이(50자)로 생성할 수 있다")
        void shouldCreateWithMaxLength() {
            // given
            String maxLengthValue = "a".repeat(50);

            // when
            Resource resource = Resource.of(maxLengthValue);

            // then
            assertThat(resource.value()).isEqualTo(maxLengthValue);
        }

        @Test
        @DisplayName("최대 길이를 초과하면 예외가 발생한다")
        void shouldThrowExceptionWhenExceedsMaxLength() {
            // given
            String tooLongValue = "a".repeat(51);

            // when & then
            assertThatThrownBy(() -> Resource.of(tooLongValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자를 초과할 수 없습니다");
        }

        @Test
        @DisplayName("영문 소문자로 시작하는 유효한 값으로 생성할 수 있다")
        void shouldCreateWithValidPattern() {
            // when
            Resource resource1 = Resource.of("user");
            Resource resource2 = Resource.of("role");
            Resource resource3 = Resource.of("organization");

            // then
            assertThat(resource1.value()).isEqualTo("user");
            assertThat(resource2.value()).isEqualTo("role");
            assertThat(resource3.value()).isEqualTo("organization");
        }

        @Test
        @DisplayName("숫자가 포함된 유효한 값으로 생성할 수 있다")
        void shouldCreateWithValidPatternIncludingNumbers() {
            // when
            Resource resource = Resource.of("user123");

            // then
            assertThat(resource.value()).isEqualTo("user123");
        }

        @Test
        @DisplayName("하이픈이 포함된 유효한 값으로 생성할 수 있다")
        void shouldCreateWithValidPatternIncludingHyphen() {
            // when
            Resource resource = Resource.of("user-role");

            // then
            assertThat(resource.value()).isEqualTo("user-role");
        }

        @Test
        @DisplayName("대문자로 시작하면 예외가 발생한다")
        void shouldThrowExceptionWhenStartsWithUpperCase() {
            // when & then
            assertThatThrownBy(() -> Resource.of("User"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자로 시작");
        }

        @Test
        @DisplayName("숫자로 시작하면 예외가 발생한다")
        void shouldThrowExceptionWhenStartsWithNumber() {
            // when & then
            assertThatThrownBy(() -> Resource.of("1user"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자로 시작");
        }

        @Test
        @DisplayName("하이픈으로 시작하면 예외가 발생한다")
        void shouldThrowExceptionWhenStartsWithHyphen() {
            // when & then
            assertThatThrownBy(() -> Resource.of("-user"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자로 시작");
        }

        @Test
        @DisplayName("특수문자가 포함되면 예외가 발생한다")
        void shouldThrowExceptionWhenContainsSpecialCharacters() {
            // when & then
            assertThatThrownBy(() -> Resource.of("user_data"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자, 숫자, 하이픈만 허용됩니다");
        }

        @Test
        @DisplayName("공백이 포함되면 예외가 발생한다")
        void shouldThrowExceptionWhenContainsWhitespace() {
            // when & then
            assertThatThrownBy(() -> Resource.of("user role"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자, 숫자, 하이픈만 허용됩니다");
        }
    }

    @Nested
    @DisplayName("Resource equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 Resource는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            Resource resource1 = Resource.of("user");
            Resource resource2 = Resource.of("user");

            // then
            assertThat(resource1).isEqualTo(resource2);
            assertThat(resource1.hashCode()).isEqualTo(resource2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 Resource는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            Resource resource1 = Resource.of("user");
            Resource resource2 = Resource.of("role");

            // then
            assertThat(resource1).isNotEqualTo(resource2);
        }

        @Test
        @DisplayName("of()와 forNew()로 생성한 Resource는 동등하다")
        void shouldBeEqualWhenCreatedViaDifferentFactoryMethods() {
            // given
            Resource resource1 = Resource.of("user");
            Resource resource2 = Resource.forNew("user");

            // then
            assertThat(resource1).isEqualTo(resource2);
        }

        @Test
        @DisplayName("of()와 reconstitute()로 생성한 Resource는 동등하다")
        void shouldBeEqualWhenCreatedViaOfAndReconstitute() {
            // given
            Resource resource1 = Resource.of("user");
            Resource resource2 = Resource.reconstitute("user");

            // then
            assertThat(resource1).isEqualTo(resource2);
        }
    }
}
