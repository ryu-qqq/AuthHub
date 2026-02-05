package com.ryuqq.authhub.domain.userrole.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRoleSortKey Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRoleSortKey 테스트")
class UserRoleSortKeyTest {

    @Nested
    @DisplayName("UserRoleSortKey 인터페이스 구현 테스트")
    class InterfaceImplementationTests {

        @Test
        @DisplayName("UserRoleSortKey는 SortKey 인터페이스를 구현한다")
        void shouldImplementSortKeyInterface() {
            // given
            UserRoleSortKey sortKey = UserRoleSortKey.CREATED_AT;

            // then
            assertThat(sortKey).isInstanceOf(SortKey.class);
        }

        @Test
        @DisplayName("fieldName()은 SortKey 인터페이스의 메서드를 구현한다")
        void fieldNameShouldImplementSortKeyInterface() {
            // given
            UserRoleSortKey sortKey = UserRoleSortKey.CREATED_AT;

            // when
            String fieldName = sortKey.fieldName();

            // then
            assertThat(fieldName).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("UserRoleSortKey Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("fieldName()은 각 enum 값에 맞는 필드명을 반환한다")
        void fieldNameShouldReturnCorrectFieldName() {
            // then
            assertThat(UserRoleSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }
    }
}
