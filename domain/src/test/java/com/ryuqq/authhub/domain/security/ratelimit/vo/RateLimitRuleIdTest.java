package com.ryuqq.authhub.domain.security.ratelimit.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * RateLimitRuleId Value Object 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("RateLimitRuleId 단위 테스트")
class RateLimitRuleIdTest {

    @Test
    @DisplayName("newId()로 새로운 RateLimitRuleId를 생성할 수 있다")
    void testNewId() {
        // when
        RateLimitRuleId id = RateLimitRuleId.newId();

        // then
        assertThat(id).isNotNull();
        assertThat(id.value()).isNotNull();
    }

    @Test
    @DisplayName("fromString()으로 UUID 문자열로부터 RateLimitRuleId를 생성할 수 있다")
    void testFromString() {
        // given
        String uuidString = UUID.randomUUID().toString();

        // when
        RateLimitRuleId id = RateLimitRuleId.fromString(uuidString);

        // then
        assertThat(id).isNotNull();
        assertThat(id.asString()).isEqualTo(uuidString);
    }

    @Test
    @DisplayName("fromString()에 null을 전달하면 예외가 발생한다")
    void testFromStringWithNull() {
        // when & then
        assertThatThrownBy(() -> RateLimitRuleId.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("fromString()에 빈 문자열을 전달하면 예외가 발생한다")
    void testFromStringWithEmpty() {
        // when & then
        assertThatThrownBy(() -> RateLimitRuleId.fromString(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("fromString()에 잘못된 UUID 형식을 전달하면 예외가 발생한다")
    void testFromStringWithInvalidFormat() {
        // when & then
        assertThatThrownBy(() -> RateLimitRuleId.fromString("invalid-uuid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid UUID format");
    }

    @Test
    @DisplayName("from()으로 UUID 객체로부터 RateLimitRuleId를 생성할 수 있다")
    void testFrom() {
        // given
        UUID uuid = UUID.randomUUID();

        // when
        RateLimitRuleId id = RateLimitRuleId.from(uuid);

        // then
        assertThat(id).isNotNull();
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("생성자에 null UUID를 전달하면 예외가 발생한다")
    void testConstructorWithNull() {
        // when & then
        assertThatThrownBy(() -> new RateLimitRuleId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("asString()으로 UUID를 문자열로 변환할 수 있다")
    void testAsString() {
        // given
        RateLimitRuleId id = RateLimitRuleId.newId();

        // when
        String result = id.asString();

        // then
        assertThat(result).isNotNull();
        assertThat(result).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    @DisplayName("같은 UUID 값을 가진 RateLimitRuleId는 동등하다")
    void testEquality() {
        // given
        UUID uuid = UUID.randomUUID();
        RateLimitRuleId id1 = RateLimitRuleId.from(uuid);
        RateLimitRuleId id2 = RateLimitRuleId.from(uuid);

        // when & then
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    @DisplayName("다른 UUID 값을 가진 RateLimitRuleId는 동등하지 않다")
    void testInequality() {
        // given
        RateLimitRuleId id1 = RateLimitRuleId.newId();
        RateLimitRuleId id2 = RateLimitRuleId.newId();

        // when & then
        assertThat(id1).isNotEqualTo(id2);
    }
}
