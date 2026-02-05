package com.ryuqq.authhub.domain.service.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceUpdateData Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceUpdateData 테스트")
class ServiceUpdateDataTest {

    @Nested
    @DisplayName("ServiceUpdateData 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            ServiceUpdateData updateData = ServiceUpdateData.of("새 이름", "새 설명", "ACTIVE");

            // then
            assertThat(updateData.name()).isEqualTo("새 이름");
            assertThat(updateData.description()).isEqualTo("새 설명");
            assertThat(updateData.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            ServiceUpdateData updateData = new ServiceUpdateData("이름", "설명", "INACTIVE");

            // then
            assertThat(updateData.name()).isEqualTo("이름");
            assertThat(updateData.description()).isEqualTo("설명");
            assertThat(updateData.status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("name만 null로 생성할 수 있다")
        void shouldCreateWithNullName() {
            // when
            ServiceUpdateData updateData = ServiceUpdateData.of(null, "설명", "ACTIVE");

            // then
            assertThat(updateData.name()).isNull();
            assertThat(updateData.description()).isEqualTo("설명");
            assertThat(updateData.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("description만 null로 생성할 수 있다")
        void shouldCreateWithNullDescription() {
            // when
            ServiceUpdateData updateData = ServiceUpdateData.of("이름", null, "ACTIVE");

            // then
            assertThat(updateData.name()).isEqualTo("이름");
            assertThat(updateData.description()).isNull();
            assertThat(updateData.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("status만 null로 생성할 수 있다")
        void shouldCreateWithNullStatus() {
            // when
            ServiceUpdateData updateData = ServiceUpdateData.of("이름", "설명", null);

            // then
            assertThat(updateData.name()).isEqualTo("이름");
            assertThat(updateData.description()).isEqualTo("설명");
            assertThat(updateData.status()).isNull();
        }

        @Test
        @DisplayName("모든 필드를 null로 생성할 수 있다")
        void shouldCreateWithAllNull() {
            // when
            ServiceUpdateData updateData = ServiceUpdateData.of(null, null, null);

            // then
            assertThat(updateData.name()).isNull();
            assertThat(updateData.description()).isNull();
            assertThat(updateData.status()).isNull();
        }
    }

    @Nested
    @DisplayName("ServiceUpdateData hasName 테스트")
    class HasNameTests {

        @Test
        @DisplayName("name이 있으면 true를 반환한다")
        void shouldReturnTrueWhenNameExists() {
            // given
            ServiceUpdateData updateData = ServiceUpdateData.of("이름", "설명", "ACTIVE");

            // when & then
            assertThat(updateData.hasName()).isTrue();
        }

        @Test
        @DisplayName("name이 null이면 false를 반환한다")
        void shouldReturnFalseWhenNameIsNull() {
            // given
            ServiceUpdateData updateData = ServiceUpdateData.of(null, "설명", "ACTIVE");

            // when & then
            assertThat(updateData.hasName()).isFalse();
        }
    }

    @Nested
    @DisplayName("ServiceUpdateData hasDescription 테스트")
    class HasDescriptionTests {

        @Test
        @DisplayName("description이 있으면 true를 반환한다")
        void shouldReturnTrueWhenDescriptionExists() {
            // given
            ServiceUpdateData updateData = ServiceUpdateData.of("이름", "설명", "ACTIVE");

            // when & then
            assertThat(updateData.hasDescription()).isTrue();
        }

        @Test
        @DisplayName("description이 null이면 false를 반환한다")
        void shouldReturnFalseWhenDescriptionIsNull() {
            // given
            ServiceUpdateData updateData = ServiceUpdateData.of("이름", null, "ACTIVE");

            // when & then
            assertThat(updateData.hasDescription()).isFalse();
        }
    }

    @Nested
    @DisplayName("ServiceUpdateData hasStatus 테스트")
    class HasStatusTests {

        @Test
        @DisplayName("status가 있으면 true를 반환한다")
        void shouldReturnTrueWhenStatusExists() {
            // given
            ServiceUpdateData updateData = ServiceUpdateData.of("이름", "설명", "ACTIVE");

            // when & then
            assertThat(updateData.hasStatus()).isTrue();
        }

        @Test
        @DisplayName("status가 null이면 false를 반환한다")
        void shouldReturnFalseWhenStatusIsNull() {
            // given
            ServiceUpdateData updateData = ServiceUpdateData.of("이름", "설명", null);

            // when & then
            assertThat(updateData.hasStatus()).isFalse();
        }
    }

    @Nested
    @DisplayName("ServiceUpdateData hasAnyUpdate 테스트")
    class HasAnyUpdateTests {

        @Test
        @DisplayName("모두 있으면 true를 반환한다")
        void shouldReturnTrueWhenAllExist() {
            // given
            ServiceUpdateData updateData = ServiceUpdateData.of("이름", "설명", "ACTIVE");

            // when & then
            assertThat(updateData.hasAnyUpdate()).isTrue();
        }

        @Test
        @DisplayName("name만 있으면 true를 반환한다")
        void shouldReturnTrueWhenOnlyNameExists() {
            // given
            ServiceUpdateData updateData = ServiceUpdateData.of("이름", null, null);

            // when & then
            assertThat(updateData.hasAnyUpdate()).isTrue();
        }

        @Test
        @DisplayName("description만 있으면 true를 반환한다")
        void shouldReturnTrueWhenOnlyDescriptionExists() {
            // given
            ServiceUpdateData updateData = ServiceUpdateData.of(null, "설명", null);

            // when & then
            assertThat(updateData.hasAnyUpdate()).isTrue();
        }

        @Test
        @DisplayName("status만 있으면 true를 반환한다")
        void shouldReturnTrueWhenOnlyStatusExists() {
            // given
            ServiceUpdateData updateData = ServiceUpdateData.of(null, null, "ACTIVE");

            // when & then
            assertThat(updateData.hasAnyUpdate()).isTrue();
        }

        @Test
        @DisplayName("모두 null이면 false를 반환한다")
        void shouldReturnFalseWhenAllAreNull() {
            // given
            ServiceUpdateData updateData = ServiceUpdateData.of(null, null, null);

            // when & then
            assertThat(updateData.hasAnyUpdate()).isFalse();
        }
    }

    @Nested
    @DisplayName("ServiceUpdateData equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 ServiceUpdateData는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            ServiceUpdateData updateData1 = ServiceUpdateData.of("이름", "설명", "ACTIVE");
            ServiceUpdateData updateData2 = ServiceUpdateData.of("이름", "설명", "ACTIVE");

            // then
            assertThat(updateData1).isEqualTo(updateData2);
            assertThat(updateData1.hashCode()).isEqualTo(updateData2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ServiceUpdateData는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            ServiceUpdateData updateData1 = ServiceUpdateData.of("이름1", "설명1", "ACTIVE");
            ServiceUpdateData updateData2 = ServiceUpdateData.of("이름2", "설명2", "INACTIVE");

            // then
            assertThat(updateData1).isNotEqualTo(updateData2);
        }
    }
}
