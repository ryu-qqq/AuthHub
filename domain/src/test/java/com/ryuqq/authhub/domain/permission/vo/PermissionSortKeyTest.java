package com.ryuqq.authhub.domain.permission.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionSortKey Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionSortKey 테스트")
class PermissionSortKeyTest {

    @Nested
    @DisplayName("PermissionSortKey 인터페이스 구현 테스트")
    class InterfaceImplementationTests {

        @Test
        @DisplayName("PermissionSortKey는 SortKey 인터페이스를 구현한다")
        void shouldImplementSortKeyInterface() {
            // given
            PermissionSortKey sortKey = PermissionSortKey.CREATED_AT;

            // then
            assertThat(sortKey).isInstanceOf(SortKey.class);
        }

        @Test
        @DisplayName("fieldName()은 SortKey 인터페이스의 메서드를 구현한다")
        void fieldNameShouldImplementSortKeyInterface() {
            // given
            PermissionSortKey sortKey = PermissionSortKey.CREATED_AT;

            // when
            String fieldName = sortKey.fieldName();

            // then
            assertThat(fieldName).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("PermissionSortKey Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("fieldName()은 각 enum 값에 맞는 필드명을 반환한다")
        void fieldNameShouldReturnCorrectFieldName() {
            // then
            assertThat(PermissionSortKey.PERMISSION_ID.fieldName()).isEqualTo("permissionId");
            assertThat(PermissionSortKey.PERMISSION_KEY.fieldName()).isEqualTo("permissionKey");
            assertThat(PermissionSortKey.RESOURCE.fieldName()).isEqualTo("resource");
            assertThat(PermissionSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
            assertThat(PermissionSortKey.UPDATED_AT.fieldName()).isEqualTo("updatedAt");
        }
    }

    @Nested
    @DisplayName("PermissionSortKey 팩토리 메서드 테스트")
    class FactoryMethodTests {

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void defaultKeyShouldReturnCreatedAt() {
            // when
            PermissionSortKey defaultKey = PermissionSortKey.defaultKey();

            // then
            assertThat(defaultKey).isEqualTo(PermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("fromString()는 유효한 enum 이름을 파싱한다")
        void fromStringShouldParseValidEnumNames() {
            // when & then
            assertThat(PermissionSortKey.fromString("PERMISSION_ID"))
                    .isEqualTo(PermissionSortKey.PERMISSION_ID);
            assertThat(PermissionSortKey.fromString("PERMISSION_KEY"))
                    .isEqualTo(PermissionSortKey.PERMISSION_KEY);
            assertThat(PermissionSortKey.fromString("RESOURCE"))
                    .isEqualTo(PermissionSortKey.RESOURCE);
            assertThat(PermissionSortKey.fromString("CREATED_AT"))
                    .isEqualTo(PermissionSortKey.CREATED_AT);
            assertThat(PermissionSortKey.fromString("UPDATED_AT"))
                    .isEqualTo(PermissionSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("fromString()는 대소문자 무관하게 파싱한다")
        void fromStringShouldParseCaseInsensitive() {
            // when & then
            assertThat(PermissionSortKey.fromString("permission_id"))
                    .isEqualTo(PermissionSortKey.PERMISSION_ID);
            assertThat(PermissionSortKey.fromString("permission_key"))
                    .isEqualTo(PermissionSortKey.PERMISSION_KEY);
            assertThat(PermissionSortKey.fromString("resource"))
                    .isEqualTo(PermissionSortKey.RESOURCE);
            assertThat(PermissionSortKey.fromString("created_at"))
                    .isEqualTo(PermissionSortKey.CREATED_AT);
            assertThat(PermissionSortKey.fromString("updated_at"))
                    .isEqualTo(PermissionSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("fromString()는 하이픈을 언더스코어로 변환하여 파싱한다")
        void fromStringShouldConvertHyphenToUnderscore() {
            // when & then
            assertThat(PermissionSortKey.fromString("PERMISSION-ID"))
                    .isEqualTo(PermissionSortKey.PERMISSION_ID);
            assertThat(PermissionSortKey.fromString("CREATED-AT"))
                    .isEqualTo(PermissionSortKey.CREATED_AT);
            assertThat(PermissionSortKey.fromString("UPDATED-AT"))
                    .isEqualTo(PermissionSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("fromString()는 fieldName으로도 파싱할 수 있다")
        void fromStringShouldParseByFieldName() {
            // when & then
            assertThat(PermissionSortKey.fromString("permissionId"))
                    .isEqualTo(PermissionSortKey.PERMISSION_ID);
            assertThat(PermissionSortKey.fromString("permissionKey"))
                    .isEqualTo(PermissionSortKey.PERMISSION_KEY);
            assertThat(PermissionSortKey.fromString("resource"))
                    .isEqualTo(PermissionSortKey.RESOURCE);
            assertThat(PermissionSortKey.fromString("createdAt"))
                    .isEqualTo(PermissionSortKey.CREATED_AT);
            assertThat(PermissionSortKey.fromString("updatedAt"))
                    .isEqualTo(PermissionSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("fromString()는 fieldName도 대소문자 무관하게 파싱한다")
        void fromStringShouldParseFieldNameCaseInsensitive() {
            // when & then
            assertThat(PermissionSortKey.fromString("PermissionId"))
                    .isEqualTo(PermissionSortKey.PERMISSION_ID);
            assertThat(PermissionSortKey.fromString("PERMISSIONID"))
                    .isEqualTo(PermissionSortKey.PERMISSION_ID);
            assertThat(PermissionSortKey.fromString("CreatedAt"))
                    .isEqualTo(PermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("fromString()는 null이면 기본값을 반환한다")
        void fromStringShouldReturnDefaultWhenNull() {
            // when
            PermissionSortKey result = PermissionSortKey.fromString(null);

            // then
            assertThat(result).isEqualTo(PermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("fromString()는 빈 문자열이면 기본값을 반환한다")
        void fromStringShouldReturnDefaultWhenBlank() {
            // when
            PermissionSortKey result1 = PermissionSortKey.fromString("");
            PermissionSortKey result2 = PermissionSortKey.fromString("   ");

            // then
            assertThat(result1).isEqualTo(PermissionSortKey.CREATED_AT);
            assertThat(result2).isEqualTo(PermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("fromString()는 무효한 값이면 기본값을 반환한다")
        void fromStringShouldReturnDefaultWhenInvalid() {
            // when
            PermissionSortKey result = PermissionSortKey.fromString("INVALID");

            // then
            assertThat(result).isEqualTo(PermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("fromString()는 앞뒤 공백을 제거하고 파싱한다")
        void fromStringShouldTrimWhitespace() {
            // when & then
            assertThat(PermissionSortKey.fromString("  CREATED_AT  "))
                    .isEqualTo(PermissionSortKey.CREATED_AT);
            assertThat(PermissionSortKey.fromString("  createdAt  "))
                    .isEqualTo(PermissionSortKey.CREATED_AT);
        }
    }
}
