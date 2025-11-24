package com.ryuqq.authhub.domain.organization;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.InvalidOrganizationStateException;
import com.ryuqq.authhub.domain.organization.vo.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Organization 비즈니스 메서드 테스트")
class OrganizationBusinessMethodTest {

    private final Clock clock = () -> Instant.parse("2025-11-24T00:00:00Z");

    @Test
    @DisplayName("activate - INACTIVE → ACTIVE 상태 전환 성공")
    void shouldActivateInactiveOrganization() {
        // Given
        OrganizationId id = OrganizationId.of(100L);
        OrganizationName name = OrganizationName.of("Test Org");
        Long tenantId = 1L;
        Instant createdAt = Instant.parse("2025-11-20T00:00:00Z");
        Instant oldUpdatedAt = Instant.parse("2025-11-22T00:00:00Z");

        Organization organization = Organization.of(id, name, tenantId, OrganizationStatus.INACTIVE, createdAt, oldUpdatedAt);

        // When
        Organization activated = organization.activate(clock);

        // Then
        assertThat(activated.statusValue()).isEqualTo("ACTIVE");
        assertThat(activated.updatedAt()).isEqualTo(clock.now());
        assertThat(activated.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("activate - DELETED 상태에서 activate 시도하면 예외 발생")
    void shouldThrowExceptionWhenActivatingDeletedOrganization() {
        // Given
        OrganizationId id = OrganizationId.of(100L);
        Organization organization = Organization.of(
                id,
                OrganizationName.of("Deleted Org"),
                1L,
                OrganizationStatus.DELETED,
                clock.now(),
                clock.now()
        );

        // When & Then
        assertThatThrownBy(() -> organization.activate(clock))
                .isInstanceOf(InvalidOrganizationStateException.class)
                .hasMessageContaining("Invalid organization status");
    }

    @Test
    @DisplayName("deactivate - ACTIVE → INACTIVE 상태 전환 성공")
    void shouldDeactivateActiveOrganization() {
        // Given
        Organization organization = Organization.forNew(OrganizationName.of("Active Org"), 1L, clock);

        // When
        Organization deactivated = organization.deactivate(clock);

        // Then
        assertThat(deactivated.statusValue()).isEqualTo("INACTIVE");
        assertThat(deactivated.updatedAt()).isEqualTo(clock.now());
    }

    @Test
    @DisplayName("deactivate - DELETED 상태에서 deactivate 시도하면 예외 발생")
    void shouldThrowExceptionWhenDeactivatingDeletedOrganization() {
        // Given
        OrganizationId id = OrganizationId.of(100L);
        Organization organization = Organization.of(
                id,
                OrganizationName.of("Deleted Org"),
                1L,
                OrganizationStatus.DELETED,
                clock.now(),
                clock.now()
        );

        // When & Then
        assertThatThrownBy(() -> organization.deactivate(clock))
                .isInstanceOf(InvalidOrganizationStateException.class);
    }

    @Test
    @DisplayName("delete - ACTIVE/INACTIVE → DELETED 상태 전환 성공")
    void shouldDeleteOrganization() {
        // Given
        Organization organization = Organization.forNew(OrganizationName.of("To Delete"), 1L, clock);

        // When
        Organization deleted = organization.delete(clock);

        // Then
        assertThat(deleted.statusValue()).isEqualTo("DELETED");
        assertThat(deleted.updatedAt()).isEqualTo(clock.now());
    }

    @Test
    @DisplayName("delete - 이미 DELETED 상태에서 delete 시도하면 예외 발생")
    void shouldThrowExceptionWhenDeletingAlreadyDeletedOrganization() {
        // Given
        OrganizationId id = OrganizationId.of(100L);
        Organization organization = Organization.of(
                id,
                OrganizationName.of("Already Deleted"),
                1L,
                OrganizationStatus.DELETED,
                clock.now(),
                clock.now()
        );

        // When & Then
        assertThatThrownBy(() -> organization.delete(clock))
                .isInstanceOf(InvalidOrganizationStateException.class);
    }

    @Test
    @DisplayName("isActive - ACTIVE 상태일 때 true 반환")
    void shouldReturnTrueWhenOrganizationIsActive() {
        // Given
        Organization organization = Organization.forNew(OrganizationName.of("Active"), 1L, clock);

        // When & Then
        assertThat(organization.isActive()).isTrue();
    }

    @Test
    @DisplayName("isActive - INACTIVE 상태일 때 false 반환")
    void shouldReturnFalseWhenOrganizationIsInactive() {
        // Given
        Organization organization = Organization.forNew(OrganizationName.of("Active"), 1L, clock);
        Organization deactivated = organization.deactivate(clock);

        // When & Then
        assertThat(deactivated.isActive()).isFalse();
    }

    @Test
    @DisplayName("isDeleted - DELETED 상태일 때 true 반환")
    void shouldReturnTrueWhenOrganizationIsDeleted() {
        // Given
        Organization organization = Organization.forNew(OrganizationName.of("To Delete"), 1L, clock);
        Organization deleted = organization.delete(clock);

        // When & Then
        assertThat(deleted.isDeleted()).isTrue();
    }
}
