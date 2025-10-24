package com.ryuqq.authhub.domain.identity.organization.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * OrganizationType Enum 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("OrganizationType 단위 테스트")
class OrganizationTypeTest {

    @Test
    @DisplayName("SELLER 타입을 가져올 수 있다")
    void testSellerType() {
        // when
        OrganizationType type = OrganizationType.SELLER;

        // then
        assertThat(type).isNotNull();
        assertThat(type.getDescription()).isEqualTo("판매자");
    }

    @Test
    @DisplayName("COMPANY 타입을 가져올 수 있다")
    void testCompanyType() {
        // when
        OrganizationType type = OrganizationType.COMPANY;

        // then
        assertThat(type).isNotNull();
        assertThat(type.getDescription()).isEqualTo("기업");
    }

    @Test
    @DisplayName("fromString()으로 문자열로부터 OrganizationType을 생성할 수 있다")
    void testFromString() {
        // when
        OrganizationType seller = OrganizationType.fromString("SELLER");
        OrganizationType company = OrganizationType.fromString("COMPANY");

        // then
        assertThat(seller).isEqualTo(OrganizationType.SELLER);
        assertThat(company).isEqualTo(OrganizationType.COMPANY);
    }

    @Test
    @DisplayName("fromString()에 null을 전달하면 예외가 발생한다")
    void testFromStringWithNull() {
        // when & then
        assertThatThrownBy(() -> OrganizationType.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("fromString()에 잘못된 타입을 전달하면 예외가 발생한다")
    void testFromStringWithInvalidType() {
        // when & then
        assertThatThrownBy(() -> OrganizationType.fromString("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid organization type");
    }

    @Test
    @DisplayName("isValid()로 유효한 타입 문자열을 검증할 수 있다")
    void testIsValidWithValidType() {
        // when & then
        assertThat(OrganizationType.isValid("SELLER")).isTrue();
        assertThat(OrganizationType.isValid("COMPANY")).isTrue();
    }

    @Test
    @DisplayName("isValid()로 잘못된 타입 문자열을 검증할 수 있다")
    void testIsValidWithInvalidType() {
        // when & then
        assertThat(OrganizationType.isValid(null)).isFalse();
        assertThat(OrganizationType.isValid("INVALID")).isFalse();
    }
}
