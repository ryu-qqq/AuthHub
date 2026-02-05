package com.ryuqq.authhub.domain.tenantservice.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantServiceSortKey Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantServiceSortKey 테스트")
class TenantServiceSortKeyTest {

    @Nested
    @DisplayName("TenantServiceSortKey 인터페이스 구현 테스트")
    class InterfaceImplementationTests {

        @Test
        @DisplayName("TenantServiceSortKey는 SortKey 인터페이스를 구현한다")
        void shouldImplementSortKeyInterface() {
            // given
            TenantServiceSortKey sortKey = TenantServiceSortKey.SUBSCRIBED_AT;

            // then
            assertThat(sortKey).isInstanceOf(SortKey.class);
        }

        @Test
        @DisplayName("fieldName()은 SortKey 인터페이스의 메서드를 구현한다")
        void fieldNameShouldImplementSortKeyInterface() {
            // given
            TenantServiceSortKey sortKey = TenantServiceSortKey.CREATED_AT;

            // when
            String fieldName = sortKey.fieldName();

            // then
            assertThat(fieldName).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("TenantServiceSortKey fieldName() 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("fieldName()은 각 enum 값에 맞는 필드명을 반환한다")
        void fieldNameShouldReturnCorrectFieldName() {
            // then
            assertThat(TenantServiceSortKey.SUBSCRIBED_AT.fieldName()).isEqualTo("subscribedAt");
            assertThat(TenantServiceSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }
    }
}
