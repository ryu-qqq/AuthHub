package com.ryuqq.authhub.domain.identity.profile.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link UserProfileId} Value Object에 대한 Unit Test.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("UserProfileId Value Object 테스트")
class UserProfileIdTest {

    @Nested
    @DisplayName("생성 성공 테스트")
    class CreateSuccessTest {

        @Test
        @DisplayName("유효한 UUID로 생성 성공")
        void createWithValidUuid() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            UserProfileId userProfileId = new UserProfileId(uuid);

            // then
            assertThat(userProfileId.value()).isEqualTo(uuid);
            assertThat(userProfileId.asString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("newId() 팩토리 메서드로 생성 성공")
        void createWithNewId() {
            // when
            UserProfileId userProfileId = UserProfileId.newId();

            // then
            assertThat(userProfileId.value()).isNotNull();
            assertThat(userProfileId.asString()).isNotBlank();
        }

        @Test
        @DisplayName("fromString() 팩토리 메서드로 생성 성공")
        void createFromString() {
            // given
            String uuidString = UUID.randomUUID().toString();

            // when
            UserProfileId userProfileId = UserProfileId.fromString(uuidString);

            // then
            assertThat(userProfileId.asString()).isEqualTo(uuidString);
        }

        @Test
        @DisplayName("from() 팩토리 메서드로 생성 성공")
        void createFrom() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            UserProfileId userProfileId = UserProfileId.from(uuid);

            // then
            assertThat(userProfileId.value()).isEqualTo(uuid);
        }
    }

    @Nested
    @DisplayName("생성 실패 테스트")
    class CreateFailureTest {

        @Test
        @DisplayName("null UUID로 생성 시 예외 발생")
        void createWithNullUuid() {
            // when & then
            assertThatThrownBy(() -> new UserProfileId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("UserProfileId value cannot be null");
        }

        @Test
        @DisplayName("null 문자열로 fromString() 호출 시 예외 발생")
        void createFromNullString() {
            // when & then
            assertThatThrownBy(() -> UserProfileId.fromString(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("UUID string cannot be null or empty");
        }

        @Test
        @DisplayName("빈 문자열로 fromString() 호출 시 예외 발생")
        void createFromEmptyString() {
            // when & then
            assertThatThrownBy(() -> UserProfileId.fromString(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("UUID string cannot be null or empty");
        }

        @Test
        @DisplayName("유효하지 않은 UUID 형식으로 fromString() 호출 시 예외 발생")
        void createFromInvalidUuidString() {
            // given
            String invalidUuidString = "invalid-uuid";

            // when & then
            assertThatThrownBy(() -> UserProfileId.fromString(invalidUuidString))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid UUID format");
        }
    }

    @Nested
    @DisplayName("동등성 비교 테스트")
    class EqualsTest {

        @Test
        @DisplayName("같은 UUID를 가진 두 객체는 동등하다")
        void equalsWithSameUuid() {
            // given
            UUID uuid = UUID.randomUUID();
            UserProfileId id1 = new UserProfileId(uuid);
            UserProfileId id2 = new UserProfileId(uuid);

            // when & then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 UUID를 가진 두 객체는 동등하지 않다")
        void notEqualsWithDifferentUuid() {
            // given
            UserProfileId id1 = UserProfileId.newId();
            UserProfileId id2 = UserProfileId.newId();

            // when & then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
