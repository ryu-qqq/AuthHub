package com.ryuqq.authhub.domain.service.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceSortKey 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceSortKey 테스트")
class ServiceSortKeyTest {

    @Nested
    @DisplayName("ServiceSortKey fieldName 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("각 ServiceSortKey는 올바른 fieldName을 반환한다")
        void shouldReturnCorrectFieldName() {
            // when & then
            assertThat(ServiceSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
            assertThat(ServiceSortKey.UPDATED_AT.fieldName()).isEqualTo("updatedAt");
            assertThat(ServiceSortKey.NAME.fieldName()).isEqualTo("name");
        }
    }

    @Nested
    @DisplayName("ServiceSortKey defaultKey 테스트")
    class DefaultKeyTests {

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void shouldReturnCreatedAt() {
            // when
            ServiceSortKey result = ServiceSortKey.defaultKey();

            // then
            assertThat(result).isEqualTo(ServiceSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("ServiceSortKey fromString 테스트")
    class FromStringTests {

        @Test
        @DisplayName("유효한 문자열로 ServiceSortKey를 파싱한다")
        void shouldParseValidString() {
            // when & then
            assertThat(ServiceSortKey.fromString("CREATED_AT"))
                    .isEqualTo(ServiceSortKey.CREATED_AT);
            assertThat(ServiceSortKey.fromString("UPDATED_AT"))
                    .isEqualTo(ServiceSortKey.UPDATED_AT);
            assertThat(ServiceSortKey.fromString("NAME")).isEqualTo(ServiceSortKey.NAME);
        }

        @Test
        @DisplayName("fieldName으로도 파싱할 수 있다")
        void shouldParseByFieldName() {
            // when & then
            assertThat(ServiceSortKey.fromString("createdAt")).isEqualTo(ServiceSortKey.CREATED_AT);
            assertThat(ServiceSortKey.fromString("updatedAt")).isEqualTo(ServiceSortKey.UPDATED_AT);
            assertThat(ServiceSortKey.fromString("name")).isEqualTo(ServiceSortKey.NAME);
        }

        @Test
        @DisplayName("null 입력 시 기본값(CREATED_AT)을 반환한다")
        void shouldReturnDefaultWhenInputIsNull() {
            // when
            ServiceSortKey result = ServiceSortKey.fromString(null);

            // then
            assertThat(result).isEqualTo(ServiceSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("빈 문자열 입력 시 기본값(CREATED_AT)을 반환한다")
        void shouldReturnDefaultWhenInputIsBlank() {
            // when
            ServiceSortKey result = ServiceSortKey.fromString("   ");

            // then
            assertThat(result).isEqualTo(ServiceSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("유효하지 않은 값 입력 시 기본값(CREATED_AT)을 반환한다")
        void shouldReturnDefaultWhenInputIsInvalid() {
            // when
            ServiceSortKey result = ServiceSortKey.fromString("INVALID");

            // then
            assertThat(result).isEqualTo(ServiceSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("하이픈이 포함된 문자열도 파싱할 수 있다")
        void shouldParseStringWithHyphen() {
            // when & then
            assertThat(ServiceSortKey.fromString("CREATED-AT"))
                    .isEqualTo(ServiceSortKey.CREATED_AT);
            assertThat(ServiceSortKey.fromString("UPDATED-AT"))
                    .isEqualTo(ServiceSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("대소문자 무관하게 파싱한다")
        void shouldParseCaseInsensitive() {
            // when & then
            assertThat(ServiceSortKey.fromString("created_at"))
                    .isEqualTo(ServiceSortKey.CREATED_AT);
            assertThat(ServiceSortKey.fromString("name")).isEqualTo(ServiceSortKey.NAME);
        }
    }
}
