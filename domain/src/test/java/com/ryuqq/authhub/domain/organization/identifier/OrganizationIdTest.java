package com.ryuqq.authhub.domain.organization.identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @Test
    @DisplayName("[forNew] 아직 영속화되지 않은 상태의 OrganizationId 생성")
    void forNew_shouldCreateOrganizationIdWithNullValue() {
        // When
        OrganizationId organizationId = OrganizationId.forNew();

        // Then
        assertThat(organizationId).isNotNull();
        assertThat(organizationId.value()).isNull();
    }

    @Test
    @DisplayName("[isNew] null 값을 가진 OrganizationId는 true 반환")
    void isNew_shouldReturnTrueForNullValue() {
        // Given
        OrganizationId organizationId = OrganizationId.forNew();

        // When
        boolean result = organizationId.isNew();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("[isNew] 영속화된 OrganizationId는 false 반환")
    void isNew_shouldReturnFalseForPersistedId() {
        // Given
        OrganizationId organizationId = OrganizationId.of(100L);

        // When
        boolean result = organizationId.isNew();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("[equals/hashCode] 동일한 값을 가진 OrganizationId는 동등함")
    void equals_shouldReturnTrueForSameValue() {
        // Given
        OrganizationId organizationId1 = OrganizationId.of(100L);
        OrganizationId organizationId2 = OrganizationId.of(100L);

        // Then
        assertThat(organizationId1).isEqualTo(organizationId2);
        assertThat(organizationId1.hashCode()).isEqualTo(organizationId2.hashCode());
    }

    @Test
    @DisplayName("[equals/hashCode] 다른 값을 가진 OrganizationId는 동등하지 않음")
    void equals_shouldReturnFalseForDifferentValue() {
        // Given
        OrganizationId organizationId1 = OrganizationId.of(100L);
        OrganizationId organizationId2 = OrganizationId.of(200L);

        // Then
        assertThat(organizationId1).isNotEqualTo(organizationId2);
    }
}
