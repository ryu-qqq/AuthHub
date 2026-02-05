package com.ryuqq.authhub.domain.permission.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionSearchField Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionSearchField 테스트")
class PermissionSearchFieldTest {

    @Nested
    @DisplayName("PermissionSearchField Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("isPermissionKey()는 PERMISSION_KEY일 때 true를 반환한다")
        void isPermissionKeyShouldReturnTrueForPermissionKey() {
            // given
            PermissionSearchField field = PermissionSearchField.PERMISSION_KEY;

            // then
            assertThat(field.isPermissionKey()).isTrue();
        }

        @Test
        @DisplayName("isPermissionKey()는 다른 필드일 때 false를 반환한다")
        void isPermissionKeyShouldReturnFalseForOtherFields() {
            // given
            PermissionSearchField resourceField = PermissionSearchField.RESOURCE;
            PermissionSearchField actionField = PermissionSearchField.ACTION;
            PermissionSearchField descriptionField = PermissionSearchField.DESCRIPTION;

            // then
            assertThat(resourceField.isPermissionKey()).isFalse();
            assertThat(actionField.isPermissionKey()).isFalse();
            assertThat(descriptionField.isPermissionKey()).isFalse();
        }

        @Test
        @DisplayName("isResource()는 RESOURCE일 때 true를 반환한다")
        void isResourceShouldReturnTrueForResource() {
            // given
            PermissionSearchField field = PermissionSearchField.RESOURCE;

            // then
            assertThat(field.isResource()).isTrue();
        }

        @Test
        @DisplayName("isResource()는 다른 필드일 때 false를 반환한다")
        void isResourceShouldReturnFalseForOtherFields() {
            // given
            PermissionSearchField permissionKeyField = PermissionSearchField.PERMISSION_KEY;
            PermissionSearchField actionField = PermissionSearchField.ACTION;
            PermissionSearchField descriptionField = PermissionSearchField.DESCRIPTION;

            // then
            assertThat(permissionKeyField.isResource()).isFalse();
            assertThat(actionField.isResource()).isFalse();
            assertThat(descriptionField.isResource()).isFalse();
        }

        @Test
        @DisplayName("isAction()는 ACTION일 때 true를 반환한다")
        void isActionShouldReturnTrueForAction() {
            // given
            PermissionSearchField field = PermissionSearchField.ACTION;

            // then
            assertThat(field.isAction()).isTrue();
        }

        @Test
        @DisplayName("isAction()는 다른 필드일 때 false를 반환한다")
        void isActionShouldReturnFalseForOtherFields() {
            // given
            PermissionSearchField permissionKeyField = PermissionSearchField.PERMISSION_KEY;
            PermissionSearchField resourceField = PermissionSearchField.RESOURCE;
            PermissionSearchField descriptionField = PermissionSearchField.DESCRIPTION;

            // then
            assertThat(permissionKeyField.isAction()).isFalse();
            assertThat(resourceField.isAction()).isFalse();
            assertThat(descriptionField.isAction()).isFalse();
        }

        @Test
        @DisplayName("isDescription()는 DESCRIPTION일 때 true를 반환한다")
        void isDescriptionShouldReturnTrueForDescription() {
            // given
            PermissionSearchField field = PermissionSearchField.DESCRIPTION;

            // then
            assertThat(field.isDescription()).isTrue();
        }

        @Test
        @DisplayName("isDescription()는 다른 필드일 때 false를 반환한다")
        void isDescriptionShouldReturnFalseForOtherFields() {
            // given
            PermissionSearchField permissionKeyField = PermissionSearchField.PERMISSION_KEY;
            PermissionSearchField resourceField = PermissionSearchField.RESOURCE;
            PermissionSearchField actionField = PermissionSearchField.ACTION;

            // then
            assertThat(permissionKeyField.isDescription()).isFalse();
            assertThat(resourceField.isDescription()).isFalse();
            assertThat(actionField.isDescription()).isFalse();
        }
    }

    @Nested
    @DisplayName("PermissionSearchField 팩토리 메서드 테스트")
    class FactoryMethodTests {

        @Test
        @DisplayName("defaultField()는 PERMISSION_KEY를 반환한다")
        void defaultFieldShouldReturnPermissionKey() {
            // when
            PermissionSearchField defaultField = PermissionSearchField.defaultField();

            // then
            assertThat(defaultField).isEqualTo(PermissionSearchField.PERMISSION_KEY);
        }

        @Test
        @DisplayName("fromString()는 유효한 값을 파싱한다")
        void fromStringShouldParseValidValues() {
            // when & then
            assertThat(PermissionSearchField.fromString("PERMISSION_KEY"))
                    .isEqualTo(PermissionSearchField.PERMISSION_KEY);
            assertThat(PermissionSearchField.fromString("RESOURCE"))
                    .isEqualTo(PermissionSearchField.RESOURCE);
            assertThat(PermissionSearchField.fromString("ACTION"))
                    .isEqualTo(PermissionSearchField.ACTION);
            assertThat(PermissionSearchField.fromString("DESCRIPTION"))
                    .isEqualTo(PermissionSearchField.DESCRIPTION);
        }

        @Test
        @DisplayName("fromString()는 대소문자 무관하게 파싱한다")
        void fromStringShouldParseCaseInsensitive() {
            // when & then
            assertThat(PermissionSearchField.fromString("permission_key"))
                    .isEqualTo(PermissionSearchField.PERMISSION_KEY);
            assertThat(PermissionSearchField.fromString("resource"))
                    .isEqualTo(PermissionSearchField.RESOURCE);
            assertThat(PermissionSearchField.fromString("action"))
                    .isEqualTo(PermissionSearchField.ACTION);
            assertThat(PermissionSearchField.fromString("description"))
                    .isEqualTo(PermissionSearchField.DESCRIPTION);
        }

        @Test
        @DisplayName("fromString()는 null이면 기본값을 반환한다")
        void fromStringShouldReturnDefaultWhenNull() {
            // when
            PermissionSearchField result = PermissionSearchField.fromString(null);

            // then
            assertThat(result).isEqualTo(PermissionSearchField.PERMISSION_KEY);
        }

        @Test
        @DisplayName("fromString()는 빈 문자열이면 기본값을 반환한다")
        void fromStringShouldReturnDefaultWhenBlank() {
            // when
            PermissionSearchField result1 = PermissionSearchField.fromString("");
            PermissionSearchField result2 = PermissionSearchField.fromString("   ");

            // then
            assertThat(result1).isEqualTo(PermissionSearchField.PERMISSION_KEY);
            assertThat(result2).isEqualTo(PermissionSearchField.PERMISSION_KEY);
        }

        @Test
        @DisplayName("fromString()는 무효한 값이면 기본값을 반환한다")
        void fromStringShouldReturnDefaultWhenInvalid() {
            // when
            PermissionSearchField result = PermissionSearchField.fromString("INVALID");

            // then
            assertThat(result).isEqualTo(PermissionSearchField.PERMISSION_KEY);
        }
    }
}
