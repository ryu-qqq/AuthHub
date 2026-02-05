package com.ryuqq.authhub.domain.role.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleUpdateData Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleUpdateData 테스트")
class RoleUpdateDataTest {

    @Nested
    @DisplayName("RoleUpdateData 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            RoleUpdateData updateData = RoleUpdateData.of("새 표시명", "새 설명");

            // then
            assertThat(updateData.displayName()).isEqualTo("새 표시명");
            assertThat(updateData.description()).isEqualTo("새 설명");
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            RoleUpdateData updateData = new RoleUpdateData("표시명", "설명");

            // then
            assertThat(updateData.displayName()).isEqualTo("표시명");
            assertThat(updateData.description()).isEqualTo("설명");
        }

        @Test
        @DisplayName("displayName만 null로 생성할 수 있다")
        void shouldCreateWithNullDisplayName() {
            // when
            RoleUpdateData updateData = RoleUpdateData.of(null, "설명");

            // then
            assertThat(updateData.displayName()).isNull();
            assertThat(updateData.description()).isEqualTo("설명");
        }

        @Test
        @DisplayName("description만 null로 생성할 수 있다")
        void shouldCreateWithNullDescription() {
            // when
            RoleUpdateData updateData = RoleUpdateData.of("표시명", null);

            // then
            assertThat(updateData.displayName()).isEqualTo("표시명");
            assertThat(updateData.description()).isNull();
        }

        @Test
        @DisplayName("둘 다 null로 생성할 수 있다")
        void shouldCreateWithBothNull() {
            // when
            RoleUpdateData updateData = RoleUpdateData.of(null, null);

            // then
            assertThat(updateData.displayName()).isNull();
            assertThat(updateData.description()).isNull();
        }
    }

    @Nested
    @DisplayName("RoleUpdateData hasDisplayName 테스트")
    class HasDisplayNameTests {

        @Test
        @DisplayName("displayName이 있으면 true를 반환한다")
        void shouldReturnTrueWhenDisplayNameExists() {
            // given
            RoleUpdateData updateData = RoleUpdateData.of("표시명", "설명");

            // when & then
            assertThat(updateData.hasDisplayName()).isTrue();
        }

        @Test
        @DisplayName("displayName이 null이면 false를 반환한다")
        void shouldReturnFalseWhenDisplayNameIsNull() {
            // given
            RoleUpdateData updateData = RoleUpdateData.of(null, "설명");

            // when & then
            assertThat(updateData.hasDisplayName()).isFalse();
        }
    }

    @Nested
    @DisplayName("RoleUpdateData hasDescription 테스트")
    class HasDescriptionTests {

        @Test
        @DisplayName("description이 있으면 true를 반환한다")
        void shouldReturnTrueWhenDescriptionExists() {
            // given
            RoleUpdateData updateData = RoleUpdateData.of("표시명", "설명");

            // when & then
            assertThat(updateData.hasDescription()).isTrue();
        }

        @Test
        @DisplayName("description이 null이면 false를 반환한다")
        void shouldReturnFalseWhenDescriptionIsNull() {
            // given
            RoleUpdateData updateData = RoleUpdateData.of("표시명", null);

            // when & then
            assertThat(updateData.hasDescription()).isFalse();
        }
    }

    @Nested
    @DisplayName("RoleUpdateData hasAnyUpdate 테스트")
    class HasAnyUpdateTests {

        @Test
        @DisplayName("둘 다 있으면 true를 반환한다")
        void shouldReturnTrueWhenBothExist() {
            // given
            RoleUpdateData updateData = RoleUpdateData.of("표시명", "설명");

            // when & then
            assertThat(updateData.hasAnyUpdate()).isTrue();
        }

        @Test
        @DisplayName("displayName만 있으면 true를 반환한다")
        void shouldReturnTrueWhenOnlyDisplayNameExists() {
            // given
            RoleUpdateData updateData = RoleUpdateData.of("표시명", null);

            // when & then
            assertThat(updateData.hasAnyUpdate()).isTrue();
        }

        @Test
        @DisplayName("description만 있으면 true를 반환한다")
        void shouldReturnTrueWhenOnlyDescriptionExists() {
            // given
            RoleUpdateData updateData = RoleUpdateData.of(null, "설명");

            // when & then
            assertThat(updateData.hasAnyUpdate()).isTrue();
        }

        @Test
        @DisplayName("둘 다 null이면 false를 반환한다")
        void shouldReturnFalseWhenBothAreNull() {
            // given
            RoleUpdateData updateData = RoleUpdateData.of(null, null);

            // when & then
            assertThat(updateData.hasAnyUpdate()).isFalse();
        }
    }

    @Nested
    @DisplayName("RoleUpdateData equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 RoleUpdateData는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            RoleUpdateData updateData1 = RoleUpdateData.of("표시명", "설명");
            RoleUpdateData updateData2 = RoleUpdateData.of("표시명", "설명");

            // then
            assertThat(updateData1).isEqualTo(updateData2);
            assertThat(updateData1.hashCode()).isEqualTo(updateData2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 RoleUpdateData는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            RoleUpdateData updateData1 = RoleUpdateData.of("표시명1", "설명1");
            RoleUpdateData updateData2 = RoleUpdateData.of("표시명2", "설명2");

            // then
            assertThat(updateData1).isNotEqualTo(updateData2);
        }
    }
}
