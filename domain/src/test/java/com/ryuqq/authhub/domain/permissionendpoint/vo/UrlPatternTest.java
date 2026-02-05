package com.ryuqq.authhub.domain.permissionendpoint.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * UrlPattern Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UrlPattern Value Object 테스트")
class UrlPatternTest {

    @Nested
    @DisplayName("UrlPattern 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("정상적인 URL 패턴으로 생성한다")
        void shouldCreateWithValidPattern() {
            // when
            UrlPattern pattern = UrlPattern.of("/api/v1/users");

            // then
            assertThat(pattern.value()).isEqualTo("/api/v1/users");
        }

        @Test
        @DisplayName("경로 변수를 포함한 패턴으로 생성한다")
        void shouldCreateWithPathVariable() {
            // when
            UrlPattern pattern = UrlPattern.of("/api/v1/users/{id}");

            // then
            assertThat(pattern.value()).isEqualTo("/api/v1/users/{id}");
        }

        @Test
        @DisplayName("와일드카드를 포함한 패턴으로 생성한다")
        void shouldCreateWithWildcard() {
            // when
            UrlPattern pattern = UrlPattern.of("/api/v1/admin/**");

            // then
            assertThat(pattern.value()).isEqualTo("/api/v1/admin/**");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> UrlPattern.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> UrlPattern.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsWhitespace() {
            // when & then
            assertThatThrownBy(() -> UrlPattern.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("'/'로 시작하지 않으면 예외가 발생한다")
        void shouldThrowExceptionWhenNotStartingWithSlash() {
            // when & then
            assertThatThrownBy(() -> UrlPattern.of("api/v1/users"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("'/'로 시작해야 합니다");
        }

        @Test
        @DisplayName("500자를 초과하면 예외가 발생한다")
        void shouldThrowExceptionWhenExceedsMaxLength() {
            // given
            String longPattern = "/" + "a".repeat(500);

            // when & then
            assertThatThrownBy(() -> UrlPattern.of(longPattern))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("500자를 초과할 수 없습니다");
        }

        @Test
        @DisplayName("정확히 500자이면 생성된다")
        void shouldCreateWithExactly500Characters() {
            // given
            String pattern500 = "/" + "a".repeat(499);

            // when
            UrlPattern pattern = UrlPattern.of(pattern500);

            // then
            assertThat(pattern.value()).isEqualTo(pattern500);
            assertThat(pattern.value().length()).isEqualTo(500);
        }
    }

    @Nested
    @DisplayName("UrlPattern 매칭 테스트")
    class MatchingTests {

        @Test
        @DisplayName("정확한 URL 경로가 매칭된다")
        void shouldMatchExactUrlPath() {
            // given
            UrlPattern pattern = UrlPattern.of("/api/v1/users");

            // when & then
            assertThat(pattern.matches("/api/v1/users")).isTrue();
        }

        @Test
        @DisplayName("경로가 다르면 매칭되지 않는다")
        void shouldNotMatchDifferentPath() {
            // given
            UrlPattern pattern = UrlPattern.of("/api/v1/users");

            // when & then
            assertThat(pattern.matches("/api/v1/roles")).isFalse();
        }

        @Test
        @DisplayName("Path Variable 패턴이 URL과 매칭된다")
        void shouldMatchPathVariablePattern() {
            // given
            UrlPattern pattern = UrlPattern.of("/api/v1/users/{id}");

            // when & then
            assertThat(pattern.matches("/api/v1/users/123")).isTrue();
            assertThat(pattern.matches("/api/v1/users/abc")).isTrue();
            assertThat(pattern.matches("/api/v1/users/abc-def")).isTrue();
            assertThat(pattern.matches("/api/v1/users/01941234-5678-7000-8000-123456789abc"))
                    .isTrue();
        }

        @Test
        @DisplayName("Path Variable 패턴에서 추가 세그먼트가 있으면 매칭되지 않는다")
        void shouldNotMatchWhenExtraSegmentExists() {
            // given
            UrlPattern pattern = UrlPattern.of("/api/v1/users/{id}");

            // when & then
            assertThat(pattern.matches("/api/v1/users/123/orders")).isFalse();
        }

        @Test
        @DisplayName("중첩 Path Variable 패턴이 매칭된다")
        void shouldMatchNestedPathVariablePattern() {
            // given
            UrlPattern pattern = UrlPattern.of("/api/v1/organizations/{orgId}/members/{memberId}");

            // when & then
            assertThat(pattern.matches("/api/v1/organizations/org123/members/member456")).isTrue();
        }

        @Test
        @DisplayName("단일 와일드카드 패턴이 매칭된다")
        void shouldMatchSingleWildcardPattern() {
            // given
            UrlPattern pattern = UrlPattern.of("/api/v1/users/*");

            // when & then
            assertThat(pattern.matches("/api/v1/users/123")).isTrue();
            assertThat(pattern.matches("/api/v1/users/abc")).isTrue();
            assertThat(pattern.matches("/api/v1/users/123/orders")).isFalse();
        }

        @Test
        @DisplayName("더블 와일드카드 패턴이 모든 하위 경로를 매칭한다")
        void shouldMatchDoubleWildcardPattern() {
            // given
            UrlPattern pattern = UrlPattern.of("/api/v1/admin/**");

            // when & then
            assertThat(pattern.matches("/api/v1/admin/users")).isTrue();
            assertThat(pattern.matches("/api/v1/admin/roles/permissions")).isTrue();
            assertThat(pattern.matches("/api/v1/admin/a/b/c/d")).isTrue();
        }

        @Test
        @DisplayName("regex 메타문자가 포함된 패턴이 정확히 매칭된다")
        void shouldMatchPatternWithRegexMetaChars() {
            // given
            UrlPattern pattern = UrlPattern.of("/api.v1/users");

            // when & then
            assertThat(pattern.matches("/api.v1/users")).isTrue();
            assertThat(pattern.matches("/api/v1/users")).isFalse();
        }

        @ParameterizedTest
        @DisplayName("다양한 URL 패턴 매칭 시나리오")
        @CsvSource({
            "/api/v1/users, /api/v1/users, true",
            "/api/v1/users/{id}, /api/v1/users/123, true",
            "/api/v1/users/{id}, /api/v1/users/abc, true",
            "/api/v1/users/{id}, /api/v1/users/123/extra, false",
            "/api/v1/users, /api/v1/roles, false",
            "/api/v1/admin/**, /api/v1/admin/users, true",
            "/api/v1/admin/**, /api/v1/admin/roles/permissions, true",
        })
        void shouldMatchVariousPatterns(
                String patternStr, String requestUrl, boolean expectedMatch) {
            // given
            UrlPattern pattern = UrlPattern.of(patternStr);

            // when & then
            assertThat(pattern.matches(requestUrl)).isEqualTo(expectedMatch);
        }

        @Test
        @DisplayName("빈 URL은 매칭되지 않는다")
        void shouldNotMatchEmptyUrl() {
            // given
            UrlPattern pattern = UrlPattern.of("/api/v1/users");

            // when & then
            assertThat(pattern.matches("")).isFalse();
        }

        @Test
        @DisplayName("루트 경로만 매칭된다")
        void shouldMatchRootPath() {
            // given
            UrlPattern pattern = UrlPattern.of("/");

            // when & then
            assertThat(pattern.matches("/")).isTrue();
            assertThat(pattern.matches("/api")).isFalse();
        }
    }

    @Nested
    @DisplayName("UrlPattern 동등성 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 패턴은 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            UrlPattern pattern1 = UrlPattern.of("/api/v1/users");
            UrlPattern pattern2 = UrlPattern.of("/api/v1/users");

            // then
            assertThat(pattern1).isEqualTo(pattern2);
            assertThat(pattern1.hashCode()).isEqualTo(pattern2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 패턴은 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            UrlPattern pattern1 = UrlPattern.of("/api/v1/users");
            UrlPattern pattern2 = UrlPattern.of("/api/v1/roles");

            // then
            assertThat(pattern1).isNotEqualTo(pattern2);
        }
    }
}
