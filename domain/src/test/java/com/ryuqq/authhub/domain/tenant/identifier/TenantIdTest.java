package com.ryuqq.authhub.domain.tenant.identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @Test
    @DisplayName("[forNew] 아직 영속화되지 않은 상태의 TenantId 생성")
    void forNew_shouldCreateTenantIdWithNullValue() {
        // When
        TenantId tenantId = TenantId.forNew();

        // Then
        assertThat(tenantId).isNotNull();
        assertThat(tenantId.value()).isNull();
    }

    @Test
    @DisplayName("[isNew] null 값을 가진 TenantId는 true 반환")
    void isNew_shouldReturnTrueForNullValue() {
        // Given
        TenantId tenantId = TenantId.forNew();

        // When
        boolean result = tenantId.isNew();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("[isNew] 영속화된 TenantId는 false 반환")
    void isNew_shouldReturnFalseForPersistedId() {
        // Given
        TenantId tenantId = TenantId.of(1L);

        // When
        boolean result = tenantId.isNew();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("[equals/hashCode] 동일한 값을 가진 TenantId는 동등함")
    void equals_shouldReturnTrueForSameValue() {
        // Given
        TenantId tenantId1 = TenantId.of(1L);
        TenantId tenantId2 = TenantId.of(1L);

        // Then
        assertThat(tenantId1).isEqualTo(tenantId2);
        assertThat(tenantId1.hashCode()).isEqualTo(tenantId2.hashCode());
    }

    @Test
    @DisplayName("[equals/hashCode] 다른 값을 가진 TenantId는 동등하지 않음")
    void equals_shouldReturnFalseForDifferentValue() {
        // Given
        TenantId tenantId1 = TenantId.of(1L);
        TenantId tenantId2 = TenantId.of(2L);

        // Then
        assertThat(tenantId1).isNotEqualTo(tenantId2);
    }
}
