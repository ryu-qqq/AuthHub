package com.ryuqq.authhub.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * SortDirection enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SortDirection 테스트")
class SortDirectionTest {

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("ASC와 DESC 두 가지 값이 존재한다")
        void shouldHaveTwoValues() {
            // when
            SortDirection[] values = SortDirection.values();

            // then
            assertThat(values).hasSize(2);
            assertThat(values).contains(SortDirection.ASC, SortDirection.DESC);
        }
    }

    @Nested
    @DisplayName("defaultDirection 메서드")
    class DefaultDirectionTest {

        @Test
        @DisplayName("기본 정렬 방향은 DESC이다")
        void shouldReturnDescAsDefault() {
            // when
            SortDirection defaultDirection = SortDirection.defaultDirection();

            // then
            assertThat(defaultDirection).isEqualTo(SortDirection.DESC);
        }
    }

    @Nested
    @DisplayName("isAscending 메서드")
    class IsAscendingTest {

        @Test
        @DisplayName("ASC일 때 true를 반환한다")
        void shouldReturnTrueForAsc() {
            assertThat(SortDirection.ASC.isAscending()).isTrue();
        }

        @Test
        @DisplayName("DESC일 때 false를 반환한다")
        void shouldReturnFalseForDesc() {
            assertThat(SortDirection.DESC.isAscending()).isFalse();
        }
    }

    @Nested
    @DisplayName("isDescending 메서드")
    class IsDescendingTest {

        @Test
        @DisplayName("DESC일 때 true를 반환한다")
        void shouldReturnTrueForDesc() {
            assertThat(SortDirection.DESC.isDescending()).isTrue();
        }

        @Test
        @DisplayName("ASC일 때 false를 반환한다")
        void shouldReturnFalseForAsc() {
            assertThat(SortDirection.ASC.isDescending()).isFalse();
        }
    }

    @Nested
    @DisplayName("reverse 메서드")
    class ReverseTest {

        @Test
        @DisplayName("ASC를 reverse하면 DESC가 된다")
        void shouldReturnDescWhenReverseAsc() {
            // when
            SortDirection reversed = SortDirection.ASC.reverse();

            // then
            assertThat(reversed).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("DESC를 reverse하면 ASC가 된다")
        void shouldReturnAscWhenReverseDesc() {
            // when
            SortDirection reversed = SortDirection.DESC.reverse();

            // then
            assertThat(reversed).isEqualTo(SortDirection.ASC);
        }

        @Test
        @DisplayName("두 번 reverse하면 원래 값이 된다")
        void shouldReturnOriginalAfterDoubleReverse() {
            // when & then
            assertThat(SortDirection.ASC.reverse().reverse()).isEqualTo(SortDirection.ASC);
            assertThat(SortDirection.DESC.reverse().reverse()).isEqualTo(SortDirection.DESC);
        }
    }

    @Nested
    @DisplayName("displayName 메서드")
    class DisplayNameTest {

        @Test
        @DisplayName("ASC의 표시 이름은 '오름차순'이다")
        void shouldReturnAscDisplayName() {
            assertThat(SortDirection.ASC.displayName()).isEqualTo("오름차순");
        }

        @Test
        @DisplayName("DESC의 표시 이름은 '내림차순'이다")
        void shouldReturnDescDisplayName() {
            assertThat(SortDirection.DESC.displayName()).isEqualTo("내림차순");
        }
    }

    @Nested
    @DisplayName("fromString 메서드")
    class FromStringTest {

        @Test
        @DisplayName("'asc' 문자열로 ASC를 반환한다")
        void shouldReturnAscFromLowercaseString() {
            assertThat(SortDirection.fromString("asc")).isEqualTo(SortDirection.ASC);
        }

        @Test
        @DisplayName("'ASC' 문자열로 ASC를 반환한다")
        void shouldReturnAscFromUppercaseString() {
            assertThat(SortDirection.fromString("ASC")).isEqualTo(SortDirection.ASC);
        }

        @Test
        @DisplayName("'Asc' 문자열로 ASC를 반환한다")
        void shouldReturnAscFromMixedCaseString() {
            assertThat(SortDirection.fromString("Asc")).isEqualTo(SortDirection.ASC);
        }

        @Test
        @DisplayName("'desc' 문자열로 DESC를 반환한다")
        void shouldReturnDescFromLowercaseString() {
            assertThat(SortDirection.fromString("desc")).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("'DESC' 문자열로 DESC를 반환한다")
        void shouldReturnDescFromUppercaseString() {
            assertThat(SortDirection.fromString("DESC")).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("앞뒤 공백이 있어도 올바르게 파싱한다")
        void shouldTrimWhitespace() {
            assertThat(SortDirection.fromString("  asc  ")).isEqualTo(SortDirection.ASC);
            assertThat(SortDirection.fromString("  desc  ")).isEqualTo(SortDirection.DESC);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null이나 빈 문자열은 기본값(DESC)을 반환한다")
        void shouldReturnDefaultForNullOrEmpty(String value) {
            assertThat(SortDirection.fromString(value)).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("공백만 있는 문자열은 기본값(DESC)을 반환한다")
        void shouldReturnDefaultForBlankString() {
            assertThat(SortDirection.fromString("   ")).isEqualTo(SortDirection.DESC);
        }

        @ParameterizedTest
        @ValueSource(strings = {"ascending", "descending", "up", "down", "invalid", "123"})
        @DisplayName("유효하지 않은 문자열은 기본값(DESC)을 반환한다")
        void shouldReturnDefaultForInvalidString(String invalidValue) {
            assertThat(SortDirection.fromString(invalidValue)).isEqualTo(SortDirection.DESC);
        }
    }

    @Nested
    @DisplayName("일관성 테스트")
    class ConsistencyTest {

        @Test
        @DisplayName("isAscending과 isDescending은 상호 배타적이다")
        void shouldBeMutuallyExclusive() {
            // ASC
            assertThat(SortDirection.ASC.isAscending()).isTrue();
            assertThat(SortDirection.ASC.isDescending()).isFalse();

            // DESC
            assertThat(SortDirection.DESC.isAscending()).isFalse();
            assertThat(SortDirection.DESC.isDescending()).isTrue();
        }

        @Test
        @DisplayName("모든 enum 값은 유효한 displayName을 가진다")
        void shouldHaveValidDisplayNames() {
            for (SortDirection direction : SortDirection.values()) {
                assertThat(direction.displayName()).isNotBlank();
            }
        }
    }
}
