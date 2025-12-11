package com.ryuqq.authhub.domain.permission.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionKey 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionKey 테스트")
class PermissionKeyTest {

    @Nested
    @DisplayName("of(Resource, Action) 팩토리 메서드")
    class OfResourceActionTest {

        @Test
        @DisplayName("Resource와 Action으로 PermissionKey를 생성한다")
        void shouldCreatePermissionKey() {
            // given
            Resource resource = Resource.of("user");
            Action action = Action.of("read");

            // when
            PermissionKey key = PermissionKey.of(resource, action);

            // then
            assertThat(key).isNotNull();
            assertThat(key.resource()).isEqualTo(resource);
            assertThat(key.action()).isEqualTo(action);
            assertThat(key.value()).isEqualTo("user:read");
        }

        @Test
        @DisplayName("Resource가 null이면 예외 발생")
        void shouldThrowExceptionWhenResourceNull() {
            // given
            Action action = Action.of("read");

            // when & then
            assertThatThrownBy(() -> PermissionKey.of(null, action))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Resource는 null");
        }

        @Test
        @DisplayName("Action이 null이면 예외 발생")
        void shouldThrowExceptionWhenActionNull() {
            // given
            Resource resource = Resource.of("user");

            // when & then
            assertThatThrownBy(() -> PermissionKey.of(resource, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Action은 null");
        }
    }

    @Nested
    @DisplayName("of(String) 팩토리 메서드")
    class OfStringTest {

        @Test
        @DisplayName("문자열로 PermissionKey를 생성한다")
        void shouldCreatePermissionKeyFromString() {
            // when
            PermissionKey key = PermissionKey.of("user:read");

            // then
            assertThat(key.value()).isEqualTo("user:read");
            assertThat(key.resource().value()).isEqualTo("user");
            assertThat(key.action().value()).isEqualTo("read");
        }

        @Test
        @DisplayName("복잡한 리소스와 액션 이름도 파싱한다")
        void shouldParseComplexNames() {
            // when
            PermissionKey key = PermissionKey.of("user_profile:read-all");

            // then
            assertThat(key.resource().value()).isEqualTo("user_profile");
            assertThat(key.action().value()).isEqualTo("read-all");
        }

        @Test
        @DisplayName("null 문자열이면 예외 발생")
        void shouldThrowExceptionWhenNullString() {
            assertThatThrownBy(() -> PermissionKey.of((String) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 문자열");
        }

        @Test
        @DisplayName("빈 문자열이면 예외 발생")
        void shouldThrowExceptionWhenEmptyString() {
            assertThatThrownBy(() -> PermissionKey.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 문자열");
        }

        @Test
        @DisplayName("콜론이 없으면 예외 발생")
        void shouldThrowExceptionWhenNoColon() {
            assertThatThrownBy(() -> PermissionKey.of("userread"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("{resource}:{action}");
        }

        @Test
        @DisplayName("콜론이 2개 이상이면 예외 발생")
        void shouldThrowExceptionWhenMultipleColons() {
            assertThatThrownBy(() -> PermissionKey.of("user:read:all"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("{resource}:{action}");
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 resource와 action을 가진 PermissionKey는 동일하다")
        void shouldBeEqualWhenSameResourceAndAction() {
            // given
            PermissionKey key1 = PermissionKey.of("user:read");
            PermissionKey key2 = PermissionKey.of(Resource.of("user"), Action.of("read"));

            // then
            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("다른 resource를 가진 PermissionKey는 다르다")
        void shouldNotBeEqualWhenDifferentResource() {
            // given
            PermissionKey key1 = PermissionKey.of("user:read");
            PermissionKey key2 = PermissionKey.of("organization:read");

            // then
            assertThat(key1).isNotEqualTo(key2);
        }

        @Test
        @DisplayName("다른 action을 가진 PermissionKey는 다르다")
        void shouldNotBeEqualWhenDifferentAction() {
            // given
            PermissionKey key1 = PermissionKey.of("user:read");
            PermissionKey key2 = PermissionKey.of("user:write");

            // then
            assertThat(key1).isNotEqualTo(key2);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("PermissionKey의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            PermissionKey key = PermissionKey.of("user:read");

            // when
            String toString = key.toString();

            // then
            assertThat(toString).contains("PermissionKey");
            assertThat(toString).contains("user:read");
        }
    }
}
