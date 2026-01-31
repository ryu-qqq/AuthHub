package com.ryuqq.authhub.application.common.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TimeProvider 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TimeProvider 단위 테스트")
class TimeProviderTest {

    private TimeProvider sut;

    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T12:00:00Z");

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
        sut = new TimeProvider(fixedClock);
    }

    @Nested
    @DisplayName("now 메서드")
    class Now {

        @Test
        @DisplayName("성공: Clock에서 현재 Instant 반환")
        void shouldReturnCurrentInstant_FromClock() {
            // when
            Instant result = sut.now();

            // then
            assertThat(result).isEqualTo(FIXED_INSTANT);
        }
    }
}
