package com.ryuqq.authhub.domain.tenant.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TenantId VO 테스트")
class TenantIdTest {

    @Test
    @DisplayName("유효한 Long으로 TenantId 생성 성공")
    void shouldCreateTenantIdWithValidLong() {
        // Given
        Long id = 1L;

        // When
        TenantId tenantId = TenantId.of(id);

        // Then
        assertThat(tenantId).isNotNull();
        assertThat(tenantId.value()).isEqualTo(id);
    }

    @Test
    @DisplayName("null로 TenantId 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullId() {
        // Given
        Long nullId = null;

        // When & Then
        assertThatThrownBy(() -> TenantId.of(nullId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantId는 null일 수 없습니다");
    }

    @Test
    @DisplayName("0 이하의 값으로 TenantId 생성 시 예외 발생")
    void shouldThrowExceptionWhenNonPositiveId() {
        // Given
        Long zeroId = 0L;
        Long negativeId = -1L;

        // When & Then
        assertThatThrownBy(() -> TenantId.of(zeroId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantId는 양수여야 합니다");

        assertThatThrownBy(() -> TenantId.of(negativeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantId는 양수여야 합니다");
    }
}
