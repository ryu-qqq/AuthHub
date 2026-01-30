package com.ryuqq.authhub.adapter.in.rest.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DateTimeFormatUtils 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DateTimeFormatUtils 단위 테스트")
class DateTimeFormatUtilsTest {

    @Nested
    @DisplayName("formatIso8601() 메서드는")
    class FormatIso8601Method {

        @Test
        @DisplayName("Instant를 ISO 8601 형식 문자열로 변환한다")
        void shouldFormatInstantToIso8601() {
            // Given
            Instant instant = Instant.parse("2025-01-15T09:30:00Z");

            // When
            String result = DateTimeFormatUtils.formatIso8601(instant);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("2025");
            assertThat(result).contains("01");
            assertThat(result).contains("15");
        }

        @Test
        @DisplayName("null Instant는 null을 반환한다")
        void shouldReturnNullWhenInstantIsNull() {
            // When
            String result = DateTimeFormatUtils.formatIso8601(null);

            // Then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("nowIso8601() 메서드는")
    class NowIso8601Method {

        @Test
        @DisplayName("현재 시각을 ISO 8601 형식으로 반환한다")
        void shouldReturnCurrentTimeInIso8601() {
            // When
            String result = DateTimeFormatUtils.nowIso8601();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
