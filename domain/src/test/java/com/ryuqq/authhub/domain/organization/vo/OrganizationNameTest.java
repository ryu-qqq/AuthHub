package com.ryuqq.authhub.domain.organization.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OrganizationName VO 테스트")
class OrganizationNameTest {

    @Test
    @DisplayName("유효한 이름으로 OrganizationName 생성 성공")
    void shouldCreateOrganizationNameWithValidName() {
        // Given
        String name = "Valid Organization";

        // When
        OrganizationName organizationName = OrganizationName.of(name);

        // Then
        assertThat(organizationName).isNotNull();
        assertThat(organizationName.value()).isEqualTo(name);
    }

    @Test
    @DisplayName("null로 OrganizationName 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullName() {
        // Given
        String nullName = null;

        // When & Then
        assertThatThrownBy(() -> OrganizationName.of(nullName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrganizationName은 null일 수 없습니다");
    }

    @Test
    @DisplayName("빈 문자열로 OrganizationName 생성 시 예외 발생")
    void shouldThrowExceptionWhenEmptyName() {
        // Given
        String emptyName = "";

        // When & Then
        assertThatThrownBy(() -> OrganizationName.of(emptyName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrganizationName은 2자 이상 100자 이하여야 합니다");
    }

    @Test
    @DisplayName("2자 미만으로 OrganizationName 생성 시 예외 발생")
    void shouldThrowExceptionWhenTooShortName() {
        // Given
        String tooShortName = "A";

        // When & Then
        assertThatThrownBy(() -> OrganizationName.of(tooShortName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrganizationName은 2자 이상 100자 이하여야 합니다");
    }

    @Test
    @DisplayName("100자 초과로 OrganizationName 생성 시 예외 발생")
    void shouldThrowExceptionWhenTooLongName() {
        // Given
        String tooLongName = "A".repeat(101);

        // When & Then
        assertThatThrownBy(() -> OrganizationName.of(tooLongName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrganizationName은 2자 이상 100자 이하여야 합니다");
    }

    @Test
    @DisplayName("경계값 테스트 - 2자 정확히")
    void shouldCreateOrganizationNameWithExactlyTwoCharacters() {
        // Given
        String twoChars = "AB";

        // When
        OrganizationName organizationName = OrganizationName.of(twoChars);

        // Then
        assertThat(organizationName).isNotNull();
        assertThat(organizationName.value()).isEqualTo(twoChars);
    }

    @Test
    @DisplayName("경계값 테스트 - 100자 정확히")
    void shouldCreateOrganizationNameWithExactlyOneHundredCharacters() {
        // Given
        String hundredChars = "A".repeat(100);

        // When
        OrganizationName organizationName = OrganizationName.of(hundredChars);

        // Then
        assertThat(organizationName).isNotNull();
        assertThat(organizationName.value()).isEqualTo(hundredChars);
    }
}
