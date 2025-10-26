package com.ryuqq.authhub.domain.security.blacklist.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BlacklistedTokenId 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("BlacklistedTokenId 단위 테스트")
class BlacklistedTokenIdTest {

    @Test
    @DisplayName("newId()로 새로운 BlacklistedTokenId를 생성할 수 있다")
    void newId_ShouldCreateNewBlacklistedTokenId() {
        // when
        final BlacklistedTokenId id = BlacklistedTokenId.newId();

        // then
        assertThat(id).isNotNull();
        assertThat(id.value()).isNotNull();
    }

    @Test
    @DisplayName("newId()로 생성한 ID는 매번 다른 값이어야 한다")
    void newId_ShouldCreateUniqueIds() {
        // when
        final BlacklistedTokenId id1 = BlacklistedTokenId.newId();
        final BlacklistedTokenId id2 = BlacklistedTokenId.newId();

        // then
        assertThat(id1.value()).isNotEqualTo(id2.value());
    }

    @Test
    @DisplayName("fromString()으로 UUID 문자열로부터 BlacklistedTokenId를 생성할 수 있다")
    void fromString_ShouldCreateBlacklistedTokenIdFromValidUuidString() {
        // given
        final String uuidString = "550e8400-e29b-41d4-a716-446655440000";

        // when
        final BlacklistedTokenId id = BlacklistedTokenId.fromString(uuidString);

        // then
        assertThat(id).isNotNull();
        assertThat(id.asString()).isEqualTo(uuidString);
    }

    @Test
    @DisplayName("fromString()은 null UUID 문자열을 거부한다")
    void fromString_ShouldRejectNullUuidString() {
        // when & then
        assertThatThrownBy(() -> BlacklistedTokenId.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UUID string cannot be null or empty");
    }

    @Test
    @DisplayName("fromString()은 빈 UUID 문자열을 거부한다")
    void fromString_ShouldRejectEmptyUuidString() {
        // when & then
        assertThatThrownBy(() -> BlacklistedTokenId.fromString(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UUID string cannot be null or empty");
    }

    @Test
    @DisplayName("fromString()은 공백만 있는 UUID 문자열을 거부한다")
    void fromString_ShouldRejectBlankUuidString() {
        // when & then
        assertThatThrownBy(() -> BlacklistedTokenId.fromString("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UUID string cannot be null or empty");
    }

    @Test
    @DisplayName("fromString()은 유효하지 않은 UUID 형식을 거부한다")
    void fromString_ShouldRejectInvalidUuidFormat() {
        // given
        final String invalidUuid = "not-a-valid-uuid";

        // when & then
        assertThatThrownBy(() -> BlacklistedTokenId.fromString(invalidUuid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid UUID format");
    }

    @Test
    @DisplayName("from()으로 UUID 객체로부터 BlacklistedTokenId를 생성할 수 있다")
    void from_ShouldCreateBlacklistedTokenIdFromUuid() {
        // given
        final UUID uuid = UUID.randomUUID();

        // when
        final BlacklistedTokenId id = BlacklistedTokenId.from(uuid);

        // then
        assertThat(id).isNotNull();
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("from()은 null UUID를 거부한다")
    void from_ShouldRejectNullUuid() {
        // when & then
        assertThatThrownBy(() -> BlacklistedTokenId.from(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("BlacklistedTokenId value cannot be null");
    }

    @Test
    @DisplayName("asString()은 UUID를 문자열로 변환한다")
    void asString_ShouldConvertUuidToString() {
        // given
        final String uuidString = "550e8400-e29b-41d4-a716-446655440000";
        final BlacklistedTokenId id = BlacklistedTokenId.fromString(uuidString);

        // when
        final String result = id.asString();

        // then
        assertThat(result).isEqualTo(uuidString);
    }

    @Test
    @DisplayName("동일한 UUID를 가진 BlacklistedTokenId는 equals()로 같다고 판단된다")
    void equals_ShouldReturnTrueForSameUuid() {
        // given
        final UUID uuid = UUID.randomUUID();
        final BlacklistedTokenId id1 = BlacklistedTokenId.from(uuid);
        final BlacklistedTokenId id2 = BlacklistedTokenId.from(uuid);

        // when & then
        assertThat(id1).isEqualTo(id2);
    }

    @Test
    @DisplayName("다른 UUID를 가진 BlacklistedTokenId는 equals()로 다르다고 판단된다")
    void equals_ShouldReturnFalseForDifferentUuid() {
        // given
        final BlacklistedTokenId id1 = BlacklistedTokenId.newId();
        final BlacklistedTokenId id2 = BlacklistedTokenId.newId();

        // when & then
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("동일한 UUID를 가진 BlacklistedTokenId는 같은 hashCode를 반환한다")
    void hashCode_ShouldReturnSameHashCodeForSameUuid() {
        // given
        final UUID uuid = UUID.randomUUID();
        final BlacklistedTokenId id1 = BlacklistedTokenId.from(uuid);
        final BlacklistedTokenId id2 = BlacklistedTokenId.from(uuid);

        // when & then
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}
