package com.ryuqq.authhub.domain.permission.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionUpdateData Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionUpdateData 테스트")
class PermissionUpdateDataTest {

    @Nested
    @DisplayName("PermissionUpdateData 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            PermissionUpdateData updateData = PermissionUpdateData.of("새로운 설명");

            // then
            assertThat(updateData.description()).isEqualTo("새로운 설명");
        }

        @Test
        @DisplayName("null description으로 생성할 수 있다")
        void shouldCreateWithNullDescription() {
            // when
            PermissionUpdateData updateData = PermissionUpdateData.of(null);

            // then
            assertThat(updateData.description()).isNull();
        }

        @Test
        @DisplayName("빈 문자열 description으로 생성할 수 있다")
        void shouldCreateWithEmptyDescription() {
            // when
            PermissionUpdateData updateData = PermissionUpdateData.of("");

            // then
            assertThat(updateData.description()).isEmpty();
        }
    }

    @Nested
    @DisplayName("PermissionUpdateData Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("hasDescription()은 description이 null이 아니면 true를 반환한다")
        void hasDescriptionShouldReturnTrueWhenDescriptionIsNotNull() {
            // given
            PermissionUpdateData updateData = PermissionUpdateData.of("설명");

            // then
            assertThat(updateData.hasDescription()).isTrue();
        }

        @Test
        @DisplayName("hasDescription()은 description이 null이면 false를 반환한다")
        void hasDescriptionShouldReturnFalseWhenDescriptionIsNull() {
            // given
            PermissionUpdateData updateData = PermissionUpdateData.of(null);

            // then
            assertThat(updateData.hasDescription()).isFalse();
        }

        @Test
        @DisplayName("hasDescription()은 빈 문자열 description에서도 true를 반환한다")
        void hasDescriptionShouldReturnTrueWhenDescriptionIsEmpty() {
            // given
            PermissionUpdateData updateData = PermissionUpdateData.of("");

            // then
            assertThat(updateData.hasDescription()).isTrue();
        }
    }

    @Nested
    @DisplayName("PermissionUpdateData equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 description을 가진 PermissionUpdateData는 동등하다")
        void shouldBeEqualWhenSameDescription() {
            // given
            PermissionUpdateData updateData1 = PermissionUpdateData.of("설명");
            PermissionUpdateData updateData2 = PermissionUpdateData.of("설명");

            // then
            assertThat(updateData1).isEqualTo(updateData2);
            assertThat(updateData1.hashCode()).isEqualTo(updateData2.hashCode());
        }

        @Test
        @DisplayName("null description을 가진 PermissionUpdateData는 동등하다")
        void shouldBeEqualWhenBothHaveNullDescription() {
            // given
            PermissionUpdateData updateData1 = PermissionUpdateData.of(null);
            PermissionUpdateData updateData2 = PermissionUpdateData.of(null);

            // then
            assertThat(updateData1).isEqualTo(updateData2);
        }

        @Test
        @DisplayName("다른 description을 가진 PermissionUpdateData는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentDescription() {
            // given
            PermissionUpdateData updateData1 = PermissionUpdateData.of("설명1");
            PermissionUpdateData updateData2 = PermissionUpdateData.of("설명2");

            // then
            assertThat(updateData1).isNotEqualTo(updateData2);
        }
    }
}
