package com.ryuqq.authhub.domain.security.ratelimit.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

/**
 * TimeWindow Value Object 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("TimeWindow 단위 테스트")
class TimeWindowTest {

    @Test
    @DisplayName("ofSeconds()로 초 단위 시간 윈도우를 생성할 수 있다")
    void testOfSeconds() {
        // when
        TimeWindow window = TimeWindow.ofSeconds(60);

        // then
        assertThat(window).isNotNull();
        assertThat(window.seconds()).isEqualTo(60);
    }

    @Test
    @DisplayName("ofMinutes()로 분 단위 시간 윈도우를 생성할 수 있다")
    void testOfMinutes() {
        // when
        TimeWindow window = TimeWindow.ofMinutes(5);

        // then
        assertThat(window.seconds()).isEqualTo(300);
        assertThat(window.toMinutes()).isEqualTo(5);
    }

    @Test
    @DisplayName("ofHours()로 시간 단위 시간 윈도우를 생성할 수 있다")
    void testOfHours() {
        // when
        TimeWindow window = TimeWindow.ofHours(1);

        // then
        assertThat(window.seconds()).isEqualTo(3600);
        assertThat(window.toHours()).isEqualTo(1);
    }

    @Test
    @DisplayName("ofDays()로 일 단위 시간 윈도우를 생성할 수 있다")
    void testOfDays() {
        // when
        TimeWindow window = TimeWindow.ofDays(1);

        // then
        assertThat(window.seconds()).isEqualTo(86400);
        assertThat(window.toDays()).isEqualTo(1);
    }

    @Test
    @DisplayName("1초 미만의 값으로 생성하면 예외가 발생한다")
    void testInvalidValue() {
        // when & then
        assertThatThrownBy(() -> TimeWindow.ofSeconds(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be at least 1 second");

        assertThatThrownBy(() -> TimeWindow.ofSeconds(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be at least 1 second");
    }

    @Test
    @DisplayName("from()으로 Duration으로부터 TimeWindow를 생성할 수 있다")
    void testFrom() {
        // given
        Duration duration = Duration.ofMinutes(10);

        // when
        TimeWindow window = TimeWindow.from(duration);

        // then
        assertThat(window.seconds()).isEqualTo(600);
    }

    @Test
    @DisplayName("from()에 null Duration을 전달하면 예외가 발생한다")
    void testFromWithNull() {
        // when & then
        assertThatThrownBy(() -> TimeWindow.from(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("toDuration()으로 Duration 객체로 변환할 수 있다")
    void testToDuration() {
        // given
        TimeWindow window = TimeWindow.ofMinutes(5);

        // when
        Duration duration = window.toDuration();

        // then
        assertThat(duration.getSeconds()).isEqualTo(300);
    }

    @Test
    @DisplayName("시간 단위 변환 메서드들이 정확히 동작한다")
    void testTimeConversions() {
        // given
        TimeWindow window = TimeWindow.ofHours(2);

        // when & then
        assertThat(window.toMinutes()).isEqualTo(120);
        assertThat(window.toHours()).isEqualTo(2);
        assertThat(window.seconds()).isEqualTo(7200);
    }

    @Test
    @DisplayName("같은 값을 가진 TimeWindow는 동등하다")
    void testEquality() {
        // given
        TimeWindow window1 = TimeWindow.ofSeconds(60);
        TimeWindow window2 = TimeWindow.ofSeconds(60);

        // when & then
        assertThat(window1).isEqualTo(window2);
        assertThat(window1.hashCode()).isEqualTo(window2.hashCode());
    }
}
