package com.ryuqq.authhub.domain.tenant.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TenantName VO 테스트")
class TenantNameTest {

    @Test
    @DisplayName("유효한 이름으로 TenantName 생성 성공")
    void shouldCreateTenantNameWithValidName() {
        // Given
        String name = "Valid Tenant";

        // When
        TenantName tenantName = TenantName.of(name);

        // Then
        assertThat(tenantName).isNotNull();
        assertThat(tenantName.value()).isEqualTo(name);
    }

    @Test
    @DisplayName("null로 TenantName 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullName() {
        // Given
        String nullName = null;

        // When & Then
        assertThatThrownBy(() -> TenantName.of(nullName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantName은 null일 수 없습니다");
    }

    @Test
    @DisplayName("빈 문자열로 TenantName 생성 시 예외 발생")
    void shouldThrowExceptionWhenEmptyName() {
        // Given
        String emptyName = "";

        // When & Then
        assertThatThrownBy(() -> TenantName.of(emptyName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantName은 2자 이상 100자 이하여야 합니다");
    }

    @Test
    @DisplayName("2자 미만으로 TenantName 생성 시 예외 발생")
    void shouldThrowExceptionWhenTooShortName() {
        // Given
        String tooShortName = "A";

        // When & Then
        assertThatThrownBy(() -> TenantName.of(tooShortName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantName은 2자 이상 100자 이하여야 합니다");
    }

    @Test
    @DisplayName("100자 초과로 TenantName 생성 시 예외 발생")
    void shouldThrowExceptionWhenTooLongName() {
        // Given
        String tooLongName = "A".repeat(101);

        // When & Then
        assertThatThrownBy(() -> TenantName.of(tooLongName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantName은 2자 이상 100자 이하여야 합니다");
    }

    @Test
    @DisplayName("경계값 테스트 - 2자 정확히")
    void shouldCreateTenantNameWithExactlyTwoCharacters() {
        // Given
        String twoChars = "AB";

        // When
        TenantName tenantName = TenantName.of(twoChars);

        // Then
        assertThat(tenantName).isNotNull();
        assertThat(tenantName.value()).isEqualTo(twoChars);
    }

    @Test
    @DisplayName("경계값 테스트 - 100자 정확히")
    void shouldCreateTenantNameWithExactlyOneHundredCharacters() {
        // Given
        String hundredChars = "A".repeat(100);

        // When
        TenantName tenantName = TenantName.of(hundredChars);

        // Then
        assertThat(tenantName).isNotNull();
        assertThat(tenantName.value()).isEqualTo(hundredChars);
    }
}
