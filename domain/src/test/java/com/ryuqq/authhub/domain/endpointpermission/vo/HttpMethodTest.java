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

/** HttpMethod Value Object 단위 테스트 */
@Tag("unit")
@Tag("domain")
@Tag("vo")
@DisplayName("HttpMethod VO 단위 테스트")
class HttpMethodTest {

    @Nested
    @DisplayName("fromString 테스트")
    class FromStringTests {

        @ParameterizedTest
        @ValueSource(strings = {"GET", "get", "Get", "gEt"})
        @DisplayName("GET 문자열 변환 성공 (대소문자 무관)")
        void fromString_Get_ShouldReturnGet(String value) {
            assertThat(HttpMethod.fromString(value)).isEqualTo(HttpMethod.GET);
        }

        @ParameterizedTest
        @ValueSource(strings = {"POST", "post", "Post"})
        @DisplayName("POST 문자열 변환 성공")
        void fromString_Post_ShouldReturnPost(String value) {
            assertThat(HttpMethod.fromString(value)).isEqualTo(HttpMethod.POST);
        }

        @ParameterizedTest
        @ValueSource(strings = {"PUT", "put", "Put"})
        @DisplayName("PUT 문자열 변환 성공")
        void fromString_Put_ShouldReturnPut(String value) {
            assertThat(HttpMethod.fromString(value)).isEqualTo(HttpMethod.PUT);
        }

        @ParameterizedTest
        @ValueSource(strings = {"PATCH", "patch", "Patch"})
        @DisplayName("PATCH 문자열 변환 성공")
        void fromString_Patch_ShouldReturnPatch(String value) {
            assertThat(HttpMethod.fromString(value)).isEqualTo(HttpMethod.PATCH);
        }

        @ParameterizedTest
        @ValueSource(strings = {"DELETE", "delete", "Delete"})
        @DisplayName("DELETE 문자열 변환 성공")
        void fromString_Delete_ShouldReturnDelete(String value) {
            assertThat(HttpMethod.fromString(value)).isEqualTo(HttpMethod.DELETE);
        }

        @ParameterizedTest
        @ValueSource(strings = {"OPTIONS", "options", "Options"})
        @DisplayName("OPTIONS 문자열 변환 성공")
        void fromString_Options_ShouldReturnOptions(String value) {
            assertThat(HttpMethod.fromString(value)).isEqualTo(HttpMethod.OPTIONS);
        }

        @ParameterizedTest
        @ValueSource(strings = {"HEAD", "head", "Head"})
        @DisplayName("HEAD 문자열 변환 성공")
        void fromString_Head_ShouldReturnHead(String value) {
            assertThat(HttpMethod.fromString(value)).isEqualTo(HttpMethod.HEAD);
        }

        @Test
        @DisplayName("공백이 포함된 문자열은 trim 처리")
        void fromString_WithWhitespace_ShouldTrim() {
            assertThat(HttpMethod.fromString("  GET  ")).isEqualTo(HttpMethod.GET);
        }
    }

    @Nested
    @DisplayName("검증 실패 테스트")
    class ValidationFailureTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("null 또는 빈 문자열은 예외 발생")
        void fromString_NullOrEmpty_ShouldThrowException(String value) {
            assertThatThrownBy(() -> HttpMethod.fromString(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("HttpMethod는 null이거나 빈 문자열일 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {"INVALID", "CONNECT", "TRACE", "ABC"})
        @DisplayName("유효하지 않은 HTTP 메서드는 예외 발생")
        void fromString_InvalidMethod_ShouldThrowException(String value) {
            assertThatThrownBy(() -> HttpMethod.fromString(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 HTTP 메서드입니다");
        }
    }

    @Nested
    @DisplayName("메서드 분류 테스트")
    class MethodClassificationTests {

        @Test
        @DisplayName("GET, HEAD, OPTIONS는 읽기 전용 메서드")
        void isReadOnly_ReadOnlyMethods_ShouldReturnTrue() {
            assertThat(HttpMethod.GET.isReadOnly()).isTrue();
            assertThat(HttpMethod.HEAD.isReadOnly()).isTrue();
            assertThat(HttpMethod.OPTIONS.isReadOnly()).isTrue();
        }

        @Test
        @DisplayName("POST, PUT, PATCH, DELETE는 읽기 전용 메서드가 아님")
        void isReadOnly_MutatingMethods_ShouldReturnFalse() {
            assertThat(HttpMethod.POST.isReadOnly()).isFalse();
            assertThat(HttpMethod.PUT.isReadOnly()).isFalse();
            assertThat(HttpMethod.PATCH.isReadOnly()).isFalse();
            assertThat(HttpMethod.DELETE.isReadOnly()).isFalse();
        }

        @Test
        @DisplayName("POST, PUT, PATCH, DELETE는 상태 변경 메서드")
        void isMutating_MutatingMethods_ShouldReturnTrue() {
            assertThat(HttpMethod.POST.isMutating()).isTrue();
            assertThat(HttpMethod.PUT.isMutating()).isTrue();
            assertThat(HttpMethod.PATCH.isMutating()).isTrue();
            assertThat(HttpMethod.DELETE.isMutating()).isTrue();
        }

        @Test
        @DisplayName("GET, HEAD, OPTIONS는 상태 변경 메서드가 아님")
        void isMutating_ReadOnlyMethods_ShouldReturnFalse() {
            assertThat(HttpMethod.GET.isMutating()).isFalse();
            assertThat(HttpMethod.HEAD.isMutating()).isFalse();
            assertThat(HttpMethod.OPTIONS.isMutating()).isFalse();
        }
    }

    @Nested
    @DisplayName("value() 테스트")
    class ValueTests {

        @Test
        @DisplayName("value()는 대문자 HTTP 메서드 문자열 반환")
        void value_ShouldReturnUpperCaseString() {
            assertThat(HttpMethod.GET.value()).isEqualTo("GET");
            assertThat(HttpMethod.POST.value()).isEqualTo("POST");
            assertThat(HttpMethod.PUT.value()).isEqualTo("PUT");
            assertThat(HttpMethod.PATCH.value()).isEqualTo("PATCH");
            assertThat(HttpMethod.DELETE.value()).isEqualTo("DELETE");
            assertThat(HttpMethod.OPTIONS.value()).isEqualTo("OPTIONS");
            assertThat(HttpMethod.HEAD.value()).isEqualTo("HEAD");
        }
    }
}
