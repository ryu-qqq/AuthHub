package com.ryuqq.authhub.domain.role.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleType 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleType 테스트")
class RoleTypeTest {

    @Nested
    @DisplayName("RoleType isSystem 테스트")
    class IsSystemTests {

        @Test
        @DisplayName("SYSTEM은 isSystem()이 true를 반환한다")
        void shouldReturnTrueForSystem() {
            // when & then
            assertThat(RoleType.SYSTEM.isSystem()).isTrue();
        }

        @Test
        @DisplayName("CUSTOM은 isSystem()이 false를 반환한다")
        void shouldReturnFalseForCustom() {
            // when & then
            assertThat(RoleType.CUSTOM.isSystem()).isFalse();
        }
    }

    @Nested
    @DisplayName("RoleType isCustom 테스트")
    class IsCustomTests {

        @Test
        @DisplayName("CUSTOM은 isCustom()이 true를 반환한다")
        void shouldReturnTrueForCustom() {
            // when & then
            assertThat(RoleType.CUSTOM.isCustom()).isTrue();
        }

        @Test
        @DisplayName("SYSTEM은 isCustom()이 false를 반환한다")
        void shouldReturnFalseForSystem() {
            // when & then
            assertThat(RoleType.SYSTEM.isCustom()).isFalse();
        }
    }

    @Nested
    @DisplayName("RoleType parseList 테스트")
    class ParseListTests {

        @Test
        @DisplayName("유효한 문자열 목록으로 RoleType 목록을 파싱한다")
        void shouldParseValidStringList() {
            // when
            List<RoleType> result = RoleType.parseList(List.of("SYSTEM", "CUSTOM"));

            // then
            assertThat(result).containsExactly(RoleType.SYSTEM, RoleType.CUSTOM);
        }

        @Test
        @DisplayName("null 입력 시 null을 반환한다")
        void shouldReturnNullWhenInputIsNull() {
            // when
            List<RoleType> result = RoleType.parseList(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환한다")
        void shouldReturnNullWhenInputIsEmpty() {
            // when
            List<RoleType> result = RoleType.parseList(List.of());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("유효하지 않은 값은 무시된다")
        void shouldIgnoreInvalidValues() {
            // when
            List<RoleType> result = RoleType.parseList(List.of("SYSTEM", "INVALID", "CUSTOM"));

            // then
            assertThat(result).containsExactly(RoleType.SYSTEM, RoleType.CUSTOM);
        }

        @Test
        @DisplayName("모든 값이 유효하지 않으면 null을 반환한다")
        void shouldReturnNullWhenAllValuesAreInvalid() {
            // when
            List<RoleType> result = RoleType.parseList(List.of("INVALID1", "INVALID2"));

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("대소문자 무관하게 파싱한다")
        void shouldParseCaseInsensitive() {
            // when
            List<RoleType> result = RoleType.parseList(List.of("system", "CUSTOM", "System"));

            // then
            assertThat(result).containsExactly(RoleType.SYSTEM, RoleType.CUSTOM, RoleType.SYSTEM);
        }
    }
}
