package com.ryuqq.authhub.domain.organization.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrganizationId VO 테스트")
class OrganizationIdTest {

    @Test
    @DisplayName("유효한 Long으로 OrganizationId 생성 성공")
    void shouldCreateOrganizationIdWithValidLong() {
        // Given
        Long id = 100L;

        // When
        OrganizationId organizationId = OrganizationId.of(id);

        // Then
        assertThat(organizationId).isNotNull();
        assertThat(organizationId.value()).isEqualTo(id);
    }

    @Test
    @DisplayName("null로 OrganizationId 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullId() {
        // Given
        Long nullId = null;

        // When & Then
        assertThatThrownBy(() -> OrganizationId.of(nullId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrganizationId는 null일 수 없습니다");
    }

    @Test
    @DisplayName("0 이하의 값으로 OrganizationId 생성 시 예외 발생")
    void shouldThrowExceptionWhenNonPositiveId() {
        // Given
        Long zeroId = 0L;
        Long negativeId = -1L;

        // When & Then
        assertThatThrownBy(() -> OrganizationId.of(zeroId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrganizationId는 양수여야 합니다");

        assertThatThrownBy(() -> OrganizationId.of(negativeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrganizationId는 양수여야 합니다");
    }
}
