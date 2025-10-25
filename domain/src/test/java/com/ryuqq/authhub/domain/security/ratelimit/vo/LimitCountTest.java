package com.ryuqq.authhub.domain.security.ratelimit.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * LimitCount Value Object 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("LimitCount 단위 테스트")
class LimitCountTest {

    @Test
    @DisplayName("of()로 유효한 제한 횟수를 생성할 수 있다")
    void testOf() {
        // when
        LimitCount count = LimitCount.of(100);

        // then
        assertThat(count).isNotNull();
        assertThat(count.value()).isEqualTo(100);
    }

    @Test
    @DisplayName("1 미만의 값으로 생성하면 예외가 발생한다")
    void testInvalidValue() {
        // when & then
        assertThatThrownBy(() -> LimitCount.of(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be at least 1");

        assertThatThrownBy(() -> LimitCount.of(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be at least 1");
    }

    @Test
    @DisplayName("isExceeded()로 제한 초과 여부를 확인할 수 있다")
    void testIsExceeded() {
        // given
        LimitCount count = LimitCount.of(100);

        // when & then
        assertThat(count.isExceeded(99)).isFalse();
        assertThat(count.isExceeded(100)).isTrue();
        assertThat(count.isExceeded(101)).isTrue();
    }

    @Test
    @DisplayName("isWithinLimit()로 제한 이내 여부를 확인할 수 있다")
    void testIsWithinLimit() {
        // given
        LimitCount count = LimitCount.of(100);

        // when & then
        assertThat(count.isWithinLimit(99)).isTrue();
        assertThat(count.isWithinLimit(100)).isFalse();
        assertThat(count.isWithinLimit(101)).isFalse();
    }

    @Test
    @DisplayName("remainingCount()로 남은 요청 횟수를 계산할 수 있다")
    void testRemainingCount() {
        // given
        LimitCount count = LimitCount.of(100);

        // when & then
        assertThat(count.remainingCount(50)).isEqualTo(50);
        assertThat(count.remainingCount(99)).isEqualTo(1);
        assertThat(count.remainingCount(100)).isEqualTo(0);
        assertThat(count.remainingCount(150)).isEqualTo(0);
    }

    @Test
    @DisplayName("같은 값을 가진 LimitCount는 동등하다")
    void testEquality() {
        // given
        LimitCount count1 = LimitCount.of(100);
        LimitCount count2 = LimitCount.of(100);

        // when & then
        assertThat(count1).isEqualTo(count2);
        assertThat(count1.hashCode()).isEqualTo(count2.hashCode());
    }
}
