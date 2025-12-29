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
 * SearchType enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SearchType 테스트")
class SearchTypeTest {

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("PREFIX_LIKE, CONTAINS_LIKE, MATCH_AGAINST 세 가지 값이 존재한다")
        void shouldHaveThreeValues() {
            // when
            SearchType[] values = SearchType.values();

            // then
            assertThat(values).hasSize(3);
            assertThat(values)
                    .contains(
                            SearchType.PREFIX_LIKE,
                            SearchType.CONTAINS_LIKE,
                            SearchType.MATCH_AGAINST);
        }
    }

    @Nested
    @DisplayName("defaultType 메서드")
    class DefaultTypeTest {

        @Test
        @DisplayName("기본 검색 방식은 CONTAINS_LIKE이다")
        void shouldReturnContainsLikeAsDefault() {
            // when
            SearchType defaultType = SearchType.defaultType();

            // then
            assertThat(defaultType).isEqualTo(SearchType.CONTAINS_LIKE);
        }
    }

    @Nested
    @DisplayName("forLargeData 메서드")
    class ForLargeDataTest {

        @Test
        @DisplayName("대용량 데이터용 검색 방식은 MATCH_AGAINST이다")
        void shouldReturnMatchAgainstForLargeData() {
            // when
            SearchType largeDataType = SearchType.forLargeData();

            // then
            assertThat(largeDataType).isEqualTo(SearchType.MATCH_AGAINST);
        }
    }

    @Nested
    @DisplayName("forIndexOptimized 메서드")
    class ForIndexOptimizedTest {

        @Test
        @DisplayName("인덱스 최적화 검색 방식은 PREFIX_LIKE이다")
        void shouldReturnPrefixLikeForIndexOptimized() {
            // when
            SearchType indexOptimizedType = SearchType.forIndexOptimized();

            // then
            assertThat(indexOptimizedType).isEqualTo(SearchType.PREFIX_LIKE);
        }
    }

    @Nested
    @DisplayName("isLikeBased 메서드")
    class IsLikeBasedTest {

        @Test
        @DisplayName("PREFIX_LIKE는 LIKE 기반 검색이다")
        void shouldReturnTrueForPrefixLike() {
            assertThat(SearchType.PREFIX_LIKE.isLikeBased()).isTrue();
        }

        @Test
        @DisplayName("CONTAINS_LIKE는 LIKE 기반 검색이다")
        void shouldReturnTrueForContainsLike() {
            assertThat(SearchType.CONTAINS_LIKE.isLikeBased()).isTrue();
        }

        @Test
        @DisplayName("MATCH_AGAINST는 LIKE 기반 검색이 아니다")
        void shouldReturnFalseForMatchAgainst() {
            assertThat(SearchType.MATCH_AGAINST.isLikeBased()).isFalse();
        }
    }

    @Nested
    @DisplayName("isFullTextSearch 메서드")
    class IsFullTextSearchTest {

        @Test
        @DisplayName("MATCH_AGAINST는 전문 검색이다")
        void shouldReturnTrueForMatchAgainst() {
            assertThat(SearchType.MATCH_AGAINST.isFullTextSearch()).isTrue();
        }

        @Test
        @DisplayName("PREFIX_LIKE는 전문 검색이 아니다")
        void shouldReturnFalseForPrefixLike() {
            assertThat(SearchType.PREFIX_LIKE.isFullTextSearch()).isFalse();
        }

        @Test
        @DisplayName("CONTAINS_LIKE는 전문 검색이 아니다")
        void shouldReturnFalseForContainsLike() {
            assertThat(SearchType.CONTAINS_LIKE.isFullTextSearch()).isFalse();
        }
    }

    @Nested
    @DisplayName("isIndexable 메서드")
    class IsIndexableTest {

        @Test
        @DisplayName("PREFIX_LIKE는 인덱스 활용이 가능하다")
        void shouldReturnTrueForPrefixLike() {
            assertThat(SearchType.PREFIX_LIKE.isIndexable()).isTrue();
        }

        @Test
        @DisplayName("MATCH_AGAINST는 인덱스 활용이 가능하다")
        void shouldReturnTrueForMatchAgainst() {
            assertThat(SearchType.MATCH_AGAINST.isIndexable()).isTrue();
        }

        @Test
        @DisplayName("CONTAINS_LIKE는 인덱스 활용이 불가능하다")
        void shouldReturnFalseForContainsLike() {
            assertThat(SearchType.CONTAINS_LIKE.isIndexable()).isFalse();
        }
    }

    @Nested
    @DisplayName("displayName 메서드")
    class DisplayNameTest {

        @Test
        @DisplayName("PREFIX_LIKE의 표시 이름은 '시작 문자 검색'이다")
        void shouldReturnPrefixLikeDisplayName() {
            assertThat(SearchType.PREFIX_LIKE.displayName()).isEqualTo("시작 문자 검색");
        }

        @Test
        @DisplayName("CONTAINS_LIKE의 표시 이름은 '포함 검색'이다")
        void shouldReturnContainsLikeDisplayName() {
            assertThat(SearchType.CONTAINS_LIKE.displayName()).isEqualTo("포함 검색");
        }

        @Test
        @DisplayName("MATCH_AGAINST의 표시 이름은 '전문 검색'이다")
        void shouldReturnMatchAgainstDisplayName() {
            assertThat(SearchType.MATCH_AGAINST.displayName()).isEqualTo("전문 검색");
        }
    }

    @Nested
    @DisplayName("fromString 메서드")
    class FromStringTest {

        @Test
        @DisplayName("'prefix_like' 문자열로 PREFIX_LIKE를 반환한다")
        void shouldReturnPrefixLikeFromLowercaseString() {
            assertThat(SearchType.fromString("prefix_like")).isEqualTo(SearchType.PREFIX_LIKE);
        }

        @Test
        @DisplayName("'PREFIX_LIKE' 문자열로 PREFIX_LIKE를 반환한다")
        void shouldReturnPrefixLikeFromUppercaseString() {
            assertThat(SearchType.fromString("PREFIX_LIKE")).isEqualTo(SearchType.PREFIX_LIKE);
        }

        @Test
        @DisplayName("'contains_like' 문자열로 CONTAINS_LIKE를 반환한다")
        void shouldReturnContainsLikeFromLowercaseString() {
            assertThat(SearchType.fromString("contains_like")).isEqualTo(SearchType.CONTAINS_LIKE);
        }

        @Test
        @DisplayName("'match_against' 문자열로 MATCH_AGAINST를 반환한다")
        void shouldReturnMatchAgainstFromLowercaseString() {
            assertThat(SearchType.fromString("match_against")).isEqualTo(SearchType.MATCH_AGAINST);
        }

        @Test
        @DisplayName("앞뒤 공백이 있어도 올바르게 파싱한다")
        void shouldTrimWhitespace() {
            assertThat(SearchType.fromString("  prefix_like  ")).isEqualTo(SearchType.PREFIX_LIKE);
            assertThat(SearchType.fromString("  MATCH_AGAINST  "))
                    .isEqualTo(SearchType.MATCH_AGAINST);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null이나 빈 문자열은 기본값(CONTAINS_LIKE)을 반환한다")
        void shouldReturnDefaultForNullOrEmpty(String value) {
            assertThat(SearchType.fromString(value)).isEqualTo(SearchType.CONTAINS_LIKE);
        }

        @Test
        @DisplayName("공백만 있는 문자열은 기본값(CONTAINS_LIKE)을 반환한다")
        void shouldReturnDefaultForBlankString() {
            assertThat(SearchType.fromString("   ")).isEqualTo(SearchType.CONTAINS_LIKE);
        }

        @ParameterizedTest
        @ValueSource(strings = {"like", "prefix", "contains", "match", "invalid", "123"})
        @DisplayName("유효하지 않은 문자열은 기본값(CONTAINS_LIKE)을 반환한다")
        void shouldReturnDefaultForInvalidString(String invalidValue) {
            assertThat(SearchType.fromString(invalidValue)).isEqualTo(SearchType.CONTAINS_LIKE);
        }
    }

    @Nested
    @DisplayName("일관성 테스트")
    class ConsistencyTest {

        @Test
        @DisplayName("isLikeBased와 isFullTextSearch는 상호 배타적이다")
        void shouldBeMutuallyExclusive() {
            // PREFIX_LIKE
            assertThat(SearchType.PREFIX_LIKE.isLikeBased()).isTrue();
            assertThat(SearchType.PREFIX_LIKE.isFullTextSearch()).isFalse();

            // CONTAINS_LIKE
            assertThat(SearchType.CONTAINS_LIKE.isLikeBased()).isTrue();
            assertThat(SearchType.CONTAINS_LIKE.isFullTextSearch()).isFalse();

            // MATCH_AGAINST
            assertThat(SearchType.MATCH_AGAINST.isLikeBased()).isFalse();
            assertThat(SearchType.MATCH_AGAINST.isFullTextSearch()).isTrue();
        }

        @Test
        @DisplayName("모든 enum 값은 유효한 displayName을 가진다")
        void shouldHaveValidDisplayNames() {
            for (SearchType type : SearchType.values()) {
                assertThat(type.displayName()).isNotBlank();
            }
        }

        @Test
        @DisplayName("LIKE 기반 검색과 전문 검색은 함께 true가 될 수 없다")
        void likeAndFullTextShouldNotBothBeTrue() {
            for (SearchType type : SearchType.values()) {
                if (type.isLikeBased()) {
                    assertThat(type.isFullTextSearch()).isFalse();
                }
                if (type.isFullTextSearch()) {
                    assertThat(type.isLikeBased()).isFalse();
                }
            }
        }
    }
}
