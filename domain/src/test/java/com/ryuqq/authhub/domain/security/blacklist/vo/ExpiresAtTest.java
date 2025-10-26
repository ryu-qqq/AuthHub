package com.ryuqq.authhub.domain.security.blacklist.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ExpiresAt 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("ExpiresAt 단위 테스트")
class ExpiresAtTest {

    @Test
    @DisplayName("of()로 유효한 Instant로부터 ExpiresAt을 생성할 수 있다")
    void of_ShouldCreateExpiresAtFromValidInstant() {
        // given
        final Instant now = Instant.now();

        // when
        final ExpiresAt expiresAt = ExpiresAt.of(now);

        // then
        assertThat(expiresAt).isNotNull();
        assertThat(expiresAt.value()).isEqualTo(now);
    }

    @Test
    @DisplayName("of()는 null Instant를 거부한다")
    void of_ShouldRejectNullInstant() {
        // when & then
        assertThatThrownBy(() -> ExpiresAt.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ExpiresAt value cannot be null");
    }

    @Test
    @DisplayName("fromEpochSeconds()로 epoch seconds로부터 ExpiresAt을 생성할 수 있다")
    void fromEpochSeconds_ShouldCreateExpiresAtFromEpochSeconds() {
        // given
        final long epochSeconds = 1735689600L; // 2025-01-01 00:00:00 UTC

        // when
        final ExpiresAt expiresAt = ExpiresAt.fromEpochSeconds(epochSeconds);

        // then
        assertThat(expiresAt).isNotNull();
        assertThat(expiresAt.toEpochSeconds()).isEqualTo(epochSeconds);
    }

    @Test
    @DisplayName("isExpired()는 과거 시간에 대해 true를 반환한다")
    void isExpired_ShouldReturnTrueForPastTime() {
        // given
        final Instant pastTime = Instant.now().minus(1, ChronoUnit.HOURS);
        final ExpiresAt expiresAt = ExpiresAt.of(pastTime);

        // when
        final boolean expired = expiresAt.isExpired();

        // then
        assertThat(expired).isTrue();
    }

    @Test
    @DisplayName("isExpired()는 미래 시간에 대해 false를 반환한다")
    void isExpired_ShouldReturnFalseForFutureTime() {
        // given
        final Instant futureTime = Instant.now().plus(1, ChronoUnit.HOURS);
        final ExpiresAt expiresAt = ExpiresAt.of(futureTime);

        // when
        final boolean expired = expiresAt.isExpired();

        // then
        assertThat(expired).isFalse();
    }

    @Test
    @DisplayName("isBefore()는 비교 시간보다 이전이면 true를 반환한다")
    void isBefore_ShouldReturnTrueWhenBeforeComparisonTime() {
        // given
        final Instant earlier = Instant.now();
        final Instant later = earlier.plus(1, ChronoUnit.HOURS);
        final ExpiresAt expiresAt = ExpiresAt.of(earlier);

        // when
        final boolean before = expiresAt.isBefore(later);

        // then
        assertThat(before).isTrue();
    }

    @Test
    @DisplayName("isBefore()는 비교 시간보다 이후면 false를 반환한다")
    void isBefore_ShouldReturnFalseWhenAfterComparisonTime() {
        // given
        final Instant earlier = Instant.now();
        final Instant later = earlier.plus(1, ChronoUnit.HOURS);
        final ExpiresAt expiresAt = ExpiresAt.of(later);

        // when
        final boolean before = expiresAt.isBefore(earlier);

        // then
        assertThat(before).isFalse();
    }

    @Test
    @DisplayName("isBefore()는 null 비교 시간을 거부한다")
    void isBefore_ShouldRejectNullComparisonTime() {
        // given
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now());

        // when & then
        assertThatThrownBy(() -> expiresAt.isBefore(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Comparison time cannot be null");
    }

    @Test
    @DisplayName("isAfter()는 비교 시간보다 이후면 true를 반환한다")
    void isAfter_ShouldReturnTrueWhenAfterComparisonTime() {
        // given
        final Instant earlier = Instant.now();
        final Instant later = earlier.plus(1, ChronoUnit.HOURS);
        final ExpiresAt expiresAt = ExpiresAt.of(later);

        // when
        final boolean after = expiresAt.isAfter(earlier);

        // then
        assertThat(after).isTrue();
    }

    @Test
    @DisplayName("isAfter()는 비교 시간보다 이전이면 false를 반환한다")
    void isAfter_ShouldReturnFalseWhenBeforeComparisonTime() {
        // given
        final Instant earlier = Instant.now();
        final Instant later = earlier.plus(1, ChronoUnit.HOURS);
        final ExpiresAt expiresAt = ExpiresAt.of(earlier);

        // when
        final boolean after = expiresAt.isAfter(later);

        // then
        assertThat(after).isFalse();
    }

    @Test
    @DisplayName("isAfter()는 null 비교 시간을 거부한다")
    void isAfter_ShouldRejectNullComparisonTime() {
        // given
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now());

        // when & then
        assertThatThrownBy(() -> expiresAt.isAfter(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Comparison time cannot be null");
    }

    @Test
    @DisplayName("toEpochSeconds()는 Instant를 epoch seconds로 변환한다")
    void toEpochSeconds_ShouldConvertInstantToEpochSeconds() {
        // given
        final long epochSeconds = 1735689600L;
        final ExpiresAt expiresAt = ExpiresAt.fromEpochSeconds(epochSeconds);

        // when
        final long result = expiresAt.toEpochSeconds();

        // then
        assertThat(result).isEqualTo(epochSeconds);
    }

    @Test
    @DisplayName("asInstant()는 Instant 값을 반환한다")
    void asInstant_ShouldReturnInstantValue() {
        // given
        final Instant instant = Instant.now();
        final ExpiresAt expiresAt = ExpiresAt.of(instant);

        // when
        final Instant result = expiresAt.asInstant();

        // then
        assertThat(result).isEqualTo(instant);
    }

    @Test
    @DisplayName("동일한 Instant를 가진 ExpiresAt은 equals()로 같다고 판단된다")
    void equals_ShouldReturnTrueForSameInstant() {
        // given
        final Instant instant = Instant.now();
        final ExpiresAt expiresAt1 = ExpiresAt.of(instant);
        final ExpiresAt expiresAt2 = ExpiresAt.of(instant);

        // when & then
        assertThat(expiresAt1).isEqualTo(expiresAt2);
    }

    @Test
    @DisplayName("다른 Instant를 가진 ExpiresAt은 equals()로 다르다고 판단된다")
    void equals_ShouldReturnFalseForDifferentInstant() {
        // given
        final Instant instant1 = Instant.now();
        final Instant instant2 = instant1.plus(1, ChronoUnit.SECONDS);
        final ExpiresAt expiresAt1 = ExpiresAt.of(instant1);
        final ExpiresAt expiresAt2 = ExpiresAt.of(instant2);

        // when & then
        assertThat(expiresAt1).isNotEqualTo(expiresAt2);
    }

    @Test
    @DisplayName("동일한 Instant를 가진 ExpiresAt은 같은 hashCode를 반환한다")
    void hashCode_ShouldReturnSameHashCodeForSameInstant() {
        // given
        final Instant instant = Instant.now();
        final ExpiresAt expiresAt1 = ExpiresAt.of(instant);
        final ExpiresAt expiresAt2 = ExpiresAt.of(instant);

        // when & then
        assertThat(expiresAt1.hashCode()).isEqualTo(expiresAt2.hashCode());
    }
}
