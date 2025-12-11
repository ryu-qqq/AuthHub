package com.ryuqq.authhub.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DateRange VO 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DateRange 테스트")
class DateRangeTest {

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("유효한 날짜 범위로 DateRange를 생성한다")
        void shouldCreateDateRangeWithValidRange() {
            // given
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 12, 31);

            // when
            DateRange dateRange = new DateRange(startDate, endDate);

            // then
            assertThat(dateRange.startDate()).isEqualTo(startDate);
            assertThat(dateRange.endDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("시작일과 종료일이 같아도 생성된다")
        void shouldCreateDateRangeWhenStartEqualsEnd() {
            // given
            LocalDate sameDate = LocalDate.of(2024, 6, 15);

            // when
            DateRange dateRange = new DateRange(sameDate, sameDate);

            // then
            assertThat(dateRange.startDate()).isEqualTo(sameDate);
            assertThat(dateRange.endDate()).isEqualTo(sameDate);
        }

        @Test
        @DisplayName("시작일만 있고 종료일이 null이어도 생성된다")
        void shouldCreateDateRangeWithNullEndDate() {
            // given
            LocalDate startDate = LocalDate.of(2024, 1, 1);

            // when
            DateRange dateRange = new DateRange(startDate, null);

            // then
            assertThat(dateRange.startDate()).isEqualTo(startDate);
            assertThat(dateRange.endDate()).isNull();
        }

        @Test
        @DisplayName("종료일만 있고 시작일이 null이어도 생성된다")
        void shouldCreateDateRangeWithNullStartDate() {
            // given
            LocalDate endDate = LocalDate.of(2024, 12, 31);

            // when
            DateRange dateRange = new DateRange(null, endDate);

            // then
            assertThat(dateRange.startDate()).isNull();
            assertThat(dateRange.endDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("시작일이 종료일보다 늦으면 예외가 발생한다")
        void shouldThrowExceptionWhenStartAfterEnd() {
            // given
            LocalDate startDate = LocalDate.of(2024, 12, 31);
            LocalDate endDate = LocalDate.of(2024, 1, 1);

            // when & then
            assertThatThrownBy(() -> new DateRange(startDate, endDate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("시작일")
                    .hasMessageContaining("종료일");
        }
    }

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("정적 팩토리 메서드로 DateRange를 생성한다")
        void shouldCreateDateRangeUsingOf() {
            // given
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 12, 31);

            // when
            DateRange dateRange = DateRange.of(startDate, endDate);

            // then
            assertThat(dateRange.startDate()).isEqualTo(startDate);
            assertThat(dateRange.endDate()).isEqualTo(endDate);
        }
    }

    @Nested
    @DisplayName("lastDays 팩토리 메서드")
    class LastDaysTest {

        @Test
        @DisplayName("최근 N일 범위를 생성한다")
        void shouldCreateLastDaysRange() {
            // given
            int days = 7;
            LocalDate today = LocalDate.now();

            // when
            DateRange dateRange = DateRange.lastDays(days);

            // then
            assertThat(dateRange.startDate()).isEqualTo(today.minusDays(days));
            assertThat(dateRange.endDate()).isEqualTo(today);
        }

        @Test
        @DisplayName("0일을 전달하면 오늘만 포함하는 범위를 생성한다")
        void shouldCreateTodayOnlyRangeWhenZeroDays() {
            // given
            LocalDate today = LocalDate.now();

            // when
            DateRange dateRange = DateRange.lastDays(0);

            // then
            assertThat(dateRange.startDate()).isEqualTo(today);
            assertThat(dateRange.endDate()).isEqualTo(today);
        }

        @Test
        @DisplayName("음수를 전달하면 예외가 발생한다")
        void shouldThrowExceptionWhenNegativeDays() {
            assertThatThrownBy(() -> DateRange.lastDays(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0 이상");
        }
    }

    @Nested
    @DisplayName("thisMonth 팩토리 메서드")
    class ThisMonthTest {

        @Test
        @DisplayName("이번 달 범위를 생성한다")
        void shouldCreateThisMonthRange() {
            // given
            LocalDate today = LocalDate.now();
            LocalDate expectedStart = today.withDayOfMonth(1);
            LocalDate expectedEnd = today.withDayOfMonth(today.lengthOfMonth());

            // when
            DateRange dateRange = DateRange.thisMonth();

            // then
            assertThat(dateRange.startDate()).isEqualTo(expectedStart);
            assertThat(dateRange.endDate()).isEqualTo(expectedEnd);
        }
    }

    @Nested
    @DisplayName("lastMonth 팩토리 메서드")
    class LastMonthTest {

        @Test
        @DisplayName("지난 달 범위를 생성한다")
        void shouldCreateLastMonthRange() {
            // given
            LocalDate today = LocalDate.now();
            LocalDate expectedStart = today.minusMonths(1).withDayOfMonth(1);
            LocalDate expectedEnd = today.withDayOfMonth(1).minusDays(1);

            // when
            DateRange dateRange = DateRange.lastMonth();

            // then
            assertThat(dateRange.startDate()).isEqualTo(expectedStart);
            assertThat(dateRange.endDate()).isEqualTo(expectedEnd);
        }
    }

    @Nested
    @DisplayName("until 팩토리 메서드")
    class UntilTest {

        @Test
        @DisplayName("종료일까지의 범위를 생성한다 (시작일 없음)")
        void shouldCreateUntilRange() {
            // given
            LocalDate endDate = LocalDate.of(2024, 12, 31);

            // when
            DateRange dateRange = DateRange.until(endDate);

            // then
            assertThat(dateRange.startDate()).isNull();
            assertThat(dateRange.endDate()).isEqualTo(endDate);
        }
    }

    @Nested
    @DisplayName("from 팩토리 메서드")
    class FromTest {

        @Test
        @DisplayName("시작일부터의 범위를 생성한다 (종료일 없음)")
        void shouldCreateFromRange() {
            // given
            LocalDate startDate = LocalDate.of(2024, 1, 1);

            // when
            DateRange dateRange = DateRange.from(startDate);

            // then
            assertThat(dateRange.startDate()).isEqualTo(startDate);
            assertThat(dateRange.endDate()).isNull();
        }
    }

    @Nested
    @DisplayName("startInstant 메서드")
    class StartInstantTest {

        @Test
        @DisplayName("시작일을 Instant로 변환한다")
        void shouldConvertStartDateToInstant() {
            // given
            LocalDate startDate = LocalDate.of(2024, 1, 15);
            DateRange dateRange = DateRange.of(startDate, LocalDate.of(2024, 12, 31));

            // when
            Instant instant = dateRange.startInstant();

            // then
            Instant expected = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            assertThat(instant).isEqualTo(expected);
        }

        @Test
        @DisplayName("시작일이 null이면 null을 반환한다")
        void shouldReturnNullWhenStartDateIsNull() {
            // given
            DateRange dateRange = DateRange.until(LocalDate.of(2024, 12, 31));

            // when
            Instant instant = dateRange.startInstant();

            // then
            assertThat(instant).isNull();
        }
    }

    @Nested
    @DisplayName("endInstant 메서드")
    class EndInstantTest {

        @Test
        @DisplayName("종료일을 Instant로 변환한다 (23:59:59.999999999)")
        void shouldConvertEndDateToInstant() {
            // given
            LocalDate endDate = LocalDate.of(2024, 1, 15);
            DateRange dateRange = DateRange.of(LocalDate.of(2024, 1, 1), endDate);

            // when
            Instant instant = dateRange.endInstant();

            // then
            Instant expected =
                    endDate.plusDays(1)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .minusNanos(1);
            assertThat(instant).isEqualTo(expected);
        }

        @Test
        @DisplayName("종료일이 null이면 null을 반환한다")
        void shouldReturnNullWhenEndDateIsNull() {
            // given
            DateRange dateRange = DateRange.from(LocalDate.of(2024, 1, 1));

            // when
            Instant instant = dateRange.endInstant();

            // then
            assertThat(instant).isNull();
        }
    }

    @Nested
    @DisplayName("isEmpty 메서드")
    class IsEmptyTest {

        @Test
        @DisplayName("시작일과 종료일 모두 null이면 비어있다")
        void shouldReturnTrueWhenBothNull() {
            // given
            DateRange dateRange = new DateRange(null, null);

            // when & then
            assertThat(dateRange.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("시작일이 있으면 비어있지 않다")
        void shouldReturnFalseWhenStartDateExists() {
            // given
            DateRange dateRange = DateRange.from(LocalDate.of(2024, 1, 1));

            // when & then
            assertThat(dateRange.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("종료일이 있으면 비어있지 않다")
        void shouldReturnFalseWhenEndDateExists() {
            // given
            DateRange dateRange = DateRange.until(LocalDate.of(2024, 12, 31));

            // when & then
            assertThat(dateRange.isEmpty()).isFalse();
        }
    }

    @Nested
    @DisplayName("contains 메서드")
    class ContainsTest {

        @Test
        @DisplayName("범위 내의 날짜에 대해 true를 반환한다")
        void shouldReturnTrueWhenDateIsWithinRange() {
            // given
            DateRange dateRange =
                    DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            LocalDate targetDate = LocalDate.of(2024, 6, 15);

            // when & then
            assertThat(dateRange.contains(targetDate)).isTrue();
        }

        @Test
        @DisplayName("시작일에 대해 true를 반환한다")
        void shouldReturnTrueWhenDateIsStartDate() {
            // given
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            DateRange dateRange = DateRange.of(startDate, LocalDate.of(2024, 12, 31));

            // when & then
            assertThat(dateRange.contains(startDate)).isTrue();
        }

        @Test
        @DisplayName("종료일에 대해 true를 반환한다")
        void shouldReturnTrueWhenDateIsEndDate() {
            // given
            LocalDate endDate = LocalDate.of(2024, 12, 31);
            DateRange dateRange = DateRange.of(LocalDate.of(2024, 1, 1), endDate);

            // when & then
            assertThat(dateRange.contains(endDate)).isTrue();
        }

        @Test
        @DisplayName("범위 이전의 날짜에 대해 false를 반환한다")
        void shouldReturnFalseWhenDateIsBeforeRange() {
            // given
            DateRange dateRange =
                    DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            LocalDate beforeDate = LocalDate.of(2023, 12, 31);

            // when & then
            assertThat(dateRange.contains(beforeDate)).isFalse();
        }

        @Test
        @DisplayName("범위 이후의 날짜에 대해 false를 반환한다")
        void shouldReturnFalseWhenDateIsAfterRange() {
            // given
            DateRange dateRange =
                    DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            LocalDate afterDate = LocalDate.of(2025, 1, 1);

            // when & then
            assertThat(dateRange.contains(afterDate)).isFalse();
        }

        @Test
        @DisplayName("null 날짜에 대해 false를 반환한다")
        void shouldReturnFalseWhenDateIsNull() {
            // given
            DateRange dateRange =
                    DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

            // when & then
            assertThat(dateRange.contains(null)).isFalse();
        }

        @Test
        @DisplayName("시작일만 있는 범위에서 시작일 이후 날짜는 포함된다")
        void shouldReturnTrueWhenDateIsAfterStartInOpenEndRange() {
            // given
            DateRange dateRange = DateRange.from(LocalDate.of(2024, 1, 1));
            LocalDate futureDate = LocalDate.of(2099, 12, 31);

            // when & then
            assertThat(dateRange.contains(futureDate)).isTrue();
        }

        @Test
        @DisplayName("종료일만 있는 범위에서 종료일 이전 날짜는 포함된다")
        void shouldReturnTrueWhenDateIsBeforeEndInOpenStartRange() {
            // given
            DateRange dateRange = DateRange.until(LocalDate.of(2024, 12, 31));
            LocalDate pastDate = LocalDate.of(1900, 1, 1);

            // when & then
            assertThat(dateRange.contains(pastDate)).isTrue();
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 날짜 범위를 가진 DateRange는 동일하다")
        void shouldBeEqualWhenSameRange() {
            // given
            DateRange range1 = DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            DateRange range2 = DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

            // then
            assertThat(range1).isEqualTo(range2);
            assertThat(range1.hashCode()).isEqualTo(range2.hashCode());
        }

        @Test
        @DisplayName("다른 날짜 범위를 가진 DateRange는 다르다")
        void shouldNotBeEqualWhenDifferentRange() {
            // given
            DateRange range1 = DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            DateRange range2 = DateRange.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30));

            // then
            assertThat(range1).isNotEqualTo(range2);
        }
    }
}
