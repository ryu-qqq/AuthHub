package com.ryuqq.authhub.domain.tenant.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantSortKey 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantSortKey 테스트")
class TenantSortKeyTest {

    @Nested
    @DisplayName("TenantSortKey fieldName 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("각 SortKey의 fieldName을 반환한다")
        void fieldNameShouldReturnCorrectValue() {
            // then
            assertThat(TenantSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
            assertThat(TenantSortKey.UPDATED_AT.fieldName()).isEqualTo("updatedAt");
        }
    }

    @Nested
    @DisplayName("TenantSortKey defaultKey 테스트")
    class DefaultKeyTests {

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void defaultKeyShouldReturnCreatedAt() {
            // then
            assertThat(TenantSortKey.defaultKey()).isEqualTo(TenantSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("TenantSortKey fromString 테스트")
    class FromStringTests {

        @Test
        @DisplayName("'CREATED_AT' 문자열로 CREATED_AT을 파싱한다")
        void shouldParseCreatedAtFromString() {
            // when
            TenantSortKey result = TenantSortKey.fromString("CREATED_AT");

            // then
            assertThat(result).isEqualTo(TenantSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("소문자 'createdAt' 문자열로 CREATED_AT을 파싱한다")
        void shouldParseCreatedAtFromLowerCaseString() {
            // when
            TenantSortKey result = TenantSortKey.fromString("createdAt");

            // then
            assertThat(result).isEqualTo(TenantSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("하이픈이 포함된 문자열을 파싱한다")
        void shouldParseWithHyphen() {
            // when
            TenantSortKey result = TenantSortKey.fromString("CREATED-AT");

            // then
            assertThat(result).isEqualTo(TenantSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("'UPDATED_AT' 문자열로 UPDATED_AT을 파싱한다")
        void shouldParseUpdatedAtFromString() {
            // when
            TenantSortKey result = TenantSortKey.fromString("UPDATED_AT");

            // then
            assertThat(result).isEqualTo(TenantSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("소문자 'updatedAt' 문자열로 UPDATED_AT을 파싱한다")
        void shouldParseUpdatedAtFromLowerCaseString() {
            // when
            TenantSortKey result = TenantSortKey.fromString("updatedAt");

            // then
            assertThat(result).isEqualTo(TenantSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("null 문자열이면 기본값을 반환한다")
        void shouldReturnDefaultWhenNull() {
            // when
            TenantSortKey result = TenantSortKey.fromString(null);

            // then
            assertThat(result).isEqualTo(TenantSortKey.defaultKey());
        }

        @Test
        @DisplayName("빈 문자열이면 기본값을 반환한다")
        void shouldReturnDefaultWhenBlank() {
            // when
            TenantSortKey result = TenantSortKey.fromString("   ");

            // then
            assertThat(result).isEqualTo(TenantSortKey.defaultKey());
        }

        @Test
        @DisplayName("유효하지 않은 값이면 기본값을 반환한다")
        void shouldReturnDefaultWhenInvalid() {
            // when
            TenantSortKey result = TenantSortKey.fromString("INVALID");

            // then
            assertThat(result).isEqualTo(TenantSortKey.defaultKey());
        }
    }
}
