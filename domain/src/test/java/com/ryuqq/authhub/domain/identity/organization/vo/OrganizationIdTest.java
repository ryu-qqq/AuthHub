package com.ryuqq.authhub.domain.identity.organization.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * OrganizationId Value Object 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("OrganizationId 단위 테스트")
class OrganizationIdTest {

    @Test
    @DisplayName("newId()로 새로운 OrganizationId를 생성할 수 있다")
    void testNewId() {
        // when
        OrganizationId id = OrganizationId.newId();

        // then
        assertThat(id).isNotNull();
        assertThat(id.value()).isNotNull();
    }

    @Test
    @DisplayName("fromString()으로 UUID 문자열로부터 OrganizationId를 생성할 수 있다")
    void testFromString() {
        // given
        String uuidString = UUID.randomUUID().toString();

        // when
        OrganizationId id = OrganizationId.fromString(uuidString);

        // then
        assertThat(id).isNotNull();
        assertThat(id.asString()).isEqualTo(uuidString);
    }

    @Test
    @DisplayName("fromString()에 null을 전달하면 예외가 발생한다")
    void testFromStringWithNull() {
        // when & then
        assertThatThrownBy(() -> OrganizationId.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("fromString()에 빈 문자열을 전달하면 예외가 발생한다")
    void testFromStringWithEmpty() {
        // when & then
        assertThatThrownBy(() -> OrganizationId.fromString(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("fromString()에 잘못된 UUID 형식을 전달하면 예외가 발생한다")
    void testFromStringWithInvalidFormat() {
        // when & then
        assertThatThrownBy(() -> OrganizationId.fromString("invalid-uuid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid UUID format");
    }

    @Test
    @DisplayName("from()으로 UUID 객체로부터 OrganizationId를 생성할 수 있다")
    void testFrom() {
        // given
        UUID uuid = UUID.randomUUID();

        // when
        OrganizationId id = OrganizationId.from(uuid);

        // then
        assertThat(id).isNotNull();
        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("생성자에 null UUID를 전달하면 예외가 발생한다")
    void testConstructorWithNull() {
        // when & then
        assertThatThrownBy(() -> new OrganizationId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("asString()으로 UUID를 문자열로 변환할 수 있다")
    void testAsString() {
        // given
        OrganizationId id = OrganizationId.newId();

        // when
        String result = id.asString();

        // then
        assertThat(result).isNotNull();
        assertThat(result).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    @DisplayName("같은 UUID 값을 가진 OrganizationId는 동등하다")
    void testEquality() {
        // given
        UUID uuid = UUID.randomUUID();
        OrganizationId id1 = OrganizationId.from(uuid);
        OrganizationId id2 = OrganizationId.from(uuid);

        // when & then
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    @DisplayName("다른 UUID 값을 가진 OrganizationId는 동등하지 않다")
    void testInequality() {
        // given
        OrganizationId id1 = OrganizationId.newId();
        OrganizationId id2 = OrganizationId.newId();

        // when & then
        assertThat(id1).isNotEqualTo(id2);
    }
}
