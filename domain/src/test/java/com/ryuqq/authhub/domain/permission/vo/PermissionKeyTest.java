package com.ryuqq.authhub.domain.permission.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionKey Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionKey 테스트")
class PermissionKeyTest {

    @Nested
    @DisplayName("PermissionKey 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of(String) 팩토리 메서드로 생성한다")
        void shouldCreateViaStringFactoryMethod() {
            // when
            PermissionKey permissionKey = PermissionKey.of("user:read");

            // then
            assertThat(permissionKey.value()).isEqualTo("user:read");
        }

        @Test
        @DisplayName("of(Resource, Action) 팩토리 메서드로 생성한다")
        void shouldCreateViaResourceActionFactoryMethod() {
            // given
            Resource resource = Resource.of("user");
            Action action = Action.of("read");

            // when
            PermissionKey permissionKey = PermissionKey.of(resource, action);

            // then
            assertThat(permissionKey.value()).isEqualTo("user:read");
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            PermissionKey permissionKey = new PermissionKey("user:read");

            // then
            assertThat(permissionKey.value()).isEqualTo("user:read");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> PermissionKey.of((String) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> PermissionKey.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("최대 길이(102자)로 생성할 수 있다")
        void shouldCreateWithMaxLength() {
            // given
            String resource = "a".repeat(50);
            String action = "b".repeat(50);
            String maxLengthValue = resource + ":" + action;

            // when
            PermissionKey permissionKey = PermissionKey.of(maxLengthValue);

            // then
            assertThat(permissionKey.value()).isEqualTo(maxLengthValue);
        }

        @Test
        @DisplayName("최대 길이를 초과하면 예외가 발생한다")
        void shouldThrowExceptionWhenExceedsMaxLength() {
            // given - 103자: resource(51) + ":" + action(51) = 103 > MAX_LENGTH(102)
            String resource = "a".repeat(51);
            String action = "b".repeat(51);
            String tooLongValue = resource + ":" + action;

            // when & then
            assertThatThrownBy(() -> PermissionKey.of(tooLongValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("102자를 초과할 수 없습니다");
        }

        @Test
        @DisplayName("올바른 형식(resource:action)으로 생성할 수 있다")
        void shouldCreateWithValidFormat() {
            // when
            PermissionKey permissionKey1 = PermissionKey.of("user:read");
            PermissionKey permissionKey2 = PermissionKey.of("order:create");
            PermissionKey permissionKey3 = PermissionKey.of("organization:manage");

            // then
            assertThat(permissionKey1.value()).isEqualTo("user:read");
            assertThat(permissionKey2.value()).isEqualTo("order:create");
            assertThat(permissionKey3.value()).isEqualTo("organization:manage");
        }

        @Test
        @DisplayName("콜론이 없으면 예외가 발생한다")
        void shouldThrowExceptionWhenMissingColon() {
            // when & then
            assertThatThrownBy(() -> PermissionKey.of("userread"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("'{resource}:{action}' 형식이어야 합니다");
        }

        @Test
        @DisplayName("콜론이 여러 개이면 예외가 발생한다")
        void shouldThrowExceptionWhenMultipleColons() {
            // when & then
            assertThatThrownBy(() -> PermissionKey.of("user:read:extra"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("'{resource}:{action}' 형식이어야 합니다");
        }

        @Test
        @DisplayName("resource 부분이 빈 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenResourceIsEmpty() {
            // when & then
            assertThatThrownBy(() -> PermissionKey.of(":read"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("'{resource}:{action}' 형식이어야 합니다");
        }

        @Test
        @DisplayName("action 부분이 빈 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenActionIsEmpty() {
            // when & then
            assertThatThrownBy(() -> PermissionKey.of("user:"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("'{resource}:{action}' 형식이어야 합니다");
        }

        @Test
        @DisplayName("대문자가 포함되면 예외가 발생한다")
        void shouldThrowExceptionWhenContainsUpperCase() {
            // when & then
            assertThatThrownBy(() -> PermissionKey.of("User:read"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("'{resource}:{action}' 형식이어야 합니다");
        }
    }

    @Nested
    @DisplayName("PermissionKey Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("extractResource()는 resource 부분을 반환한다")
        void extractResourceShouldReturnResourcePart() {
            // given
            PermissionKey permissionKey = PermissionKey.of("user:read");

            // when
            String resource = permissionKey.extractResource();

            // then
            assertThat(resource).isEqualTo("user");
        }

        @Test
        @DisplayName("extractAction()는 action 부분을 반환한다")
        void extractActionShouldReturnActionPart() {
            // given
            PermissionKey permissionKey = PermissionKey.of("user:read");

            // when
            String action = permissionKey.extractAction();

            // then
            assertThat(action).isEqualTo("read");
        }

        @Test
        @DisplayName("extractResource()는 하이픈이 포함된 resource도 올바르게 추출한다")
        void extractResourceShouldHandleHyphenatedResource() {
            // given
            PermissionKey permissionKey = PermissionKey.of("user-role:read");

            // when
            String resource = permissionKey.extractResource();

            // then
            assertThat(resource).isEqualTo("user-role");
        }

        @Test
        @DisplayName("extractAction()는 하이픈이 포함된 action도 올바르게 추출한다")
        void extractActionShouldHandleHyphenatedAction() {
            // given
            PermissionKey permissionKey = PermissionKey.of("user:read-data");

            // when
            String action = permissionKey.extractAction();

            // then
            assertThat(action).isEqualTo("read-data");
        }
    }

    @Nested
    @DisplayName("PermissionKey equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 PermissionKey는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            PermissionKey permissionKey1 = PermissionKey.of("user:read");
            PermissionKey permissionKey2 = PermissionKey.of("user:read");

            // then
            assertThat(permissionKey1).isEqualTo(permissionKey2);
            assertThat(permissionKey1.hashCode()).isEqualTo(permissionKey2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 PermissionKey는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            PermissionKey permissionKey1 = PermissionKey.of("user:read");
            PermissionKey permissionKey2 = PermissionKey.of("user:write");

            // then
            assertThat(permissionKey1).isNotEqualTo(permissionKey2);
        }

        @Test
        @DisplayName("Resource와 Action으로 생성한 PermissionKey는 문자열로 생성한 것과 동등하다")
        void shouldBeEqualWhenCreatedFromResourceAction() {
            // given
            Resource resource = Resource.of("user");
            Action action = Action.of("read");
            PermissionKey permissionKey1 = PermissionKey.of(resource, action);
            PermissionKey permissionKey2 = PermissionKey.of("user:read");

            // then
            assertThat(permissionKey1).isEqualTo(permissionKey2);
        }
    }
}
