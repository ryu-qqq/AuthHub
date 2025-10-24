package com.ryuqq.authhub.domain.identity.organization.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * OrganizationName Value Object 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("OrganizationName 단위 테스트")
class OrganizationNameTest {

    @Test
    @DisplayName("유효한 조직명으로 OrganizationName을 생성할 수 있다")
    void testValidOrganizationName() {
        // given
        String name = "Nike Store";

        // when
        OrganizationName organizationName = new OrganizationName(name);

        // then
        assertThat(organizationName).isNotNull();
        assertThat(organizationName.getValue()).isEqualTo(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"나이키", "Nike", "Nike123", "Nike-Store", "Nike_Store", "나이키 스토어"})
    @DisplayName("다양한 형식의 유효한 조직명을 생성할 수 있다")
    void testVariousValidNames(String name) {
        // when & then
        assertThatCode(() -> new OrganizationName(name))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("null 조직명으로 생성 시 예외가 발생한다")
    void testNullName() {
        // when & then
        assertThatThrownBy(() -> new OrganizationName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("빈 문자열 조직명으로 생성 시 예외가 발생한다")
    void testEmptyName() {
        // when & then
        assertThatThrownBy(() -> new OrganizationName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("공백만 있는 조직명으로 생성 시 예외가 발생한다")
    void testBlankName() {
        // when & then
        assertThatThrownBy(() -> new OrganizationName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("시작 공백이 있는 조직명으로 생성 시 예외가 발생한다")
    void testLeadingSpace() {
        // when & then
        assertThatThrownBy(() -> new OrganizationName(" Nike"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("leading or trailing spaces");
    }

    @Test
    @DisplayName("끝 공백이 있는 조직명으로 생성 시 예외가 발생한다")
    void testTrailingSpace() {
        // when & then
        assertThatThrownBy(() -> new OrganizationName("Nike "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("leading or trailing spaces");
    }

    @Test
    @DisplayName("연속 공백이 있는 조직명으로 생성 시 예외가 발생한다")
    void testConsecutiveSpaces() {
        // when & then
        assertThatThrownBy(() -> new OrganizationName("Nike  Store"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("consecutive spaces");
    }

    @Test
    @DisplayName("최소 길이(2자) 미만 조직명으로 생성 시 예외가 발생한다")
    void testTooShortName() {
        // when & then
        assertThatThrownBy(() -> new OrganizationName("N"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be between");
    }

    @Test
    @DisplayName("최대 길이(100자) 초과 조직명으로 생성 시 예외가 발생한다")
    void testTooLongName() {
        // given
        String longName = "A".repeat(101);

        // when & then
        assertThatThrownBy(() -> new OrganizationName(longName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be between");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Nike@Store", "Nike#Store", "Nike!Store", "Nike$Store", "Nike%Store"})
    @DisplayName("특수문자(하이픈, 언더스코어 제외)가 포함된 조직명으로 생성 시 예외가 발생한다")
    void testInvalidCharacters(String name) {
        // when & then
        assertThatThrownBy(() -> new OrganizationName(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("can only contain");
    }

    @Test
    @DisplayName("getValue()로 조직명 문자열을 반환할 수 있다")
    void testGetValue() {
        // given
        String name = "Nike Store";
        OrganizationName organizationName = new OrganizationName(name);

        // when
        String result = organizationName.getValue();

        // then
        assertThat(result).isEqualTo(name);
    }

    @Test
    @DisplayName("getLength()로 조직명 길이를 반환할 수 있다")
    void testGetLength() {
        // given
        OrganizationName organizationName = new OrganizationName("Nike");

        // when
        int length = organizationName.getLength();

        // then
        assertThat(length).isEqualTo(4);
    }

    @Test
    @DisplayName("isValid()로 유효한 조직명을 검증할 수 있다")
    void testIsValidWithValidName() {
        // when
        boolean result = OrganizationName.isValid("Nike Store");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValid()로 잘못된 조직명을 검증할 수 있다")
    void testIsValidWithInvalidName() {
        // when & then
        assertThat(OrganizationName.isValid(null)).isFalse();
        assertThat(OrganizationName.isValid("")).isFalse();
        assertThat(OrganizationName.isValid("N")).isFalse();
        assertThat(OrganizationName.isValid(" Nike")).isFalse();
        assertThat(OrganizationName.isValid("Nike  Store")).isFalse();
        assertThat(OrganizationName.isValid("Nike@Store")).isFalse();
    }

    @Test
    @DisplayName("같은 값을 가진 OrganizationName은 동등하다")
    void testEquality() {
        // given
        OrganizationName name1 = new OrganizationName("Nike");
        OrganizationName name2 = new OrganizationName("Nike");

        // when & then
        assertThat(name1).isEqualTo(name2);
        assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
    }

    @Test
    @DisplayName("다른 값을 가진 OrganizationName은 동등하지 않다")
    void testInequality() {
        // given
        OrganizationName name1 = new OrganizationName("Nike");
        OrganizationName name2 = new OrganizationName("Adidas");

        // when & then
        assertThat(name1).isNotEqualTo(name2);
    }
}
