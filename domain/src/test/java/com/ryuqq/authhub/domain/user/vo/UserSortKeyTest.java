package com.ryuqq.authhub.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserSortKey Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserSortKey 테스트")
class UserSortKeyTest {

    @Nested
    @DisplayName("fieldName 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("CREATED_AT의 fieldName은 createdAt이다")
        void createdAtShouldHaveCorrectFieldName() {
            assertThat(UserSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("UPDATED_AT의 fieldName은 updatedAt이다")
        void updatedAtShouldHaveCorrectFieldName() {
            assertThat(UserSortKey.UPDATED_AT.fieldName()).isEqualTo("updatedAt");
        }
    }
}
