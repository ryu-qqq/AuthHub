package com.ryuqq.authhub.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserSearchField Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserSearchField 테스트")
class UserSearchFieldTest {

    @Nested
    @DisplayName("fieldName 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("IDENTIFIER의 fieldName은 identifier이다")
        void identifierShouldHaveCorrectFieldName() {
            assertThat(UserSearchField.IDENTIFIER.fieldName()).isEqualTo("identifier");
        }

        @Test
        @DisplayName("PHONE_NUMBER의 fieldName은 phoneNumber이다")
        void phoneNumberShouldHaveCorrectFieldName() {
            assertThat(UserSearchField.PHONE_NUMBER.fieldName()).isEqualTo("phoneNumber");
        }
    }
}
