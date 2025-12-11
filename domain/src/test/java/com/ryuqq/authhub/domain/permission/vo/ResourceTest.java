package com.ryuqq.authhub.domain.permission.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Resource 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("Resource 테스트")
class ResourceTest {

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("유효한 리소스 이름을 생성한다")
        void shouldCreateValidResource() {
            // when
            Resource resource = Resource.of("user");

            // then
            assertThat(resource).isNotNull();
            assertThat(resource.value()).isEqualTo("user");
        }

        @Test
        @DisplayName("대문자를 소문자로 변환한다")
        void shouldConvertToLowerCase() {
            // when
            Resource resource = Resource.of("USER");

            // then
            assertThat(resource.value()).isEqualTo("user");
        }

        @Test
        @DisplayName("언더스코어가 포함된 이름을 허용한다")
        void shouldAllowUnderscore() {
            // when
            Resource resource = Resource.of("user_profile");

            // then
            assertThat(resource.value()).isEqualTo("user_profile");
        }

        @Test
        @DisplayName("하이픈이 포함된 이름을 허용한다")
        void shouldAllowHyphen() {
            // when
            Resource resource = Resource.of("user-profile");

            // then
            assertThat(resource.value()).isEqualTo("user-profile");
        }

        @Test
        @DisplayName("숫자가 포함된 이름을 허용한다")
        void shouldAllowNumbers() {
            // when
            Resource resource = Resource.of("user2");

            // then
            assertThat(resource.value()).isEqualTo("user2");
        }

        @Test
        @DisplayName("공백을 트림한다")
        void shouldTrimWhitespace() {
            // when
            Resource resource = Resource.of("  user  ");

            // then
            assertThat(resource.value()).isEqualTo("user");
        }

        @Test
        @DisplayName("null이면 예외 발생")
        void shouldThrowExceptionWhenNull() {
            assertThatThrownBy(() -> Resource.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 문자열");
        }

        @Test
        @DisplayName("빈 문자열이면 예외 발생")
        void shouldThrowExceptionWhenEmpty() {
            assertThatThrownBy(() -> Resource.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 문자열");
        }

        @Test
        @DisplayName("50자 초과하면 예외 발생")
        void shouldThrowExceptionWhenTooLong() {
            String longName = "a".repeat(51);
            assertThatThrownBy(() -> Resource.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자 이하");
        }

        @Test
        @DisplayName("숫자로 시작하면 예외 발생")
        void shouldThrowExceptionWhenStartsWithNumber() {
            assertThatThrownBy(() -> Resource.of("1user"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("소문자로 시작");
        }

        @Test
        @DisplayName("특수문자가 포함되면 예외 발생")
        void shouldThrowExceptionWhenContainsSpecialCharacters() {
            assertThatThrownBy(() -> Resource.of("user@profile"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("소문자, 숫자, 언더스코어, 하이픈");
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 Resource는 동일하다")
        void shouldBeEqualWhenSameValue() {
            // given
            Resource resource1 = Resource.of("user");
            Resource resource2 = Resource.of("user");

            // then
            assertThat(resource1).isEqualTo(resource2);
            assertThat(resource1.hashCode()).isEqualTo(resource2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 Resource는 다르다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            Resource resource1 = Resource.of("user");
            Resource resource2 = Resource.of("organization");

            // then
            assertThat(resource1).isNotEqualTo(resource2);
        }

        @Test
        @DisplayName("대소문자 구분 없이 같으면 동일하다")
        void shouldBeEqualWhenSameValueIgnoreCase() {
            // given
            Resource resource1 = Resource.of("user");
            Resource resource2 = Resource.of("USER");

            // then
            assertThat(resource1).isEqualTo(resource2);
        }
    }
}
