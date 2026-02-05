package com.ryuqq.authhub.domain.permission.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionType Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionType 테스트")
class PermissionTypeTest {

    @Nested
    @DisplayName("PermissionType Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("isSystem()은 SYSTEM일 때 true를 반환한다")
        void isSystemShouldReturnTrueForSystem() {
            // given
            PermissionType systemType = PermissionType.SYSTEM;

            // then
            assertThat(systemType.isSystem()).isTrue();
        }

        @Test
        @DisplayName("isSystem()은 CUSTOM일 때 false를 반환한다")
        void isSystemShouldReturnFalseForCustom() {
            // given
            PermissionType customType = PermissionType.CUSTOM;

            // then
            assertThat(customType.isSystem()).isFalse();
        }

        @Test
        @DisplayName("isCustom()은 CUSTOM일 때 true를 반환한다")
        void isCustomShouldReturnTrueForCustom() {
            // given
            PermissionType customType = PermissionType.CUSTOM;

            // then
            assertThat(customType.isCustom()).isTrue();
        }

        @Test
        @DisplayName("isCustom()은 SYSTEM일 때 false를 반환한다")
        void isCustomShouldReturnFalseForSystem() {
            // given
            PermissionType systemType = PermissionType.SYSTEM;

            // then
            assertThat(systemType.isCustom()).isFalse();
        }
    }

    @Nested
    @DisplayName("PermissionType parseList() 테스트")
    class ParseListTests {

        @Test
        @DisplayName("parseList()는 null이면 null을 반환한다")
        void parseListShouldReturnNullWhenNull() {
            // when
            List<PermissionType> result = PermissionType.parseList(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("parseList()는 빈 목록이면 null을 반환한다")
        void parseListShouldReturnNullWhenEmpty() {
            // when
            List<PermissionType> result = PermissionType.parseList(List.of());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("parseList()는 유효한 값 목록을 파싱한다")
        void parseListShouldParseValidValues() {
            // when
            List<PermissionType> result = PermissionType.parseList(List.of("SYSTEM", "CUSTOM"));

            // then
            assertThat(result).containsExactly(PermissionType.SYSTEM, PermissionType.CUSTOM);
        }

        @Test
        @DisplayName("parseList()는 대소문자 무관하게 파싱한다")
        void parseListShouldParseCaseInsensitive() {
            // when
            List<PermissionType> result = PermissionType.parseList(List.of("system", "custom"));

            // then - 소문자도 대문자로 변환되어 정상 파싱
            assertThat(result).containsExactly(PermissionType.SYSTEM, PermissionType.CUSTOM);
        }

        @Test
        @DisplayName("parseList()는 무효한 값을 무시한다")
        void parseListShouldIgnoreInvalidValues() {
            // when
            List<PermissionType> result =
                    PermissionType.parseList(List.of("SYSTEM", "INVALID", "CUSTOM"));

            // then
            assertThat(result).containsExactly(PermissionType.SYSTEM, PermissionType.CUSTOM);
        }

        @Test
        @DisplayName("parseList()는 모든 값이 무효하면 null을 반환한다")
        void parseListShouldReturnNullWhenAllInvalid() {
            // when
            List<PermissionType> result = PermissionType.parseList(List.of("INVALID1", "INVALID2"));

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("parseList()는 null 값이 포함된 목록에서도 동작한다")
        void parseListShouldHandleNullInList() {
            // when - List.of()는 null을 허용하지 않으므로 Arrays.asList() 사용
            List<PermissionType> result =
                    PermissionType.parseList(Arrays.asList("SYSTEM", null, "CUSTOM"));

            // then
            assertThat(result).containsExactly(PermissionType.SYSTEM, PermissionType.CUSTOM);
        }

        @Test
        @DisplayName("parseList()는 빈 문자열이 포함된 목록에서도 동작한다")
        void parseListShouldHandleBlankInList() {
            // when
            List<PermissionType> result = PermissionType.parseList(List.of("SYSTEM", "", "CUSTOM"));

            // then
            assertThat(result).containsExactly(PermissionType.SYSTEM, PermissionType.CUSTOM);
        }
    }
}
