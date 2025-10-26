package com.ryuqq.authhub.domain.security.blacklist.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Jti 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("Jti 단위 테스트")
class JtiTest {

    @Test
    @DisplayName("of()로 유효한 JTI 값을 생성할 수 있다")
    void of_ShouldCreateValidJti() {
        // given
        final String jtiValue = "unique-jwt-id-123";

        // when
        final Jti jti = Jti.of(jtiValue);

        // then
        assertThat(jti).isNotNull();
        assertThat(jti.value()).isEqualTo(jtiValue);
    }

    @Test
    @DisplayName("of()는 null 값을 거부한다")
    void of_ShouldRejectNullValue() {
        // when & then
        assertThatThrownBy(() -> Jti.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JTI value cannot be null or empty");
    }

    @Test
    @DisplayName("of()는 빈 문자열을 거부한다")
    void of_ShouldRejectEmptyString() {
        // when & then
        assertThatThrownBy(() -> Jti.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JTI value cannot be null or empty");
    }

    @Test
    @DisplayName("of()는 공백만 있는 문자열을 거부한다")
    void of_ShouldRejectBlankString() {
        // when & then
        assertThatThrownBy(() -> Jti.of("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JTI value cannot be null or empty");
    }

    @Test
    @DisplayName("of()는 최대 길이를 초과하는 문자열을 거부한다")
    void of_ShouldRejectExcessiveLength() {
        // given
        final String excessiveJti = "a".repeat(256);

        // when & then
        assertThatThrownBy(() -> Jti.of(excessiveJti))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JTI value exceeds maximum length");
    }

    @Test
    @DisplayName("최대 길이(255자) 문자열은 허용된다")
    void of_ShouldAcceptMaxLength() {
        // given
        final String maxLengthJti = "a".repeat(255);

        // when
        final Jti jti = Jti.of(maxLengthJti);

        // then
        assertThat(jti).isNotNull();
        assertThat(jti.value()).hasSize(255);
    }

    @Test
    @DisplayName("asString()은 JTI 값을 문자열로 반환한다")
    void asString_ShouldReturnJtiValue() {
        // given
        final String jtiValue = "test-jti-456";
        final Jti jti = Jti.of(jtiValue);

        // when
        final String result = jti.asString();

        // then
        assertThat(result).isEqualTo(jtiValue);
    }

    @Test
    @DisplayName("isSameAs()는 동일한 JTI 값에 대해 true를 반환한다")
    void isSameAs_ShouldReturnTrueForSameValue() {
        // given
        final String value = "same-jti-value";
        final Jti jti1 = Jti.of(value);
        final Jti jti2 = Jti.of(value);

        // when & then
        assertThat(jti1.isSameAs(jti2)).isTrue();
    }

    @Test
    @DisplayName("isSameAs()는 다른 JTI 값에 대해 false를 반환한다")
    void isSameAs_ShouldReturnFalseForDifferentValue() {
        // given
        final Jti jti1 = Jti.of("jti-1");
        final Jti jti2 = Jti.of("jti-2");

        // when & then
        assertThat(jti1.isSameAs(jti2)).isFalse();
    }

    @Test
    @DisplayName("isSameAs()는 null에 대해 false를 반환한다")
    void isSameAs_ShouldReturnFalseForNull() {
        // given
        final Jti jti = Jti.of("test-jti");

        // when & then
        assertThat(jti.isSameAs(null)).isFalse();
    }

    @Test
    @DisplayName("isSameAs()는 대소문자를 구분한다")
    void isSameAs_ShouldBeCaseSensitive() {
        // given
        final Jti jti1 = Jti.of("JTI-Value");
        final Jti jti2 = Jti.of("jti-value");

        // when & then
        assertThat(jti1.isSameAs(jti2)).isFalse();
    }

    @Test
    @DisplayName("동일한 값을 가진 Jti는 equals()로 같다고 판단된다")
    void equals_ShouldReturnTrueForSameValue() {
        // given
        final String value = "jti-equals-test";
        final Jti jti1 = Jti.of(value);
        final Jti jti2 = Jti.of(value);

        // when & then
        assertThat(jti1).isEqualTo(jti2);
    }

    @Test
    @DisplayName("다른 값을 가진 Jti는 equals()로 다르다고 판단된다")
    void equals_ShouldReturnFalseForDifferentValue() {
        // given
        final Jti jti1 = Jti.of("jti-1");
        final Jti jti2 = Jti.of("jti-2");

        // when & then
        assertThat(jti1).isNotEqualTo(jti2);
    }

    @Test
    @DisplayName("동일한 값을 가진 Jti는 같은 hashCode를 반환한다")
    void hashCode_ShouldReturnSameHashCodeForSameValue() {
        // given
        final String value = "jti-hashcode-test";
        final Jti jti1 = Jti.of(value);
        final Jti jti2 = Jti.of(value);

        // when & then
        assertThat(jti1.hashCode()).isEqualTo(jti2.hashCode());
    }
}
