package com.ryuqq.authhub.domain.rolepermission.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RolePermissionSortKey Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RolePermissionSortKey 테스트")
class RolePermissionSortKeyTest {

    @Nested
    @DisplayName("RolePermissionSortKey 인터페이스 구현 테스트")
    class InterfaceImplementationTests {

        @Test
        @DisplayName("RolePermissionSortKey는 SortKey 인터페이스를 구현한다")
        void shouldImplementSortKeyInterface() {
            // given
            RolePermissionSortKey sortKey = RolePermissionSortKey.CREATED_AT;

            // then
            assertThat(sortKey).isInstanceOf(SortKey.class);
        }

        @Test
        @DisplayName("fieldName()은 SortKey 인터페이스의 메서드를 구현한다")
        void fieldNameShouldImplementSortKeyInterface() {
            // given
            RolePermissionSortKey sortKey = RolePermissionSortKey.CREATED_AT;

            // when
            String fieldName = sortKey.fieldName();

            // then
            assertThat(fieldName).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("RolePermissionSortKey Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("fieldName()은 각 enum 값에 맞는 필드명을 반환한다")
        void fieldNameShouldReturnCorrectFieldName() {
            // then
            assertThat(RolePermissionSortKey.ROLE_PERMISSION_ID.fieldName())
                    .isEqualTo("rolePermissionId");
            assertThat(RolePermissionSortKey.ROLE_ID.fieldName()).isEqualTo("roleId");
            assertThat(RolePermissionSortKey.PERMISSION_ID.fieldName()).isEqualTo("permissionId");
            assertThat(RolePermissionSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("RolePermissionSortKey 팩토리 메서드 테스트")
    class FactoryMethodTests {

        @Test
        @DisplayName("fromStringOrDefault()는 유효한 enum 이름을 파싱한다")
        void fromStringOrDefaultShouldParseValidEnumNames() {
            // when & then
            assertThat(RolePermissionSortKey.fromStringOrDefault("ROLE_PERMISSION_ID"))
                    .isEqualTo(RolePermissionSortKey.ROLE_PERMISSION_ID);
            assertThat(RolePermissionSortKey.fromStringOrDefault("ROLE_ID"))
                    .isEqualTo(RolePermissionSortKey.ROLE_ID);
            assertThat(RolePermissionSortKey.fromStringOrDefault("PERMISSION_ID"))
                    .isEqualTo(RolePermissionSortKey.PERMISSION_ID);
            assertThat(RolePermissionSortKey.fromStringOrDefault("CREATED_AT"))
                    .isEqualTo(RolePermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("fromStringOrDefault()는 대소문자 무관하게 파싱한다")
        void fromStringOrDefaultShouldParseCaseInsensitive() {
            // when & then
            assertThat(RolePermissionSortKey.fromStringOrDefault("role_permission_id"))
                    .isEqualTo(RolePermissionSortKey.ROLE_PERMISSION_ID);
            assertThat(RolePermissionSortKey.fromStringOrDefault("role_id"))
                    .isEqualTo(RolePermissionSortKey.ROLE_ID);
            assertThat(RolePermissionSortKey.fromStringOrDefault("permission_id"))
                    .isEqualTo(RolePermissionSortKey.PERMISSION_ID);
            assertThat(RolePermissionSortKey.fromStringOrDefault("created_at"))
                    .isEqualTo(RolePermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("fromStringOrDefault()는 null이면 기본값을 반환한다")
        void fromStringOrDefaultShouldReturnDefaultWhenNull() {
            // when
            RolePermissionSortKey result = RolePermissionSortKey.fromStringOrDefault(null);

            // then
            assertThat(result).isEqualTo(RolePermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("fromStringOrDefault()는 빈 문자열이면 기본값을 반환한다")
        void fromStringOrDefaultShouldReturnDefaultWhenBlank() {
            // when
            RolePermissionSortKey result1 = RolePermissionSortKey.fromStringOrDefault("");
            RolePermissionSortKey result2 = RolePermissionSortKey.fromStringOrDefault("   ");

            // then
            assertThat(result1).isEqualTo(RolePermissionSortKey.CREATED_AT);
            assertThat(result2).isEqualTo(RolePermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("fromStringOrDefault()는 무효한 값이면 기본값을 반환한다")
        void fromStringOrDefaultShouldReturnDefaultWhenInvalid() {
            // when
            RolePermissionSortKey result = RolePermissionSortKey.fromStringOrDefault("INVALID");

            // then
            assertThat(result).isEqualTo(RolePermissionSortKey.CREATED_AT);
        }
    }
}
