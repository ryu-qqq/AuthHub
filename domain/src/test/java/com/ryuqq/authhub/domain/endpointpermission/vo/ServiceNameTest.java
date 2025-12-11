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

/** ServiceName Value Object 단위 테스트 */
@Tag("unit")
@Tag("domain")
@Tag("vo")
@DisplayName("ServiceName VO 단위 테스트")
class ServiceNameTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTests {

        @ParameterizedTest
        @ValueSource(strings = {"auth-hub", "user-service", "order-api", "ab", "a1", "service123"})
        @DisplayName("유효한 서비스 이름으로 생성 성공")
        void of_ValidServiceName_ShouldCreate(String name) {
            // When
            ServiceName serviceName = ServiceName.of(name);

            // Then
            assertThat(serviceName.value()).isEqualTo(name.toLowerCase());
        }

        @Test
        @DisplayName("대문자가 포함된 서비스 이름은 소문자로 변환")
        void of_UpperCase_ShouldConvertToLowerCase() {
            // When
            ServiceName serviceName = ServiceName.of("Auth-Hub");

            // Then
            assertThat(serviceName.value()).isEqualTo("auth-hub");
        }

        @Test
        @DisplayName("공백이 포함된 서비스 이름은 trim 처리")
        void of_WithWhitespace_ShouldTrim() {
            // When
            ServiceName serviceName = ServiceName.of("  auth-hub  ");

            // Then
            assertThat(serviceName.value()).isEqualTo("auth-hub");
        }
    }

    @Nested
    @DisplayName("검증 실패 테스트")
    class ValidationFailureTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("null 또는 빈 문자열은 예외 발생")
        void of_NullOrEmpty_ShouldThrowException(String name) {
            assertThatThrownBy(() -> ServiceName.of(name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ServiceName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("1자 서비스 이름은 예외 발생 (최소 2자)")
        void of_SingleCharacter_ShouldThrowException() {
            assertThatThrownBy(() -> ServiceName.of("a"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최소 2자 이상이어야 합니다");
        }

        @Test
        @DisplayName("51자 이상 서비스 이름은 예외 발생 (최대 50자)")
        void of_TooLong_ShouldThrowException() {
            String longName = "a".repeat(51);
            assertThatThrownBy(() -> ServiceName.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최대 50자를 초과할 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {"1service", "-service", "_service", "123"})
        @DisplayName("영문 소문자로 시작하지 않는 서비스 이름은 예외 발생")
        void of_NotStartingWithLetter_ShouldThrowException(String name) {
            assertThatThrownBy(() -> ServiceName.of(name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자로 시작");
        }

        @ParameterizedTest
        @ValueSource(strings = {"auth_hub", "auth.hub", "auth hub", "auth@hub"})
        @DisplayName("허용되지 않는 특수문자가 포함된 서비스 이름은 예외 발생")
        void of_InvalidCharacters_ShouldThrowException(String name) {
            assertThatThrownBy(() -> ServiceName.of(name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자, 숫자, 하이픈만 포함");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTests {

        @Test
        @DisplayName("동일한 값을 가진 ServiceName은 동등하다")
        void equals_SameValue_ShouldBeEqual() {
            // Given
            ServiceName name1 = ServiceName.of("auth-hub");
            ServiceName name2 = ServiceName.of("auth-hub");

            // Then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("대소문자가 다른 ServiceName도 동등하다 (소문자 변환)")
        void equals_DifferentCase_ShouldBeEqual() {
            // Given
            ServiceName name1 = ServiceName.of("auth-hub");
            ServiceName name2 = ServiceName.of("AUTH-HUB");

            // Then
            assertThat(name1).isEqualTo(name2);
        }
    }
}
