package com.ryuqq.authhub.domain.user.identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserId 식별자 VO 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserId 테스트")
class UserIdTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드")
    class ForNewTest {

        @Test
        @DisplayName("새로운 UUIDv7 기반 UserId를 생성한다")
        void shouldCreateNewUserIdWithUuidV7() {
            // when
            UserId userId = UserId.forNew(UUID.randomUUID());

            // then
            assertThat(userId).isNotNull();
            assertThat(userId.value()).isNotNull();
        }

        @Test
        @DisplayName("매번 새로운 ID를 생성한다")
        void shouldCreateUniqueIdEachTime() {
            // when
            UserId id1 = UserId.forNew(UUID.randomUUID());
            UserId id2 = UserId.forNew(UUID.randomUUID());

            // then
            assertThat(id1).isNotEqualTo(id2);
            assertThat(id1.value()).isNotEqualTo(id2.value());
        }
    }

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("UUID로 UserId를 생성한다")
        void shouldCreateUserIdFromUuid() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            UserId userId = UserId.of(uuid);

            // then
            assertThat(userId).isNotNull();
            assertThat(userId.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("null UUID로 생성 시 예외 발생")
        void shouldThrowExceptionWhenUuidIsNull() {
            assertThatThrownBy(() -> UserId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("UserId value cannot be null");
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 UserId는 동일하다")
        void shouldBeEqualWhenSameValue() {
            // given
            UUID uuid = UUID.randomUUID();
            UserId id1 = UserId.of(uuid);
            UserId id2 = UserId.of(uuid);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 UserId는 다르다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            UserId id1 = UserId.of(UUID.randomUUID());
            UserId id2 = UserId.of(UUID.randomUUID());

            // then
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("자기 자신과 같다")
        void shouldBeEqualToItself() {
            // given
            UserId userId = UserId.forNew(UUID.randomUUID());

            // then
            assertThat(userId).isEqualTo(userId);
        }

        @Test
        @DisplayName("null과 같지 않다")
        void shouldNotBeEqualToNull() {
            // given
            UserId userId = UserId.forNew(UUID.randomUUID());

            // then
            assertThat(userId).isNotEqualTo(null);
        }

        @Test
        @DisplayName("다른 타입과 같지 않다")
        void shouldNotBeEqualToDifferentType() {
            // given
            UserId userId = UserId.forNew(UUID.randomUUID());

            // then
            assertThat(userId).isNotEqualTo("not-a-user-id");
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("UserId의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            UUID uuid = UUID.randomUUID();
            UserId userId = UserId.of(uuid);

            // when
            String toString = userId.toString();

            // then
            assertThat(toString).contains("UserId");
            assertThat(toString).contains(uuid.toString());
        }
    }
}
